package teampixl.com.pixlpos.application;

import javafx.application.Application;
import teampixl.com.pixlpos.common.GuiCommon;
import javafx.stage.Stage;

/**
 * Entry point for the POS Application.
 * This class extends the JavaFX Application class and overrides the start method to load the Login Screen FXML file.
 * This class utilizes the GuiCommon class to load the Login Screen FXML file and set the title of the stage.
 */
public class POSApplication extends Application {

    /**
     * Starts the JavaFX application by loading the Login Screen FXML file.
     *
     * @param stage the primary stage for this application, onto which the application scene can be set.
     */
    @Override
    public void start(Stage stage) {
        GuiCommon.loadStage(stage, GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE);
    }

    /**
     * Stops the JavaFX application.
     */
    @Override
    public void stop() { GuiCommon.exit(); }

    /**
     * Launches the JavaFX application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) { launch(); }
}
