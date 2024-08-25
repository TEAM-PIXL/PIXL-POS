package teampixl.com.pixlpos.controllers.loginconsole;

import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

public class LoginScreenController {

    /*===================================================================================================================================================================================
    Code Description:
    This class is the controller for the login screen of the application. It handles the login functionality and navigation to different screens based on the user role.
    At the moment, this is skeleton code and will be implemented further in the future.
    ====================================================================================================================================================================================*/


    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    protected void onLoginButtonClick() {
    }

}
