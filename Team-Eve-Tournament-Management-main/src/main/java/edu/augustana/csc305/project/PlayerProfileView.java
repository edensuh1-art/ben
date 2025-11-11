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

public class PlayerProfileView extends VBox {

    private final Player player;

    public PlayerProfileView(Player player) {
        this.player = Objects.requireNonNull(player, "player must not be null");

        setPadding(new Insets(12));
        setSpacing(10);

        Label title = new Label(display(player.getName(), "Player Profile"));
        title.getStyleClass().add("player-title");

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);

        // Labels for static display
        Label nameVal     = new Label(display(player.getName(), "-"));
        Label ageVal      = new Label(player.getAge() > 0 ? Integer.toString(player.getAge()) : "-");
        Label heightVal   = new Label(display(player.getHeight(), "-", "0ft 0in"));
        Label weightVal   = new Label(display(player.getWeight(), "-", "0lbs"));
        Label positionVal = new Label(display(player.getPosition(), "-", "None entered"));

        TextArea skillsVal = new TextArea(display(player.getSkills(), "-", "None entered"));
        skillsVal.setEditable(false);
        skillsVal.setWrapText(true);
        skillsVal.setPrefRowCount(4);
        skillsVal.setFocusTraversable(false);
        skillsVal.getStyleClass().add("readonly-area");

        grid.addRow(0, new Label("Name:"),     nameVal);
        grid.addRow(1, new Label("Age:"),      ageVal);
        grid.addRow(2, new Label("Height:"),   heightVal);
        grid.addRow(3, new Label("Weight:"),   weightVal);
        grid.addRow(4, new Label("Position:"), positionVal);
        grid.addRow(5, new Label("Skills:"),   skillsVal);

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> {
            Stage st = (Stage) getScene().getWindow();
            if (st != null) st.close();
        });

        HBox actions = new HBox(10, closeBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(title, new Separator(), grid, actions);
    }

    /** Convenience helper to open as a modal window. */
    public static void show(Player player, Window owner) {
        Stage stage = new Stage();
        stage.setTitle("Player Profile");
        if (owner != null) {
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
        }

        PlayerProfileView view = new PlayerProfileView(player);
        stage.setScene(new Scene(view, 420, 360));
        stage.showAndWait();
    }

    // --- helpers ---
    private static String display(String value, String fallback, String... placeholders) {
        if (value == null) return fallback;

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return fallback;
        }

        for (String placeholder : placeholders) {
            if (trimmed.equalsIgnoreCase(placeholder)) {
                return fallback;
            }
        }

        return trimmed;
    }
}