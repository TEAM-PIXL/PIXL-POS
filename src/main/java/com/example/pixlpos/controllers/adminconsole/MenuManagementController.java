package com.example.pixlpos.controllers.adminconsole;

import com.example.pixlpos.POSApplication;
import com.example.pixlpos.constructs.MenuItem;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class MenuManagementController {

    @FXML
    private TextField itemNameField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField priceField;

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
    private ListView<String> menuListView;

    private ObservableList<MenuItem> menuItems;

    @FXML
    public void initialize() {
        // Get the menu items from the singleton method
    }

    @FXML
    protected void onAddButtonClick() {
        // Add a new menu item to the list
    }

    @FXML
    protected void onRemoveButtonClick() {
        // Remove the selected menu item from the list
    }

    @FXML
    protected void onEditButtonClick() {
        // Edit the selected menu item
    }

    @FXML
    protected void onConfirmButtonClick() {
        // Save the changes to the menu items
    }

    @FXML
    protected void onLogoutButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login-screen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), POSApplication.WIDTH, POSApplication.HEIGHT);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onGoBackButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/adminconsole/admin-console.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), POSApplication.WIDTH, POSApplication.HEIGHT);
            Stage stage = (Stage) goBackButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Admin Console");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateMenuListView() {
        menuListView.getItems().clear();
        for (MenuItem item : menuItems) {
            menuListView.getItems().add(item.getItemName());
        }
    }

    private void clearFields() {
        itemNameField.clear();
        descriptionField.clear();
        priceField.clear();
    }

}
