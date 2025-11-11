package edu.augustana.csc305.project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class ProjectApp extends Application {

    @Override public void start(Stage stage) throws IOException {
        //elios changes
		HelloView helloView = new HelloView(stage);
		
        Scene scene = new Scene(helloView, 
            Screen.getPrimary().getVisualBounds().getWidth(), 
			Screen.getPrimary().getVisualBounds().getHeight());
        scene.getRoot().setStyle("-fx-background-color: lightblue;");
        
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setTitle("Tournament Manager");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
