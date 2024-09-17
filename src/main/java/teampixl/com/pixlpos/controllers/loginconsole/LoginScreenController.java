package teampixl.com.pixlpos.controllers.loginconsole;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.database.DataStore;

public class LoginScreenController extends GuiCommon {

    @FXML
    private TextField usernameField;

    @FXML
    private Button loginButton;

    @FXML
    private Button exitButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordVisibleField;

    @FXML
    private Button eyeButton;

    private boolean isPasswordVisible = false;

    @FXML
    private ImageView eyeIcon;

    @FXML
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            passwordVisibleField.setText(passwordField.getText());
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            eyeIcon.setImage(new Image(getClass().getResourceAsStream("teampixl/com/pixlpos/images/loginscreen/EYE_OPEN_ICON.png")));
        } else {
            passwordField.setText(passwordVisibleField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
            eyeIcon.setImage(new Image(getClass().getResourceAsStream("teampixl/com/pixlpos/images/loginscreen/EYE_CLOSED_ICON.png")));
        }
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void initialize() {
    }

    private AuthenticationManager authManager = new AuthenticationManager();
    private DataStore dataStore = DataStore.getInstance();
    private UserStack userStack = UserStack.getInstance();

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean auth = authManager.login(username, password);
        System.out.println("Auth: " + auth);
        if (auth) {
            userStack.setCurrentUser(username);
            Users user = userStack.getCurrentUser();
            Users.UserRole role = (Users.UserRole) user.getMetadata().metadata().get("role");
            Stage stage = (Stage) loginButton.getScene().getWindow();
            switch (role) {
                case ADMIN:
                    System.out.println("Loading Admin Page");
                    GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_FXML, GuiCommon.ADMIN_SCREEN_TITLE, loginButton);
                    break;
                case COOK:
                    System.out.println("Loading Cook Page");
                    GuiCommon.loadScene(GuiCommon.COOK_SCREEN_FXML, GuiCommon.COOK_SCREEN_TITLE, loginButton);
                    break;
                case WAITER:
                    System.out.println("Loading Waiter Page");
                    GuiCommon.loadScene(GuiCommon.WAITER_SCREEN_FXML, GuiCommon.WAITER_SCREEN_TITLE, loginButton);
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