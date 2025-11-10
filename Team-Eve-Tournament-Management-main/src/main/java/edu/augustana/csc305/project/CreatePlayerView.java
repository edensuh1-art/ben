package edu.augustana.csc305.project;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CreatePlayerView extends VBox {

    public CreatePlayerView(ObservableList<Team> teams) {
        setSpacing(10);
        setPadding(new Insets(6));

        Label title = new Label("Players");

        // Team filter (which team's players to show)
        ComboBox<Team> teamFilter = new ComboBox<>(teams);
        teamFilter.setPromptText("Select a team");
        teamFilter.setMaxWidth(Double.MAX_VALUE);

        // Visible players for the selected team
        ObservableList<Player> visiblePlayers = FXCollections.observableArrayList();
        ListView<Player> playerList = new ListView<>(visiblePlayers);
        playerList.setPlaceholder(new Label("Select a team to view players"));
        playerList.setPrefHeight(200);

        // Player form fields
        TextField name = new TextField();
        name.setPromptText("Name");

        Spinner<Integer> age = new Spinner<>(0, 120, 0);
        age.setEditable(true);

        TextField height = new TextField();
        height.setPromptText("Height (e.g. 6ft 2in)");

        TextField weight = new TextField();
        weight.setPromptText("Weight (e.g. 150lbs)");

        TextField position = new TextField();
        position.setPromptText("Position");

        TextArea skills = new TextArea();
        skills.setPromptText("Skills");
        skills.setPrefRowCount(3);

        // Team assignment for the player
        ComboBox<Team> teamAssignment = new ComboBox<>(teams);
        teamAssignment.setPromptText("Assign to team");
        teamAssignment.setMaxWidth(Double.MAX_VALUE);

        // Make both ComboBoxes show team names instead of default toString()
        renderTeams(teamFilter);
        renderTeams(teamAssignment);

        // Form layout
        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.addRow(0, new Label("Assign to Team:"), teamAssignment);
        form.addRow(1, new Label("Name:"), name);
        form.addRow(2, new Label("Age:"), age);
        form.addRow(3, new Label("Height:"), height);
        form.addRow(4, new Label("Weight:"), weight);
        form.addRow(5, new Label("Position:"), position);
        form.addRow(6, new Label("Skills:"), skills);

        // Actions
        Button newPlayer = new Button("New Player");
        Button save = new Button("Save");
        Button delete = new Button("Delete");

        // Disable UI when no teams exist
        BooleanBinding noTeams = Bindings.isEmpty(teams);
        teamFilter.disableProperty().bind(noTeams);
        teamAssignment.disableProperty().bind(noTeams);
        playerList.disableProperty().bind(noTeams);
        form.disableProperty().bind(noTeams);
        newPlayer.disableProperty().bind(noTeams);
        save.disableProperty().bind(noTeams);
        delete.disableProperty().bind(noTeams.or(Bindings.isNull(playerList.getSelectionModel().selectedItemProperty())));

        // When team filter changes, update visible players and default assignment
        teamFilter.valueProperty().addListener((obs, oldTeam, newTeam) -> {
            if (newTeam == null) {
                visiblePlayers.clear();
                teamAssignment.setValue(null);
            } else {
                visiblePlayers.setAll(newTeam.getPlayers());
                teamAssignment.setValue(newTeam);
            }
            playerList.getSelectionModel().clearSelection();
        });

        // When a player is selected, load their details into the form
        playerList.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel == null) {
                name.clear();
                age.getValueFactory().setValue(0);
                height.clear();
                weight.clear();
                position.clear();
                skills.clear();
                if (!teams.isEmpty()) {
                    teamAssignment.setValue(teamFilter.getValue());
                } else {
                    teamAssignment.setValue(null);
                }
            } else {
                name.setText(sel.getName());
                age.getValueFactory().setValue(sel.getAge());
                height.setText(sel.getHeight());
                weight.setText(sel.getWeight());
                position.setText(sel.getPosition());
                skills.setText(sel.getSkills());
                teamAssignment.setValue(sel.getTeam());
            }
        });

        // New player clears selection and defaults team
        newPlayer.setOnAction(e -> {
            playerList.getSelectionModel().clearSelection();
            teamAssignment.setValue(teamFilter.getValue());
        });

        // Save (create or update)
        save.setOnAction(e -> {
            String playerName = name.getText().trim();
            if (playerName.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Player name is required.").showAndWait();
                return;
            }

            int playerAge;
            try {
                playerAge = age.getValue();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Age must be a number.").showAndWait();
                return;
            }

            Team targetTeam = teamAssignment.getValue();
            if (targetTeam == null) {
                new Alert(Alert.AlertType.ERROR, "Select a team for this player.").showAndWait();
                return;
            }

            Player selected = playerList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                // New player
                Player player = new Player(playerName);
                updatePlayer(player, playerName, playerAge, height.getText().trim(), weight.getText().trim(),
                        position.getText().trim(), skills.getText().trim());
                targetTeam.addPlayer(player);

                if (targetTeam == teamFilter.getValue()) {
                    visiblePlayers.add(player);
                    playerList.getSelectionModel().select(player);
                } else {
                    playerList.getSelectionModel().clearSelection();
                }
            } else {
                // Update existing player
                updatePlayer(selected, playerName, playerAge, height.getText().trim(), weight.getText().trim(),
                        position.getText().trim(), skills.getText().trim());

                Team originalTeam = selected.getTeam();
                if (originalTeam != targetTeam) {
                    if (originalTeam != null) {
                        originalTeam.removePlayer(selected);
                        if (originalTeam == teamFilter.getValue()) {
                            visiblePlayers.remove(selected);
                        }
                    }
                    targetTeam.addPlayer(selected);
                }

                if (targetTeam == teamFilter.getValue()) {
                    if (!visiblePlayers.contains(selected)) {
                        visiblePlayers.add(selected);
                    }
                    playerList.getSelectionModel().select(selected);
                    playerList.refresh();
                } else {
                    playerList.getSelectionModel().clearSelection();
                }
            }
        });

        // Delete player
        delete.setOnAction(e -> {
            Player selected = playerList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Delete \"" + selected.getName() + "\"? This cannot be undone.",
                        ButtonType.CANCEL, ButtonType.OK);
                confirm.setTitle("Confirm Delete");
                confirm.showAndWait().ifPresent(button -> {
                    if (button == ButtonType.OK) {
                        Team assignedTeam = selected.getTeam();
                        if (assignedTeam != null) {
                            assignedTeam.removePlayer(selected);
                            if (assignedTeam == teamFilter.getValue()) {
                                visiblePlayers.remove(selected);
                            }
                        }
                        playerList.getSelectionModel().clearSelection();
                    }
                });
            }
        });

        HBox actions = new HBox(8, newPlayer, save, delete);

        VBox.setVgrow(playerList, Priority.ALWAYS);
        getChildren().addAll(title, teamFilter, playerList, form, actions);
    }

    private static void updatePlayer(Player player, String name, int age, String height,
                                     String weight, String position, String skills) {
        player.setName(name);
        player.setAge(age);
        player.setHeight(height);
        player.setWeight(weight);
        player.setPosition(position);
        player.setSkills(skills);
    }

    /**
     * Renders a ComboBox<Team> using the team's name (fallback: "Unnamed Team").
     */
    private static void renderTeams(ComboBox<Team> combo) {
        combo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String n = item.getName();
                    setText((n == null || n.isBlank()) ? "Unnamed Team" : n);
                }
            }
        });

        combo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(combo.getPromptText());
                } else {
                    String n = item.getName();
                    setText((n == null || n.isBlank()) ? "Unnamed Team" : n);
                }
            }
        });
    }
}