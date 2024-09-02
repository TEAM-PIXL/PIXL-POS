package teampixl.com.pixlpos.common;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GuiCommon {

    /*===================================================================================================================================================================================
    Code Description:
    This section contains the common constants used in the GUI of the application.

    Variables:
    - WIDTH: The width of the GUI window.
    - HEIGHT: The height of the GUI window.
    - ICON_PATH: The path to the icon image of the application.
    ===================================================================================================================================================================================*/

    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
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
    - loadScene: This method loads a new scene in the application by setting the FXML file, title, and stage.
    ====================================================================================================================================================================================*/

    private static final Map<String, Scene> sceneCache = new HashMap<>();

    public static void loadScene(String fxmlPath, String title, Stage stage) {
        try {
            Scene scene = sceneCache.get(fxmlPath);
            if (scene == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(GuiCommon.class.getResource(fxmlPath));
                Parent root = fxmlLoader.load();
                scene = new Scene(root, WIDTH, HEIGHT);
                sceneCache.put(fxmlPath, scene);
            }
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
