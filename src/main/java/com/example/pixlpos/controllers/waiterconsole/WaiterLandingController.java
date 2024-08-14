package com.example.pixlpos.controllers.waiterconsole;

import com.example.pixlpos.POSApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class WaiterLandingController {

    @FXML
    private Button logoutButton;

    @FXML
    private Button menuButton;

    @FXML
    private Button newOrderButton;

    @FXML
    private Button ordersButton;

    @FXML
    public void initialize() {
        // Initialization logic if needed
    }

    @FXML
    protected void onLogoutButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(POSApplication.class.getResource("/fxml/login-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), POSApplication.WIDTH, POSApplication.HEIGHT);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onMenuButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(POSApplication.class.getResource("/fxml/waiterconsole/menu-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), POSApplication.WIDTH, POSApplication.HEIGHT);
            Stage stage = (Stage) menuButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Menu");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onNewOrderButtonClick() {
        // Order screen not implemented
    }

    @FXML
    protected void onOrdersButtonClick() {
        // Orders screen not implemented
    }
}
