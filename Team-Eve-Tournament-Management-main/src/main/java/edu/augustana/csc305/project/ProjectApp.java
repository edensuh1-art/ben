package edu.augustana.csc305.project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class ProjectApp extends Application {

    @Override public void start(Stage stage) throws IOException {
        LoginView loginView = new LoginView(stage);
        
        Scene scene = new Scene(loginView,Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setTitle("Tournament Manager");
        stage.show();

        /* Scene scene2 = new Scene(new HelloView(), Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());
        stage.setScene(scene2);
        stage.setMaximized(true);
        stage.setTitle("Tournament Manager"); */
        
        
    }

    public static void main(String[] args) {
        launch();
    }
}
