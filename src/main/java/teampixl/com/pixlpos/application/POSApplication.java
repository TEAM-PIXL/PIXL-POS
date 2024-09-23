package teampixl.com.pixlpos.application;

import javafx.application.Application;
import teampixl.com.pixlpos.common.GuiCommon;
import javafx.stage.Stage;

/**
 * Entry point for the POS Application.
 * This class extends the JavaFX Application class and overrides the start method to load the Login Screen FXML file.
 * Utilizes the GuiCommon class to load the Login Screen FXML file and set the title of the stage.
 */
public class POSApplication extends Application {

    /**
     * Starts the JavaFX application by loading the Login Screen FXML file.
     *
     * @param stage the primary stage for this application, onto which the application scene can be set.
     */
    @Override
    public void start(Stage stage) {
        GuiCommon.loadStage(GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE);
    }

    /**
     * Optionally, if you need to perform any cleanup or initialization before the application starts,
     * you can override the init() and stop() methods.
     */
    @Override
    public void init() throws Exception {
        super.init();
        // Perform any necessary initialization here
        System.out.println("Application initialization tasks are completed.");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // Perform any necessary cleanup here
        System.out.println("Application cleanup tasks are completed.");
    }

    /**
     * The main method to launch the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}