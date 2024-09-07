package teampixl.com.pixlpos.controllers.adminconsole;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import teampixl.com.pixlpos.constructs.Users;
import teampixl.com.pixlpos.database.DataStore;
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
    private ChoiceBox<String> roleField;

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

    private Label usernameLabel;

    @FXML
    public void initialize() {
        // Initialization code here
    }

    @FXML
    protected void onExitButtonClick() {
        // Handle exit button click
    }

    @FXML
    protected void onNewUserButtonClick() {
        // Handle new user button click
    }

    @FXML
    protected void onDeleteUserButtonClick() {
        // Handle delete user button click
    }

    @FXML
    protected void onSubmitChangesButtonClick() {
        // Handle submit changes button click
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
            Users searchedUser = dataStore.getUser(searchInput);

            Object username = searchedUser.getMetadata().metadata().get("username");
            Object password = searchedUser.getData().get("password");
            Object email = searchedUser.getData().get("email");
            Object role = searchedUser.getMetadata().metadata().get("role");

            usernameField.setText(username.toString());
            passwordField.setText(password.toString());
            emailField.setText(email.toString());
            roleField.setValue(role.toString());
        }
    }

    @FXML
    protected void onClearButtonClick() {
        // Handle clear button click
        searchField.clear();
    }
}