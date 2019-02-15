package view_controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import static utils.Logger.validateLogin;


public class LoginController {

    @FXML
    private Label loginTitle;
    @FXML
    private Label userLoginLabel;
    @FXML
    private TextField userLoginTxt;
    @FXML
    private Label pwLoginLabel;
    @FXML
    private TextField pwLoginTxt;
    @FXML
    private Label errorMessage;
    @FXML
    private Button loginSubmitBtn;

    // SET LANGUAGE
    private void language() {
        ResourceBundle rb = ResourceBundle.getBundle("resources/login", Locale.getDefault());
        loginTitle.setText(rb.getString("title"));
        userLoginLabel.setText(rb.getString("username"));
        pwLoginLabel.setText(rb.getString("password"));
        loginSubmitBtn.setText(rb.getString("submitBtn"));
    }

    // VALIDATE LOGIN
    @FXML
    private void loginSubmit(ActionEvent event) {
        String userName = userLoginTxt.getText();
        String password = pwLoginTxt.getText();
        ResourceBundle rb = ResourceBundle.getBundle("resources/login", Locale.getDefault());

        boolean validateLogin = validateLogin(userName, password);
        if (validateLogin) {
            try {
                Parent mainScreen = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
                Scene scene = new Scene(mainScreen, 400, 350);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            errorMessage.setText(rb.getString("errorText"));
        } 
    }

    // INITIALIZE SCREEN
    @FXML
    public void initialize() {
        language();
        
        // BUTTON ACTION
        loginSubmitBtn.setOnAction(event -> loginSubmit(event));
    }
}
