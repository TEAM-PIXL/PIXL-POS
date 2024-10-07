package teampixl.com.pixlpos.controllers.loginconsole;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.common.GuiCommon;
import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.models.logs.UserLogTask;

import java.util.Objects;

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
        // Bind the visibility of passwordField to the inverse of isPasswordVisible ->> passwordField is visible when isPasswordVisible is false
        passwordField.visibleProperty().bind(isPasswordVisible.not());
        passwordField.managedProperty().bind(passwordField.visibleProperty());

        // Bind the visibility of passwordVisibleField to isPasswordVisible ->> passwordVisibleField is visible when isPasswordVisible is true
        passwordVisibleField.visibleProperty().bind(isPasswordVisible);
        passwordVisibleField.managedProperty().bind(passwordVisibleField.visibleProperty());

        // Synchronize text between passwordField and passwordVisibleField ->> When the text in one field changes, the other field is updated
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());

        // Apply initial theme when the scene is ready ->> This is necessary because the scene is not ready when the controller is initialized i.e. when the controller is created
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
            eyeIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/teampixl/com/pixlpos/fxml/loginconsole/icons/EYE_OPEN_ICON.png"))));
        } else {
            eyeIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/teampixl/com/pixlpos/fxml/loginconsole/icons/EYE_CLOSED_ICON.png"))));
        }
    }

    @FXML
    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        Scene scene = loginButton.getScene();
        applyTheme(scene);

        // Change the icon of the theme toggle button based on the current theme ->> Dark mode is on when the icon is TOGGLE_ON.png
        if (isDarkMode) {
            themeToggleIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/teampixl/com/pixlpos/fxml/loginconsole/icons/TOGGLE_ON.png"))));
        } else {
            themeToggleIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/teampixl/com/pixlpos/fxml/loginconsole/icons/TOGGLE_OFF.png"))));
        }
    }

    private void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        if (isDarkMode) {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/teampixl/com/pixlpos/fxml/loginconsole/stylesheets/loginstage-dark.css")).toExternalForm());
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/teampixl/com/pixlpos/fxml/loginconsole/stylesheets/loginstage.css")).toExternalForm());
        }
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private final UserStack userStack = UserStack.getInstance();

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.isVisible() ? passwordField.getText() : passwordVisibleField.getText();
        boolean auth = AuthenticationManager.login(username, password);
        System.out.println("Auth: " + auth);
        if (auth) {
            userStack.setCurrentUser(username);
            GuiCommon.settings();
            Users user = userStack.getCurrentUser();
            Users.UserRole role = (Users.UserRole) user.getMetadata().metadata().get("role");
            UserLogTask.login();
            switch (role) {
                case ADMIN:
                    System.out.println("Loading Admin Page");
                    GuiCommon.loadRoot(GuiCommon.ADMIN_SCREEN_HOME_FXML, GuiCommon.ADMIN_SCREEN_HOME_TITLE, loginButton);
                    break;
                case COOK:
                    System.out.println("Loading Cook Page");
                    GuiCommon.loadRoot(GuiCommon.COOK_SCREEN_FXML, GuiCommon.COOK_SCREEN_TITLE, loginButton);
                    break;
                case WAITER:
                    System.out.println("Loading Waiter Page");
                    GuiCommon.loadNewRoot(GuiCommon.WAITER_SCREEN_FXML, GuiCommon.WAITER_SCREEN_TITLE, loginButton);
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
