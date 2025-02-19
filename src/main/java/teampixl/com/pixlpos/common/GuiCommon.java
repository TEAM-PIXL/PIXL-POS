package teampixl.com.pixlpos.common;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.image.Image;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.models.UserSettings;
import teampixl.com.pixlpos.models.logs.UserLogTask;
import java.awt.Dimension;


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Utility class for common GUI operations in the application.
 * This class provides methods to load different stages and scenes using FXML files.
 */
public class GuiCommon {

    public static double WIDTH = 1280;
    public static double HEIGHT = 720;

    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);

    /**
     * Load the settings for the current user.
     */
    public static void settings() {
        String currentUserId = UserStack.getInstance().getCurrentUserId();
        List<UserSettings> userSettingsList = DataStore.getInstance().readUserSettings();

        userSettingsList.stream()
                .filter(settings -> currentUserId.equals(settings.getMetadataValue("user_id")))
                .findFirst()
                .ifPresentOrElse(settings -> {
                    String resolution = settings.getMetadataValue("resolution").toString();
                    switch (resolution) {
                        case "HD":
                            Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                            WIDTH = screenSize.getWidth();
                            HEIGHT = screenSize.getHeight() - 30;

                            break;
                        case "SD":
                        default:
                            WIDTH = 1280;
                            HEIGHT = 720;
                            break;
                    }
                }, () -> {
                    WIDTH = 1280;
                    HEIGHT = 720;
                });
    }

    public static final String ICON_PATH = "/teampixl/com/pixlpos/app-icon.jpg";

    public static final String LOGIN_SCREEN_TITLE = "Login Screen";
    public static final String LOGIN_SCREEN_FXML = "/teampixl/com/pixlpos/fxml/loginconsole/LoginStage.fxml";
    public static final String ADMIN_SCREEN_HOME_TITLE = "Admin Home Screen";
    public static final String ADMIN_SCREEN_HOME_FXML = "/teampixl/com/pixlpos/fxml/adminconsole/Home/AdminHome.fxml";
    public static final String ADMIN_SCREEN_USERS_TITLE = "Admin Users Screen";
    public static final String ADMIN_SCREEN_USERS_FXML = "/teampixl/com/pixlpos/fxml/adminconsole/Users/AdminUsers.fxml";
    public static final String ADMIN_SCREEN_MENU_TITLE = "Admin Menu Screen";
    public static final String ADMIN_SCREEN_MENU_FXML = "/teampixl/com/pixlpos/fxml/adminconsole/Menu/AdminMenu.fxml";
    public static final String ADMIN_SCREEN_STOCK_TITLE = "Admin Stock Screen";
    public static final String ADMIN_SCREEN_STOCK_FXML = "/teampixl/com/pixlpos/fxml/adminconsole/Stock/AdminStock.fxml";
    public static final String ADMIN_SCREEN_ANALYTICS_TITLE = "Admin Analytics Screen";
    public static final String ADMIN_SCREEN_ANALYTICS_FXML = "/teampixl/com/pixlpos/fxml/adminconsole/Analytics/AdminAnalytics.fxml";

    public static final String COOK_SCREEN_TITLE = "Cook Screen";
    public static final String COOK_SCREEN_FXML = "/teampixl/com/pixlpos/fxml/cookconsole/CookStage.fxml";

    public static final String WAITER_SCREEN_TITLE = "Waiter Screen";
    public static final String WAITER_SCREEN_FXML = "/teampixl/com/pixlpos/fxml/waiterconsole/WaiterStage.fxml";

    /**
     * Loads a new stage with the given FXML file and title.
     *
     * @param stage the stage to load the FXML file onto
     * @param fxmlPath the path to the FXML file
     * @param title the title of the stage
     */
    public static void loadStage(Stage stage, String fxmlPath, String title) {
        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                URL fxmlURL = GuiCommon.class.getResource(fxmlPath);
                if (fxmlURL == null) {
                    throw new IOException("FXML file not found at path: " + fxmlPath);
                }
                FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
                return fxmlLoader.load();
            }
        };

        loadTask.setOnSucceeded(event -> {
            Parent root = loadTask.getValue();
            Scene scene = new Scene(root, WIDTH, HEIGHT);
            stage.getIcons().add(new Image(Objects.requireNonNull(GuiCommon.class.getResourceAsStream(ICON_PATH))));
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
            Platform.runLater(stage::centerOnScreen);
        });

        loadTask.setOnFailed(event -> {
            Throwable e = loadTask.getException();
            System.err.println("Failed to load stage: " + e.getMessage());
            e.printStackTrace();
        });

        executorService.submit(loadTask);
    }

    /**
     * Loads a new scene into the given stage using the specified FXML file and title.
     *
     * @param fxmlPath the path to the FXML file
     * @param title the title of the stage
     * @param stage the stage to set the scene on
     */
    public static void loadScene(String fxmlPath, String title, Stage stage) {
        loadScene(fxmlPath, title, stage, WIDTH, HEIGHT);
    }

    /**
     * Loads a new scene into the given stage using the specified FXML file, title, and dimensions.
     *
     * @param fxmlPath the path to the FXML file
     * @param title the title of the stage
     * @param stage the stage to set the scene on
     * @param width the width of the scene
     * @param height the height of the scene
     */
    public static void loadScene(String fxmlPath, String title, Stage stage, double width, double height) {
        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                URL fxmlURL = GuiCommon.class.getResource(fxmlPath);
                if (fxmlURL == null) {
                    throw new IOException("FXML file not found at path: " + fxmlPath);
                }
                FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
                return fxmlLoader.load();
            }
        };

        loadTask.setOnSucceeded(event -> {
            Parent root = loadTask.getValue();
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.getIcons().add(new Image(Objects.requireNonNull(GuiCommon.class.getResourceAsStream(ICON_PATH))));
            stage.centerOnScreen();
            stage.show();
        });

        loadTask.setOnFailed(event -> {
            Throwable e = loadTask.getException();
            System.err.println("Failed to load scene: " + e.getMessage());
            e.printStackTrace();
        });

        executorService.submit(loadTask);
    }

    /**
     * Loads a new scene into the stage associated with the given node.
     *
     * @param fxmlPath the path to the FXML file
     * @param title the title of the stage
     * @param node a node from the current scene
     */
    public static void loadScene(String fxmlPath, String title, Node node) {
        loadScene(fxmlPath, title, node, WIDTH, HEIGHT);
    }

    /**
     * Loads a new scene into the stage associated with the given node, with specified dimensions.
     *
     * @param fxmlPath the path to the FXML file
     * @param title the title of the stage
     * @param node a node from the current scene
     * @param width the width of the scene
     * @param height the height of the scene
     */
    public static void loadScene(String fxmlPath, String title, Node node, double width, double height) {
        if (node == null || node.getScene() == null) {
            System.err.println("Error: Provided node is null or not attached to any scene.");
            return;
        }

        Stage stage = (Stage) node.getScene().getWindow();
        loadScene(fxmlPath, title, stage, width, height);
    }

    /**
     * Loads a new scene into the given stage using the specified FXML file and title.
     *
     * @param fxmlPath the path to the FXML file
     * @param title the title of the stage
     * @param stage the stage to set the scene on
     * @param width the width of the scene
     * @param height the height of the scene
     */
    public static void loadRoot(String fxmlPath, String title, Stage stage, int width, int height) {
        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                URL fxmlURL = GuiCommon.class.getResource(fxmlPath);
                return getParent(fxmlPath, fxmlURL);
            }
        };

        loadTask.setOnSucceeded(event -> {
            Parent root = loadTask.getValue();
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();
        });

        loadTask.setOnFailed(event -> {
            Throwable e = loadTask.getException();
            System.err.println("Failed to load scene: " + e.getMessage());
            e.printStackTrace();
        });

        executorService.submit(loadTask);
    }

    private static Parent getParent(String fxmlPath, URL fxmlURL) throws IOException {
        if (fxmlURL == null) {
            throw new IOException("FXML file not found at path: " + fxmlPath);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);

        Parent root;
        try {
            root = fxmlLoader.load();
        } catch (IllegalStateException ex) {
            if (ex.getMessage().contains("Root value already specified")) {
                fxmlLoader = new FXMLLoader(fxmlURL);
                root = fxmlLoader.load();
            } else {
                throw ex;
            }
        }
        return root;
    }

    /**
     * Loads a new scene into the stage associated with the given node.
     *
     * @param fxmlPath the path to the FXML file
     * @param title the title of the stage
     * @param node a node from the current scene
     */
    public static void loadRoot(String fxmlPath, String title, Node node) {
        loadScene(fxmlPath, title, node, WIDTH, HEIGHT);
    }

    /**
     * Loads a new scene into the given stage using the specified FXML file and title.
     *
     * @param fxmlPath the path to the FXML file
     * @param title the title of the stage
     * @param stage the stage to set the scene on
     * @param width the width of the scene
     * @param height the height of the scene
     */
    public static void loadNewRoot(String fxmlPath, String title, Stage stage, double width, double height) {
        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                URL fxmlURL = GuiCommon.class.getResource(fxmlPath);
                if (fxmlURL == null) {
                    throw new IOException("FXML file not found at path: " + fxmlPath);
                }

                FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);

                Parent root = new BorderPane();
                fxmlLoader.setRoot(root);

                return fxmlLoader.load();
            }
        };

        loadTask.setOnSucceeded(event -> {
            Parent root = loadTask.getValue();
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();
        });

        loadTask.setOnFailed(event -> {
            Throwable e = loadTask.getException();
            System.err.println("Failed to load scene: " + e.getMessage());
            e.printStackTrace();
        });

        executorService.submit(loadTask);
    }

    /**
     * Loads a new scene into the stage associated with the given node.
     *
     * @param fxmlPath the path to the FXML file
     * @param title the title of the stage
     * @param node a node from the current scene
     */
    public static void loadNewRoot(String fxmlPath, String title, Node node) {
        if (node == null || node.getScene() == null) {
            System.err.println("Error: Provided node is null or not attached to any scene.");
            return;
        }

        Stage stage = (Stage) node.getScene().getWindow();
        loadNewRoot(fxmlPath, title, stage, WIDTH, HEIGHT);
    }

    /**
     * Closes the current stage associated with the given node.
     *
     * @param node a node from the current scene
     */
    public static void closeStage(Node node) {
        if (node != null && node.getScene() != null) {
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Opens a new modal window with the specified FXML file and title.
     *
     * @param fxmlPath the path to the FXML file
     * @param title the title of the window
     * @param owner the owner window for the modal dialog
     */
    public static void openModalWindow(String fxmlPath, String title, Stage owner) {
        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                URL fxmlURL = GuiCommon.class.getResource(fxmlPath);
                if (fxmlURL == null) {
                    throw new IOException("FXML file not found at path: " + fxmlPath);
                }

                FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
                return fxmlLoader.load();
            }
        };

        loadTask.setOnSucceeded(event -> {
            Parent root = loadTask.getValue();

            Stage modalStage = new Stage();
            Scene scene = new Scene(root);

            modalStage.initOwner(owner);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle(title);
            modalStage.setScene(scene);
            modalStage.showAndWait();
        });

        loadTask.setOnFailed(event -> {
            Throwable e = loadTask.getException();
            System.err.println("Failed to open modal window: " + e.getMessage());
            e.printStackTrace();
        });

        executorService.submit(loadTask);
    }

    /**
     * Logs out the current user and loads the login screen.
     *
     * @param node a node from the current scene
     */
    public static void logout(Node node) {
        UserLogTask.logout().thenRun(() -> Platform.runLater(() -> {
            UserStack.getInstance();
            UserStack.clearCurrentUser();
            Stage stage = (Stage) node.getScene().getWindow();
            loadScene(LOGIN_SCREEN_FXML, LOGIN_SCREEN_TITLE, stage);
        })).exceptionally(ex -> {
            ex.getCause().printStackTrace();
            return null;
        });
    }

    /**
     * Exits the application. Runs custom thread to log out the current user before closing.
     * This ensures that the user's logout is recorded in the database. And the session is properly closed.
     */
    public static void exit() {
        UserLogTask.logout().thenRun(() -> Platform.runLater(() -> {
            System.out.println("Application is closing...");
            Platform.exit();
            System.exit(0);
        })).exceptionally(ex -> {
            ex.getCause().printStackTrace();
            return null;
        });
    }
}
