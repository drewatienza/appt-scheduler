package calendar;

import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;

import java.time.LocalDate;

class APNode extends AnchorPane {

    // Date for individual calendar pane
    private LocalDate date;

    // Creates individual date panes for days in calendar
    public APNode (Node... children) {
        super(children);
    }

    // Setter for date
    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Getter for date
    public LocalDate getDate() {
        return date;
    }
}
