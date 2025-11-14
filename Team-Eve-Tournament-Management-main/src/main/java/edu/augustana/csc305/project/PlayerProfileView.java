package edu.augustana.csc305.project;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Objects;

/**
 * Read-only display of a player's profile.
 */
public class PlayerProfileView extends VBox {

    public PlayerProfileView(Player player) {
        Objects.requireNonNull(player, "player");

        setPadding(new Insets(16));
        setSpacing(12);

        String displayName = format(player.getName());
        Label title = new Label(displayName.equals("-") ? "Player Profile" : displayName);
        title.getStyleClass().add("player-title");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        grid.addRow(0, label("Name"), value(displayName));
        grid.addRow(1, label("Age"), value(player.getAge() > 0 ? String.valueOf(player.getAge()) : "-"));
        grid.addRow(2, label("Height"), value(format(player.getHeight())));
        grid.addRow(3, label("Weight"), value(format(player.getWeight())));
        grid.addRow(4, label("Position"), value(format(player.getPosition())));

        TextArea skills = new TextArea(format(player.getSkills()));
        skills.setEditable(false);
        skills.setWrapText(true);
        skills.setPrefRowCount(4);
        skills.getStyleClass().add("readonly-area");
        skills.setFocusTraversable(false);

        grid.add(label("Skills"), 0, 5);
        grid.add(skills, 1, 5);

        Button close = new Button("Close");
        close.setOnAction(e -> closeWindow());

        HBox actions = new HBox(close);
        actions.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(title, new Separator(), grid, actions);
    }

    private void closeWindow() {
        Stage stage = (Stage) getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    private Label label(String text) {
        Label label = new Label(text + ":");
        label.getStyleClass().add("profile-label");
        return label;
    }

    private Label value(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("profile-value");
        return label;
    }

    private String format(String value) {
        if (value == null) {
            return "-";
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return "-";
        }
        String lower = trimmed.toLowerCase();
        if (lower.equals("0ft 0in") || lower.equals("0lbs") || lower.equals("none entered")) {
            return "-";
        }
        return trimmed;
    }

    public static void show(Player player, Window owner) {
        Stage stage = new Stage();
        stage.setTitle("Player Profile");
        if (owner != null) {
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
        }

        PlayerProfileView view = new PlayerProfileView(player);
        stage.setScene(new Scene(view, 420, 340));
        stage.showAndWait();
    }
}
