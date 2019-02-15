package view_controller;

import calendar.MonthView;
import calendar.WeekView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;

import static model.Database.updateApptList;

public class CalendarViewController {

    @FXML
    public GridPane apptScreenGrid;
    @FXML
    public Button apptMenuBtnCV;
    @FXML
    public Button reportsBtnCV;
    @FXML
    public Button mainScrCurrentDateBtn;
    @FXML
    public Button mainScrToggleViewBtn;
    @FXML
    public Button custMenuBtnCV;
    @FXML
    public Button apptExitBtnCV;

    private boolean monthlyView = true;
    private MonthView monthlyCalendar;
    private WeekView weeklyCalendar;
    private VBox monthView;
    private VBox weekView;

    @FXML
    private void openAppointmentSummary(ActionEvent event) {
        try {
            Parent apptSum = FXMLLoader.load(getClass().getResource("Appointment.fxml"));
            Scene appointmentSummaryScene = new Scene(apptSum);
            Stage appointmentSummaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appointmentSummaryStage.setScene(appointmentSummaryScene);
            appointmentSummaryStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GO TO CUSTOMER SCREEN
    @FXML
    private void goToCustScreen(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("Customer.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GO TO REPORTS SCREEN
    @FXML
    private void goToReportsScreen(ActionEvent event) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("Reports.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToCurrentDate(ActionEvent event) {
        if (monthlyView) {
            apptScreenGrid.getChildren().remove(monthView);
            YearMonth currentYearMonth = YearMonth.now();
            monthlyCalendar = new MonthView(currentYearMonth);
            monthView = monthlyCalendar.getView();
            apptScreenGrid.add(monthView, 0, 0);
        } else {
            apptScreenGrid.getChildren().remove(weekView);
            LocalDate currentLocalDate = LocalDate.now();
            weeklyCalendar = new WeekView(currentLocalDate);
            weekView = weeklyCalendar.getView();
            apptScreenGrid.add(weekView, 0, 0);
        }
    }

    @FXML
    private void toggleCalendarView(ActionEvent event) {
        if (monthlyView) {
            apptScreenGrid.getChildren().remove(monthView);
            YearMonth currentYearMonth = monthlyCalendar.getCurrentYearMonth();
            LocalDate currentLocalDate = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonth(), 1);
            weeklyCalendar = new WeekView(currentLocalDate);
            weekView = weeklyCalendar.getView();
            apptScreenGrid.add(weekView, 0, 0);
            mainScrToggleViewBtn.setText("Switch to Monthly View");
            monthlyView = false;
        } else {
            apptScreenGrid.getChildren().remove(weekView);
            LocalDate currentLocalDate = weeklyCalendar.getCurrentLocalDate();
            YearMonth currentYearMonth = YearMonth.from(currentLocalDate);
            monthlyCalendar = new MonthView(currentYearMonth);
            monthView = monthlyCalendar.getView();
            apptScreenGrid.add(monthView, 0, 0);
            mainScrToggleViewBtn.setText("Switch to Weekly View");
            monthlyView = true;
        }
    }

    @FXML
    private void exit(ActionEvent event) {
        MainMenuController.exitApp();
    }

    @FXML
    public void initialize() {
        // BUTTON ACTION
        apptMenuBtnCV.setOnAction(event -> openAppointmentSummary(event));
        custMenuBtnCV.setOnAction(event -> goToCustScreen(event));
        reportsBtnCV.setOnAction(event -> goToReportsScreen(event));
        mainScrCurrentDateBtn.setOnAction(event -> goToCurrentDate(event));
        mainScrToggleViewBtn.setOnAction(event -> toggleCalendarView(event));
        apptExitBtnCV.setOnAction(event -> exit(event));
        
        updateApptList();
        monthlyCalendar = new MonthView(YearMonth.now());
        monthView = monthlyCalendar.getView();
        apptScreenGrid.add(monthView, 0, 0);
    }
}
