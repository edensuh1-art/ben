package edu.augustana.csc305.project;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * Organizer view for managing player profiles across teams.
 */
public class CreatePlayerView extends BorderPane {

    private final ObservableList<Team> teams;
    private final ComboBox<Team> teamSelector;
    private final ObservableList<Player> visiblePlayers = FXCollections.observableArrayList();
    private final ListView<Player> playerList = new ListView<>(visiblePlayers);

    public CreatePlayerView(ObservableList<Team> teams) {
        this.teams = Objects.requireNonNull(teams, "teams");

        setPadding(new Insets(12));
        setPrefSize(520, 420);

        Label title = new Label("Player Profiles");
        title.getStyleClass().add("view-title");

        teamSelector = new ComboBox<>(teams);
        teamSelector.setPromptText("Select a team");
        teamSelector.setMaxWidth(Double.MAX_VALUE);
        teamSelector.setCellFactory(list -> new ListCell<>() {
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
        teamSelector.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(teamSelector.getPromptText());
                } else {
                    String name = item.getName();
                    setText(name == null || name.isBlank() ? "Unnamed Team" : name);
                }
            }
        });

        teamSelector.valueProperty().addListener((obs, oldTeam, newTeam) -> refreshPlayers(newTeam));
        teams.addListener((ListChangeListener<Team>) change -> {
            if (!teams.contains(teamSelector.getValue())) {
                teamSelector.setValue(null);
            }
            refreshPlayers(teamSelector.getValue());
        });

        if (!teams.isEmpty()) {
            teamSelector.setValue(teams.get(0));
        }

        playerList.setPlaceholder(new Label("Select a team to see players"));
        playerList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Player item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String name = item.getName();
                    setText(name == null || name.isBlank() ? "Unnamed Player" : name);
                }
            }
        });

        Button addButton = new Button("Add Player");
        Button editButton = new Button("Edit Player");
        Button deleteButton = new Button("Delete Player");

        editButton.disableProperty().bind(playerList.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(playerList.getSelectionModel().selectedItemProperty().isNull());
        addButton.disableProperty().bind(teamSelector.valueProperty().isNull());

        addButton.setOnAction(e -> openEditor(null));
        editButton.setOnAction(e -> openEditor(playerList.getSelectionModel().getSelectedItem()));
        deleteButton.setOnAction(e -> deleteSelectedPlayer());

        VBox top = new VBox(10, title, teamSelector);
        top.setPadding(new Insets(0, 0, 12, 0));

        VBox center = new VBox(8, playerList);
        VBox.setVgrow(playerList, Priority.ALWAYS);

        HBox actions = new HBox(8, addButton, editButton, deleteButton);

        setTop(top);
        setCenter(center);
        setBottom(actions);
    }

    private void openEditor(Player player) {
        Team selectedTeam = player == null ? teamSelector.getValue() : player.getTeam();
        PlayerEdit dialog = new PlayerEdit(teams, selectedTeam, player);
        dialog.showAndWait().ifPresent(result -> {
            if (player == null) {
                Player created = new Player(result.name());
                applyDetails(created, result);
                result.team().addPlayer(created);
                refreshPlayers(teamSelector.getValue());
                playerList.getSelectionModel().select(created);
            } else {
                Team originalTeam = player.getTeam();
                applyDetails(player, result);
                if (result.team() != originalTeam) {
                    if (originalTeam != null) {
                        originalTeam.removePlayer(player);
                    }
                    result.team().addPlayer(player);
                }
                refreshPlayers(teamSelector.getValue());
                playerList.getSelectionModel().select(player);
            }
        });
    }

    private void deleteSelectedPlayer() {
        Player selected = playerList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete \"" + selected.getName() + "\"? This cannot be undone.",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setTitle("Confirm Delete");
        confirm.showAndWait().ifPresent(button -> {
            if (button == ButtonType.OK) {
                Team team = selected.getTeam();
                if (team != null) {
                    team.removePlayer(selected);
                }
                refreshPlayers(teamSelector.getValue());
            }
        });
    }

    private void refreshPlayers(Team team) {
        visiblePlayers.clear();
        if (team != null) {
            visiblePlayers.addAll(team.getPlayers());
        }
    }

    private void applyDetails(Player player, PlayerEdit.Result result) {
        player.setName(result.name());
        player.setAge(result.age());
        player.setHeight(result.height());
        player.setWeight(result.weight());
        player.setPosition(result.position());
        player.setSkills(result.skills());
    }
}
