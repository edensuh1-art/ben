package edu.augustana.csc305.project;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TournamentView extends VBox {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a");
    private final boolean organizerMode;

    public TournamentView(ObservableList<Tournament> store, boolean organizerMode) {
        this.organizerMode = organizerMode;
        setSpacing(10);
        setPadding(new Insets(10));

        Label title = new Label("Tournaments");

        TableView<Tournament> table = new TableView<>();
        table.setItems(store);
        table.setPlaceholder(new Label("No tournaments yet"));

        TableColumn<Tournament, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(nameCell -> Bindings.createStringBinding(() ->
                nameCell.getValue().getName() == null || nameCell.getValue().getName().isBlank()
                        ? "-" : nameCell.getValue().getName()));
        colName.setPrefWidth(260);

        TableColumn<Tournament, String> colStart = new TableColumn<>("Start");
        colStart.setCellValueFactory(startCell -> Bindings.createStringBinding(() ->
                startCell.getValue().getStart() == null ? "-" : DT_FMT.format(startCell.getValue().getStart())));
        colStart.setPrefWidth(180);

        TableColumn<Tournament, String> colCourts = new TableColumn<>("#Courts");
        colCourts.setCellValueFactory(c -> Bindings.createStringBinding(() ->
                String.valueOf(c.getValue().getCourts().size())));
        colCourts.setPrefWidth(90);

        TableColumn<Tournament, String> colCategory = new TableColumn<>("Category");
        colCategory.setCellValueFactory(catCell -> Bindings.createStringBinding(() ->
                catCell.getValue().getCategory()));
        colCategory.setPrefWidth(100);

        table.getColumns().setAll(colName, colStart, colCourts, colCategory);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        Button btnDetails = new Button("Details");
        btnDetails.setOnAction(e -> {
            Tournament selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showInfo("Select a tournament to view."); return; }
            showDetailsDialog(selected, store, table.getScene().getWindow());
        });

        Button btnTeams = new Button("Teams");
        btnTeams.setOnAction(e -> {
            Tournament selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showInfo("Select a tournament to view teams."); return; }
            showTeamsDialog(selected, table.getScene().getWindow());
        });

        HBox actions = new HBox(8, btnDetails, btnTeams);
        getChildren().addAll(title, table, actions);
    }

    private void showDetailsDialog(Tournament tournament, ObservableList<Tournament> store, Window owner) {
        TextArea area = new TextArea(formatTournament(tournament));
        area.setEditable(false);
        area.setWrapText(true);

        Alert dlg = new Alert(Alert.AlertType.INFORMATION);
        dlg.setTitle("Tournament Details");
        if (owner != null) dlg.initOwner(owner);

        Button btnAssign = new Button("Assign Courts");
        btnAssign.setOnAction(e -> {
            AssignCourts.assign(tournament);
            AssignCourtsView.show(tournament);
            area.setText(formatTournament(tournament));
        });

        Button btnPlayers = new Button("Create Player Profiles");
        btnPlayers.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setTitle((tournament.getName() == null ? "Tournament" : tournament.getName()) + " Players");
            if (owner != null) { stage.initOwner(owner); stage.initModality(Modality.WINDOW_MODAL); }
            CreatePlayerView view = new CreatePlayerView(FXCollections.observableList(tournament.getTeams()));
            stage.setScene(new Scene(view));
            stage.setOnHidden(evt -> area.setText(formatTournament(tournament)));
            stage.showAndWait();
        });

        Button btnDelete = new Button("Delete Tournament");
        btnDelete.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete \"" + tournament.getName() + "\"? This cannot be undone.",
                    ButtonType.CANCEL, ButtonType.OK);
            confirm.setTitle("Confirm Delete");
            if (owner != null) confirm.initOwner(owner);
            confirm.showAndWait().ifPresent(b -> {
                if (b == ButtonType.OK) store.remove(tournament);
                dlg.close();
            });
        });

        Button btnSchedule = new Button("Match Schedule");
        btnSchedule.setOnAction(e -> MatchScheduleView.show(tournament, owner));
        
        Button btnBracket = new Button("Veiw Bracket");
        btnBracket.setOnAction(e -> {
            if (!isRobinMatches(tournament.getMatches()) && tournament.getCategory().equals("Volleyball")) {

                BracketView.show(tournament.getTeams());
            }
        }

        );

        HBox actionBox = new HBox(8, btnSchedule);
        if (organizerMode) actionBox.getChildren().addAll(btnAssign, btnPlayers, btnDelete, btnBracket);

        VBox content = new VBox(10, area, actionBox);
        content.setPadding(new Insets(10,0,0,0));
        dlg.getDialogPane().setContent(content);
        dlg.getButtonTypes().setAll(ButtonType.CLOSE);

        dlg.showAndWait();
    }

    private boolean isRobinMatches(List<Match> matches) {
        for (Match match : matches){
            if (match.isRobin()) return true;
        }
        return false;
    }

    private void showTeamsDialog(Tournament t, Window owner) {
        Stage stage = new Stage();
        stage.setTitle((t.getName() == null ? "Tournament" : t.getName()) + " Teams");
        if (owner != null) { stage.initOwner(owner); stage.initModality(Modality.WINDOW_MODAL); }

        ListView<Team> teamList = new ListView<>(FXCollections.observableArrayList(t.getTeams()));
        teamList.setPlaceholder(new Label("No teams"));
        teamList.setCellFactory(l -> new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null || item.getName() == null || item.getName().isBlank())
                        ? "Unnamed Team" : item.getName());
            }
        });

        teamList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Team selected = teamList.getSelectionModel().getSelectedItem();
                if (selected != null) TeamView.show(selected, organizerMode, stage.getScene().getWindow(), teamList::refresh);
            }
        });

        VBox layout = new VBox(10, teamList);
        layout.setPadding(new Insets(10));
        stage.setScene(new Scene(layout, 320, 400));
        stage.showAndWait();
    }

    private String formatTournament(Tournament t) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tournament: ").append(t.getName() == null ? "-" : t.getName()).append("\n");
        sb.append("Starts: ").append(t.getStart() == null ? "-" : DT_FMT.format(t.getStart())).append("\n");
        sb.append("Sport: ").append(t.getCategory()).append("\n");

        if (t.getCourts().isEmpty()) sb.append("Courts: -\n");
        else {
            String courts = t.getCourts().stream()
                    .map(c -> String.valueOf(c.getNumber()))
                    .reduce((a,b)->a+", "+b).orElse("-");
            sb.append("Courts: ").append(courts).append("\n");
        }

        sb.append("\nTeams (").append(t.getTeams().size()).append("):");
        for (Team team : t.getTeams()) {
            sb.append("\n").append(team.getName());
            if (team.getPlayers().isEmpty()) sb.append("\n\tNo players added.");
            else {
                sb.append("\n\t");
                for (int i=0;i<team.getPlayers().size();i++) {
                    sb.append(team.getPlayers().get(i).getName());
                    if (i < team.getPlayers().size()-1) sb.append(", ");
                }
            }
        }

        if (!t.getMatches().isEmpty()) {
            sb.append("\nMatches:");
            if (t.getCategory().equals("Volleyball")){
                for (Match m : t.getMatches()) {
                    String home = m.getHome() == null || m.getHome().getName() == null ? "-" : m.getHome().getName();
                    String away = m.getAway() == null || m.getAway().getName() == null ? "-" : m.getAway().getName();
                    String court = m.getAssignedCourt() < 0 ? "-" : String.valueOf(m.getAssignedCourt());
                    String sched = m.getScheduledStart() == null ? "-" : DT_FMT.format(m.getScheduledStart());
                    int homeScore = m.getHome().getPointsScored();
                    int awayScore = m.getAway().getPointsScored();
                    int set = m.getSet();
                    String winner = m.getWinner() == null ? "No winner yet" : m.getWinner().getName();
                    String progress = m.isInProgress() == false ? "Match not started" : "Match in Progress";
                    sb.append(String.format("\n  %s vs %s | Court: %s | %s | Score: %i vs %i, Set %i, Winner %s | %s", home, away, court, sched, homeScore, awayScore, set, winner, progress));
                }
            } else {
                for (Match m : t.getMatches()) {
                    String home = m.getHome() == null || m.getHome().getName() == null ? "-" : m.getHome().getName();
                    String away = m.getAway() == null || m.getAway().getName() == null ? "-" : m.getAway().getName();
                    String court = m.getAssignedCourt() < 0 ? "-" : String.valueOf(m.getAssignedCourt());
                    String sched = m.getScheduledStart() == null ? "-" : DT_FMT.format(m.getScheduledStart());
                    int homeScore = m.getHome().getPointsScored();
                    int awayScore = m.getAway().getPointsScored();
                    String winner = m.getWinner() == null ? "No winner yet" : m.getWinner().getName();
                    String progress = m.isInProgress() == false ? "Match not started" : "Match in Progress";
                    sb.append(String.format("\n  %s vs %s | Court: %s | %s | Score: %i vs %i, Winner %s | %s", home, away, court, sched, homeScore, awayScore, winner, progress));
                }
            }
        }

        return sb.toString();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle("Info");
        alert.showAndWait();
    }
}
