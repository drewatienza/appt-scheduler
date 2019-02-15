package view_controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.Optional;

import static model.Appointment.apptNotify;

public class MainMenuController {

    @FXML
    public Button goToCustBtn;
    @FXML
    public Button goToApptBtn;
    @FXML
    public Button goToReportsBtn;
    @FXML
    public Button mainMenuExitBtn;

    // GO TO CUSTOMER SCREEN
    @FXML
    private void goToCustScreen(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("Customer.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GO TO APPOINTMENT SCREEN
    @FXML
    private void goToApptScreen(ActionEvent event) {
        try {
            Parent customerScreen = FXMLLoader.load(getClass().getResource("Appointment.fxml"));
            Scene scene = new Scene(customerScreen);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GO TO REPORTS SCREEN
    @FXML
    private void goToReportsScreen(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("Reports.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // EXIT APPLICATION
    @FXML
    private void exit(ActionEvent event) {
        exitApp();
    }

    static void exitApp() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Exit");
        alert.setHeaderText("Exit Confirmation");
        alert.setContentText("Are you sure you want to exit the application?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    @FXML
    public void initialize() {
        // BUTTON ACTION
        goToCustBtn.setOnAction(event -> goToCustScreen(event));
        goToApptBtn.setOnAction(event -> goToApptScreen(event));
        goToReportsBtn.setOnAction(event -> goToReportsScreen(event));
        mainMenuExitBtn.setOnAction(event -> exit(event));
        
       
        apptNotify();
    }
}
