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
import teampixl.com.pixlpos.database.interfaces;
import teampixl.com.pixlpos.database.interfaces.IUserStore;

public class LoginScreenController implements IUserStore {

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
    }

    @Override
    public ObservableList<Users> getUsers() {
        return null;
    }

    @Override
    public void addUser(Users user) {

    }

    @Override
    public void removeUser(Users user) {

    }

    @Override
    public Users getUser(String username) {
        return null;
    }

    @Override
    public void updateUser(Users user) {

    }

    @Override
    public void updateUserPassword(Users user, String newPassword) {

    }
}
