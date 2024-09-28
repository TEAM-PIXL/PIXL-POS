package teampixl.com.pixlpos.controllers.adminconsole;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.ListView;
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

public class AdminScreenStockController
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

     /*
    Stock Components
     */

    @FXML
    private TextField itemnamefield;
    @FXML
    private TextField desiredquantityfield;
    @FXML
    private TextField actualquantityfield;
    @FXML
    private TextField itempricefield;
    @FXML
    private TextArea itemdescriptionfield;

    @FXML
    private Button submitbutton;
    @FXML
    private Button additembutton;
    @FXML
    private Button cancelbutton;
    @FXML
    private Button editbutton;
    @FXML
    private Button removebutton;
    @FXML
    private ListView<HBox> itemlist;












    public static void addItemToListView(ListView<HBox> listView, String name, String desiredNumber, String actualNumber, String itemPrice) {

        // Create the main HBox
        HBox hBox = new HBox();
        hBox.setPrefHeight(50.0);
        hBox.setPrefWidth(200.0);

        // AnchorPane for Name
        AnchorPane nameAnchorPane = new AnchorPane();
        nameAnchorPane.setPrefHeight(50.0);
        nameAnchorPane.setPrefWidth(212.0);

        Label nameLabel = new Label(name);
        nameLabel.setLayoutX(60.0);
        nameLabel.setLayoutY(16.0);
        nameLabel.setAlignment(Pos.CENTER); // Center text within the label

        nameAnchorPane.getChildren().add(nameLabel);

        // AnchorPane for Desired #
        AnchorPane desiredAnchorPane = new AnchorPane();
        desiredAnchorPane.setPrefHeight(200.0);
        desiredAnchorPane.setPrefWidth(200.0);

        Label desiredLabel = new Label(desiredNumber);
        desiredLabel.setLayoutX(60.0);
        desiredLabel.setLayoutY(16.0);
        desiredLabel.setAlignment(Pos.CENTER); // Center text within the label

        desiredAnchorPane.getChildren().add(desiredLabel);

        // AnchorPane for Actual #
        AnchorPane actualAnchorPane = new AnchorPane();
        actualAnchorPane.setPrefHeight(200.0);
        actualAnchorPane.setPrefWidth(200.0);

        Label actualLabel = new Label(actualNumber);
        actualLabel.setLayoutX(69.0);
        actualLabel.setLayoutY(16.0);
        actualLabel.setAlignment(Pos.CENTER); // Center text within the label

        actualAnchorPane.getChildren().add(actualLabel);

        // AnchorPane for Item Price
        AnchorPane priceAnchorPane = new AnchorPane();
        priceAnchorPane.setPrefHeight(200.0);
        priceAnchorPane.setPrefWidth(200.0);

        Label priceLabel = new Label(itemPrice);
        priceLabel.setLayoutX(75.0);
        priceLabel.setLayoutY(16.0);
        priceLabel.setAlignment(Pos.CENTER); // Center text within the label

        priceAnchorPane.getChildren().add(priceLabel);

        // Add all anchor panes to the HBox
        hBox.getChildren().addAll(nameAnchorPane, desiredAnchorPane, actualAnchorPane, priceAnchorPane);

        // Add the HBox to the ListView
        listView.getItems().add(hBox);
    }


    @FXML
    protected void onSubmitButtonClick(){

    }
    @FXML
    protected void onAddItemButtonClick(){
        addItemToListView(itemlist,"lettuce","#54","23","$5.0");
    }
    @FXML
    protected void onCancelButtonClick(){

    }
    @FXML
    protected void onEditButtonClick(){

    }
    @FXML
    protected void onRemoveButtonClick(){

    }


    @FXML
    protected void onUsersButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) usersbutton.getScene().getWindow();
        GuiCommon.loadStage(GuiCommon.ADMIN_SCREEN_USERS_FXML, GuiCommon.ADMIN_SCREEN_USERS_TITLE, stage);
    }
    @FXML
    protected void onMenuButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) menubutton.getScene().getWindow();
        GuiCommon.loadStage(GuiCommon.ADMIN_SCREEN_MENU_FXML, GuiCommon.ADMIN_SCREEN_MENU_TITLE, stage);
    }
    @FXML
    protected void onHomeButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) homebutton.getScene().getWindow();
        GuiCommon.loadStage(GuiCommon.ADMIN_SCREEN_HOME_FXML, GuiCommon.ADMIN_SCREEN_HOME_TITLE, stage);
    }
    @FXML
    protected void onStockButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) stockbutton.getScene().getWindow();
        GuiCommon.loadStage(GuiCommon.ADMIN_SCREEN_STOCK_FXML, GuiCommon.ADMIN_SCREEN_STOCK_TITLE, stage);
    }
    @FXML
    protected void onAnalyticsButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) analyticsbutton.getScene().getWindow();
        GuiCommon.loadStage(GuiCommon.ADMIN_SCREEN_ANALYTICS_FXML, GuiCommon.ADMIN_SCREEN_ANALYTICS_TITLE, stage);
    }
    @FXML
    protected void onLogoutButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) logoutbutton.getScene().getWindow();
        GuiCommon.loadStage(GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE, stage);
    }
}
