package view_controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Customer;

import java.io.IOException;

import static model.CustList.getCustomerList;
import static model.Database.setCustInactive;
import static model.Database.updateCustomerRoster;

public class CustomerController {

    @FXML
    public Button addCustBtn;
    @FXML
    public Button modCustBtn;
    @FXML
    public Button deleteCustBtn;
    @FXML
    public Button apptMenuBtn;
    @FXML
    public Button mainMenuBtn;
    @FXML
    public Button exitBtn;
    @FXML
    private TableView<Customer> tvCustomers;
    @FXML
    private TableColumn<Customer, String> custNameColTV;
    @FXML
    private TableColumn<Customer, String> custAddressColTV;
    @FXML
    private TableColumn<Customer, String> custAddress2ColTV;
    @FXML
    private TableColumn<Customer, String> custCityColTV;
    @FXML
    private TableColumn<Customer, String> custCountryColTV;
    @FXML
    private TableColumn<Customer, String> custPhoneColTV;

    private static int customerIndexToModify;

    // ADD CUSTOMER
    @FXML
    private void openAddCustomer(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("AddCust.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // MODIFY CUSTOMER
    @FXML
    private void openModifyCustomer(ActionEvent event) {
        Customer customerToModify = tvCustomers.getSelectionModel().getSelectedItem();
        if (customerToModify == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Modifying Customer Error");
            alert.setContentText("Please select a customer to modify");
            alert.showAndWait();
            return;
        }
        customerIndexToModify = getCustomerList().indexOf(customerToModify);
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("ModCust.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // DELETE CUSTOMER
    @FXML
    private void removeCustomer(ActionEvent event) {
        Customer customerToRemove = tvCustomers.getSelectionModel().getSelectedItem();
        if (customerToRemove == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Delete Customer Error");
            alert.setContentText("Please select a customer from the table to delete.");
            alert.showAndWait();
            return;
        }
        setCustInactive(customerToRemove);
    }

    // GO TO APPOINTMENT SCREEN
    @FXML
    private void goToApptMenu(ActionEvent event) {
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

    public static int getCustomerIndexToModify() {
        return customerIndexToModify;
    }

    public void updateCustomerTableView() {
        updateCustomerRoster();
        tvCustomers.setItems(getCustomerList());
    }

    @FXML
    public void initialize() {
        // BUTTON ACTION
        addCustBtn.setOnAction(event -> openAddCustomer(event));
        modCustBtn.setOnAction(event -> openModifyCustomer(event));
        deleteCustBtn.setOnAction(event -> removeCustomer(event));
        apptMenuBtn.setOnAction(event -> goToApptMenu(event));
        mainMenuBtn.setOnAction(event -> goToMainMenu(event));
        exitBtn.setOnAction(event -> exit(event));

        // POPULATE TABLE
        custNameColTV.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        custAddressColTV.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        custAddress2ColTV.setCellValueFactory(cellData -> cellData.getValue().address2Property());
        custCityColTV.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        custCountryColTV.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        custPhoneColTV.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());

        updateCustomerTableView();
    }
}
