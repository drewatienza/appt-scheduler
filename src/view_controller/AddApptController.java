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

import static model.Database.addNewAppt;

public class AddApptController {

    @FXML
    private Label addApptLabel;
    @FXML
    private Label addApptTitleLabel;
    @FXML
    private TextField addApptTitleField;
    @FXML
    private Label addApptDescLabel;
    @FXML
    private TextArea addApptDescField;
    @FXML
    private Label addApptLocationLabel;
    @FXML
    private TextField addApptLocationField;
    @FXML
    private Label addApptContactLabel;
    @FXML
    private TextField addApptContactField;
    @FXML
    private Label addApptUrlLabel;
    @FXML
    private TextField addApptUrlField;
    @FXML
    private Label addApptDateLabel;
    @FXML
    private DatePicker addApptDatePicker;
    @FXML
    private Label addApptStartLabel;
    @FXML
    private TextField addApptStartHrField;
    @FXML
    private TextField addApptStartMinField;
    @FXML
    private ChoiceBox<String> addApptStartChoice;
    @FXML
    private Label addApptEndLabel;
    @FXML
    private TextField addApptEndHrField;
    @FXML
    private TextField addApptEndMinField;
    @FXML
    private ChoiceBox<String> addApptEndChoice;
    @FXML
    private TableView<Customer> addApptAddTV;
    @FXML
    private TableColumn<Customer, String> addNameColTV;
    @FXML
    private TableColumn<Customer, String> addCityColTV;
    @FXML
    private TableColumn<Customer, String> addCountryColTV;
    @FXML
    private TableColumn<Customer, String> addPhoneColTV;
    @FXML
    private Button addApptAddBtn;
    @FXML
    private TableView<Customer> addApptDelTV;
    @FXML
    private TableColumn<Customer, String> delNameColTV;
    @FXML
    private TableColumn<Customer, String> delCityColTV;
    @FXML
    private TableColumn<Customer, String> delCountryColTV;
    @FXML
    private TableColumn<Customer, String> delPhoneColTV;
    @FXML
    private Button addApptDelBtn;
    @FXML
    private Button addApptSaveBtn;
    @FXML
    private Button addApptCancelBtn;

    @FXML
    private ObservableList<Customer> currentCust = FXCollections.observableArrayList();

    // ADD CUSTOMER TO THE APPOINTMENT TABLEVIEW
    @FXML
    private void addCustToAppt(ActionEvent event) {
        if (addCustToApptAlert(addApptAddTV, currentCust)) return;
        updateAddApptDelTV();
    }

    static boolean addCustToApptAlert(TableView<Customer> addApptAddTV, ObservableList<Customer> currentCust) {
        Customer customer = addApptAddTV.getSelectionModel().getSelectedItem();
        if (customer == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Customer Add Error");
            alert.setContentText("A customer from the table must be selected to add to the appointment.");
            alert.showAndWait();
            return true;
        }
        if (currentCust.size() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Customer Error");
            alert.setContentText("You can select only one customer to add to the appointment.");
            alert.showAndWait();
            return true;
        }
        currentCust.add(customer);
        return false;
    }

    // DELETE CUSTOMER FROM THE APPOINTMENT TABLEVIEW
    @FXML
    private void delCustDelTV(ActionEvent event) {
        Customer customer = addApptDelTV.getSelectionModel().getSelectedItem();
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
            updateAddApptDelTV();
        }
    }

    // SAVE APPOINTMENT
    @FXML
    private void addApptSave(ActionEvent event) {
        Customer customer = null;
        if (currentCust.size() == 1) {
            customer = currentCust.get(0);
        }
        String title = addApptTitleField.getText();
        String description = addApptDescField.getText();
        String location = addApptLocationField.getText();
        String contact = addApptContactField.getText();
        if (contact.length() == 0 && customer != null) {
            contact = customer.getCustomerName() + ", " + customer.getPhone();
        }
        String url = addApptUrlField.getText();
        LocalDate appointmentDate = addApptDatePicker.getValue();
        String startHour = addApptStartHrField.getText();
        String startMinute = addApptStartMinField.getText();
        String startAmPm = addApptStartChoice.getSelectionModel().getSelectedItem();
        String endHour = addApptEndHrField.getText();
        String endMinute = addApptEndMinField.getText();
        String endAmPm = addApptEndChoice.getSelectionModel().getSelectedItem();
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
        if (addNewAppt(customer, title, description, location, contact, url, startUTC, endUTC)) {
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

    // CANCEL ADD
    @FXML
    private void addApptCancel(ActionEvent event) {
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
    public void updateAddApptAddTV() {
        addApptAddTV.setItems(CustList.getCustomerList());
    }

    // UPDATE TABLEVIEW OF CUSTOMERS TO DELETE
    public void updateAddApptDelTV() {
        addApptDelTV.setItems(currentCust);
    }

    @FXML
    public void initialize() {
        // BUTTON ACTION
        addApptAddBtn.setOnAction(event -> addCustToAppt(event));
        addApptDelBtn.setOnAction(event -> delCustDelTV(event));
        addApptSaveBtn.setOnAction(event -> addApptSave(event));
        addApptCancelBtn.setOnAction(event -> addApptCancel(event));

        // POPULATE THE TABLEVIEW
        addNameColTV.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        addCityColTV.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        addCountryColTV.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        addPhoneColTV.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        delNameColTV.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        delCityColTV.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        delCountryColTV.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        delPhoneColTV.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());

        updateAddApptAddTV();
        updateAddApptDelTV();
    }
}
