package edu.augustana.csc305.project;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TournamentView extends VBox {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a");

    // Example tournament for quick testing (optional)
    private final Tournament testTournament =
            new Tournament("Test", LocalDateTime.parse("10-09-2000 02:00 PM", DT_FMT));

    public TournamentView(ObservableList<Tournament> store) {
        setSpacing(10);
        setPadding(new Insets(10));

        // --- Demo data (remove if you don't want sample content) ---
        for (int c = 0; c < 4; c++) {
            testTournament.addCourt();
        }
        for (int t = 0; t < 16; t++) {
            testTournament.addTeam(new Team(Integer.toString(t)));
        }
        testTournament.getTeams().get(0).addPlayer(new Player("Sally"));
        testTournament.getTeams().get(0).addPlayer(new Player("John"));
        testTournament.getTeams().get(0).addPlayer(new Player("Alex"));
        if (!store.contains(testTournament)) {
            store.add(testTournament);
        }
        // -----------------------------------------------------------

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

        // Actions
        Button btnView = new Button("View");
        btnView.setOnAction(e -> {
            Tournament t = table.getSelectionModel().getSelectedItem();
            if (t == null) {
                showInfo("Select a tournament to view.");
                return;
            }
            showDetailsDialog(t, store);
        });

        getChildren().addAll(title, table, new HBox(8, btnView));
    }

    // Creates popup with formatted tournament info
    private void showDetailsDialog(Tournament t, ObservableList<Tournament> store) {
        TextArea area = new TextArea(formatTournament(t));
        area.setEditable(false);
        area.setWrapText(false);
        area.setPrefRowCount(20);
        area.setPrefColumnCount(60);

        Alert dlg = new Alert(Alert.AlertType.INFORMATION);
        dlg.setTitle("Tournament Details");

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
            if (getScene() != null && getScene().getWindow() != null) {
                stage.initOwner(getScene().getWindow());
                stage.initModality(Modality.APPLICATION_MODAL);
            }
            PlayerView view = new PlayerView(FXCollections.observableList(t.getTeams()));
            stage.setScene(new Scene(view));
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
            confirm.showAndWait().ifPresent(button -> {
                if (button == ButtonType.OK) {
                    store.remove(t);
                    dlg.close();
                }
            });
        });

        HBox actions = new HBox(8, btnAssign, btnPlayers, btnDelete);

        VBox content = new VBox(10, area, actions);
        content.setPadding(new Insets(10, 0, 0, 0));

        dlg.getDialogPane().setContent(content);
        dlg.getButtonTypes().setAll(ButtonType.CLOSE);
        dlg.showAndWait();
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
