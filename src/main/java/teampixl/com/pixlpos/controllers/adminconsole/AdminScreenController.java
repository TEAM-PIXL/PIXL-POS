package teampixl.com.pixlpos.controllers.adminconsole;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TabPane;
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
    private TextField roleField;

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
            System.out.println(username);
        }
    }

    @FXML
    protected void onClearButtonClick() {
        // Handle clear button click
    }
}