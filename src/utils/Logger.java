package utils;

import view_controller.LoginController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.time.Instant;
import java.util.Arrays;

import utils.DBManager;

public class Logger {

    public static String currentUser;

    // VALIDATE LOGIN
    public static boolean validateLogin(String userName, String password) {
        int userId = getUserId(userName);
        boolean pword = validatePassword(userId, password);
        if (pword) {
            setCurrentUser(userName);
            try {
                Path path = Paths.get("UserLogin.txt");
                Files.write(path, Arrays.asList(currentUser + " logged in at " + Date.from(Instant.now()).toString() + "."),
                        StandardCharsets.UTF_8, Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        else {
            return false;
        }
    }

    private static int getUserId(String userName) {
        int userId = -1;
        try {
            Statement statement = DBManager.getConnection().createStatement();
            ResultSet result = statement.executeQuery("SELECT userId FROM user WHERE userName = '" + userName + "'");

            if (result.next()) {
                userId = result.getInt("userId");
            }
            result.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }
    // VALIDATE PASSWORD
    private static boolean validatePassword(int userId, String password) {
        try {
            Statement statement = DBManager.getConnection().createStatement();
            ResultSet result = statement.executeQuery("SELECT password FROM user WHERE userId = " + userId);

            String pwordInDB = null;
            if (result.next()) {
                pwordInDB = result.getString("password");
            }
            else {
                return false;
            }
            result.close();
            if (pwordInDB.equals(password)) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void setCurrentUser(String userName) {
        currentUser = userName;
    }
}
