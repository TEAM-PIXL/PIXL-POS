package com.example.pixlpos.controllers;

import com.example.pixlpos.constructs.Users;
import com.example.pixlpos.database.DataStore;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class LoginScreenController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if ("admin".equals(username) && "admin".equals(password)) {
            loadScene("/fxml/adminconsole/admin-console.fxml", "Admin Console");
            return;
        }

        Users user = DataStore.getInstance().getUsers().stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (user != null) {
            String role = user.getRole();
            switch (role) {
                case "Admin":
                    loadScene("/fxml/adminconsole/admin-console.fxml", "Admin Console");
                    break;
                case "Waiter":
                    loadScene("/fxml/waiterconsole/waiter-landing.fxml", "Waiter Page");
                    break;
                case "Cook":
                    loadScene("/fxml/cookconsole/cook-landing.fxml", "Cook Page");
                    break;
                default:
                    showErrorDialog("Invalid role");
            }
        } else {
            showErrorDialog("Invalid username or password");
        }
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
