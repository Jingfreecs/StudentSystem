package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Login;
import model.Session;
import model.SignUp;
import passwordhasher.PasswordHash;
import database.DBConnection;

public class LoginController implements Initializable {

    private double x = 0;
    private double y = 0;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    @FXML
    private AnchorPane main_form;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Text errorHandler;

    @FXML
    private Button loginbtn;

    @FXML
    private AnchorPane login_form;

    @FXML
    private AnchorPane signup_form;

    @FXML
    private ComboBox<String> chooseCourse;

    @FXML
    private ComboBox<String> chooseYear;

    @FXML
    private TextField new_name;

    @FXML
    private TextField new_username;

    @FXML
    private TextField new_email;

    @FXML
    private PasswordField new_password;

    @FXML
    private PasswordField new_retype_password;

    @FXML
    private void alreadyHaveAccount() {
        login_form.setVisible(true);
        signup_form.setVisible(false);
    }

    @FXML
    private void createAccount() {
        login_form.setVisible(false);
        signup_form.setVisible(true);
    }

    @FXML
    private void btnClose() {
        System.exit(0);
    }

    @FXML
    private void btnMinimize() {
        Stage stage = (Stage) main_form.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void btnLogin() {
        String checkUserFromStore = "SELECT * FROM storestudent WHERE (username = ? OR email = ?)";

        connect = DBConnection.connect();
        try {
            Login user = new Login(usernameField.getText(), passwordField.getText());

            prepare = connect.prepareStatement(checkUserFromStore);
            prepare.setString(1, user.getUsernameOrEmail());
            prepare.setString(2, user.getUsernameOrEmail());
            result = prepare.executeQuery();

            if (result.next()) {
                boolean verifyPassword = PasswordHash.password_verify(user.getPassword(), result.getString("password"));
                if (verifyPassword) {
                    showAlert(AlertType.INFORMATION, "Message from admin",
                            "Your account is pending, please wait for admin approval.");
                    return;
                } else {
                    errorHandler.setText("Wrong password, please try again!");
                    errorHandler.setFill(Color.web("EE4266"));
                    return;
                }
            } else {
                errorHandler.setText("Username or email not found!");
                errorHandler.setFill(Color.web("EE4266"));
            }

            String sql = "SELECT * FROM login WHERE (username = ? OR email = ?)";

            if (user.getUsernameOrEmail().isEmpty() || user.getPassword().isEmpty()) {
                showAlert(AlertType.WARNING, "Error message", "Please fill the blank fields.");
                return;
            }

            prepare = connect.prepareStatement(sql);
            prepare.setString(1, user.getUsernameOrEmail());
            prepare.setString(2, user.getUsernameOrEmail());
            result = prepare.executeQuery();

            if (result.next()) {
                String verifyPassword = result.getString("password");

                if (passwordField.getText().equals(verifyPassword)) {
                    Session.setUserID(result.getString("id"));
                    errorHandler.setText("Log in successfully");
                    errorHandler.setFill(Color.web("198754"));

                    showAlert(AlertType.INFORMATION, "Success message", "Log in successfully.");

                    loginbtn.getScene().getWindow().hide();

                    Stage stage = new Stage();

                    Parent root = FXMLLoader.load(getClass().getResource("../views/dashboard_form.fxml"));
                    Scene scene = new Scene(root);

                    root.setOnMousePressed((MouseEvent event) -> {
                        x = event.getSceneX();
                        y = event.getSceneY();
                    });

                    root.setOnMouseDragged((MouseEvent event) -> {
                        stage.setX(event.getScreenX() - x);
                        stage.setY(event.getScreenY() - y);

                        stage.setOpacity(.8);
                    });

                    root.setOnMouseReleased((MouseEvent event) -> {
                        stage.setOpacity(1);
                    });

                    stage.initStyle(StageStyle.TRANSPARENT);

                    stage.setScene(scene);
                    stage.show();

                } else {
                    errorHandler.setText("Wrong password, please try again!");
                    errorHandler.setFill(Color.web("EE4266"));
                }
            } else {
                errorHandler.setText("Username or email not found!");
                errorHandler.setFill(Color.web("EE4266"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnSignUp() {
        String sql = "INSERT INTO storestudent (name, username, email, course, year, password) VALUES (?, ?, ?, ?, ?, ?)";

        connect = DBConnection.connect();
        try {
            SignUp user = new SignUp(new_name.getText(), new_username.getText(), new_email.getText(),
                    chooseCourse.getSelectionModel().getSelectedItem(),
                    chooseYear.getSelectionModel().getSelectedItem(), new_password.getText());

            if (user.getName().isEmpty() || user.getUsername().isEmpty() || user.getEmail().isEmpty()
                    || chooseCourse.getSelectionModel().getSelectedItem() == null
                    || chooseYear.getSelectionModel().getSelectedItem() == null || user.getPassword().isEmpty()) {
                showAlert(AlertType.WARNING, "Error message", "Please fill the blank fields.");
                return;
            }

            String checkUsernameFromStore = "SELECT username FROM storestudent WHERE username = ?";
            prepare = connect.prepareStatement(checkUsernameFromStore);
            prepare.setString(1, user.getUsername());
            result = prepare.executeQuery();
            if (result.next()) {
                showAlert(AlertType.ERROR, "Error message", "Username: " + user.getUsername() + " already taken!");
                return;
            }

            String checkUsernameLogin = "SELECT username FROM login WHERE username = ?";
            prepare = connect.prepareStatement(checkUsernameLogin);
            prepare.setString(1, user.getUsername());
            result = prepare.executeQuery();
            if (result.next()) {
                showAlert(AlertType.ERROR, "Error message", "Username: " + user.getUsername() + " already taken!");
                return;
            }

            if (!EMAIL_PATTERN.matcher(new_email.getText()).matches()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error message");
                alert.setHeaderText(null);
                alert.setContentText("\t\t\t\tInvalid email\n\nPlease put and @ in your email (ex. name@example.com)");
                alert.showAndWait();
                return;
            }

            String checkEmailFromStore = "SELECT email FROM storestudent WHERE email = ?";

            prepare = connect.prepareStatement(checkEmailFromStore);
            prepare.setString(1, user.getEmail());
            result = prepare.executeQuery();
            if (result.next()) {
                showAlert(AlertType.ERROR, "Error message", "Email: " + user.getEmail() + " already taken!");
                return;
            }

            String checkEmailFromLogin = "SELECT email FROM login WHERE email = ?";
            prepare = connect.prepareStatement(checkEmailFromLogin);
            prepare.setString(1, user.getEmail());
            result = prepare.executeQuery();
            if (result.next()) {
                showAlert(AlertType.ERROR, "Error message", "Email: " + user.getEmail() + " already taken!");
                return;
            }

            if (!user.getPassword().equals(new_retype_password.getText())) {
                showAlert(AlertType.ERROR, "Error message", "Password doesn't match");
                return;
            }

            String encryptPassword = PasswordHash.password_hash(user.getPassword());

            prepare = connect.prepareStatement(sql);
            prepare.setString(1, user.getName());
            prepare.setString(2, user.getUsername());
            prepare.setString(3, user.getEmail());
            prepare.setString(4, user.getCourse());
            prepare.setString(5, user.getYear());
            prepare.setString(6, encryptPassword);
            prepare.executeUpdate();

            showAlert(AlertType.INFORMATION, "Approval message",
                    "Account registration requested successfully, please wait for admin approval.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType alertType, String title, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resource) {
        ObservableList<String> course = FXCollections.observableArrayList("BS Information Technology",
                "BS Business Administration", "BSED English", "BSED Math", "BEED Generalist", "BS Computer Science",
                "BS Information System", "BS Tourism Management", "BS Computer Engineering");
        chooseCourse.setItems(course);

        ObservableList<String> year = FXCollections.observableArrayList("First Year", "Second Year", "Third Year",
                "Fourth Year");
        chooseYear.setItems(year);
    }
}