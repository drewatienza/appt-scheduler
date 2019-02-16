package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import utils.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import static model.Database.updateCustomerRoster;
import static model.Database.updateApptList;

public class Appointment {

    private IntegerProperty appointmentId;
    private IntegerProperty customerId;
    private StringProperty title;
    private StringProperty description;
    private StringProperty location;
    private StringProperty contact;
    private StringProperty url;
    private Timestamp start;
    private Timestamp end;
    private Date startDate;
    private Date endDate;
    private StringProperty dateString;
    private StringProperty startString;
    private StringProperty endString;
    private StringProperty createdBy;
    
    private static int openCount = 0;

    // CONSTRUCTORS
    public Appointment(int appointmentId, int customerId, String title, String description, String location, String contact,
                       String url, Timestamp start, Timestamp end, Date startDate, Date endDate, String createdBy) {
        this.appointmentId = new SimpleIntegerProperty(appointmentId);
        this.customerId = new SimpleIntegerProperty(customerId);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.location = new SimpleStringProperty(location);
        this.contact = new SimpleStringProperty(contact);
        this.url = new SimpleStringProperty(url);
        this.start = start;
        this.end = end;
        this.startDate = startDate;
        this.endDate = endDate;
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        this.dateString = new SimpleStringProperty(format.format(startDate));
        SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm a z");
        this.startString = new SimpleStringProperty(formatTime.format(startDate));
        this.endString = new SimpleStringProperty(formatTime.format(endDate));
        this.createdBy = new SimpleStringProperty(createdBy);
    }

    // GETTERS
    public int getAppointmentId() {
        return this.appointmentId.get();
    }

    public IntegerProperty appointmentIdProperty() {
        return this.appointmentId;
    }

    public int getCustomerId() {
        return this.customerId.get();
    }

    public IntegerProperty customerIdProperty() {
        return this.customerId;
    }

    public String getTitle() {
        return this.title.get();
    }

    public StringProperty titleProperty() {
        return this.title;
    }

    public String getDescription() {
        return this.description.get();
    }

    public StringProperty descriptionProperty() {
        return this.description;
    }

    public String getLocation() {
        return this.location.get();
    }

    public StringProperty locationProperty() {
        return this.location;
    }

    public String getContact() {
        return this.contact.get();
    }

    public StringProperty contactProperty() {
        return this.contact;
    }

    public String getUrl() {
        return this.url.get();
    }

    public StringProperty urlProperty() {
        return this.url;
    }

    public Timestamp getStart() {
        return this.start;
    }

    public Timestamp getEnd() {
        return this.end;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public String getDateString() {
        return this.dateString.get();
    }

    public StringProperty dateStringProperty() {
        return this.dateString;
    }

    public String getStartString() {
        return this.startString.get();
    }

    public StringProperty startStringProperty() {
        return this.startString;
    }

    public String getEndString() {
        return this.endString.get();
    }

    public StringProperty endStringProperty() {
        return this.endString;
    }

    public String getCreatedBy() {
        return this.createdBy.get();
    }

    public StringProperty createdByProperty() {
        return this.createdBy;
    }

    // SETTERS
    public void setAppointmentId(int appointmentId) {
        this.appointmentId.set(appointmentId);
    }

    public void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public void setContact(String contact) {
        this.contact.set(contact);
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setCreatedBy (String createdBy) {
        this.createdBy.set(createdBy);
    }

    // VALIDATION
    public static String isApptValid(Customer customer,
                                     String title,
                                     String description,
                                     String location,
                                     LocalDate appointmentDate,
                                     String startHour,
                                     String startMinute,
                                     String startAmPm,
                                     String endHour,
                                     String endMinute,
                                     String endAmPm)
            throws NumberFormatException {
        String error = "";
        try {
            if (customer == null) {
                error = error + "You must add a customer to the appointment.";
            }
            if (title.length() == 0) {
                error = error + "The title field needs to be filled out.";
            }
            if (description.length() == 0) {
                error = error + "Please write a description in the description field provided.";
            }
            if (location.length() == 0) {
                error = error + "The location field cannot be blank.  Please fill in the loction field.";
            }
            if (appointmentDate == null ||
                    startHour.equals("") ||
                    startMinute.equals("") ||
                    startAmPm.equals("") ||
                    endHour.equals("") ||
                    endMinute.equals("") ||
                    endAmPm.equals("")) {
                error = error + "A start and end time is needed.";
            }
            if (Integer.parseInt(startHour) < 1 ||
                    Integer.parseInt(startHour) > 12 ||
                    Integer.parseInt(endHour) < 1 ||
                    Integer.parseInt(endHour) > 12 ||
                    Integer.parseInt(startMinute) < 0 ||
                    Integer.parseInt(startMinute) > 59 ||
                    Integer.parseInt(endMinute) < 0 ||
                    Integer.parseInt(endMinute) > 59) {
                error = error + "The hours value entered must be between 1 and 12 and the minutes value must be between 0 and 59.";
            }
            if ((startAmPm.equals("PM") && endAmPm.equals("AM")) ||
                    (startAmPm.equals(endAmPm) && Integer.parseInt(startHour) != 12 && Integer.parseInt(startHour) > Integer.parseInt(endHour)) ||
                    (startAmPm.equals(endAmPm) && startHour.equals(endHour) && Integer.parseInt(startMinute) > Integer.parseInt(endMinute))) {
                error = error + "The start time cannot come after the end time.";
            }
//            if ((Integer.parseInt(startHour) < 9 && startAmPm.equals("AM")) ||
//                    (Integer.parseInt(endHour) < 9 && endAmPm.equals("AM")) ||
//                    (Integer.parseInt(startHour) >= 5 && Integer.parseInt(startHour) < 12 && startAmPm.equals("PM")) ||
//                    (Integer.parseInt(endHour) >= 5 && Integer.parseInt(endHour) < 12 && endAmPm.equals("PM")) ||
//                    (Integer.parseInt(startHour) == 12 && startAmPm.equals("AM")) ||
//                    (Integer.parseInt(endHour)) == 12 && endAmPm.equals("AM")) {
//                error = error + "The appointment start and end time must be between the normal business hours of 9 AM to 5 PM.";

            // THIS IS NEW
            if ((Integer.parseInt(startHour) < 9 && startAmPm.equals("AM")) ||
                    (Integer.parseInt(startHour) >= 5 && startAmPm.equals("PM")) ||
                    (Integer.parseInt(endHour) < 9 && startAmPm.equals("AM")) ||
                    (Integer.parseInt(endHour) >= 5 && startAmPm.equals("PM"))) {
                error = error + "The appointment start and end time must be between the normal business hours of 9 AM and 5 PM.";
            }
            if (appointmentDate.getDayOfWeek().toString().toUpperCase().equals("SATURDAY") ||
                    appointmentDate.getDayOfWeek().toString().toUpperCase().equals("SUNDAY")) {
                error = error + "The appointment cannot start or end during the weekend.";
            }
        } catch (NumberFormatException e) {
            error = error + "The start and end time values must be integers.";
        } finally {
            return error;
        }
    }

    // ALERT FOR APPOINTMENTS WITHIN 15 MINUTES OF USER'S LOGIN
    public static void apptNotify() {
        if (openCount == 0) {
            updateCustomerRoster();
            updateApptList();
            ObservableList<Appointment> userAppointments = FXCollections.observableArrayList();
            for (Appointment appointment : ApptList.getAppointmentList()) {
                if (appointment.getCreatedBy().equals(Logger.currentUser)) {
                    userAppointments.add(appointment);
                }
            }
            for (Appointment appointment : userAppointments) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(java.sql.Date.from(Instant.now()));
                calendar.add(Calendar.MINUTE, 15);
                java.util.Date currentTime = calendar.getTime();
                if (appointment.getStartDate().before(currentTime)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("APPOINTMENT NOTIFICATION");
                    alert.setHeaderText("Appointment Notification");
                    alert.setContentText("UPCOMING APPOINTMENT INFORMATION:" + "\n" +
                            "Title:" + appointment.getTitle() + "\n" +
                            "Description: " + appointment.getDescription() + "\n" +
                            "Location: " + appointment.getLocation() + "\n" +
                            "Contact: " + appointment.getContact() + "\n" +
                            "URL: " + appointment.getUrl() + "\n" +
                            "Date: " + appointment.getDateString() + "\n" +
                            "Start Time: " + appointment.getStartString() + "\n" +
                            "End Time: " + appointment.getEndString());
                    alert.showAndWait();
                }
            }
            openCount++;
        }
    }
}
