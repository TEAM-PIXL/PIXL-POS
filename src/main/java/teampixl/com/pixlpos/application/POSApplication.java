package teampixl.com.pixlpos.application;

import javafx.application.Application;
import teampixl.com.pixlpos.common.GuiCommon;
import javafx.stage.Stage;

public class POSApplication extends Application {

    /*===================================================================================================================================================================================
    Code Description:
    Entry point for the POS Application. This class extends the JavaFX Application class and overrides the start method to load the Login Screen FXML file.
    This class utilizes the GuiCommon class to load the Login Screen FXML file and set the title of the stage.
    ====================================================================================================================================================================================*/

    @Override
    public void start(Stage stage) {
        GuiCommon.loadStage(GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE, stage);
    }

    public static void main(String[] args) { launch(); }
}
