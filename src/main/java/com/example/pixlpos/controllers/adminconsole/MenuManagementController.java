package com.example.pixlpos.controllers.adminconsole;

import com.example.pixlpos.POSApplication;
import com.example.pixlpos.constructs.MenuItem;
import javafx.collections.FXCollections;
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
    private MenuItem selectedItem = null;

    @FXML
    public void initialize() {
        menuItems = FXCollections.observableArrayList();
        updateMenuListView();
    }

    @FXML
    protected void onAddButtonClick() {
        String itemName = itemNameField.getText();
        String description = descriptionField.getText();
        double price = Double.parseDouble(priceField.getText());

        if (itemName.isEmpty() || description.isEmpty() || price <= 0) {
            return;
        }

        if (menuItems.stream().anyMatch(item -> item.getItemName().equals(itemName))) {
            return;
        }

        MenuItem item = new MenuItem(itemName, description, price);
        menuItems.add(item);

        updateMenuListView();
        clearFields();
    }

    @FXML
    protected void onRemoveButtonClick() {
        if (menuListView.getSelectionModel().getSelectedItem() != null) {
            String selectedItemName = menuListView.getSelectionModel().getSelectedItem();
            MenuItem selectedItem = menuItems.stream().filter(item -> item.getItemName().equals(selectedItemName)).findFirst().orElse(null);
            if (selectedItem != null) {
                menuItems.remove(selectedItem);
                updateMenuListView();
                clearFields();
            }
        }
    }

    @FXML
    protected void onEditButtonClick() {
        if (menuListView.getSelectionModel().getSelectedItem() != null) {
            String selectedItemName = menuListView.getSelectionModel().getSelectedItem();
            MenuItem selectedItem = menuItems.stream().filter(item -> item.getItemName().equals(selectedItemName)).findFirst().orElse(null);
            if (selectedItem != null) {
                itemNameField.setText(selectedItem.getItemName());
                descriptionField.setText(selectedItem.getDescription());
                priceField.setText(String.valueOf(selectedItem.getPrice()));
            }
        }
    }

    @FXML
    protected void onConfirmButtonClick() {
        if (menuListView.getSelectionModel().getSelectedItem() != null) {
            String selectedItemName = menuListView.getSelectionModel().getSelectedItem();
            MenuItem selectedItem = menuItems.stream().filter(item -> item.getItemName().equals(selectedItemName)).findFirst().orElse(null);
            if (selectedItem != null) {
                selectedItem.setItemName(itemNameField.getText());
                selectedItem.setDescription(descriptionField.getText());
                selectedItem.setPrice(Double.parseDouble(priceField.getText()));
                updateMenuListView();
                clearFields();
            }
        }
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
