package edu.augustana.csc305.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginView extends VBox{
    private TextField usernameField = new TextField();
    private TextField passwordField = new TextField();

    public LoginView(Stage stage) {
        setPadding(new Insets(12));
        setSpacing(10);

        usernameField.setPromptText("Username");
        passwordField.setPromptText("Password");
        
        GridPane loginForm =  new GridPane();
        loginForm.setHgap(8);
        loginForm.setVgap(8);
        loginForm.addRow(0, new Label("Username:"), usernameField);
        loginForm.addRow(1, new Label("Password:"), passwordField);
        
    Button loginButton = new Button("Login");
    loginButton.setOnAction(e -> {
    String username = usernameField.getText().trim();
    String password = passwordField.getText().trim();

    try {
        if (!username.isEmpty() && !password.isEmpty()){
            User user = new Organizer(username, password);
            
            stage.getScene().setRoot(new HelloView());
        } else {
            throw new IllegalArgumentException("Username and password cannot be empty");
        }
    } catch (IllegalArgumentException ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText("Invalid Login");
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
        return;
    }
    });

        getChildren().addAll(loginForm, loginButton);
    }
}
