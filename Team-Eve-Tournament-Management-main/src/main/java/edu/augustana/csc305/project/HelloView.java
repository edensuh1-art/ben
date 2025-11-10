package edu.augustana.csc305.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HelloView extends BorderPane {

    private final ObservableList<Tournament> tournaments = FXCollections.observableArrayList();
    private final ObservableList<Team> stagingTeams = FXCollections.observableArrayList();

    private final TabPane organizerTabs;
    private final TabPane visitorTabs;
    private final VBox welcomePane;

    private final Label statusLabel = new Label("Select a role to continue.");
    private final Button backButton = new Button("Back");

    public HelloView(Stage stage) {
        setPadding(new Insets(12));

        organizerTabs = buildOrganizerTabs();
        visitorTabs = buildVisitorTabs();
        welcomePane = buildWelcomePane(stage);

        backButton.setOnAction(e -> showWelcome());
        backButton.setVisible(false);

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(statusLabel, spacer, backButton);
        setTop(topBar);

        showWelcome();
    }

    private VBox buildWelcomePane(Stage stage) {
        Label welcome = new Label("Welcome to Tournament Manager");
        welcome.getStyleClass().add("title-label");

        Button visitorButton = new Button("I'm a Visitor");
        visitorButton.setOnAction(e -> showVisitor());

        Button organizerButton = new Button("I'm an Organizer");
        organizerButton.setOnAction(e -> openLogin(stage));

        VBox layout = new VBox(12, welcome, visitorButton, organizerButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        return layout;
    }

    private TabPane buildOrganizerTabs() {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().add(new Tab("Tournaments", new TournamentView(tournaments, true)));
        tabs.getTabs().add(new Tab("Create", new CreateTournamentView(tournaments, stagingTeams)));
        return tabs;
    }

    private TabPane buildVisitorTabs() {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().add(new Tab("Tournaments", new TournamentView(tournaments, false)));
        return tabs;
    }

    private void openLogin(Stage owner) {
        Stage dialog = new Stage();
        dialog.setTitle("Organizer Login");
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);

        LoginView loginView = new LoginView(dialog);
        dialog.setScene(new Scene(loginView));
        dialog.showAndWait();  // blocks until dialog closes

        // If login succeeded, we showed no error and closed the dialog:
        showOrganizer();
    }

    private void showOrganizer() {
        statusLabel.setText("Organizer View");
        backButton.setVisible(true);
        setCenter(organizerTabs);
    }

    private void showVisitor() {
        statusLabel.setText("Visitor View");
        backButton.setVisible(true);
        setCenter(visitorTabs);
    }

    private void showWelcome() {
        statusLabel.setText("Select a role to continue.");
        backButton.setVisible(false);
        setCenter(welcomePane);
    }
}
