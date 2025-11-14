//ai used to help with file saving and loading

package edu.augustana.csc305.project;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HelloView extends BorderPane {
    //elios changes
    private final ObservableList<Tournament> tournaments = FXCollections.observableArrayList();
    private String username;
    private String password;
    private final ObservableList<Team> stagingTeams = FXCollections.observableArrayList();

    private final TabPane organizerTabs;
    private final TabPane visitorTabs;

    private final Label statusLabel;
    private final Button backButton;

    public HelloView(String username, String password, Boolean isOrganizer) {
        this.username = username;
        this.password = password;
        //if (preloaded != null) this.tournaments.addAll(preloaded);

            /* 
        // attach listener to auto-save on changes
        this.tournaments.addListener((ListChangeListener<Tournament>) change -> {
            try {
                SaveLoadService.saveUser(username, password, new ArrayList<>()); // causing forever loop of file?
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        });
        */
        organizerTabs = buildOrganizerTabs();
        visitorTabs = buildVisitorTabs();
        this.statusLabel = new Label();
        this.backButton = new Button();

        if (isOrganizer) {
            showOrganizer();
        } else showVisitor();

        initializeUI();
    }

    private void initializeUI() {
        setPadding(new Insets(12));
    }

    private TabPane buildOrganizerTabs() {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().add(new Tab("Tournaments", new TournamentView(tournaments, true)));
        tabs.getTabs().add(new Tab("Create", new CreateTournamentView(tournaments, stagingTeams, username, password)));
        return tabs;

    }

    private TabPane buildVisitorTabs() {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().add(new Tab("Tournaments", new TournamentView(tournaments, false)));
        return tabs;
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
}
