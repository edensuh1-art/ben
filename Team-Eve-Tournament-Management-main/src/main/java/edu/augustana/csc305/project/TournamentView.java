package edu.augustana.csc305.project;

import java.time.format.DateTimeFormatter;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDateTime;
import javafx.stage.Stage;
import javafx.stage.Window;

/*
DELETE THIS IF ALL/MOST ARE DONE

add buttons (to different screen?):
edit (needs selected tournament)-> edit teams(add or delete, change name,
edit players), name, (maybe not courts since that will involve complicated 
checking and possibly redoing matches and pools, mabye don't do pools or 
matches either)

matches (shows list of running matches then maybe seperator and all other 
matches if applicable to start them - could auto pick which to run next for 
user) -> add point to selected match(box pops up to select which team to add 
point to)

bracket (won't show if in robin if going) -> (only create button to a new 
screen for now)
*/

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
        colName.setCellValueFactory(nameCell -> Bindings.createStringBinding(() -> {
            String name = nameCell.getValue().getName();
            return (name == null || name.isBlank()) ? "-" : name;
        }));
        colName.setPrefWidth(260);

        TableColumn<Tournament, String> colStart = new TableColumn<>("Start");
        colStart.setCellValueFactory(startCell -> Bindings.createStringBinding(() -> {
            if (startCell.getValue().getStart() == null) return "-";
            return DT_FMT.format(startCell.getValue().getStart());
        }));
        colStart.setPrefWidth(180);

        TableColumn<Tournament, String> colCourts = new TableColumn<>("#Courts");
        colCourts.setCellValueFactory(courtsCell -> Bindings.createStringBinding(() ->
                String.valueOf(courtsCell.getValue().getCourts().size())));
        colCourts.setPrefWidth(90);

        TableColumn<Tournament, String> colCategory = new TableColumn<>("Category");
        colCategory.setCellValueFactory(categCell -> Bindings.createStringBinding(() -> String.valueOf(categCell.getValue().getCategory())));

        table.getColumns().setAll(colName, colStart, colCourts, colCategory); //should have no problems since tournament can't be created without them
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        Button btnDetails = new Button("Details");
        btnDetails.setOnAction(e -> {
            Tournament t = table.getSelectionModel().getSelectedItem();
            if (t == null) {
                showInfo("Select a tournament to view.");
                return;
            }
            showDetailsDialog(tournament, store, table.getScene() != null ? table.getScene().getWindow() : null);
        });

        Button btnTeams = new Button("Teams");
        btnTeams.setOnAction(e -> {
            Tournament t = table.getSelectionModel().getSelectedItem();
            if (t == null) {
                showInfo("Select a tournament to view teams.");
                return;
            }
            showTeamsDialog(t, table.getScene() != null ? table.getScene().getWindow() : null);
        });

        HBox actions = new HBox(8, btnDetails, btnTeams);

        getChildren().addAll(title, table, actions);
    }

    private void showDetailsDialog(Tournament tournament, ObservableList<Tournament> store, Window owner) {
        TextArea area = new TextArea(formatTournament(t));
        area.setEditable(false);
        area.setWrapText(false);
        area.setPrefRowCount(20);
        area.setPrefColumnCount(60);

        Alert dlg = new Alert(Alert.AlertType.INFORMATION);
        dlg.setTitle("Tournament Details");
        if (owner != null) {
            dlg.initOwner(owner);
        }

        Button btnAssign = new Button("Assign Courts");
        btnAssign.setOnAction(e -> {
            //elios changes
            Tournament tournament = table.getSelectionModel().getSelectedItem();
            if (tournament == null) {
                showInfo("Select a tournament to assign.");
                return;
            }
            AssignCourts.assign(tournament);
            AssignCourtsView.show(tournament);
        });

        btnDelete.setOnAction(e -> {
            Tournament tournament = table.getSelectionModel().getSelectedItem();
            if (tournament == null) {
                showInfo("Select a tournament to delete.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete \"" + tournament.getName() + "\"? This cannot be undone.",
                    ButtonType.CANCEL, ButtonType.OK);
            //end elios changes
            //eden changes
            AssignCourts.assign(t);
            AssignCourtsView.show(t);
            area.setText(formatTournament(t));
        });

        Button btnPlayers = new Button("Create Player Profiles");
        btnPlayers.setOnAction(e -> {
            Stage stage = new Stage();
            String title = (t.getName() == null || t.getName().isBlank() ? "Tournament" : t.getName()) + " Players";
            stage.setTitle(title);
            if (owner != null) {
                stage.initOwner(owner);
                stage.initModality(Modality.WINDOW_MODAL);
            }
            CreatePlayerView view = new CreatePlayerView(FXCollections.observableList(tournament.getTeams()));
            stage.setScene(new javafx.scene.Scene(view));
            stage.setOnHidden(evt -> area.setText(formatTournament(t)));
            stage.showAndWait();
        });

        Button btnDelete = new Button("Delete Tournament");
        btnDelete.setOnAction(e -> {
            Alert confirm = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Delete \"" + t.getName() + "\"? This cannot be undone.",
                    ButtonType.CANCEL, ButtonType.OK
            );
            //end eden changes
            confirm.setTitle("Confirm Delete");
            if (owner != null) {
                confirm.initOwner(owner);
            }
            confirm.showAndWait().ifPresent(button -> {
                if (button == ButtonType.OK) {
                    //elios changes - don't know if needed
                    table.getSelectionModel().clearSelection();
                    store.remove(tournament);
                    //end
                    //eden changes
                    store.remove(t);
                    dlg.close();
                    //end
                }
            });
        });

        Button btnTeams = new Button("Teams");
        btnTeams.setOnAction(e -> showTeamsDialog(t, owner));

        //elios changes - dont know if needed
        //creates popup using formatTournament()
        private void showDetailsDialog (Tournament tournament){
            TextArea area = new TextArea(formatTournament(tournament));
            area.setEditable(false);
            area.setWrapText(true);
            area.setPrefRowCount(20);
            area.setPrefColumnCount(60);
            //end
            Button btnSchedule = new Button("Match Schedule");
            btnSchedule.setOnAction(e -> MatchScheduleView.show(tournament, owner));

            HBox actions = new HBox(8, btnSchedule, btnTeams);
            if (organizerMode) {
                actions.getChildren().addAll(btnAssign, btnPlayers, btnDelete);
            }

            VBox content = new VBox(10, area, actions);
            content.setPadding(new Insets(10, 0, 0, 0));

            dlg.getDialogPane().setContent(content);
            dlg.getButtonTypes().setAll(ButtonType.CLOSE);
            if (!organizerMode) {
                btnAssign.setDisable(true);
                btnPlayers.setDisable(true);
                btnDelete.setDisable(true);
            }
            dlg.showAndWait();
        }

        //elios changes
        private String formatTournament (Tournament tournament){
            StringBuilder stringBuild = new StringBuilder();
            //tournament name
            stringBuild.append("Tournament: ").append(
                    (tournament.getName() == null || tournament.getName().isBlank()) ? "-" : tournament.getName()).append("\n");
            //start date + time
            stringBuild.append("Starts: ").append(
                    tournament.getStart() == null ? "-" : DT_FMT.format(tournament.getStart())).append("\n");
            //category
            stringBuild.append("Sport: ").append(
                    tournament.getCategory() == null ? "None selected" : tournament.getCategory()).append("\n");
            //courts
            if (tournament.getCourts().isEmpty()) {
                stringBuild.append("Courts: -");
                //end
                //eden changes
                private void showTeamsDialog (Tournament t, Window owner){
                    Stage stage = new Stage();
                    stage.setTitle((t.getName() == null || t.getName().isBlank() ? "Tournament" : t.getName()) + " Teams");
                    if (owner != null) {
                        stage.initOwner(owner);
                        stage.initModality(Modality.WINDOW_MODAL);
                    }

                    ListView<Team> teamList = new ListView<>(FXCollections.observableArrayList(t.getTeams()));
                    teamList.setPlaceholder(new Label("No teams"));
                    teamList.setCellFactory(list -> new ListCell<>() {
                        @Override
                        protected void updateItem(Team item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                            } else {
                                String name = item.getName();
                                setText(name == null || name.isBlank() ? "Unnamed Team" : name);
                            }
                        }
                    });

                    teamList.setOnMouseClicked(e -> {
                        if (e.getClickCount() == 2) {
                            Team selected = teamList.getSelectionModel().getSelectedItem();
                            if (selected != null) {
                                TeamView.show(selected, organizerMode, stage.getScene().getWindow(), teamList::refresh);
                            }
                        }
                    });

                    VBox layout = new VBox(10, teamList);
                    layout.setPadding(new Insets(10));

                    stage.setScene(new javafx.scene.Scene(layout, 320, 400));
                    stage.showAndWait();
                }

                private String formatTournament (Tournament t){
                    StringBuilder sb = new StringBuilder();
                    sb.append("Tournament: ")
                            .append((t.getName() == null || t.getName().isBlank()) ? "-" : t.getName())
                            .append("\n");

                    sb.append("Starts: ")
                            .append(t.getStart() == null ? "-" : DT_FMT.format(t.getStart()))
                            .append("\n");

                    sb.append("Sport: ").append(t.getCategory()).append("\n");

                    if (t.getCourts().isEmpty()) {
                        sb.append("Courts: -");
                        //end
                    } else {
                        String courts = tournament.getCourts().stream()
                                .map(c -> String.valueOf(c.getNumber()))
                                .reduce((a, b) -> a + ", " + b).orElse("-");
                        stringBuild.append("Courts: ").append(courts);
                    }
                    //teams
                    stringBuild.append("\n\nTeams (").append(
                            tournament.getTeams().size()).append("):");
                    //players - may not have player names if none were added
                    for (Team team : tournament.getTeams()) {
                        stringBuild.append("\n" + team.getName());
                        if (team.getPlayers().isEmpty()) {
                            stringBuild.append("\n\tNo players added for team.");
                        } else {
                            stringBuild.append("\n\t");
                            for (int i = 0; i < team.getPlayers().size(); i++) {
                                stringBuild.append(team.getPlayers().get(i).getName());
                                if (i < team.getPlayers().size() - 1) stringBuild.append(", ");
                            }
                        }
                    }

                    if (!tournament.getMatches().isEmpty()) {
                        stringBuild.append("\nMatches:");
                        for (Match match : tournament.getMatches()) {
                            String home = (match.getHome() == null || match.getHome().getName() == null) ? "-" : match.getHome().getName();
                            String away = (match.getAway() == null || match.getAway().getName() == null) ? "-" : match.getAway().getName();
                            String court = (match.getAssignedCourt() < 0) ? "-" : String.valueOf(match.getAssignedCourt());
                            String sched = (match.getScheduledStart() == null) ? "-" : DT_FMT.format(match.getScheduledStart());
                            String winner;
                            if (tournament.getCategory().equals("Volleyball")) {
                                winner = (match.getWinner() == null) ? "Match in progress, Set: " /*match.getSet()*/ : match.getWinner().getName(); //fix to show which set the game is in when other things done to do so
                            } else if (tournament.getCategory().equals("Soccer")) {
                                winner = (match.getWinner() == null) ? "Match in progress" : match.getWinner().getName();
                            } else { //should not show up because of how tournaments are created
                                winner = "Category needed";
                            }
                            stringBuild.append(String.format("\n  %s vs %s | Court %s | %s | Winner %s", home, away, court, sched, winner));
                        }
                    }

                    return stringBuild.toString();
                }


                private void showInfo (String message){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
                    alert.setHeaderText(null);
                    alert.setTitle("Info");
                    alert.showAndWait();
                }
            }
        }
    }
}