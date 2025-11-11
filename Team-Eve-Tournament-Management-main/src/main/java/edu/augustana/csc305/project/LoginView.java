package edu.augustana.csc305.project;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView extends VBox {
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField(); // better than TextField for passwords

    public LoginView(Stage dialogStage) {
        setPadding(new Insets(12));
        setSpacing(10);

        usernameField.setPromptText("Username");
        passwordField.setPromptText("Password");

        GridPane loginForm = new GridPane();
        loginForm.setHgap(8);
        loginForm.setVgap(8);
        loginForm.addRow(0, new Label("Username:"), usernameField);
        loginForm.addRow(1, new Label("Password:"), passwordField);

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            try {
                if (!username.isEmpty() && !password.isEmpty()) {
                    // Do your real auth here. For now we just create a User:
                    User user = new Organizer(username, password);
                    // SUCCESS: close the dialog; HelloView will continue after showAndWait()
                    dialogStage.close();
                } else {
                    throw new IllegalArgumentException("Username and password cannot be empty");
                }
            } catch (IllegalArgumentException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Error");
                alert.setHeaderText("Invalid Login");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        getChildren().addAll(loginForm, loginButton);
    }
}
