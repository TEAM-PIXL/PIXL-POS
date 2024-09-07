package teampixl.com.pixlpos.controllers.adminconsole;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
        populateUserGrid();
        roleField.getItems().addAll(Arrays.asList(Users.UserRole.values()));
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
        if (dataStore.getUser(usernameField.getText()) == null)  {

            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            Users.UserRole role = roleField.getSelectionModel().getSelectedItem();

            boolean registerUser = AuthenticationManager.register(username,password,email,role);

            onCancelButtonClick();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("User Already Exists");
            alert.showAndWait();
        }
    }

    @FXML
    protected void onDeleteUserButtonClick() {
        // Handle delete user button click
        if (loadedUser != null) {
            dataStore.removeUser(loadedUser);
            onCancelButtonClick();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("User Deleted Successfully");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Please Select a User");
            alert.showAndWait();
        }

    }

    @FXML
    protected void onSubmitChangesButtonClick() {
        // Handle submit changes button click
            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            Users.UserRole role = roleField.getSelectionModel().getSelectedItem();

            loadedUser.updateMetadata("username", username);
            loadedUser.updateMetadata("role", role);
            loadedUser.setDataValue("password", password);
            loadedUser.setDataValue("email", email);
            loadedUser.updateMetadata("updated_at", System.currentTimeMillis());
            dataStore.updateUser(loadedUser);

            onCancelButtonClick();
            loadedUser = null;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success!");
            alert.setHeaderText(null); // You can set a header text or leave it null
            alert.setContentText("Successfully Updated User");
            alert.showAndWait(); // Show the alert and wait for user action
    }

    @FXML
    protected void onCustomiseButtonClick() {
        // Handle customise button click
    }

    @FXML
    protected void onSearchButtonClick() {
        // Handle search button click
        String searchInput = searchField.getText();
        if (!searchInput.isEmpty()) {
            loadedUser = dataStore.getUser(searchInput);

            Object username = loadedUser.getMetadata().metadata().get("username");
            Object password = loadedUser.getData().get("password");
            Object email = loadedUser.getData().get("email");
            Object role = loadedUser.getMetadata().metadata().get("role");

            usernameField.setText(username.toString());
            passwordField.setText(password.toString());
            emailField.setText(email.toString());
            roleField.setValue(Users.UserRole.valueOf(role.toString()));
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("User Not Found");
            alert.showAndWait();
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
}