package view_controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ReportsController {

    @FXML
    private TextArea apptByMonthReport;
    @FXML
    private TextArea consultantReport;
    @FXML
    private TextArea customerReport;
    @FXML
    public Button reportsMainMenuBtn;
    @FXML
    private Button reportsExitBtn;

    // NUMBER OF APPOINTMENT TYPES BY MONTH
    public void apptTypesByMonth() {
        Database.updateApptList();
        String report = "NUMBER OF APPOINTMENT TYPES BY MONTH:" + "\n";
        ArrayList<String> monthsWithAppts = new ArrayList<>();
        for (Appointment appointment : ApptList.getAppointmentList()) {
            java.util.Date startDate = appointment.getStartDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            String yearMonth = year + "-" + month;
            if (month < 10) {
                yearMonth = year + "-0" + month;
            }
            if (!monthsWithAppts.contains(yearMonth)) {
                monthsWithAppts.add(yearMonth);
            }
        }
        Collections.sort(monthsWithAppts);
        for (String yearMonth : monthsWithAppts) {
            int year = Integer.parseInt(yearMonth.substring(0,4));
            int month = Integer.parseInt(yearMonth.substring(5,7));
            int typeCount = 0;
            ArrayList<String> descriptions = new ArrayList<>();
            for (Appointment appointment : ApptList.getAppointmentList()) {
                java.util.Date startDate = appointment.getStartDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                int appointmentYear = calendar.get(Calendar.YEAR);
                int appointmentMonth = calendar.get(Calendar.MONTH) + 1;
                if (year == appointmentYear && month == appointmentMonth) {
                    String description = appointment.getDescription();
                    if (!descriptions.contains(description)) {
                        descriptions.add(description);
                        typeCount++;
                    }
                }
            }
            report = report + yearMonth + ": " + typeCount + "\n";
            report = report + "Appointment Types: ";
            for (String description : descriptions) {
                report = report + " " + description + ",";
            }
            report = report.substring(0, report.length()-1);
            report = report + "\n \n";
            apptByMonthReport.setText(report);
        }
    }


    // SCHEDULE FOR EACH CONSULTANT
    public void schedForConsults() {
        Database.updateApptList();
        String report = "THE SCHEDULE FOR EACH CONSULTANT:" + "\n";
        ArrayList<String> consultsWithAppts = new ArrayList<>();
        for (Appointment appointment : ApptList.getAppointmentList()) {
            String consultant = appointment.getCreatedBy();
            if (!consultsWithAppts.contains(consultant)) {
                consultsWithAppts.add(consultant);
            }
        }
        Collections.sort(consultsWithAppts);
        for (String consultant : consultsWithAppts) {
            report = report + consultant + ": \n";
            for (Appointment appointment : ApptList.getAppointmentList()) {
                String appointmentConsultant = appointment.getCreatedBy();
                if (consultant.equals(appointmentConsultant)) {
                    String date = appointment.getDateString();
                    String title = appointment.getTitle();
                    java.util.Date startDate = appointment.getStartDate();
                    String startTime = startDate.toString().substring(11,16);
                    if (Integer.parseInt(startTime.substring(0,2)) > 12) {
                        startTime = Integer.parseInt(startTime.substring(0,2)) - 12 + startTime.substring(2,5) + "PM";
                    }
                    else if (Integer.parseInt(startTime.substring(0,2)) == 12) {
                        startTime = startTime + "PM";
                    }
                    else {
                        startTime = startTime + "AM";
                    }
                    java.util.Date endDate = appointment.getEndDate();
                    String endTime = endDate.toString().substring(11,16);
                    if (Integer.parseInt(endTime.substring(0,2)) > 12) {
                        endTime = Integer.parseInt(endTime.substring(0,2)) - 12 + endTime.substring(2,5) + "PM";
                    }
                    else if (Integer.parseInt(endTime.substring(0,2)) == 12) {
                        endTime = endTime + "PM";
                    }
                    else {
                        endTime = endTime + "AM";
                    }
                    String timeZone = startDate.toString().substring(20,23);
                    report = report + date + ": " + title + " from " + startTime + " to " +
                            endTime + " " + timeZone + ". \n";
                }
            }
            report = report + "\n \n";
            consultantReport.setText(report);
        }
    }


    // CUSTOMER SCHEDULE
    public void schedForCustomers() {
        Database.updateApptList();
        String report = "THE SCHEDULE FOR EACH CUSTOMER:" + "\n";
        ArrayList<Integer> custWithAppt = new ArrayList<>();
        for (Appointment appointment : ApptList.getAppointmentList()) {
            int customerId = appointment.getCustomerId();
            if (!custWithAppt.contains(customerId)) {
                custWithAppt.add(customerId);
            }
        }
        Collections.sort(custWithAppt);
        Database.updateCustomerRoster();
        for (int customerId : custWithAppt) {
            for (Customer customer : CustList.getCustomerList()) {
                int customerIdToCheck = customer.getCustomerId();
                if (customerId == customerIdToCheck) {
                    report = report + customer.getCustomerName() + ": \n";
                }
            }
            for (Appointment appointment : ApptList.getAppointmentList()) {
                int appointmentCustomerId = appointment.getCustomerId();
                if (customerId == appointmentCustomerId) {
                    String date = appointment.getDateString();
                    String description = appointment.getDescription();
                    java.util.Date startDate = appointment.getStartDate();
                    String startTime = startDate.toString().substring(11,16);
                    if (Integer.parseInt(startTime.substring(0,2)) > 12) {
                        startTime = Integer.parseInt(startTime.substring(0,2)) - 12 + startTime.substring(2,5) + "PM";
                    }
                    else if (Integer.parseInt(startTime.substring(0,2)) == 12) {
                        startTime = startTime + "PM";
                    }
                    else {
                        startTime = startTime + "AM";
                    }
                    java.util.Date endDate = appointment.getEndDate();
                    String endTime = endDate.toString().substring(11,16);
                    if (Integer.parseInt(endTime.substring(0,2)) > 12) {
                        endTime = Integer.parseInt(endTime.substring(0,2)) - 12 + endTime.substring(2,5) + "PM";
                    }
                    else if (Integer.parseInt(endTime.substring(0,2)) == 12) {
                        endTime = endTime + "PM";
                    }
                    else {
                        endTime = endTime + "AM";
                    }
                    String timeZone = startDate.toString().substring(20,23);
                    report = report + date + ": " + description + " from " + startTime + " to " +
                            endTime + " " + timeZone + ". \n";
                }
            }
            report = report + "\n \n";
            customerReport.setText(report);
        }
    }

    // GO TO MAIN MENU
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
    private void exit(ActionEvent event) { MainMenuController.exitApp(); }

    public void initialize() {
        // BUTTON ACTION
        reportsMainMenuBtn.setOnAction(event -> goToMainMenu(event));
        reportsExitBtn.setOnAction(event -> exit(event));
        
        apptTypesByMonth();
        schedForConsults();
        schedForCustomers();
    }
}
