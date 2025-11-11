package edu.augustana.csc305.project;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Dialog used by organizers to create or edit a player.
 */
public class PlayerEdit extends Dialog<PlayerEdit.Result> {

    private final TextField nameField = new TextField();
    private final Spinner<Integer> ageSpinner = new Spinner<>(0, 120, 0);
    private final TextField heightField = new TextField();
    private final TextField weightField = new TextField();
    private final TextField positionField = new TextField();
    private final TextArea skillsArea = new TextArea();
    private final ComboBox<Team> teamCombo;

    public PlayerEdit(ObservableList<Team> teams, Team defaultTeam, Player player) {
        setTitle(player == null ? "Create Player" : "Edit Player");
        setHeaderText(null);

        teamCombo = new ComboBox<>(teams);
        teamCombo.setPromptText("Assign to team");
        teamCombo.setCellFactory(list -> new ListCell<>() {
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
        teamCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(teamCombo.getPromptText());
                } else {
                    String name = item.getName();
                    setText(name == null || name.isBlank() ? "Unnamed Team" : name);
                }
            }
        });

        ageSpinner.setEditable(true);
        heightField.setPromptText("6ft 2in");
        weightField.setPromptText("150lbs");
        positionField.setPromptText("Position");
        skillsArea.setPrefRowCount(3);

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.setPadding(new Insets(10));

        form.addRow(0, new Label("Team"), teamCombo);
        form.addRow(1, new Label("Name"), nameField);
        form.addRow(2, new Label("Age"), ageSpinner);
        form.addRow(3, new Label("Height"), heightField);
        form.addRow(4, new Label("Weight"), weightField);
        form.addRow(5, new Label("Position"), positionField);
        form.addRow(6, new Label("Skills"), skillsArea);

        getDialogPane().setContent(form);

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        Node saveButton = getDialogPane().lookupButton(saveType);
        saveButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> nameField.getText().trim().isEmpty() || teamCombo.getValue() == null,
                nameField.textProperty(), teamCombo.valueProperty()
        ));

        if (player != null) {
            nameField.setText(player.getName());
            ageSpinner.getValueFactory().setValue(player.getAge());
            heightField.setText(player.getHeight());
            weightField.setText(player.getWeight());
            positionField.setText(player.getPosition());
            skillsArea.setText(player.getSkills());
            teamCombo.setValue(player.getTeam());
        } else if (defaultTeam != null) {
            teamCombo.setValue(defaultTeam);
        }

        setResultConverter(button -> {
            if (button != saveType) {
                return null;
            }
            return new Result(
                    nameField.getText().trim(),
                    ageSpinner.getValue(),
                    heightField.getText().trim(),
                    weightField.getText().trim(),
                    positionField.getText().trim(),
                    skillsArea.getText().trim(),
                    teamCombo.getValue()
            );
        });
    }

    public record Result(
            String name,
            int age,
            String height,
            String weight,
            String position,
            String skills,
            Team team
    ) { }
}
