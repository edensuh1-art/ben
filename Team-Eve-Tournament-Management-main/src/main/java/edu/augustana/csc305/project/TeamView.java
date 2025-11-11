package edu.augustana.csc305.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Objects;

/**
 * Displays a team's roster and optional photo.
 */
public class TeamView extends BorderPane {

    private final Team team;
    private final boolean organizerMode;
    private final ObservableList<Player> players = FXCollections.observableArrayList();
    private final ImageView photoView = new ImageView();
    private final Label photoFallback = new Label("No photo available");
    private final StackPane photoHolder = new StackPane();
    private final TextField photoField = new TextField();
    private Runnable onClose = () -> {};

    public TeamView(Team team, boolean organizerMode) {
        this.team = Objects.requireNonNull(team, "team");
        this.organizerMode = organizerMode;

        setPadding(new Insets(16));
        setPrefSize(520, 420);

        Label title = new Label(teamName());
        title.getStyleClass().add("team-title");
        setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);

        players.setAll(team.getPlayers());

        ListView<Player> listView = new ListView<>(players);
        listView.setPlaceholder(new Label("No players"));
        listView.setCellFactory(list -> new ListCell<>() {
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

        listView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Player selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    PlayerProfileView.show(selected, getScene() != null ? getScene().getWindow() : null);
                    refreshPlayers();
                }
            }
        });

        Button profileButton = new Button("View Profile");
        profileButton.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull());
        profileButton.setOnAction(e -> {
            Player selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                PlayerProfileView.show(selected, getScene() != null ? getScene().getWindow() : null);
                refreshPlayers();
            }
        });

        VBox center = new VBox(8, new Label("Players"), listView, profileButton);
        center.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(listView, Priority.ALWAYS);
        setCenter(center);

        photoView.setPreserveRatio(true);
        photoView.setFitWidth(220);
        photoView.setFitHeight(220);
        photoFallback.getStyleClass().add("image-placeholder");

        photoHolder.setAlignment(Pos.CENTER);
        photoHolder.getChildren().add(photoFallback);

        VBox photoBox = new VBox(10, photoHolder);
        photoBox.setAlignment(Pos.TOP_CENTER);

        if (organizerMode) {
            photoField.setPromptText("Photo URL");
            Button updatePhoto = new Button("Update Photo");
            updatePhoto.setOnAction(e -> {
                team.setPhotoUrl(photoField.getText().trim());
                refreshPhoto();
            });
            HBox photoControls = new HBox(8, photoField, updatePhoto);
            photoControls.setAlignment(Pos.CENTER);
            photoBox.getChildren().add(photoControls);
        }

        setRight(photoBox);
        BorderPane.setMargin(photoBox, new Insets(0, 0, 0, 12));

        refreshPhoto();
    }

    private void refreshPlayers() {
        players.setAll(team.getPlayers());
    }

    private void refreshPhoto() {
        String url = team.getPhotoUrl();
        if (url == null || url.isBlank()) {
            photoView.setImage(null);
            photoFallback.setText("No photo available");
            photoHolder.getChildren().setAll(photoFallback);
            if (organizerMode) {
                photoField.setText("");
            }
            return;
        }

        try {
            Image image = new Image(url, 220, 220, true, true, true);
            if (image.isError()) {
                throw image.getException();
            }
            photoView.setImage(image);
            photoHolder.getChildren().setAll(photoView);
        } catch (Exception ex) {
            photoView.setImage(null);
            photoFallback.setText("Unable to load photo");
            photoHolder.getChildren().setAll(photoFallback);
        }

        if (organizerMode) {
            photoField.setText(url);
        }
    }

    private String teamName() {
        return formatTeamName(team);
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose == null ? () -> {} : onClose;
    }

    public static void show(Team team, boolean organizerMode, Window owner, Runnable onClose) {
        Objects.requireNonNull(team, "team");
        Stage stage = new Stage();
        String title = formatTeamName(team) + " Profile";
        stage.setTitle(title.trim());
        if (owner != null) {
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
        }

        TeamView view = new TeamView(team, organizerMode);
        view.setOnClose(onClose);
        stage.setScene(new Scene(view, 520, 420));
        stage.setOnHidden(e -> view.onClose.run());
        stage.showAndWait();
    }

    private static String formatTeamName(Team team) {
        if (team == null) {
            return "Team";
        }
        String name = team.getName();
        return (name == null || name.isBlank()) ? "Team" : name;
    }
}
