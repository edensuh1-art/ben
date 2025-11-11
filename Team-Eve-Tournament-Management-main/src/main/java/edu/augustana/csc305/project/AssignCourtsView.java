package edu.augustana.csc305.project;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class AssignCourtsView extends VBox {
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a");

    public AssignCourtsView(Tournament tournament) {
        setSpacing(8);
        setPadding(new Insets(10));

        Label title = new Label("Assigned Matches");

        // Set up tableview; if no matches, display "No matches"
        TableView<Match> table = new TableView<>();
        table.setPlaceholder(new Label("No matches"));


        TableColumn<Match, String> colHome = new TableColumn<>("Home"); //creating column "home" (team) for table
        colHome.setCellValueFactory(homeCell -> Bindings.createStringBinding( //lambda returns StringBinding that holds the text for the cell
                () -> {
                        Match match = homeCell.getValue();
                        return match.getHome() != null ? match.getHome().getName() : (match.getHomeID() != null ? match.getHomeID() : "-");
                })); //returns current match, home team, if null - displays "-"
                
        colHome.setPrefWidth(160);

        // Away team column
        TableColumn<Match, String> colAway = new TableColumn<>("Away");
        colAway.setCellValueFactory(awayCell -> Bindings.createStringBinding(
                () -> {
                    Match m = awayCell.getValue();
                    return m.getAway() != null ? m.getAway().getName() : (m.getAwayID() != null ? m.getAwayID() : "-");
                }));
        colAway.setPrefWidth(160);

        // Court column
        TableColumn<Match, String> colCourt = new TableColumn<>("Court");
        colCourt.setCellValueFactory(courtCell -> Bindings.createStringBinding(
                () -> {
                    int n = courtCell.getValue().getAssignedCourt();
                    return n > 0 ? String.valueOf(n) : "-";
                }));
        colCourt.setPrefWidth(80);
        colCourt.setEditable(true);

        // Start time column
        TableColumn<Match, String> colStart = new TableColumn<>("Start");
        colStart.setCellValueFactory(startCell -> Bindings.createStringBinding(
                () -> {
                    if (startCell.getValue().getScheduledStart() == null) return "-";
                    return DT_FMT.format(startCell.getValue().getScheduledStart());
                }));
        colStart.setPrefWidth(160);

        TableColumn<Match, String> colRunning = new TableColumn<>("In Progress");
        colRunning.setCellValueFactory(runningCell -> Bindings.createStringBinding(
                () -> {
                        if (runningCell.getValue().isInProgress() == true) return "Yes";
                        return "No";
                }));
        colRunning.setPrefWidth(80);

        //add columns to the table and fills the table with matches
        table.getColumns().setAll(colHome, colAway, colCourt, colStart, colRunning);
        if (tournament.getMatches() != null) table.getItems().setAll(tournament.getMatches());
        getChildren().addAll(title, table);
    }

    //Creates an AssignCourtsView and puts it into an alert
    public static void show(Tournament tournament) {
        AssignCourtsView view = new AssignCourtsView(tournament);
        Alert dlg = new Alert(Alert.AlertType.NONE);
        dlg.setTitle("Court Assignment");
        dlg.getDialogPane().setContent(view);
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dlg.showAndWait();
    }
}
