
package teampixl.com.pixlpos.common;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Utility class for common GUI operations in the application.
 * This class provides methods to load different stages and scenes using FXML files.
 */
public class GuiCommon {
    
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    
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
    public static final String WAITER_SCREEN_FXML = "/teampixl/com/pixlpos/fxml/waiterconsole/WaiterStage2.fxml";

    /**
     * Loads a new stage with the given FXML file and title.
     *
     * @param fxmlPath the path to the FXML file
     * @param title the title of the stage
     */
    public static void loadStage(String fxmlPath, String title) {
        try {
            URL fxmlURL = GuiCommon.class.getResource(fxmlPath);
            if (fxmlURL == null) {
                throw new IOException("FXML file not found at path: " + fxmlPath);
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root, WIDTH, HEIGHT);

            stage.getIcons().add(new Image(Objects.requireNonNull(GuiCommon.class.getResourceAsStream(ICON_PATH))));

            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load stage: " + e.getMessage());
            e.printStackTrace();
        }
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
    public static void loadScene(String fxmlPath, String title, Stage stage, int width, int height) {
        try {
            URL fxmlURL = GuiCommon.class.getResource(fxmlPath);
            if (fxmlURL == null) {
                throw new IOException("FXML file not found at path: " + fxmlPath);
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.getIcons().add(new Image(Objects.requireNonNull(String.valueOf(GuiCommon.class.getResource(ICON_PATH)))));
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load scene: " + e.getMessage());
            e.printStackTrace();
        }
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
    public static void loadScene(String fxmlPath, String title, Node node, int width, int height) {
        if (node == null || node.getScene() == null) {
            System.err.println("Error: Provided node is null or not attached to any scene.");
            return;
        }

        Stage stage = (Stage) node.getScene().getWindow();
        loadScene(fxmlPath, title, stage, width, height);
    }

    public static void loadRoot(String fxmlPath, String title, Stage stage, int width, int height) {
        try {
            URL fxmlURL = GuiCommon.class.getResource(fxmlPath);
            Parent root = getParent(fxmlPath, fxmlURL);

            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load scene: " + e.getMessage());
            e.printStackTrace();
        }
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

    public static void loadNewRoot(String fxmlPath, String title, Stage stage, int width, int height) {
        try {
            URL fxmlURL = GuiCommon.class.getResource(fxmlPath);
            if (fxmlURL == null) {
                throw new IOException("FXML file not found at path: " + fxmlPath);
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);

            Parent root = new BorderPane();
            fxmlLoader.setRoot(root);

            root = fxmlLoader.load();

            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load scene: " + e.getMessage());
            e.printStackTrace();
        }
    }

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
        try {
            URL fxmlURL = GuiCommon.class.getResource(fxmlPath);
            if (fxmlURL == null) {
                throw new IOException("FXML file not found at path: " + fxmlPath);
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
            Parent root = fxmlLoader.load();

            Stage modalStage = new Stage();
            Scene scene = new Scene(root);

            modalStage.initOwner(owner);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle(title);
            modalStage.setScene(scene);
            modalStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Failed to open modal window: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
