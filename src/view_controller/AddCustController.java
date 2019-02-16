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

import static model.Database.addNewCust;

public class AddCustController {

    @FXML
    private Label addCustTitle;
    @FXML
    private Label addCustNameLabel;
    @FXML
    private TextField addCustNameField;
    @FXML
    private Label addCustAdd1Label;
    @FXML
    private TextField addCustAdd1Field;
    @FXML
    private Label addCustAdd2Label;
    @FXML
    private TextField addCustAdd2Field;
    @FXML
    private Label addCustCityLabel;
    @FXML
    private TextField addCustCityField;
    @FXML
    private Label addCustCountryLabel;
    @FXML
    private TextField addCustCountryField;
    @FXML
    private Label addCustPostalLabel;
    @FXML
    private TextField addCustPostalField;
    @FXML
    private Label addCustPhoneLabel;
    @FXML
    private TextField addCustPhoneField;
    @FXML
    private Button addCustSaveBtn;
    @FXML
    private Button addCustCancelBtn;

    // SAVE CUSTOMER
    @FXML
    private void addCustSave(ActionEvent event) throws Exception {
        String customerName = addCustNameField.getText();
        String address = addCustAdd1Field.getText();
        String address2 = addCustAdd2Field.getText();
        String city = addCustCityField.getText();
        String country = addCustCountryField.getText();
        String postalCode = addCustPostalField.getText();
        String phone = addCustPhoneField.getText();
        String errorMessage = Customer.isCustomerValid(customerName, address, city, country, postalCode, phone);
        if (errorMessage.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Customer Error");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return;
        }
        try {
            addNewCust(customerName, address, address2, city, country, postalCode, phone);
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
    private void addCustCancel(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Cancel");
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
        // BUTTON ACTION
        addCustSaveBtn.setOnAction(event -> {
            try {
                addCustSave(event);
            } catch (Exception ex) {
                Logger.getLogger(AddCustController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        addCustCancelBtn.setOnAction(event -> addCustCancel(event));
    }
}
