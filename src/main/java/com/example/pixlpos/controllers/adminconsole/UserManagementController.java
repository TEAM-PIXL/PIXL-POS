package com.example.pixlpos.controllers.adminconsole;

import com.example.pixlpos.POSApplication;
import com.example.pixlpos.constructs.Users;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.collections.ObservableList;
import java.io.IOException;

public class UserManagementController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField emailField;

    @FXML
    private CheckBox waiterCheckBox;

    @FXML
    private CheckBox cookCheckBox;

    @FXML
    private CheckBox adminCheckBox;

    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    @FXML
    private Button editButton;

    @FXML
    private Button confirmButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button goBackButton;

    @FXML
    private ListView<String> userListView;

    @FXML
    public void initialize() {
        // Get the users from the singleton
    }

    @FXML
    protected void onAddButtonClick() {
        // Add the user to the list
    }

    @FXML
    protected void onRemoveButtonClick() {
        // Remove the user from the list
    }

    @FXML
    protected void onEditButtonClick() {
        // Edit the user in the list
    }

    @FXML
    protected void onConfirmButtonClick() {
        // Confirm the user in the list
    }

    @FXML
    protected void onLogoutButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), POSApplication.WIDTH, POSApplication.HEIGHT);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
