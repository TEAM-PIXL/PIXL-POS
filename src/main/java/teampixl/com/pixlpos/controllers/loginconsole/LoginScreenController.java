package teampixl.com.pixlpos.controllers.loginconsole;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.common.GuiCommon;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.models.Users;

public class LoginScreenController extends GuiCommon {

    @FXML
    private TextField usernameField;

    @FXML
    private Button loginButton;

    @FXML
    private Button exitButton;

    @FXML
    private TextField passwordVisibleField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ImageView eyeIcon;

    @FXML
    private Button eyeButton;

    @FXML
    private Button themeToggleButton;

    @FXML
    private ImageView themeToggleIcon;

    private final BooleanProperty isPasswordVisible = new SimpleBooleanProperty(false);
    private boolean isDarkMode = false;

    @FXML
    private void initialize() {
        // Bind the visibility of passwordField to the inverse of isPasswordVisible
        passwordField.visibleProperty().bind(isPasswordVisible.not());
        passwordField.managedProperty().bind(passwordField.visibleProperty());

        // Bind the visibility of passwordVisibleField to isPasswordVisible
        passwordVisibleField.visibleProperty().bind(isPasswordVisible);
        passwordVisibleField.managedProperty().bind(passwordVisibleField.visibleProperty());

        // Synchronize text between passwordField and passwordVisibleField
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());

        // Apply initial theme when the scene is ready
        loginButton.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                applyTheme(newScene);
            }
        });
    }

    @FXML
    private void togglePasswordVisibility() {
        isPasswordVisible.set(!isPasswordVisible.get());

        if (isPasswordVisible.get()) {
            eyeIcon.setImage(new Image(getClass().getResourceAsStream("/teampixl/com/pixlpos/fxml/loginconsole/EYE_OPEN_ICON.png")));
        } else {
            eyeIcon.setImage(new Image(getClass().getResourceAsStream("/teampixl/com/pixlpos/fxml/loginconsole/EYE_CLOSED_ICON.png")));
        }
    }

    @FXML
    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        Scene scene = loginButton.getScene();
        applyTheme(scene);

        // Update the toggle button icon
        if (isDarkMode) {
            themeToggleIcon.setImage(new Image(getClass().getResourceAsStream("/teampixl/com/pixlpos/fxml/loginconsole/TOGGLE_ON.png")));
        } else {
            themeToggleIcon.setImage(new Image(getClass().getResourceAsStream("/teampixl/com/pixlpos/fxml/loginconsole/TOGGLE_OFF.png")));
        }
    }

    private void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        if (isDarkMode) {
            scene.getStylesheets().add(getClass().getResource("/teampixl/com/pixlpos/fxml/loginconsole/loginstage-dark.css").toExternalForm());
        } else {
            scene.getStylesheets().add(getClass().getResource("/teampixl/com/pixlpos/fxml/loginconsole/loginstage.css").toExternalForm());
        }
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private final AuthenticationManager authManager = new AuthenticationManager();
    private final DataStore dataStore = DataStore.getInstance();
    private final UserStack userStack = UserStack.getInstance();

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.isVisible() ? passwordField.getText() : passwordVisibleField.getText();
        boolean auth = authManager.login(username, password);
        System.out.println("Auth: " + auth);
        if (auth) {
            userStack.setCurrentUser(username);
            Users user = userStack.getCurrentUser();
            Users.UserRole role = (Users.UserRole) user.getMetadata().metadata().get("role");
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
