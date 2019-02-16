package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import view_controller.AppointmentController;

import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import utils.*;

public class Database {


    // UPDATE CUSTOMER LIST
    public static void updateCustomerRoster() {
        try {
            Statement statement = DBManager.getConnection().createStatement();
            ObservableList<Customer> custList = CustList.getCustomerList();
            custList.clear();
            ResultSet result = statement.executeQuery("SELECT customerId FROM customer WHERE active = 1");
            ArrayList<Integer> customerIdList = new ArrayList<>();
            while (result.next()) {
                customerIdList.add(result.getInt(1));
            }
            for (int customerId : customerIdList) {
                Customer customer = new Customer();
                ResultSet resultSet = statement.executeQuery("SELECT customerName, active, addressId FROM customer" +
                        " WHERE customerId = " + customerId);
                resultSet.next();
                String customerName = resultSet.getString(1);
                int active = resultSet.getInt(2);
                int addressId = resultSet.getInt(3);
                customer.setCustomerId(customerId);
                customer.setCustomerName(customerName);
                customer.setActive(active);
                customer.setAddressId(addressId);
                ResultSet addResultSet = statement.executeQuery("SELECT address, address2, postalCode, phone, cityId " +
                        "FROM address WHERE addressId = " + addressId);
                addResultSet.next();
                String address = addResultSet.getString(1);
                String address2 = addResultSet.getString(2);
                String postalCode = addResultSet.getString(3);
                String phone = addResultSet.getString(4);
                int cityId = addResultSet.getInt(5);
                customer.setAddress(address);
                customer.setAddress2(address2);
                customer.setPostalCode(postalCode);
                customer.setPhone(phone);
                customer.setCityId(cityId);
                ResultSet cityResultSet = statement.executeQuery(
                        "SELECT city, countryId " +
                        "FROM city " +
                        "WHERE cityId = " + cityId);
                cityResultSet.next();
                String city = cityResultSet.getString(1);
                int countryId = cityResultSet.getInt(2);
                customer.setCity(city);
                customer.setCountryId(countryId);
                ResultSet countryResultSet = statement.executeQuery(
                        "SELECT country " +
                        "FROM country " +
                        "WHERE countryId = " + countryId);
                countryResultSet.next();
                String country = countryResultSet.getString(1);
                customer.setCountry(country);
                custList.add(customer);
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Database Connection Error");
            alert.setContentText("There has been an error connecting to the database.");
            alert.show();
        }
    }


    // ADD CUSTOMER
    public static void addNewCust(String customerName,
                                  String address,
                                  String address2,
                                  String city,
                                  String country,
                                  String postalCode,
                                  String phone) throws Exception {
        int countryId = country(country);
        int cityId = city(city, countryId);
        int addressId = address(address, address2, postalCode, phone, cityId);
        if (isCustomer(customerName, addressId)) {
            Statement statement = DBManager.getConnection().createStatement();
            ResultSet result = statement.executeQuery("SELECT active FROM customer WHERE " +
                    "customerName = '" + customerName + "' AND addressId = " + addressId);
            result.next();
            int active = result.getInt(1);
            if (active == 1) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Error Adding Customer");
                alert.setContentText("This customer already exists in the database");
                alert.showAndWait();
            }
            else if (active == 0) {
                setCustomerToActive(customerName, addressId);
            }
        } else {
            addCustomer(customerName, addressId);
        }
    }

    // CREATES OR RETURNS COUNTRYID
    public static int country(String country) {
        try {
            Statement statement = DBManager.getConnection().createStatement();
            ResultSet result = statement.executeQuery("SELECT countryId FROM country WHERE country = '" + country + "'");
            if (result.next()) {
                int countryId = result.getInt(1);
                result.close();
                return countryId;
            } else {
                result.close();
                int countryId;
                ResultSet resultSet = statement.executeQuery("SELECT countryId FROM country ORDER BY countryId");
                if (resultSet.last()) {
                    countryId = resultSet.getInt(1) + 1;
                    resultSet.close();
                } else {
                    resultSet.close();
                    countryId = 1;
                }
                statement.executeUpdate("INSERT INTO country VALUES (" + countryId + ", '" + country + "', CURRENT_DATE, " +
                        "'" + Logger.currentUser + "', CURRENT_TIMESTAMP, '" + Logger.currentUser + "')");
                return countryId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // CREATES OR RETURNS CITYID
    public static int city(String city, int countryId) {
        try {
            Statement statement = DBManager.getConnection().createStatement();
            ResultSet result = statement.executeQuery("SELECT cityId FROM city WHERE city = '" + city + "' AND countryid = " + countryId);
            if (result.next()) {
                int cityId = result.getInt(1);
                result.close();
                return cityId;
            } else {
                result.close();
                int cityId;
                ResultSet resultSet = statement.executeQuery("SELECT cityId FROM city ORDER BY cityId");
                if (resultSet.last()) {
                    cityId = resultSet.getInt(1) + 1;
                    resultSet.close();
                } else {
                    resultSet.close();
                    cityId = 1;
                }
                statement.executeUpdate("INSERT INTO city VALUES (" + cityId + ", '" + city + "', " + countryId + ", CURRENT_DATE, " +
                        "'" + Logger.currentUser + "', CURRENT_TIMESTAMP, '" + Logger.currentUser + "')");
                return cityId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // CREATES OR RETURNS ADDRESSID
    public static int address(String address,
                              String address2,
                              String postalCode,
                              String phone,
                              int cityId) {
        try {
            Statement statement = DBManager.getConnection().createStatement();
            ResultSet result = statement.executeQuery("SELECT addressId FROM address WHERE address = '" + address + "' AND " +
                    "address2 = '" + address2 + "' AND postalCode = '" + postalCode + "' AND phone = '" + phone + "' AND cityId = " + cityId);
            if (result.next()) {
                int addressId = result.getInt(1);
                result.close();
                return addressId;
            } else {
                result.close();
                int addressId;
                ResultSet resultSet = statement.executeQuery("SELECT addressId FROM address ORDER BY addressId");
                if (resultSet.last()) {
                    addressId = resultSet.getInt(1) + 1;
                    resultSet.close();
                } else {
                    resultSet.close();
                    addressId = 1;
                }
                statement.executeUpdate("INSERT INTO address VALUES (" + addressId + ", '" + address + "', '" + address2 + "', " + cityId + ", " +
                        "'" + postalCode + "', '" + phone + "', CURRENT_DATE, '" + Logger.currentUser + "', " +
                        "CURRENT_TIMESTAMP, '" + Logger.currentUser + "')");
                return addressId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // CHECK IF CUSTOMER IS IN DATABASE
    private static boolean isCustomer(String customerName, int addressId) throws SQLException {
        Statement statement = DBManager.getConnection().createStatement();
        ResultSet result = statement.executeQuery("SELECT customerId FROM customer WHERE customerName = '" + customerName + "' " +
                "AND addressId = " + addressId);
        if (result.next()) {
            result.close();
            return true;
        } else {
            result.close();
            return false;
        }
    }

    // CHANGE ACTIVE STATUS
    public static void setCustomerToActive(String customerName, int addressId) {
        try {
            Statement statement = DBManager.getConnection().createStatement();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle("CONFIRMATION");
            alert.setHeaderText("Change Status Confirmation");
            alert.setContentText("Are you sure you want to change the active status of the customer?");
            Optional<ButtonType> result = alert.showAndWait();
            // If 'OK' is clicked, set customer to active
            if (result.get() == ButtonType.OK) {
                statement.executeUpdate("UPDATE customer SET active = 1, lastUpdate = CURRENT_TIMESTAMP, " +
                        "lastUpdateBy = '" + Logger.currentUser + "' WHERE customerName = '" + customerName + "' AND " +
                        "addressId = " + addressId);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // CREATE CUSTOMER
    private static void addCustomer(String customerName, int addressId) throws SQLException {
        Statement statement = DBManager.getConnection().createStatement();
        ResultSet result = statement.executeQuery("SELECT customerId FROM customer ORDER BY customerId");
        int customerId;
        if (result.last()) {
            customerId = result.getInt(1) + 1;
            result.close();
        } else {
            result.close();
            customerId = 1;
        }
        statement.executeUpdate("INSERT INTO customer VALUES (" + customerId + ", '" + customerName + "', " + addressId + ", 1, " +
                "CURRENT_DATE, '" + Logger.currentUser + "', CURRENT_TIMESTAMP, '" + Logger.currentUser + "')");
    }


    // MODIFY CUSTOMER
    public static int modifyCustomer(int customerId,
                                     String customerName,
                                     String address,
                                     String address2,
                                     String city,
                                     String country,
                                     String postalCode,
                                     String phone) throws Exception {
        int countryId = country(country);
        int cityId = city(city, countryId);
        int addressId = address(address, address2, postalCode, phone, cityId);
        if (isCustomer(customerName, addressId)) {
            int existingCustomerId = getCustomerId(customerName, addressId);
            int activeStatus = getActiveStatus(existingCustomerId);
            return activeStatus;
        } else {
            updateCustomer(customerId, customerName, addressId);
            cleanDatabase();
            return -1;
        }
    }

    // GET CUSTOMERID
    private static int getCustomerId(String customerName, int addressId) throws SQLException {
        Statement statement = DBManager.getConnection().createStatement();
        ResultSet customerIdResultSet = statement.executeQuery("SELECT customerId " +
                "FROM customer " +
                "WHERE customerName = '" + customerName + "' AND addressId = " + addressId);
        customerIdResultSet.next();
        int customerId = customerIdResultSet.getInt(1);
        return customerId;
    }

    // ACTIVE STATUS
    private static int getActiveStatus(int customerId) throws SQLException {
        Statement statement = DBManager.getConnection().createStatement();
        ResultSet result = statement.executeQuery("SELECT active FROM customer WHERE customerId = " + customerId);
        result.next();
        int active = result.getInt(1);
            return active;
    }

    // UPDATE CUSTOMER
    private static void updateCustomer(int customerId, String customerName, int addressId) throws SQLException {
        Statement statement = DBManager.getConnection().createStatement();
        statement.executeUpdate("UPDATE customer SET customerName = '" + customerName + "', addressId = " + addressId + ", " +
                "lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = '" + Logger.currentUser + "' WHERE customerId = " + customerId);
    }
    
    // DELETE CUSTOMER
    public static void setCustInactive(Customer customer) {
        int customerId = customer.getCustomerId();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("CONFIRM DELETE");
        alert.setHeaderText("Confirm Customer Delete");
        alert.setContentText("Are you sure you want to delete this customer from the customer list?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                Statement statement = DBManager.getConnection().createStatement();
                statement.executeUpdate("DELETE FROM customer WHERE customerId = " + customerId);
            } catch (SQLException e) {
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
                alert2.setTitle("ERROR");
                alert2.setHeaderText("Error Deleting Customer");
                alert2.setContentText("You are currently not connected to the database.");
                alert2.showAndWait();
            }
            updateCustomerRoster();
        }
    }

    // UPDATE APPOINTMENT LIST
    public static void updateApptList() {
        try {
            Statement statement = DBManager.getConnection().createStatement();
            ObservableList<Appointment> apptList = ApptList.getAppointmentList();
            apptList.clear();
            ResultSet result = statement.executeQuery("SELECT appointmentId FROM appointment WHERE start >= CURRENT_TIMESTAMP");
            ArrayList<Integer> apptIdList = new ArrayList<>();
            while(result.next()) {
                apptIdList.add(result.getInt(1));
            }
            for (int appointmentId : apptIdList) {
                result = statement.executeQuery(
                        "SELECT customerId, title, description, location, contact, url, start, end, createdBy " +
                        "FROM appointment " +
                        "WHERE appointmentId = " + appointmentId);
                result.next();
                int customerId = result.getInt(1);
                String title = result.getString(2);
                String description = result.getString(3);
                String location = result.getString(4);
                String contact = result.getString(5);
                String url = result.getString(6);
                Timestamp start = result.getTimestamp(7);
                Timestamp end = result.getTimestamp(8);
                String createdBy = result.getString(9);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                dateFormat.setTimeZone(TimeZone.getDefault());
                java.util.Date startDate = dateFormat.parse(start.toString());
                java.util.Date endDate = dateFormat.parse(end.toString());
                Appointment appointment = new Appointment(
                        appointmentId,
                        customerId,
                        title,
                        description,
                        location,
                        contact,
                        url,
                        start,
                        end,
                        startDate,
                        endDate,
                        createdBy);
                apptList.add(appointment);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ADD APPOINTMENT TO DATABASE
    public static boolean addNewAppt(Customer customer,
                                     String title,
                                     String description,
                                     String location,
                                     String contact,
                                     String url,
                                     ZonedDateTime startUTC,
                                     ZonedDateTime endUTC) {
        try {
            String uTCStart = startUTC.toString();
            uTCStart = uTCStart.substring(0,10) + " " + uTCStart.substring(11,16) + ":00";
            Timestamp start = Timestamp.valueOf(uTCStart);   
            String uTCEnd = endUTC.toString();
            uTCEnd = uTCEnd.substring(0,10) + " " + uTCEnd.substring(11,16) + ":00";
            Timestamp end = Timestamp.valueOf(uTCEnd);
            
            ZonedDateTime startUTC2 = ZonedDateTime.ofInstant(startUTC.toInstant(), ZoneId.systemDefault());
            ZonedDateTime endUTC2 = ZonedDateTime.ofInstant(endUTC.toInstant(), ZoneId.systemDefault());
            String localStart = startUTC2.toString();
            localStart = localStart.substring(0,10) + " " + localStart.substring(11,16) + ":00";
            Timestamp start2 = Timestamp.valueOf(localStart);
            String localEnd = endUTC2.toString();
            localEnd = localEnd.substring(0,10) + " " + localEnd.substring(11,16) + ":00";
            Timestamp end2 = Timestamp.valueOf(localEnd);
            if (apptOverlap(start2, end2)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Error Adding Appointment");
                alert.setContentText("The times for this appointment overlaps with an existing appointment.");
                alert.showAndWait();
                return false;                
            } else {
                int customerId = customer.getCustomerId();
                addAppointment(customerId, title, description, location, contact, url, start, end);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean apptOverlap(Timestamp start, Timestamp end) {
        updateApptList();
        ObservableList<Appointment> appointmentList = ApptList.getAppointmentList();
        for (Appointment appointment: appointmentList) {
            Timestamp startTS = appointment.getStart();
            Timestamp endTS = appointment.getEnd();
            if (start.after(startTS) && start.before(endTS)) {
                return true;
            }
            if (end.after(startTS) && end.before(endTS)) {
                return true;
            }
            if (start.after(startTS) && end.before(endTS)) {
                return true;
            }
            if (start.before(startTS) && end.after(endTS)) {
                return true;
            }
            if (start.equals(startTS)) {
                return true;
            }
            if (end.equals(endTS)) {
                return true;
            }
        }
        return false;
    }

    // CREATE APPOINTMENT ENTRY
    private static void addAppointment(int customerId,
                                       String title,
                                       String description,
                                       String location,
                                       String contact,
                                       String url,
                                       Timestamp start,
                                       Timestamp end) {
        try {
            Statement statement = DBManager.getConnection().createStatement();
            ResultSet result = statement.executeQuery("SELECT appointmentId FROM appointment ORDER BY appointmentId");
            int appointmentId;
            if (result.last()) {
                appointmentId = result.getInt(1) + 1;
                result.close();
            } else {
                result.close();
                appointmentId = 1;
            }
            statement.executeUpdate("INSERT INTO appointment " +
                    "VALUES (" + appointmentId +", " + customerId + ", '" + title + "', '" +
                    description + "', '" + location + "', '" + contact + "', '" + url +
                    "', '" + start + "', '" + end + "', " +
                    "CURRENT_DATE, '" + Logger.currentUser + "', CURRENT_TIMESTAMP, '" + Logger.currentUser + "')");
        }
        catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Error Adding Appointment");
            alert.setContentText("You are currently not connected to the database.");
            alert.showAndWait();
        }
    }


    // MODIFY EXISTING APPOINTMENT ENTRY
    public static boolean modifyAppt(int appointmentId,
                                     Customer customer,
                                     String title,
                                     String description,
                                     String location,
                                     String contact,
                                     String url,
                                     ZonedDateTime startUTC,
                                     ZonedDateTime endUTC) {
        try {
            String uTCStart = startUTC.toString();
            uTCStart = uTCStart.substring(0,10) + " " + uTCStart.substring(11,16) + ":00";
            Timestamp start = Timestamp.valueOf(uTCStart);   
            String uTCEnd = endUTC.toString();
            uTCEnd = uTCEnd.substring(0,10) + " " + uTCEnd.substring(11,16) + ":00";
            Timestamp end = Timestamp.valueOf(uTCEnd);
            
            ZonedDateTime startUTC2 = ZonedDateTime.ofInstant(startUTC.toInstant(), ZoneId.systemDefault());
            ZonedDateTime endUTC2 = ZonedDateTime.ofInstant(endUTC.toInstant(), ZoneId.systemDefault());
            String localStart = startUTC2.toString();
            localStart = localStart.substring(0,10) + " " + localStart.substring(11,16) + ":00";
            Timestamp start2 = Timestamp.valueOf(localStart);
            String localEnd = endUTC2.toString();
            localEnd = localEnd.substring(0,10) + " " + localEnd.substring(11,16) + ":00";
            Timestamp end2 = Timestamp.valueOf(localEnd);
            if (apptOverlap(start2, end2)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Error Modifying Appointment");
                alert.setContentText("The times for this appointment overlaps with an existing appointment");
                alert.showAndWait();
                return false;               
            } else {
                int customerId = customer.getCustomerId();
                updateAppointment(appointmentId, customerId, title, description, location, contact, url, start, end);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // UPDATE APPOINTMENT ENTRY
    private static void updateAppointment(int appointmentId,
                                          int customerId,
                                          String title,
                                          String description,
                                          String location,
                                          String contact,
                                          String url,
                                          Timestamp start,
                                          Timestamp end) throws SQLException {
        Statement statement = DBManager.getConnection().createStatement();
        statement.executeUpdate("UPDATE appointment " +
                "SET customerId = " + customerId + ", title = '" + title + "', description = '" + description +
                "', location = '" + location + "', contact = '" + contact + "', url = '" + url + "', start = '" +
                start + "', end = '" + end + "', " +
                "lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = '" + Logger.currentUser +
                "' WHERE appointmentId = " + appointmentId);
    }


    // DELETE APPOINTMENT
    public static void deleteAppt(Appointment appointmentToDelete) {
        int appointmentId = appointmentToDelete.getAppointmentId();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("DELETE");
        alert.setHeaderText("Confirm Appointment Deletion");
        alert.setContentText("Are you sure you want to delete this appointment?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                Statement statement = DBManager.getConnection().createStatement();
                statement.executeUpdate("DELETE FROM appointment WHERE appointmentId =" + appointmentId);
            } catch (Exception e) {
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
                alert2.setTitle("Error");
                alert2.setHeaderText("Error Modifying Appointment");
                alert2.setContentText("You are currently not connected to the database.");
                alert2.showAndWait();
            }
            updateApptList();
        }
    }

    // DATABASE CLEANUP
    private static void cleanDatabase() {
        try {
            Statement statement = DBManager.getConnection().createStatement();
            ResultSet result = statement.executeQuery("SELECT DISTINCT addressId FROM customer ORDER BY addressId");
            ArrayList<Integer> addressIdListFromCustomer = new ArrayList<>();
            while (result.next()) {
                addressIdListFromCustomer.add(result.getInt(1));
            }
            result = statement.executeQuery("SELECT DISTINCT addressId FROM address ORDER BY addressId");
            ArrayList<Integer> addressIdListFromAddress = new ArrayList<>();
            while (result.next()) {
                addressIdListFromAddress.add(result.getInt(1));
            }
            for (int i = 0; i < addressIdListFromCustomer.size(); i++) {
                for (int j = 0; j < addressIdListFromAddress.size(); j++) {
                    if (addressIdListFromCustomer.get(i) == addressIdListFromAddress.get(j)) {
                        addressIdListFromAddress.remove(j);
                        j--;
                    }
                }
            }
            if (addressIdListFromAddress.isEmpty()) {}
            else {
                for (int addressId : addressIdListFromAddress) {
                    statement.executeUpdate("DELETE FROM address WHERE addressId = " + addressId);
                }
            }

            // CITYID LIST
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT cityId FROM address ORDER BY cityId");
            ArrayList<Integer> cityIdListFromAddress = new ArrayList<>();
            while (resultSet.next()) {
                cityIdListFromAddress.add(resultSet.getInt(1));
            }
            resultSet = statement.executeQuery("SELECT DISTINCT cityId FROM city ORDER BY cityId");
            ArrayList<Integer> cityIdListFromCity = new ArrayList<>();
            while (resultSet.next()) {
                cityIdListFromCity.add(resultSet.getInt(1));
            }
            for (int i = 0; i < cityIdListFromAddress.size(); i++) {
                for (int j = 0; j < cityIdListFromCity.size(); j++) {
                    if (cityIdListFromAddress.get(i) == cityIdListFromCity.get(j)) {
                        cityIdListFromCity.remove(j);
                        j--;
                    }
                }
            }
            if (cityIdListFromCity.isEmpty()) {}
            else {
                for (int cityId : cityIdListFromCity) {
                    statement.executeUpdate("DELETE FROM city WHERE cityId = " + cityId);
                }
            }

            // COUNTRYID LIST
            ResultSet resultSet2 = statement.executeQuery("SELECT DISTINCT countryId FROM city ORDER BY countryId");
            ArrayList<Integer> countryIdListFromCity = new ArrayList<>();
            while (resultSet2.next()) {
                countryIdListFromCity.add(resultSet2.getInt(1));
            }
            resultSet2 = statement.executeQuery("SELECT DISTINCT countryId FROM country ORDER BY countryId");
            ArrayList<Integer> countryIdListFromCountry = new ArrayList<>();
            while (resultSet2.next()) {
                countryIdListFromCountry.add(resultSet2.getInt(1));
            }
            for (int i = 0; i < countryIdListFromCity.size(); i++) {
                for (int j = 0; j < countryIdListFromCountry.size(); j++) {
                    if (countryIdListFromCity.get(i) == countryIdListFromCountry.get(j)) {
                        countryIdListFromCountry.remove(j);
                        j--;
                    }
                }
            }
            if (countryIdListFromCountry.isEmpty()) {}
            else {
                for (int countryId : countryIdListFromCountry) {
                    statement.executeUpdate("DELETE FROM country WHERE countryId = " + countryId);
                }
            }
        }
        catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Adding Appointment Error");
            alert.setContentText("You are currently not connected to the database.");
            alert.showAndWait();
        }
    }
}
