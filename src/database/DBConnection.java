package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static Connection connection;

    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://localhost/student_login", "root", "");
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
