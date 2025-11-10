package edu.augustana.csc305.project;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TournamentView extends VBox {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a");
    private Tournament testTournament = new Tournament("Test", LocalDateTime.parse("10-09-2000 02:00 PM", DT_FMT));
    

    public TournamentView(ObservableList<Tournament> store) {
        //populating test tournament
        for (int c = 0; c < 4; c++){
            testTournament.addCourt();
        }
        for (int t = 0; t < 16; t++){
            String num = Integer.toString(t);
            testTournament.addTeam(new Team(num));
        }
        testTournament.getTeams().get(0).addPlayer(new Player("Sally"));
        testTournament.getTeams().get(0).addPlayer(new Player("John"));
        testTournament.getTeams().get(0).addPlayer(new Player("Alex"));
        for (int i = 0; i < testTournament.getTeams().size(); i += 2){
            testTournament.addMatch(new Match(testTournament.getTeams().get(i), testTournament.getTeams().get(i + 1)));
        }

        
        
        setSpacing(8);
        setPadding(new Insets(6));

        //title for view, if no tournaments yet - display "No tournaments yet"
        Label title = new Label("All Tournaments");
        TableView<Tournament> table = new TableView<>(store);
        table.setPlaceholder(new Label("No tournaments yet"));

        TableColumn<Tournament, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(c -> Bindings.createStringBinding(() -> {
            String name = c.getValue().getName();
            return (name == null || name.isBlank()) ? "-" : name;
        }));
        colName.setPrefWidth(260);

        TableColumn<Tournament, String> colStart = new TableColumn<>("Start");
        colStart.setCellValueFactory(c -> Bindings.createStringBinding(() -> {
            if (c.getValue().getStart() == null) return "-";
            return DT_FMT.format(c.getValue().getStart());
        }));
        colStart.setPrefWidth(180);

        TableColumn<Tournament, String> colCourts = new TableColumn<>("#Courts");
        colCourts.setCellValueFactory(c -> Bindings.createStringBinding(() ->
                String.valueOf(c.getValue().getCourts().size())));
        colCourts.setPrefWidth(90);

        table.getColumns().setAll(colName, colStart, colCourts);

        //button controls for the view - view, assign courts, delete
        Button btnView   = new Button("View");
        Button btnAssign = new Button("Assign Courts");
        Button btnDelete = new Button("Delete");

        btnView.setOnAction(e -> {
            Tournament t = table.getSelectionModel().getSelectedItem();
            if (t == null) { showInfo("Select a tournament to view."); return; }
            showDetailsDialog(t);
        });

        btnAssign.setOnAction(e -> {
            Tournament t = table.getSelectionModel().getSelectedItem();
            if (t == null) { showInfo("Select a tournament to assign."); return; }
            AssignCourts.assign(t);
            AssignCourtsView.show(t);
        });

        btnDelete.setOnAction(e -> {
            Tournament t = table.getSelectionModel().getSelectedItem();
            if (t == null) { showInfo("Select a tournament to delete."); return; }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete \"" + t.getName() + "\"? This cannot be undone.",
                    ButtonType.CANCEL, ButtonType.OK);
            confirm.setTitle("Confirm Delete");
            confirm.showAndWait().ifPresent(button -> {
                if (button == ButtonType.OK) {
                    table.getSelectionModel().clearSelection();
                    store.remove(t);
                }
            });
        });

        getChildren().addAll(title, table, new HBox(8, btnView, btnAssign, btnDelete));
    }

    //creates popup for test using formatTournament()
    private void showDetailsDialog(Tournament t) {
        TextArea area = new TextArea(formatTournament(t));
        area.setEditable(false);
        area.setWrapText(false); //why not set to true?
        area.setPrefRowCount(20);
        area.setPrefColumnCount(60);

        Alert dlg = new Alert(Alert.AlertType.INFORMATION);
        dlg.setTitle("Tournament Details");
        dlg.getDialogPane().setContent(area);
        dlg.getButtonTypes().setAll(ButtonType.CLOSE);
        dlg.showAndWait();
    }

    private String formatTournament(Tournament t) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tournament: ").append(
                (t.getName() == null || t.getName().isBlank()) ? "-" : t.getName()
        ).append("\n");

        sb.append("Starts: ").append(
                t.getStart() == null ? "-" : DT_FMT.format(t.getStart())
        ).append("\n");

        sb.append("Sport: " + t.getCategory() + "\n");

        if (t.getCourts().isEmpty()) {
            sb.append("Courts: -");
        } else {
            String courts = t.getCourts().stream()
                    .map(c -> String.valueOf(c.getNumber()))
                    .reduce((a, b) -> a + ", " + b).orElse("-");
            sb.append("Courts: ").append(courts);
        }

        sb.append("\n\nTeams (").append(t.getTeams().size()).append("):");
        
        for (Team team : t.getTeams()){
            sb.append("\n" + team.getName());
            if (team.getPlayers().isEmpty()){
                sb.append("\n\tNo player added for team.");
            } else {
                sb.append("\n\t");
                for (int i = 0; i < team.getPlayers().size(); i++){
                    sb.append(team.getPlayers().get(i).getName());
                    if (i < team.getPlayers().size() - 1) sb.append(", ");
                }
            }
        }
        //t.getTeams().forEach(team -> sb.append(" - ").append(team.getName()).append("\n"));

        if (!t.getMatches().isEmpty()) {
            sb.append("\nMatches:");
            t.getMatches().forEach(m -> sb.append(String.format(
                    "\n  %s vs %s | Court %s | %s | Winner ",
                    m.getHome().getName(),
                    m.getAway().getName(),
                    m.getAssignedCourtNumber() == null ? "-" : m.getAssignedCourtNumber(),
                    m.getScheduledStart() == null ? "-" : DT_FMT.format(m.getScheduledStart()),
                    m.getWinner() == null ? "Match in progress or not started" : m.getWinner()
            )));
        }

        return sb.toString();
    }


    //helper for popups when view, assign, or delete are invalid
    private static void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}
