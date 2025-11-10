package edu.augustana.csc305.project;

import java.time.format.DateTimeFormatter;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

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
        colCourts.setCellValueFactory(c ->
                Bindings.createStringBinding(() -> String.valueOf(c.getValue().getCourts().size())));
        colCourts.setPrefWidth(90);

        table.getColumns().setAll(colName, colStart, colCourts);

        Button btnDetails = new Button("Details");
        btnDetails.setOnAction(e -> {
            Tournament t = table.getSelectionModel().getSelectedItem();
            if (t == null) {
                showInfo("Select a tournament to view.");
                return;
            }
            showDetailsDialog(t, store, table.getScene() != null ? table.getScene().getWindow() : null);
        });

        Button btnSchedule = new Button("Match Schedule");
        btnSchedule.setOnAction(e -> {
            Tournament t = table.getSelectionModel().getSelectedItem();
            if (t == null) {
                showInfo("Select a tournament to view the schedule.");
                return;
            }
            MatchScheduleView.show(t, table.getScene() != null ? table.getScene().getWindow() : null);
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

        HBox actions = new HBox(8, btnDetails, btnSchedule, btnTeams);

        getChildren().addAll(title, table, actions);
    }

    private void showDetailsDialog(Tournament t, ObservableList<Tournament> store, Window owner) {
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
            CreatePlayerView view = new CreatePlayerView(FXCollections.observableList(t.getTeams()));
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
            confirm.setTitle("Confirm Delete");
            if (owner != null) {
                confirm.initOwner(owner);
            }
            confirm.showAndWait().ifPresent(button -> {
                if (button == ButtonType.OK) {
                    store.remove(t);
                    dlg.close();
                }
            });
        });

        Button btnTeams = new Button("Teams");
        btnTeams.setOnAction(e -> showTeamsDialog(t, owner));

        Button btnSchedule = new Button("Match Schedule");
        btnSchedule.setOnAction(e -> MatchScheduleView.show(t, owner));

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

    private void showTeamsDialog(Tournament t, Window owner) {
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

        Button viewTeam = new Button("View Team");
        viewTeam.disableProperty().bind(teamList.getSelectionModel().selectedItemProperty().isNull());
        viewTeam.setOnAction(e -> {
            Team selected = teamList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                TeamView.show(selected, organizerMode, stage.getScene().getWindow(), teamList::refresh);
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

        VBox layout = new VBox(10, teamList, viewTeam);
        layout.setPadding(new Insets(10));

        stage.setScene(new javafx.scene.Scene(layout, 320, 400));
        stage.showAndWait();
    }

    private String formatTournament(Tournament t) {
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
        } else {
            String courts = t.getCourts().stream()
                    .map(c -> String.valueOf(c.getNumber()))
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("-");
            sb.append("Courts: ").append(courts);
        }
        return sb.toString();
    }


    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle("Info");
        alert.showAndWait();
    }
}
