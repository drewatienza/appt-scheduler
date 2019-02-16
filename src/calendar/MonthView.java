package calendar;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import model.Appointment;
import model.ApptList;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class MonthView {

    private Text monthTitle;
    private Text titleSpace;
    private Text titleSpace2;
    private YearMonth currentYearMonth;
    private ArrayList<APNode> calendarDayPanes = new ArrayList<>(35);
    private VBox monthView;

    public MonthView(YearMonth yearMonth) {
        currentYearMonth = yearMonth;

        // CREATES GRID FOR CALENDAR
        GridPane calendar = new GridPane();
        calendar.setPrefSize(600,400);
        calendar.setGridLinesVisible(true);

        // CREATES INDIVIDUAL PANES
        for (int i=0; i<5; i++) {
            // Create 7 columns
            for (int j=0; j<7; j++) {
                APNode ap = new APNode();
                ap.setPrefSize(200,200);
                calendar.add(ap, j, i);
                calendarDayPanes.add(ap);
            }
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

        // MONTH TITLE AND ARROWS
        monthTitle = new Text();
        titleSpace = new Text("  ");
        titleSpace2 = new Text("  ");
        Button btnBack = new Button("<-");
        btnBack.setOnAction(event -> backOneMonth());
        Button btnForward = new Button("->");
        btnForward.setOnAction(event -> forwardOneMonth());
        // Create HBox to hold title and buttons
        HBox titleBar = new HBox(btnBack, titleSpace, monthTitle, titleSpace2, btnForward);
        titleBar.setAlignment(Pos.BASELINE_CENTER);

        populateCalendar(yearMonth);

        monthView = new VBox(titleBar, dayLabels, calendar);
    }

    // POPULATE CALENDAR
    public void populateCalendar(YearMonth yearMonth) {

        LocalDate calendarDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);

        while (!calendarDate.getDayOfWeek().toString().equals("MONDAY")) {
            calendarDate = calendarDate.minusDays(1);
        }

        String localizedMonth = new DateFormatSymbols().getMonths()[yearMonth.getMonthValue()-1];
        String properMonth = localizedMonth.substring(0,1).toUpperCase() + localizedMonth.substring(1);
        monthTitle.setText("  " + properMonth + " " + String.valueOf(yearMonth.getYear()) + "  ");

        // POPULATE NUMBER OF APPOINTMENTS
        fillCalendar(calendarDate, calendarDayPanes);
    }

    static void fillCalendar(LocalDate calendarDate, ArrayList<APNode> calendarDayPanes) {
        for (APNode ap : calendarDayPanes) {

            // REMOVES ANY EXISTING CHILDREN
            if (ap.getChildren().size() != 0) {
                ap.getChildren().remove(0,ap.getChildren().size());
            }

            Text date = new Text(String.valueOf(calendarDate.getDayOfMonth()));
            ap.setDate(calendarDate);
            ap.setTopAnchor(date, 5.0);
            ap.setLeftAnchor(date, 5.0);
            ap.getChildren().add(date);

            // CALCULATE NUMBER OF APPOINTMENTS
            ObservableList<Appointment> appointmentList = ApptList.getAppointmentList();
            int calendarDateYear = calendarDate.getYear();
            int calendarDateMonth = calendarDate.getMonthValue();
            int calendarDateDay = calendarDate.getDayOfMonth();
            int appointmentCount = 0;
            for (Appointment appointment : appointmentList) {
                Date appointmentDate = appointment.getStartDate();
                Calendar calendar  = Calendar.getInstance(TimeZone.getDefault());
                calendar.setTime(appointmentDate);
                int appointmentYear = calendar.get(Calendar.YEAR);
                int appointmentMonth = calendar.get(Calendar.MONTH) + 1;
                int appointmentDay = calendar.get(Calendar.DAY_OF_MONTH);
                if (calendarDateYear == appointmentYear && calendarDateMonth == appointmentMonth && calendarDateDay == appointmentDay) {
                    appointmentCount++;
                }
            }
            if (appointmentCount != 0) {
                Text appointmentsForDay = new Text(String.valueOf(appointmentCount));
                appointmentsForDay.setFont(Font.font(25));
                appointmentsForDay.setFill(Color.RED);

                ap.getChildren().add(appointmentsForDay);
                ap.setTopAnchor(appointmentsForDay, 25.0);
                ap.setLeftAnchor(appointmentsForDay, 43.0);
            }
            calendarDate = calendarDate.plusDays(1);
        }
    }

    // MOVE BACK ONE MONTH
    private void backOneMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        populateCalendar(currentYearMonth);
    }

    // MOVE UP ONE MONTH
    private void forwardOneMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        populateCalendar(currentYearMonth);
    }

    public VBox getView() {
        return monthView;
    }

    public YearMonth getCurrentYearMonth() {
        return currentYearMonth;
    }
}
