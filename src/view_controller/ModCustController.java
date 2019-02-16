package view_controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Customer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static model.CustList.getCustomerList;
import static model.Database.*;

public class ModCustController {

    @FXML
    private Label modCustTitle;
    @FXML
    private Label modCustNameLabel;
    @FXML
    private TextField modCustNameField;
    @FXML
    private Label modCustAdd1Label;
    @FXML
    private TextField modCustAdd1Field;
    @FXML
    private Label modCustAdd2Label;
    @FXML
    private TextField modCustAdd2Field;
    @FXML
    private Label modCustCityLabel;
    @FXML
    private TextField modCustCityField;
    @FXML
    private Label modCustCountryLabel;
    @FXML
    private TextField modCustCountryField;
    @FXML
    private Label modCustPostalLabel;
    @FXML
    private TextField modCustPostalField;
    @FXML
    private Label modCustPhoneLabel;
    @FXML
    private TextField modCustPhoneField;
    @FXML
    private Button modCustSaveBtn;
    @FXML
    private Button modCustCancelBtn;

    @FXML
    private Customer customer;
    @FXML
    int modCustIndex = AppointmentController.getApptIndexMod();

    // SAVE CUSTOMER
    @FXML
    private void modCustSave(ActionEvent event) throws Exception {
        int customerId = customer.getCustomerId();
        String customerName = modCustNameField.getText();
        String address = modCustAdd1Field.getText();
        String address2 = modCustAdd2Field.getText();
        String city = modCustCityField.getText();
        String country = modCustCountryField.getText();
        String postalCode = modCustPostalField.getText();
        String phone = modCustPhoneField.getText();
        String errorMessage = Customer.isCustomerValid(customerName, address, city, country, postalCode, phone);
        if (errorMessage.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Add Customer Error");
            alert.setContentText("There was error adding customer.  Customer may already exist in the database.");
            alert.showAndWait();
            return;
        }
        int modifyCustomerCheck = modifyCustomer(customerId, customerName, address, address2, city, country, postalCode, phone);
        if (modifyCustomerCheck == 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Modify Customer Error");
            alert.setContentText("This customer already exists in the database.");
            alert.showAndWait();

        }
        else if (modifyCustomerCheck == 0) {
            int countryId = country(country);
            int cityId = city(city, countryId);
            int addressId = address(address, address2, postalCode, phone, cityId);
//            setCustomerToActive(customerName, addressId);
        }
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("Customer.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // CANCEL CUSTOMER
    @FXML
    private void modCustCancel(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("CANCEL");
        alert.setHeaderText("Cancel Confirmation");
        alert.setContentText("Are you sure you want to cancel adding the customer?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                Parent parent = FXMLLoader.load(getClass().getResource("Customer.fxml"));
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

    @FXML
    public void initialize() {
        // GET CUSTOMER
        customer = getCustomerList().get(modCustIndex);
        String customerName = customer.getCustomerName();
        String address = customer.getAddress();
        String address2 = customer.getAddress2();
        String city = customer.getCity();
        String country = customer.getCountry();
        String postalCode = customer.getPostalCode();
        String phone = customer.getPhone();
        
        // POPULATE FIELDS
        modCustNameField.setText(customerName);
        modCustAdd1Field.setText(address);
        modCustAdd2Field.setText(address2);
        modCustCityField.setText(city);
        modCustCountryField.setText(country);
        modCustPostalField.setText(postalCode);
        modCustPhoneField.setText(phone);

        // BUTTON ACTION
        modCustSaveBtn.setOnAction(event -> {
            try {           
                modCustSave(event);
            } catch (Exception ex) {
                Logger.getLogger(ModCustController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        modCustCancelBtn.setOnAction(event -> modCustCancel(event));
    }
}
