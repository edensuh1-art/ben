//ai used to help with file saving and loading


package edu.augustana.csc305.project;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginView extends VBox {
    private final Stage stage;
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private boolean loginSuccessful = false;

    public LoginView(Stage stage) {
        this.stage = stage;
        setPadding(new Insets(30));
        setSpacing(20);
        setAlignment(Pos.CENTER);
        setFillWidth(false);
        setStyle("-fx-background-color: lightblue;");

        usernameField.setPrefWidth(220);
        passwordField.setPrefWidth(220);

        GridPane loginForm = new GridPane();
        loginForm.setHgap(15);
        loginForm.setVgap(15);
        loginForm.addRow(0, new Label("Username:"), usernameField);
        loginForm.addRow(1, new Label("Password:"), passwordField);

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(220);
        loginButton.setStyle("-fx-font-size: 16px;");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            try {
                if (username.isEmpty() || password.isEmpty()) {
                    throw new IllegalArgumentException("Username and password cannot be empty");
                }

                // Load user data (create file if missing)
                SaveLoadService.UserFile userFile;
                try {
                    userFile = SaveLoadService.loadUser(username, true);
                    if (userFile.password == null || userFile.password.isEmpty()) {
                        // new user: set password and save
                        userFile.password = password;
                        SaveLoadService.saveUser(username, userFile);
                    } else if (!userFile.password.equals(password)) {
                        new Alert(Alert.AlertType.ERROR, "Password incorrect. Please try again.").showAndWait();
                        return;
                    }
                } catch (IOException ioEx) {
                    ioEx.printStackTrace();
                    new Alert(Alert.AlertType.WARNING, "Warning: could not load user file, proceeding with empty data.").showAndWait();
                    userFile = new SaveLoadService.UserFile();
                    userFile.username = username;
                    userFile.password = password;
                }

                loginSuccessful = true;
                stage.close();

            } catch (IllegalArgumentException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Error");
                alert.setHeaderText("Invalid Login");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        getChildren().addAll(loginForm, loginButton);

        VBox.setVgrow(this, Priority.ALWAYS);
    }

    public boolean wasLoginSuccessful() {
        return loginSuccessful;
    }
}
