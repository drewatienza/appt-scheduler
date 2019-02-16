package calendar;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.*;

public class WeekView {

    private Text weekTitle;
    private LocalDate currentLocalDate;
    private ArrayList<APNode> calendarDayPanes = new ArrayList<>(7);
    private VBox weekView;

    public WeekView(LocalDate localDate) {
        currentLocalDate = localDate;

        // CREATES GRID FOR CALENDAR
        GridPane calendar = new GridPane();
        calendar.setPrefSize(600,85);
        calendar.setGridLinesVisible(true);

        // CREATES INDIVIDUAL PANES
        for (int i=0; i<7; i++) {
            APNode ap = new APNode();
            ap.setPrefSize(200,85);
            calendar.add(ap, i, 0);
            calendarDayPanes.add(ap);
        }

        Text[] daysOfWeek;
        daysOfWeek = new Text[]{
                new Text("Monday"), new Text("Tuesday"), new Text("Wednesday"),
                new Text("Thursday"), new Text("Friday"), new Text("Saturday"),
                new Text("Sunday")};
        GridPane dayLabels = new GridPane();
        dayLabels.setPrefWidth(600);
        int col = 0;
        for (Text day : daysOfWeek) {
            AnchorPane ap = new AnchorPane();
            ap.setPrefSize(200,10);
            ap.setBottomAnchor(day, 5.0);
            day.setWrappingWidth(100);
            day.setTextAlignment(TextAlignment.CENTER);
            ap.getChildren().add(day);
            dayLabels.add(ap, col++, 0);
        }

        // WEEK TITLE AND ARROWS
        weekTitle = new Text();
        Button btnBack = new Button("<-");
        btnBack.setOnAction(event -> backOneWk());
        Button bthForward = new Button("->");
        bthForward.setOnAction(event -> upOneWk());

        HBox titleBar = new HBox(btnBack, weekTitle, bthForward);
        titleBar.setAlignment(Pos.BASELINE_CENTER);

        populateCalendar(localDate);

        weekView = new VBox(titleBar, dayLabels, calendar);
    }

    // POPULATE CALENDAR
    public void populateCalendar(LocalDate localDate) {

        LocalDate calendarDate = localDate;

        while (!calendarDate.getDayOfWeek().toString().equals("MONDAY")) {
            calendarDate = calendarDate.minusDays(1);
        }

        LocalDate startDate = calendarDate;
        LocalDate endDate = calendarDate.plusDays(6);
        String localizedStartDateMonth = new DateFormatSymbols().getMonths()[startDate.getMonthValue()-1];
        String startDateMonthProper = localizedStartDateMonth.substring(0,1).toUpperCase() + localizedStartDateMonth.substring(1);
        String startDateTitle = startDateMonthProper + " " + startDate.getDayOfMonth();
        String localizedEndDateMonth = new DateFormatSymbols().getMonths()[endDate.getMonthValue()-1];
        String endDateMonthProper = localizedEndDateMonth.substring(0,1).toUpperCase() + localizedEndDateMonth.substring(1);
        String endDateTitle = endDateMonthProper + " " + endDate.getDayOfMonth();
        weekTitle.setText("  " + startDateTitle + " - " + endDateTitle + ", " + endDate.getYear() + "  ");

        // POPULATE NUMBER OF APPOINTMENTS
        MonthView.fillCalendar(calendarDate, calendarDayPanes);
    }

    // MOVE BACK ONE WEEK
    private void backOneWk() {
        currentLocalDate = currentLocalDate.minusWeeks(1);
        populateCalendar(currentLocalDate);
    }

    // MOVE UP ONE WEEK
    private void upOneWk() {
        currentLocalDate = currentLocalDate.plusWeeks(1);
        populateCalendar(currentLocalDate);
    }

    public VBox getView() {
        return weekView;
    }

    public LocalDate getCurrentLocalDate() {
        return currentLocalDate;
    }
}