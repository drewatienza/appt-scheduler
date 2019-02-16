package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {

    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String db = "U05EUp";
    private static final String url = "jdbc:mysql://52.206.157.109/" + db;
    private static final String user = "U05EUp";
    private static final String pass = "53688478889";
    private static Connection conn;

    public DBManager() {}

    // CONNECT TO DATABASE
    public static void connect() {
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Connected to MySQL Database");
        } catch (ClassNotFoundException e) {
            System.out.println("Class Not Found " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
    }

    // CLOSE DATABASE CONNECTION
    public static void disconnect() {
        try {
            conn.close();
            System.out.println("Disconnected from MySQL Database");
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }

    // RETURN DATABASE CONNECTION
    public static Connection getConnection() {
        return conn;
    }
}
