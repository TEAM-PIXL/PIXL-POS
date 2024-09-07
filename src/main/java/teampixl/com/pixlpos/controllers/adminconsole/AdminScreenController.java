package teampixl.com.pixlpos.controllers.adminconsole;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import teampixl.com.pixlpos.constructs.Users;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.authentication.AuthenticationManager;

import java.util.Arrays;

public class AdminScreenController {

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

    private DataStore dataStore = DataStore.getInstance();

    private Users loadedUser;


    @FXML
    public void initialize() {
        // Initialization code here
        roleField.getItems().addAll(Arrays.asList(Users.UserRole.values()));
    }

    @FXML
    protected void onExitButtonClick() {
        // Handle exit button click
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
        }
    }

    @FXML
    protected void onDeleteUserButtonClick() {
        // Handle delete user button click
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
        loadedUser.setDataValue("password",password);
        loadedUser.setDataValue("email", email);
        loadedUser.updateMetadata("updated_at", System.currentTimeMillis());
        dataStore.updateUser(loadedUser);

        onCancelButtonClick();
        loadedUser = null;
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
            Object id = loadedUser.getMetadata().metadata().get("id");

            usernameField.setText(username.toString());
            passwordField.setText(password.toString());
            emailField.setText(email.toString());
            roleField.setValue(Users.UserRole.valueOf(role.toString()));
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
    }
}