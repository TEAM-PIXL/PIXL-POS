package com.example.pixlpos.controllers.cookconsole;

import com.example.pixlpos.POSApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class CookLandingController {

    @FXML
    private Button logoutButton;

    @FXML
    private Button docketsButton;

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
    protected void onDocketsButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(POSApplication.class.getResource("/fxml/cookconsole/dockets.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), POSApplication.WIDTH, POSApplication.HEIGHT);
            Stage stage = (Stage) docketsButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dockets");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
