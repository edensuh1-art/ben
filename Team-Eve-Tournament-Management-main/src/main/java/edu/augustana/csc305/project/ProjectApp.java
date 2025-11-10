package edu.augustana.csc305.project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class ProjectApp extends Application {

    @Override public void start(Stage stage) throws IOException {
        HelloView helloView = new HelloView(stage);

        Scene scene = new Scene(helloView,
                Screen.getPrimary().getVisualBounds().getWidth(),
                Screen.getPrimary().getVisualBounds().getHeight());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setTitle("Tournament Manager");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}