package view_controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointment;
import model.Database;
import model.ApptList;

import java.io.IOException;

import static model.Database.updateApptList;
import static model.ApptList.getAppointmentList;

public class AppointmentController {

    @FXML
    private TableView<Appointment> apptTableView;
    @FXML
    private TableColumn<Appointment, String> apptTitleColumnTV;
    @FXML
    private TableColumn<Appointment, String> apptDateColumnTV;
    @FXML
    private TableColumn<Appointment, String> apptContactColumnTV;
    @FXML
    public Button addApptBtn;
    @FXML
    private Button modApptBtn;
    @FXML
    private Button delApptBtn;
    @FXML
    public Button calendarBtn;
    @FXML
    public Button mainMenuBtn;
    @FXML
    private Button exitApptBtn;
    @FXML
    private static int apptIndexMod;

    // ADD APPOINTMENT
    @FXML
    private void addAppt(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("AddAppt.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // MODIFY APPOINTMENT
    @FXML
    private void modAppt(ActionEvent event) {
        Appointment apptToMod = apptTableView.getSelectionModel().getSelectedItem();
        if (apptToMod == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Modify Appointment Error");
            alert.setContentText("To modify an appointment, please select an appointment from the table first.");
            alert.showAndWait();
            return;
        }
        apptIndexMod = getAppointmentList().indexOf(apptToMod);
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("ModAppt.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getApptIndexMod() {
        return apptIndexMod;
    }

    // DELETE APPOINTMENT
    @FXML
    private void deleteAppt(ActionEvent event) {
        Appointment apptToDel = apptTableView.getSelectionModel().getSelectedItem();
        if (apptToDel == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Delete Apppointment Error");
            alert.setContentText("To delete an appointment, please select the appointment from the table first.");
            alert.showAndWait();
            return;
        }
        Database.deleteAppt(apptToDel);
    }

    // GO TO CALENDAR VIEW
    @FXML
    private void goToCalendarView(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("CalendarView.fxml"));
            Scene scene = new Scene(parent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GO TO MAIN MENU
    @FXML
    private void goToMainMenu(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    // EXIT APPLICATION
    @FXML
    private void exit(ActionEvent event) {
        MainMenuController.exitApp();
    }

    // UPDATE TABLEVIEW
    @FXML
    public void updateAddAppointmentTableView() {
        updateApptList();
        apptTableView.setItems(ApptList.getAppointmentList());
    }

    // INITIALIZE SCREEN
    @FXML
    public void initialize() {
        // BUTTON ACTION
        addApptBtn.setOnAction(event -> addAppt(event));
        modApptBtn.setOnAction(event -> modAppt(event));
        delApptBtn.setOnAction(event -> deleteAppt(event));
        calendarBtn.setOnAction(event -> goToCalendarView(event));
        mainMenuBtn.setOnAction(event -> goToMainMenu(event));
        exitApptBtn.setOnAction(event -> exit(event));

        // POPULATE TABLE
        apptTitleColumnTV.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        apptDateColumnTV.setCellValueFactory(cellData -> cellData.getValue().dateStringProperty());
        apptContactColumnTV.setCellValueFactory(cellData -> cellData.getValue().contactProperty());

        updateAddAppointmentTableView();
    }
}
