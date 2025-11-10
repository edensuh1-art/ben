package edu.augustana.csc305.project;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class AssignCourtsView extends VBox {
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AssignCourtsView(Tournament t) {
        setSpacing(8);
        setPadding(new Insets(10));

        Label title = new Label("Assigned Matches");

        // Set up tableview; if no matches, display "No matches"
        TableView<Match> table = new TableView<>();
        table.setPlaceholder(new Label("No matches"));

        // Home team column
        TableColumn<Match, String> colHome = new TableColumn<>("Home");
        colHome.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        Optional.ofNullable(c.getValue().getHome())
                                .map(Team::getName)
                                .orElse("-")
                )
        );
        colHome.setPrefWidth(160);

        // Away team column
        TableColumn<Match, String> colAway = new TableColumn<>("Away");
        colAway.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        Optional.ofNullable(c.getValue().getAway())
                                .map(Team::getName)
                                .orElse("-")
                )
        );
        colAway.setPrefWidth(160);

        // Court column
        TableColumn<Match, String> colCourt = new TableColumn<>("Court");
        colCourt.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        Objects.toString(c.getValue().getAssignedCourtNumber(), "-")
                )
        );
        colCourt.setPrefWidth(80);

        // Start time column
        TableColumn<Match, String> colStart = new TableColumn<>("Start");
        colStart.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        Optional.ofNullable(c.getValue().getScheduledStart())
                                .map(DT_FMT::format)
                                .orElse("-")
                )
        );
        colStart.setPrefWidth(160);

        // Add columns and data to the table
        table.getColumns().setAll(colHome, colAway, colCourt, colStart);
        table.getItems().setAll(t.getMatches());

        getChildren().addAll(title, table);
    }

    // Creates and shows the AssignCourtsView in an alert
    public static void show(Tournament t) {
        AssignCourtsView view = new AssignCourtsView(t);
        Alert dlg = new Alert(Alert.AlertType.NONE);
        dlg.setTitle("Court Assignment");
        dlg.getDialogPane().setContent(view);
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dlg.showAndWait();
    }
}
