package com.example.pixlpos.controllers.adminconsole;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class AdminConsoleController {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    @FXML
    private Button manageUsersButton;

    @FXML
    private Button manageMenuButton;

    @FXML
    private Button logoutButton;

    @FXML
    protected void onManageUsersButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/user-management.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
            Stage stage = (Stage) manageUsersButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("User Management");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onManageMenuButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/menu-management.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
            Stage stage = (Stage) manageMenuButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Menu Management");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onLogoutButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
