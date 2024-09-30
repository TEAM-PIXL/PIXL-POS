package teampixl.com.pixlpos.controllers.adminconsole;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.ListView;
import teampixl.com.pixlpos.common.GuiCommon;
import javafx.geometry.Pos;
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

public class AdminScreenMenuController
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
    Menu Components
     */

    @FXML
    private TextField menuitemnamefield;
    @FXML
    private TextField pricefield;
    @FXML
    private ChoiceBox<MenuItem.ItemType> itemtypefield;
    @FXML
    private ChoiceBox<MenuItem.DietaryRequirement> dietaryrequirementsfield;
    @FXML
    private TextArea itemdescriptionfield;

    @FXML
    private Button submitbutton;
    @FXML
    private Button addmenuitembutton;
    @FXML
    private Button cancelbutton;
    @FXML
    private Button editbutton;
    @FXML
    private Button removebutton;
    @FXML
    private ListView<HBox> menuitemlist;



    /**
     * Adds a menu item to the specified ListView. The menu item is represented as an HBox containing four AnchorPanes
     * that display the name, price, type, and dietary information of the item.
     *
     * @param listView the ListView to which the new menu item will be added. Each menu item will be displayed as an HBox.
     * @param name the name of the menu item (e.g., the dish name), displayed in the first column of the HBox.
     * @param price the price of the menu item, displayed in the second column of the HBox.
     * @param type the type of the menu item (e.g., appetizer, main course), displayed in the third column of the HBox.
     * @param dietary dietary information for the menu item (e.g., vegan, gluten-free), displayed in the fourth column of the HBox.
     */
    public static void addMenuItemToListView(ListView<HBox> listView, String name, String price, String type, String dietary) {

        // Create the main HBox
        HBox hBox = new HBox();
        hBox.setPrefHeight(50.0);
        hBox.setPrefWidth(200.0);

        // AnchorPane for Name
        AnchorPane nameAnchorPane = new AnchorPane();
        nameAnchorPane.setPrefHeight(50.0);
        nameAnchorPane.setPrefWidth(212.0);

        Label nameLabel = new Label(name);
        nameLabel.setAlignment(Pos.CENTER); // Center text within the label

        // Center the label in the AnchorPane
        AnchorPane.setTopAnchor(nameLabel, 0.0);
        AnchorPane.setBottomAnchor(nameLabel, 0.0);
        AnchorPane.setLeftAnchor(nameLabel, 0.0);
        AnchorPane.setRightAnchor(nameLabel, 0.0);

        nameAnchorPane.getChildren().add(nameLabel);

        // AnchorPane for Price
        AnchorPane priceAnchorPane = new AnchorPane();
        priceAnchorPane.setPrefHeight(200.0);
        priceAnchorPane.setPrefWidth(200.0);

        Label priceLabel = new Label(price);
        priceLabel.setAlignment(Pos.CENTER); // Center text within the label

        // Center the label in the AnchorPane
        AnchorPane.setTopAnchor(priceLabel, 0.0);
        AnchorPane.setBottomAnchor(priceLabel, 0.0);
        AnchorPane.setLeftAnchor(priceLabel, 0.0);
        AnchorPane.setRightAnchor(priceLabel, 0.0);

        priceAnchorPane.getChildren().add(priceLabel);

        // AnchorPane for Type
        AnchorPane typeAnchorPane = new AnchorPane();
        typeAnchorPane.setPrefHeight(200.0);
        typeAnchorPane.setPrefWidth(200.0);

        Label typeLabel = new Label(type);
        typeLabel.setAlignment(Pos.CENTER); // Center text within the label

        // Center the label in the AnchorPane
        AnchorPane.setTopAnchor(typeLabel, 0.0);
        AnchorPane.setBottomAnchor(typeLabel, 0.0);
        AnchorPane.setLeftAnchor(typeLabel, 0.0);
        AnchorPane.setRightAnchor(typeLabel, 0.0);

        typeAnchorPane.getChildren().add(typeLabel);

        // AnchorPane for Dietary Info
        AnchorPane dietaryAnchorPane = new AnchorPane();
        dietaryAnchorPane.setPrefHeight(200.0);
        dietaryAnchorPane.setPrefWidth(200.0);

        Label dietaryLabel = new Label(dietary);
        dietaryLabel.setAlignment(Pos.CENTER); // Center text within the label

        // Center the label in the AnchorPane
        AnchorPane.setTopAnchor(dietaryLabel, 0.0);
        AnchorPane.setBottomAnchor(dietaryLabel, 0.0);
        AnchorPane.setLeftAnchor(dietaryLabel, 0.0);
        AnchorPane.setRightAnchor(dietaryLabel, 0.0);

        dietaryAnchorPane.getChildren().add(dietaryLabel);

        // Add all anchor panes to the HBox
        hBox.getChildren().addAll(nameAnchorPane, priceAnchorPane, typeAnchorPane, dietaryAnchorPane);

        // Add the HBox to the ListView
        listView.getItems().add(hBox);
    }













    @FXML
    protected void onSubmitButtonClick(){

    }
    @FXML
    protected void onAddMenuItemButtonClick(){
        addMenuItemToListView(menuitemlist,"Foo Burger","$20.98","MAIN","VEGAN");
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
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_USERS_FXML, GuiCommon.ADMIN_SCREEN_USERS_TITLE, stage);
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
