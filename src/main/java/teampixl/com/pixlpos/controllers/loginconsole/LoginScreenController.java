package teampixl.com.pixlpos.controllers.loginconsole;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
        passwordField.visibleProperty().bind(isPasswordVisible.not());
        passwordField.managedProperty().bind(passwordField.visibleProperty());

        passwordVisibleField.visibleProperty().bind(isPasswordVisible);
        passwordVisibleField.managedProperty().bind(passwordVisibleField.visibleProperty());

        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());

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
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.isVisible() ? passwordField.getText() : passwordVisibleField.getText();

        loginButton.setDisable(true);

        Task<Void> loginTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                boolean auth = AuthenticationManager.login(username, password);
                if (auth) {
                    UserStack userStack = UserStack.getInstance();
                    Future<Users> userFuture = userStack.setCurrentUser(username);

                    Users user;
                    try {
                        user = userFuture.get(); // This will wait until the user is set
                    } catch (InterruptedException | ExecutionException e) {
                        showErrorDialog("An error occurred while retrieving user information.");
                        Platform.runLater(() -> loginButton.setDisable(false));
                        return null;
                    }

                    if (user == null) {
                        showErrorDialog("User not found.");
                        Platform.runLater(() -> loginButton.setDisable(false));
                        return null;
                    }

                    Users.UserRole role = (Users.UserRole) user.getMetadataValue("role");
                    UserLogTask.login();

                    Platform.runLater(() -> {
                        GuiCommon.settings();
                        switch (role) {
                            case ADMIN:
                                GuiCommon.loadRoot(GuiCommon.ADMIN_SCREEN_HOME_FXML, GuiCommon.ADMIN_SCREEN_HOME_TITLE, loginButton);
                                break;
                            case COOK:
                                GuiCommon.loadRoot(GuiCommon.COOK_SCREEN_FXML, GuiCommon.COOK_SCREEN_TITLE, loginButton);
                                break;
                            case WAITER:
                                GuiCommon.loadNewRoot(GuiCommon.WAITER_SCREEN_FXML, GuiCommon.WAITER_SCREEN_TITLE, loginButton);
                                break;
                            default:
                                showErrorDialog("Invalid user role");
                                loginButton.setDisable(false);
                        }
                    });
                } else {
                    showErrorDialog("Invalid username or password");
                    Platform.runLater(() -> loginButton.setDisable(false));
                }
                return null;
            }
        };

        loginTask.setOnFailed(event -> {
            Throwable ex = loginTask.getException();
            showErrorDialog("An error occurred during login: " + ex.getMessage());
            Platform.runLater(() -> loginButton.setDisable(false));
        });

        new Thread(loginTask).start();
    }

    @FXML
    protected void onExitButtonClick() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}


