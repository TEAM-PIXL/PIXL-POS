package com.example.pixlpos.controllers.adminconsole;

import com.example.pixlpos.POSApplication;
import com.example.pixlpos.constructs.Users;
import com.example.pixlpos.database.DataStore;
import javafx.collections.FXCollections;
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

    private ObservableList<Users> users;
    private Users selectedUser = null;

    @FXML
    public void initialize() {
        // Get the users from the singleton
        users = DataStore.getInstance().getUsers();
        updateUserListView();
        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedUser = users.stream().filter(user -> user.getUsername().equals(newSelection)).findFirst().orElse(null);
                if (selectedUser != null) {
                    usernameField.setText(selectedUser.getUsername());
                    passwordField.setText(selectedUser.getPassword());
                    emailField.setText(selectedUser.getEmail());
                    waiterCheckBox.setSelected(selectedUser.getRole().equals("Waiter"));
                    cookCheckBox.setSelected(selectedUser.getRole().equals("Cook"));
                    adminCheckBox.setSelected(selectedUser.getRole().equals("Admin"));
                }
            }
        });
    }

    @FXML
    protected void onAddButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String role = "";

        if (waiterCheckBox.isSelected()) {
            role = "Waiter";
        } else if (cookCheckBox.isSelected()) {
            role = "Cook";
        } else if (adminCheckBox.isSelected()) {
            role = "Admin";
        }

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || role.isEmpty()) {
            return;
        }

        if (users.stream().anyMatch(u -> u.getUsername().equals(username)) || users.stream().anyMatch(u -> u.getEmail().equals(email))) {
            return;
        }

        Users user = new Users(username, password, email, role);
        users.add(user);

        clearFields();
        updateUserListView();
    }

    @FXML
    protected void onRemoveButtonClick() {
        if (userListView.getSelectionModel().getSelectedItem() != null) {
            String username = userListView.getSelectionModel().getSelectedItem();
            Users user = users.stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
            if (user != null) {
                users.remove(user);
                clearFields();
                updateUserListView();
            }
        }
    }

    @FXML
    protected void onEditButtonClick() {
        if (userListView.getSelectionModel().getSelectedItem() != null) {
            String username = userListView.getSelectionModel().getSelectedItem();
            Users user = users.stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
            if (user != null) {
                usernameField.setText(user.getUsername());
                passwordField.setText(user.getPassword());
                emailField.setText(user.getEmail());
                waiterCheckBox.setSelected(user.getRole().equals("Waiter"));
                cookCheckBox.setSelected(user.getRole().equals("Cook"));
                adminCheckBox.setSelected(user.getRole().equals("Admin"));
            }
        }

        selectedUser = users.stream().filter(u -> u.getUsername().equals(usernameField.getText())).findFirst().orElse(null);
        updateUserListView();
    }

    @FXML
    protected void onConfirmButtonClick() {
        if (selectedUser != null) {
            selectedUser.setUsername(usernameField.getText());
            selectedUser.setPassword(passwordField.getText());
            selectedUser.setEmail(emailField.getText());
            String role = "";
            if (waiterCheckBox.isSelected()) {
                role = "Waiter";
            } else if (cookCheckBox.isSelected()) {
                role = "Cook";
            } else if (adminCheckBox.isSelected()) {
                role = "Admin";
            }
            selectedUser.setRole(role);
            clearFields();
            updateUserListView();
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

    @FXML
    protected void onCheckBoxClick() {
        if (waiterCheckBox.isSelected()) {
            cookCheckBox.setSelected(false);
            adminCheckBox.setSelected(false);
        } else if (cookCheckBox.isSelected()) {
            waiterCheckBox.setSelected(false);
            adminCheckBox.setSelected(false);
        } else if (adminCheckBox.isSelected()) {
            waiterCheckBox.setSelected(false);
            cookCheckBox.setSelected(false);
        }
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        waiterCheckBox.setSelected(false);
        cookCheckBox.setSelected(false);
        adminCheckBox.setSelected(false);
    }

    private void updateUserListView() {
        userListView.getItems().clear();
        users.forEach(user -> userListView.getItems().add(user.getUsername()));
    }
}
