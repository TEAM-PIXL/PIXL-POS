package teampixl.com.pixlpos.common;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class GuiCommon {

    /*===================================================================================================================================================================================
    Code Description:
    This section contains the common constants used in the GUI of the application.

    Variables:
    - WIDTH: The width of the GUI window.
    - HEIGHT: The height of the GUI window.
    - ICON_PATH: The path to the icon image of the application.
    ===================================================================================================================================================================================*/

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public static final String ICON_PATH = "images/icon.JPG";

    /*===================================================================================================================================================================================
    Code Description:
    This section contains the constants for the FXML paths and screen titles.

    Variables:
    - LOGIN_SCREEN_TITLE: The title of the login screen.
    - LOGIN_SCREEN_FXML: The path to the FXML file of the login screen.
    - ADMIN_SCREEN_TITLE: The title of the admin screen.
    - ADMIN_SCREEN_FXML: The path to the FXML file of the admin screen.
    - COOK_SCREEN_TITLE: The title of the cook screen.
    - COOK_SCREEN_FXML: The path to the FXML file of the cook screen.
    - WAITER_SCREEN_TITLE: The title of the waiter screen.
    - WAITER_SCREEN_FXML: The path to the FXML file of the waiter screen.
    ====================================================================================================================================================================================*/

    public static final String LOGIN_SCREEN_TITLE = "Login Screen";
    public static final String LOGIN_SCREEN_FXML = "/teampixl/com/pixlpos/fxml/loginconsole/LoginStage.fxml";
    public static final String ADMIN_SCREEN_TITLE = "Admin Screen";
    public static final String ADMIN_SCREEN_FXML = "/teampixl/com/pixlpos/fxml/adminconsole/AdminStage.fxml";
    public static final String COOK_SCREEN_TITLE = "Cook Screen";
    public static final String COOK_SCREEN_FXML = "/teampixl/com/pixlpos/fxml/cookconsole/CookStage.fxml";
    public static final String WAITER_SCREEN_TITLE = "Waiter Screen";
    public static final String WAITER_SCREEN_FXML = "/teampixl/com/pixlpos/fxml/waiterconsole/WaiterStage.fxml";

    /*===================================================================================================================================================================================
    Code Description:
    This section contains the method to load a new scene in the application.

    Method:
    - loadScene: This method loads a new scene in the application by setting the FXML file, title, and node.
    - loadStage: This method loads a new stage in the application by setting the FXML file, title, and stage.
    ====================================================================================================================================================================================*/

    public static void loadStage(String fxmlPath, String title, Stage stage) {
        try {
            BorderPane root = new BorderPane();
            FXMLLoader fxmlLoader = new FXMLLoader(GuiCommon.class.getResource(fxmlPath));
            fxmlLoader.setRoot(root);
            Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                if (ADMIN_SCREEN_FXML != fxmlPath){
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

    public static void loadScene(String fxmlPath, String title, Node node) {
        loadScene(fxmlPath, title, node, WIDTH, HEIGHT);
    }

}
