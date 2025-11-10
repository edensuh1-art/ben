package edu.augustana.csc305.project;

import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class TeamView extends BorderPane {
    private final Team team;
    private final boolean organizerMode;
    private final ObservableList<Player> players = FXCollections.observableArrayList();
    private final ImageView imageView = new ImageView();
    private final Label imagePlaceholder = new Label("No photo available");
    private final StackPane imageHolder = new StackPane();
    private final TextField photoField = new TextField();
    private Runnable onClose = () -> {};

    public TeamView(Team team, boolean organizerMode) {
        this.team = Objects.requireNonNull(team, "team must not be null");
        this.organizerMode = organizerMode;

        setPadding(new Insets(12));
        setPrefSize(520, 520);

        Label title = new Label(team.getName() == null || team.getName().isBlank()
                ? "Unnamed Team"
                : team.getName());
        title.getStyleClass().add("team-title");
        BorderPane.setAlignment(title, Pos.CENTER);
        setTop(title);

        players.setAll(team.getPlayers());
        ListView<Player> playerList = new ListView<>(players);
        playerList.setPlaceholder(new Label("No players"));
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

        Label hint = new Label("Double-click a player to view their profile.");

        playerList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Player selected = playerList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openPlayerProfile(selected);
                }
            }
        });

        Button viewButton = new Button("View Profile");
        viewButton.disableProperty().bind(playerList.getSelectionModel().selectedItemProperty().isNull());
        viewButton.setOnAction(e -> {
            Player selected = playerList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                openPlayerProfile(selected);
            }
        });

        VBox center = new VBox(8, hint, playerList, viewButton);
        center.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(playerList, Priority.ALWAYS);
        setCenter(center);

        imageView.setPreserveRatio(true);
        imageView.setFitWidth(220);
        imageView.setFitHeight(220);
        imagePlaceholder.getStyleClass().add("image-placeholder");
        imageHolder.getChildren().setAll(imagePlaceholder);
        imageHolder.setAlignment(Pos.CENTER);
        imageHolder.setPrefSize(240, 240);

        VBox photoBox = new VBox(10, imageHolder);
        photoBox.setAlignment(Pos.TOP_CENTER);
        photoBox.setPadding(new Insets(0, 0, 0, 12));

        if (organizerMode) {
            photoField.setPromptText("Photo URL");
            Button updatePhoto = new Button("Update Photo");
            updatePhoto.setOnAction(e -> {
                team.setPhotoUrl(photoField.getText().trim());
                refreshPhoto();
            });
            photoBox.getChildren().addAll(photoField, updatePhoto);
        }

        setRight(photoBox);

        refreshPhoto();
    }

    private void openPlayerProfile(Player player) {
        Window owner = getScene() != null ? getScene().getWindow() : null;
        // PlayerProfileView is display-only now
        PlayerProfileView.show(player, owner);

        // After the dialog closes, refresh list (no-op if nothing changed) and propagate close callback.
        players.setAll(team.getPlayers());
        if (onClose != null) {
            onClose.run();
        }
    }

    private void refreshPhoto() {
        String url = team.getPhotoUrl();
        if (url == null || url.isBlank()) {
            imageHolder.getChildren().setAll(imagePlaceholder);
            imagePlaceholder.setText("No photo available");
            if (organizerMode) {
                photoField.setText("");
            }
            return;
        }
        try {
            Image img = new Image(url, 220, 220, true, true, true);
            if (img.isError()) {
                throw img.getException();
            }
            imageView.setImage(img);
            imageHolder.getChildren().setAll(imageView);
        } catch (Exception ex) {
            imagePlaceholder.setText("Unable to load photo");
            imageHolder.getChildren().setAll(imagePlaceholder);
        }
        if (organizerMode) {
            photoField.setText(url);
        }
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = (onClose != null) ? onClose : () -> {};
    }

    public static void show(Team team, boolean organizerMode, Window owner, Runnable onClose) {
        Stage stage = new Stage();
        String name = team.getName();
        stage.setTitle((name == null || name.isBlank() ? "Team" : name) + " Profile");
        if (owner != null) {
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
        }

        TeamView view = new TeamView(team, organizerMode);
        view.setOnClose(onClose);
        stage.setScene(new Scene(view, 520, 520));
        stage.setOnHidden(e -> view.onClose.run());
        stage.showAndWait();
    }
}
