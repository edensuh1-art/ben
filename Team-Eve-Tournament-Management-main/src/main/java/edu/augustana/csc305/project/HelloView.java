package edu.augustana.csc305.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

public class HelloView extends BorderPane {
    private final ObservableList<Tournament> store = FXCollections.observableArrayList();

    public HelloView() {
        setPadding(new Insets(12));

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabs.getTabs().add(new Tab("Tournaments", new TournamentView(store)));
        tabs.getTabs().add(new Tab("Create",      new CreateTournamentView(store)));

        setCenter(tabs);
    }
}
