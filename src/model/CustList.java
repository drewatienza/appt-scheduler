package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CustList {
    private static ObservableList<Customer> customerList = FXCollections.observableArrayList();

    // Getter for customerRoster
    public static ObservableList<Customer> getCustomerList() {
        return customerList;
    }
}
