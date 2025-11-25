package payroll.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/payroll";
    private static final String USER = "root";
    private static final String PASS = "galgotias@123";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
