package teampixl.com.pixlpos.controllers.loginconsole;

import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import teampixl.com.pixlpos.constructs.Users;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.database.DataStore;


public class LoginScreenController extends GuiCommon {

    /*===================================================================================================================================================================================
    Code Description:
    This class is the controller for the login screen of the application. It handles the login functionality and navigation to different screens based on the user role.
    At the moment, this is skeleton code and will be implemented further in the future.
    ====================================================================================================================================================================================*/


    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button exitButton;

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private AuthenticationManager authManager = new AuthenticationManager();
    private DataStore dataStore = DataStore.getInstance();

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (authManager.login(username, password)) {
            Users user = dataStore.getUser(username);
            Users.UserRole role = (Users.UserRole) user.getMetadata().metadata().get("role");
            Stage stage = (Stage) loginButton.getScene().getWindow();
            switch (role) {
                case ADMIN:
                    System.out.println("Loading Admin Page");
                    GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_FXML, GuiCommon.ADMIN_SCREEN_TITLE, stage);
                    break;
                case COOK:
                    System.out.println("Loading Cook Page");
                    GuiCommon.loadScene(GuiCommon.COOK_SCREEN_FXML, GuiCommon.COOK_SCREEN_TITLE, stage);
                    break;
                case WAITER:
                    System.out.println("Loading Waiter Page");
                    GuiCommon.loadScene(GuiCommon.WAITER_SCREEN_FXML, GuiCommon.WAITER_SCREEN_TITLE, stage);
                    break;
                default:
                    showErrorDialog("Invalid user role");
            }
        } else {
            showErrorDialog("Invalid username or password");
        }
    }

    @FXML
    protected void onExitButtonClick() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
