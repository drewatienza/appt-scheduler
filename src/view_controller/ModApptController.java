package view_controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import model.CustList;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static model.ApptList.getAppointmentList;
import static model.CustList.getCustomerList;
import static model.Database.modifyAppt;

public class ModApptController {

    @FXML
    private Label modApptLabel;
    @FXML
    private Label modApptTitleLabel;
    @FXML
    private TextField modApptTitleField;
    @FXML
    private Label modApptDescLabel;
    @FXML
    private TextArea modApptDescField;
    @FXML
    private Label modApptLocationLabel;
    @FXML
    private TextField modApptLocationField;
    @FXML
    private Label modApptContactLabel;
    @FXML
    private TextField modApptContactField;
    @FXML
    private Label modApptUrlLabel;
    @FXML
    private TextField modApptUrlField;
    @FXML
    private Label modApptDateLabel;
    @FXML
    private DatePicker modApptDatePicker;
    @FXML
    private Label modApptStartLabel;
    @FXML
    private TextField modApptStartHrField;
    @FXML
    private TextField modApptStartMinField;
    @FXML
    private ChoiceBox<String> modApptStartChoice;
    @FXML
    private Label modApptEndLabel;
    @FXML
    private TextField modApptEndHrField;
    @FXML
    private TextField modApptEndMinField;
    @FXML
    private ChoiceBox<String> modApptEndChoice;
    @FXML
    private TableView<Customer> modApptAddTV;
    @FXML
    private TableColumn<Customer, String> modAddNameCol;
    @FXML
    private TableColumn<Customer, String> modAddCityCol;
    @FXML
    private TableColumn<Customer, String> modAddCountryCol;
    @FXML
    private TableColumn<Customer, String> modAddPhoneCol;
    @FXML
    private Button btnModifyAppointmentAdd;
    @FXML
    private TableView<Customer> modApptDelTV;
    @FXML
    private TableColumn<Customer, String> modDelNameCol;
    @FXML
    private TableColumn<Customer, String> modDelCityCol;
    @FXML
    private TableColumn<Customer, String> modDelCountryCol;
    @FXML
    private TableColumn<Customer, String> modDelPhoneCol;
    @FXML
    private Button modApptDelBtn;
    @FXML
    private Button modApptSaveBtn;
    @FXML
    private Button modApptCancelBtn;

    @FXML
    private Appointment appointment;
    @FXML
    int appointmentIndexToModify = AppointmentController.getApptIndexMod();
    @FXML
    private ObservableList<Customer> currentCust = FXCollections.observableArrayList();

    // ADD CUSTOMER TO THE APPOINTMENT TABLEVIEW
    @FXML
    private void addCustToAppt(ActionEvent event) {
        if (AddApptController.addCustToApptAlert(modApptAddTV, currentCust)) return;
        updateModApptDelTV();
    }

    // DELETE CUSTOMER FROM THE APPOINTMENT TABLEVIEW
    @FXML
    private void delCustDelTV(ActionEvent event) {
        Customer customer = modApptDelTV.getSelectionModel().getSelectedItem();
        if (customer == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Delete Customer Error");
            alert.setContentText("Please select a customer to delete from the appointment.");
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Confirm Delte Customer");
        alert.setContentText("Are you sure you want to delete the customer from the appointment?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            currentCust.remove(customer);
            updateModApptDelTV();
        }
    }

    // SAVE APPOINTMENT
    @FXML
    private void modApptSave(ActionEvent event) {
        Customer customer = null;
        if (currentCust.size() == 1) {
            customer = currentCust.get(0);
        }
        int appointmentId = appointment.getAppointmentId();
        String title = modApptTitleField.getText();
        String description = modApptDescField.getText();
        String location = modApptLocationField.getText();
        String contact = modApptContactField.getText();
        if (contact.length() == 0 && customer != null) {
            contact = customer.getCustomerName() + ", " + customer.getPhone();
        }
        String url = modApptUrlField.getText();
        LocalDate appointmentDate = modApptDatePicker.getValue();
        String startHour = modApptStartHrField.getText();
        String startMinute = modApptStartMinField.getText();
        String startAmPm = modApptStartChoice.getSelectionModel().getSelectedItem();
        String endHour = modApptEndHrField.getText();
        String endMinute = modApptEndMinField.getText();
        String endAmPm = modApptEndChoice.getSelectionModel().getSelectedItem();
        String error = Appointment.isApptValid(customer, title, description, location,
                appointmentDate, startHour, startMinute, startAmPm, endHour, endMinute, endAmPm);
        if (error.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Adding Appointment Error");
            alert.setContentText(error);
            alert.showAndWait();
            return;
        }
        SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm a");
        localDateFormat.setTimeZone(TimeZone.getDefault());
        Date startLocal = null;
        Date endLocal = null;
        try {
            startLocal = localDateFormat.parse(appointmentDate.toString() + " " + startHour + ":" + startMinute + " " + startAmPm);
            endLocal = localDateFormat.parse(appointmentDate.toString() + " " + endHour + ":" + endMinute + " " + endAmPm);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        ZonedDateTime startUTC = ZonedDateTime.ofInstant(startLocal.toInstant(), ZoneId.of("UTC"));
        ZonedDateTime endUTC = ZonedDateTime.ofInstant(endLocal.toInstant(), ZoneId.of("UTC"));
        if (modifyAppt(appointmentId, customer, title, description, location, contact, url, startUTC, endUTC)) {
            try {
                Parent parent = FXMLLoader.load(getClass().getResource("Appointment.fxml"));
                Scene scene = new Scene(parent);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // CANCEL MODIFICATION
    @FXML
    private void modApptCancel(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Confirm Cancellation");
        alert.setHeaderText("Confirm Cancellation");
        alert.setContentText("Are you sure you want to cancel adding the appointment?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK) {
            try {
                Parent parent = FXMLLoader.load(getClass().getResource("Appointment.fxml"));
                Scene scene = new Scene(parent);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // UPDATE TABLEVIEW OF CUSTOMERS TO ADD
    public void updateModApptAddTV() {
        modApptAddTV.setItems(CustList.getCustomerList());
    }

    // UPDATE TABLEVIEW OF CUSTOMERS TO DELETE
    public void updateModApptDelTV() {
        modApptDelTV.setItems(currentCust);
    }

    @FXML
    public void initialize() {
        appointment = getAppointmentList().get(appointmentIndexToModify);

        // BUTTON ACTION
        btnModifyAppointmentAdd.setOnAction(event -> addCustToAppt(event));
        modApptDelBtn.setOnAction(event -> delCustDelTV(event));
        modApptSaveBtn.setOnAction(event -> modApptSave(event));
        modApptCancelBtn.setOnAction(event -> modApptCancel(event));

        // GET APPOINTMENT INFO
        String title = appointment.getTitle();
        String description = appointment.getDescription();
        String location = appointment.getLocation();
        String contact = appointment.getContact();
        String url = appointment.getUrl();
        
        Date appointmentDate = appointment.getStartDate();
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(appointmentDate);
        int appointmentYear = calendar.get(Calendar.YEAR);
        int appointmentMonth = calendar.get(Calendar.MONTH) + 1;
        int appointmentDay = calendar.get(Calendar.DAY_OF_MONTH);
        LocalDate appointmentLocalDate = LocalDate.of(appointmentYear, appointmentMonth, appointmentDay);
        String startString = appointment.getStartString();
        String startHour = startString.substring(0,2);
        if (Integer.parseInt(startHour) < 10) {
            startHour = startHour.substring(1,2);
        }
        String startMinute = startString.substring(3,5);
        String startAmPm = startString.substring(6,8);
        String endString = appointment.getEndString();
        String endHour = endString.substring(0,2);
        if (Integer.parseInt(endHour) < 10) {
            endHour = endHour.substring(1,2);
        }
        String endMinute = endString.substring(3,5);
        String endAmPm = endString.substring(6,8);
        int customerId = appointment.getCustomerId();
        ObservableList<Customer> customerRoster = getCustomerList();
        for (Customer customer : customerRoster) {
            if (customer.getCustomerId() == customerId) {
                currentCust.add(customer);
            }
        }

        // POPULATE TABLEVIEW
        modAddNameCol.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        modAddCityCol.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        modAddCountryCol.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        modAddPhoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        modDelNameCol.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        modDelCityCol.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        modDelCountryCol.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        modDelPhoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        
        // POPULATE FIELDS
        modApptTitleField.setText(title);
        modApptDescField.setText(description);
        modApptLocationField.setText(location);
        modApptContactField.setText(contact);
        modApptUrlField.setText(url);
        modApptDatePicker.setValue(appointmentLocalDate);
        modApptStartHrField.setText(startHour);
        modApptStartMinField.setText(startMinute);
        modApptStartChoice.setValue(startAmPm);
        modApptEndHrField.setText(endHour);
        modApptEndMinField.setText(endMinute);
        modApptEndChoice.setValue(endAmPm);

        updateModApptAddTV();
        updateModApptDelTV();
    }
}
