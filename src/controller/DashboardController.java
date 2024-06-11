package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DashboardController implements Initializable {

    @FXML
    private BorderPane main_form;

    @Override
    public void initialize(URL url, ResourceBundle resource) {

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

}
