package edu.augustana.csc305.project;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.format.DateTimeFormatter;

public class AssignCourtsView extends VBox {
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AssignCourtsView(Tournament t) {
        setSpacing(8);
        setPadding(new Insets(10));

        Label title = new Label("Assigned Matches");

        //Set up tableview, if no matches - display "no matches"
        TableView<Match> table = new TableView<>();
        table.setPlaceholder(new Label("No matches"));


        TableColumn<Match, String> colHome = new TableColumn<>("Home"); //creating column "home" (team) for table
        colHome.setCellValueFactory(c -> Bindings.createStringBinding( //lambda returns StringBinding that holds the text for the cell (c)
                () -> c.getValue().getHome()==null ? "-" : c.getValue().getHome().getName())); //returns current match, home team, if null - displays "-"
        colHome.setPrefWidth(160);

        //Away, Court, Start, follow the same logic as Home
        TableColumn<Match, String> colAway = new TableColumn<>("Away");
        colAway.setCellValueFactory(c -> Bindings.createStringBinding(
                () -> c.getValue().getAway()==null ? "-" : c.getValue().getAway().getName()));
        colAway.setPrefWidth(160);

        TableColumn<Match, String> colCourt = new TableColumn<>("Court");
        colCourt.setCellValueFactory(c -> Bindings.createStringBinding(
                () -> c.getValue().getAssignedCourtNumber()==null ? "-" :
                        String.valueOf(c.getValue().getAssignedCourtNumber())));
        colCourt.setPrefWidth(80);

        TableColumn<Match, String> colStart = new TableColumn<>("Start");
        colStart.setCellValueFactory(c -> Bindings.createStringBinding(
                () -> c.getValue().getScheduledStart()==null ? "-" :
                        DT_FMT.format(c.getValue().getScheduledStart())));
        colStart.setPrefWidth(160);

        //add columns to the table and fills the table with matches
        table.getColumns().setAll(colHome, colAway, colCourt, colStart);
        table.getItems().setAll(t.getMatches());

        getChildren().addAll(title, table);
    }

    //Creates an AssignCourtsView and puts it into an alert
    public static void show(Tournament t) {
        AssignCourtsView view = new AssignCourtsView(t);
        Alert dlg = new Alert(Alert.AlertType.NONE);
        dlg.setTitle("Court Assignment");
        dlg.getDialogPane().setContent(view);
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dlg.showAndWait();
    }
}
