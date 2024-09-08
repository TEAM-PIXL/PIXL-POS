package teampixl.com.pixlpos.controllers.adminconsole;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;
import teampixl.com.pixlpos.constructs.Users;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.authentication.AuthenticationManager;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;

import javafx.scene.layout.HBox;

public class AdminScreenController {
    /*===================================================================================================================================================================================
    Code Description:
    This class is the controller for the admin screen of the application. It handles the user search, modification and
    display functionality.
    ====================================================================================================================================================================================*/

    @FXML
    private TabPane root;

    @FXML
    private Button exitButton;

    @FXML
    private Button newUserButton;

    @FXML
    private Button deleteUserButton;

    @FXML
    private Button submitChangesButton;

    @FXML
    private Button customiseButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailField;

    @FXML
    private ChoiceBox<Users.UserRole> roleField;

    @FXML
    private TextField itemNameField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField itemTypeField;

    @FXML
    private TextField dietaryRequirementsField;

    @FXML
    private TextArea itemDescriptionArea;

    @FXML
    private Button searchButton;

    @FXML
    private Button clearButton;

    @FXML
    private TextField searchField;

    private DataStore dataStore;

    private Users loadedUser;

    @FXML
    private GridPane userTable;

    @FXML
    public void initialize() {
        // Initialization code here
        dataStore = DataStore.getInstance();
        onCancelButtonClick();
        populateUserGrid();
        roleField.getItems().clear();
        roleField.getItems().addAll(new HashSet<>(Arrays.asList(Users.UserRole.values())));
        loadedUser = null;
    }

    @FXML
    protected void onExitButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) exitButton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE, exitButton);
    }

    @FXML
    protected void onNewUserButtonClick() {
        // Handle new user button click
        try {

            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            Users.UserRole role = roleField.getSelectionModel().getSelectedItem();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || role == null) {
                showAlert(Alert.AlertType.ERROR, "Empty Field", "All fields are required");
            } else {
            if (dataStore.getUser(username) == null) {
                boolean registerUser = AuthenticationManager.register(username, password, email, role);
                if (registerUser) {
                    initialize();
                    showAlert(Alert.AlertType.CONFIRMATION, "New User", "New User has been created");
                } else {
                    showAlert(Alert.AlertType.ERROR, "New User", "Registration Failed");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "New User", "User already exists");
            }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "New User", "Unexpected error occured: " + e.getMessage());
        }
    }

    @FXML
    protected void onDeleteUserButtonClick() {
        // Handle delete user button click
        try{
            if (loadedUser != null) {
                dataStore.removeUser(loadedUser);
                initialize();
                showAlert(Alert.AlertType.CONFIRMATION, "Deleted User", "User has been deleted");
            }
            else{
                showAlert(Alert.AlertType.ERROR, "Deleted User", "Select a user to delete");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Deleted User", "Unexpected error occured: " + e.getMessage());
        }
    }

    @FXML
    protected void onSubmitChangesButtonClick() {
        // Handle submit changes button click
        try{
            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            Users.UserRole role = roleField.getSelectionModel().getSelectedItem();
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || role == null) {
                showAlert(Alert.AlertType.ERROR, "Empty Field", "All fields are required");
            }else {


                try {
                    loadedUser.updateMetadata("username", username);
                    loadedUser.updateMetadata("role", role);
                    loadedUser.setDataValue("password", password);
                    loadedUser.setDataValue("email", email);
                    loadedUser.updateMetadata("updated_at", System.currentTimeMillis());
                    dataStore.updateUser(loadedUser);
                    showAlert(Alert.AlertType.CONFIRMATION, "Updated User", "User has been updated");
                    initialize();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Updated User", "Unexpected error occured while updating user: " + e.getMessage());
                }
            }
        }
        catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Empty Field", "Unexpected error occured: " + e.getMessage());
        }
    }

    @FXML
    protected void onCustomiseButtonClick() {
        // Handle customise button click
    }

    @FXML
    protected void onSearchButtonClick() {
        // Handle search button click
        String searchInput = searchField.getText();
        if (searchInput.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Please specify a User");
        }else {
            try {
                loadedUser = dataStore.getUser(searchInput);
                if (loadedUser == null) {
                    showAlert(Alert.AlertType.ERROR, "Failed", "User not found");
                }else {
                    populateUserParam(loadedUser);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Failed", "Unexpected Error: " + e.getMessage());
            }
        }
    }

    @FXML
    protected void onClearButtonClick() {
        // Handle clear button click
        searchField.clear();
    }

    @FXML
    protected void onCancelButtonClick() {
        // Handle clear button click
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        roleField.setValue(null);
        loadedUser = null;
    }

    private void populateUserGrid() {
        int row = 0;

        ObservableList<Users> listOfUsers = dataStore.getUsers();
        //Used to remove all but the first row, this is to keep formatting but may need to be fixed later.
        userTable.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) >= 1);

        for (Users user : listOfUsers) {

            String readableDate = toReadableDate(user.getMetadata().metadata().get("created_at").toString());
            Label usernameLabel = new Label(user.getMetadata().metadata().get("username").toString());
            Label userSinceLabel = new Label(readableDate);
            Label roleLabel = new Label(user.getMetadata().metadata().get("role").toString());

            HBox rowContainer = new HBox(10);
            rowContainer.setAlignment(Pos.CENTER_LEFT);
            rowContainer.setOnMouseClicked(event -> populateUserParam(user));
            userTable.add(usernameLabel,1, row);
            userTable.add(userSinceLabel,2, row);
            userTable.add(roleLabel,3, row);

            userTable.add(rowContainer, 0, row);
            GridPane.setColumnSpan(rowContainer, userTable.getColumnCount());
            row++;

        }
    }

    private String toReadableDate(String dateString){

        long createAt = Long.parseLong(dateString);

        LocalDateTime createdAtDateTime = Instant.ofEpochMilli(createAt).atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return createdAtDateTime.format(formatter);
    }

    private void populateUserParam(Users User) {
        Object username = User.getMetadata().metadata().get("username");
        Object password = User.getData().get("password");
        Object email = User.getData().get("email");
        Object role = User.getMetadata().metadata().get("role");

        usernameField.setText(username.toString());
        passwordField.setText(password.toString());
        emailField.setText(email.toString());
        roleField.setValue(Users.UserRole.valueOf(role.toString()));
        loadedUser = User;
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}