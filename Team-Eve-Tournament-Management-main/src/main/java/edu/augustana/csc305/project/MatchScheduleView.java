package edu.augustana.csc305.project;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MatchScheduleView extends VBox {
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a");

    public MatchScheduleView(Tournament tournament) {
        Objects.requireNonNull(tournament, "tournament must not be null");

        setSpacing(8);
        setPadding(new Insets(10));

        Label title = new Label("Match Schedule");

        TableView<Match> table = new TableView<>();
        table.setPlaceholder(new Label("No matches scheduled"));

        TableColumn<Match, String> colHome = new TableColumn<>("Home");
        colHome.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(Optional.ofNullable(c.getValue().getHome())
                        .map(Team::getName)
                        .filter(name -> !name.isBlank())
                        .orElse("-")));
        colHome.setPrefWidth(160);

        TableColumn<Match, String> colAway = new TableColumn<>("Away");
        colAway.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(Optional.ofNullable(c.getValue().getAway())
                        .map(Team::getName)
                        .filter(name -> !name.isBlank())
                        .orElse("-")));
        colAway.setPrefWidth(160);

        TableColumn<Match, String> colCourt = new TableColumn<>("Court");
        colCourt.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(Optional.ofNullable(c.getValue().getAssignedCourtNumber())
                        .map(String::valueOf)
                        .orElse("-")));
        colCourt.setPrefWidth(80);

        TableColumn<Match, String> colStart = new TableColumn<>("Start");
        colStart.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(Optional.ofNullable(c.getValue().getScheduledStart())
                        .map(DT_FMT::format)
                        .orElse("-")));
        colStart.setPrefWidth(180);

        table.getColumns().setAll(colHome, colAway, colCourt, colStart);
        table.getItems().setAll(tournament.getMatches());

        getChildren().addAll(title, table);
    }

    public static void show(Tournament tournament, Window owner) {
        if (tournament == null) {
            return;
        }

        Stage stage = new Stage();
        String name = tournament.getName();
        stage.setTitle((name == null || name.isBlank() ? "Tournament" : name) + " Schedule");
        if (owner != null) {
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
        }

        MatchScheduleView view = new MatchScheduleView(tournament);
        stage.setScene(new Scene(view, 600, 400));
        stage.showAndWait();
    }
}