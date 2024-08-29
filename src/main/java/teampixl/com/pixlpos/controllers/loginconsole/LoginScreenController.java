package teampixl.com.pixlpos.controllers.loginconsole;

import javafx.collections.ObservableList;
import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import teampixl.com.pixlpos.constructs.Users;
import teampixl.com.pixlpos.authentication.LoginService;
import teampixl.com.pixlpos.authentication.AuthenticationManager;

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

    @FXML
    private Button exitButton;

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (AuthenticationManager.login(username, password)){
            Users user = AuthenticationManager.getUser(username);
            if (user.getRole() == Users.UserRole.ADMIN){
                GuiCommon.loadAdminScreen((Stage) loginButton.getScene().getWindow());
            } else {
                GuiCommon.loadUserScreen((Stage) loginButton.getScene().getWindow());
            }
        } else {
            showErrorDialog("Invalid username or password");
        }
    }
}
