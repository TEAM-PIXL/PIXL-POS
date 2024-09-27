package teampixl.com.pixlpos.common;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

/**
 * Utility class for common GUI operations in the application.
 * This class provides methods to load different stages and scenes using FXML files.
 */
public class GuiCommon {

    /*===================================================================================================================================================================================
    Code Description:
    This section contains the common constants used in the GUI of the application.

    Variables:
    - WIDTH: The width of the GUI window.
    - HEIGHT: The height of the GUI window.
    - ICON_PATH: The path to the icon image of the application.
    ===================================================================================================================================================================================*/
    /**
     * The width of the GUI window.
     */
    public static final int WIDTH = 1280;

    /**
     * The height of the GUI window.
     */
    public static final int HEIGHT = 720;

    /**
     * The path to the icon image of the application.
     */
    public static final String ICON_PATH = "images/icon.JPG";

    /**
     * The title of the login screen.
     */
    public static final String LOGIN_SCREEN_TITLE = "Login Screen";

    /**
     * The path to the FXML file of the login screen.
     */
    public static final String LOGIN_SCREEN_FXML = "/teampixl/com/pixlpos/fxml/loginconsole/LoginStage.fxml";
    /**
     * The title of the main screen.
     */
    public static final String ADMIN_SCREEN_TITLE = "Admin Screen";
    /**
     * The path to the FXML file of the main screen.
     */
    public static final String ADMIN_SCREEN_FXML = "/teampixl/com/pixlpos/fxml/adminconsole/AdminHome.fxml";
    /**
     * The title of the cook screen.
     */
    public static final String COOK_SCREEN_TITLE = "Cook Screen";
    /**
     * The path to the FXML file of the cook screen.
     */
    public static final String COOK_SCREEN_FXML = "/teampixl/com/pixlpos/fxml/cookconsole/CookStage.fxml";
    /**
     * The title of the waiter screen.
     */
    public static final String WAITER_SCREEN_TITLE = "Waiter Screen";
    /**
     * The path to the FXML file of the waiter screen.
     */
    public static final String WAITER_SCREEN_FXML = "/teampixl/com/pixlpos/fxml/waiterconsole/WaiterStage.fxml";

    /**
     * Loads a new stage in the application by setting the FXML file, title, and stage.
     *
     * @param fxmlPath the path to the FXML file
     * @param title the title of the stage
     * @param stage the stage to be loaded
     */
    public static void loadStage(String fxmlPath, String title, Stage stage) {
        try {
            BorderPane root = new BorderPane();
            FXMLLoader fxmlLoader = new FXMLLoader(GuiCommon.class.getResource(fxmlPath));
            //fxmlLoader.setRoot(root);
            Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a new scene in the application by setting the FXML file, title, and node.
     *
     * @param fxmlPath the path to the FXML file
     * @param title the title of the scene
     * @param node the node to be loaded
     * @param width the width of the scene
     * @param height the height of the scene
     */
    public static void loadScene(String fxmlPath, String title, Node node, int width, int height) {
        if (node == null || node.getScene() == null) {
            System.err.println("Error: Provided node is null or not attached to any scene.");
            return;
        }

        try {
            URL fxmlURL = GuiCommon.class.getResource(fxmlPath);
            System.out.println(fxmlURL);
            if (fxmlURL == null) {
                throw new FileNotFoundException("FXML file not found at path: " + fxmlPath);
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
            System.out.println((String) fxmlLoader.getRoot());

            if (fxmlLoader.getRoot() == null) {
                if (!ADMIN_SCREEN_FXML.equals(fxmlPath)){
                    fxmlLoader.setRoot(new BorderPane());
                }
            }

            Parent rootNode = fxmlLoader.load();
            System.out.println(rootNode);
            Scene scene = new Scene(rootNode, width, height);

            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();


        } catch (IOException e) {
            System.err.println("Failed to load the FXML file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads a new scene in the application by setting the FXML file, title, and node.
     *
     * @param fxmlPath the path to the FXML file
     * @param title the title of the scene
     * @param node the node to be loaded
     */
    public static void loadScene(String fxmlPath, String title, Node node) {
        loadScene(fxmlPath, title, node, WIDTH, HEIGHT);
    }

}
