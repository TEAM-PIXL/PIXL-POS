package teampixl.com.pixlpos.controllers.adminconsole;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import teampixl.com.pixlpos.common.GuiCommon;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.database.api.UsersAPI;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javafx.scene.layout.HBox;

public class AdminScreenHomeController
{
    /*===================================================================================================================================================================================
    Code Description:
    This class is the controller for the home admin screen of the application.
    ====================================================================================================================================================================================*/

    /*
    Shared Components
     */

    @FXML
    private Text greeting;
    @FXML
    private TextField searchbar;
    @FXML
    private Label date;
    @FXML
    private Label time;

    @FXML
    private Button homebutton;
    @FXML
    private Button usersbutton;
    @FXML
    private Button menubutton;
    @FXML
    private Button stockbutton;
    @FXML
    private Button analyticsbutton;
    @FXML
    private Button logoutbutton;

    AnimationTimer datetime = new AnimationTimer() {
        @Override
        public void handle(long now) {
            date.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            time.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    };

    @FXML
    public void initialize() {
        datetime.start();
    }


    @FXML
    protected void onUsersButtonClick() {
        // Handle exit button click

        Stage stage = (Stage) usersbutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_USERS_FXML, GuiCommon.ADMIN_SCREEN_USERS_TITLE, stage);
//        double width = stage.getScene().getWindow().getWidth();
//        double height = stage.getScene().getWindow().getHeight();
//        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_USERS_FXML, GuiCommon.ADMIN_SCREEN_USERS_TITLE, stage,width,height);
    }
    @FXML
    protected void onMenuButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) menubutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_MENU_FXML, GuiCommon.ADMIN_SCREEN_MENU_TITLE, stage);
    }
    @FXML
    protected void onHomeButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) homebutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_HOME_FXML, GuiCommon.ADMIN_SCREEN_HOME_TITLE, stage);
    }
    @FXML
    protected void onStockButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) stockbutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_STOCK_FXML, GuiCommon.ADMIN_SCREEN_STOCK_TITLE, stage);
    }
    @FXML
    protected void onAnalyticsButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) analyticsbutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_ANALYTICS_FXML, GuiCommon.ADMIN_SCREEN_ANALYTICS_TITLE, stage);
    }
    @FXML
    protected void onLogoutButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) logoutbutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE, stage);
    }



}
