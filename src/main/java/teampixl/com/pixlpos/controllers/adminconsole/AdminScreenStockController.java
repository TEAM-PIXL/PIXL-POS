package teampixl.com.pixlpos.controllers.adminconsole;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.ListView;
import javafx.animation.AnimationTimer;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.database.api.UsersAPI;
import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.models.Users;

import java.io.IOException;
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
    private final UserStack userStack = UserStack.getInstance();
    Users currentuser = userStack.getCurrentUser();
    String firstName = currentuser.getMetadata().metadata().get("first_name").toString();
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

    int adding_counter = 0;

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
        adding_counter = 0;
        itemlist.getItems().clear();
        greeting.setText("Hello, " + firstName);
    }










    @FXML
    protected void onSubmitButtonClick(){

    }
    @FXML
    protected void onAddItemButtonClick(){


        if(adding_counter == 0){
            addInventoryItemToListView(itemlist,String.valueOf(adding_counter),"patty","20","On_Order","24-10-2024","LOW");
        }
        else if(adding_counter == 1){
            addInventoryItemToListView(itemlist,String.valueOf(adding_counter),"cheese slice","30","Not_on_order","23-10-2024","HIGH");
        }
        else if(adding_counter == 2){
            addInventoryItemToListView(itemlist,String.valueOf(adding_counter),"buns","54","On_order","10-10-2023","MEDIUM");
        }
        else{
            addInventoryItemToListView(itemlist,String.valueOf(adding_counter),"lettuce","10","Not_on_order","10-8-2023","HIGH");
        }
        adding_counter++;
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




    public void addInventoryItemToListView(ListView<HBox> listView, String id, String itemName, String actualQty, String orderstatus, String lastupdated, String stocklvl) {
        try {
            // Load HBox from FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/teampixl/com/pixlpos/fxml/adminconsole/dynamics/stockdynamic.fxml"));
            HBox hbox = loader.load();

            // Set the ID of the HBox
            hbox.setId(id);

            // Get the controller associated with the FXML (if you have one)
            // Optionally, set values via the controller, or directly access the fields
            // Example: If you have IDs set in FXML for the labels, you can access them like this:

            Label namefield = (Label) hbox.lookup("#namefield");
            Label qtyfield = (Label) hbox.lookup("#qtyfield");
            Label orderstatfield = (Label) hbox.lookup("#orderstatfield");
            Label lastupfield = (Label) hbox.lookup("#lastupfield");
            Label stocklvlfield = (Label) hbox.lookup("#stocklvlfield");

            // Set values dynamically
            namefield.setText(itemName);
            qtyfield.setText(actualQty);
            orderstatfield.setText(orderstatus);
            lastupfield.setText(lastupdated);
            stocklvlfield.setText(stocklvl);

            // Set action handlers for buttons (if they exist in your FXML)
            Button editbutton = (Button) hbox.lookup("#editbutton");
            editbutton.setOnAction(event -> onEditButtonClick(event, id));
            Button removebutton = (Button) hbox.lookup("#removebutton");
            removebutton.setOnAction(event -> onRemoveButtonClick(event, id));


            // Add the HBox to the ListView
            listView.getItems().add(hbox);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Adds an item to the specified ListView. The item is represented as an HBox containing four AnchorPanes
     * that display the name, price, type, and dietary information of the item.
     *
     * @param listView the ListView to which the new menu item will be added. Each menu item will be displayed as an HBox.
     * @param itemName the name of the item (e.g., the patty), displayed in the first column of the HBox.
     * @param desiredQty the desired number of stock
     * @param actualQty the actual number of items in stock
     * @param price the price per item reported
     */
//    public void addInventoryItemToListView(ListView<HBox> listView, String id, String itemName, String desiredQty, String actualQty, String price) {
//        HBox hbox = new HBox();
//        hbox.setPrefHeight(50.0);
//        hbox.setPrefWidth(200.0);
//
//        // Item Name
//        AnchorPane itemNamePane = new AnchorPane();
//        Label itemNameLabel = new Label(itemName);
//        itemNameLabel.setAlignment(javafx.geometry.Pos.CENTER);
//        itemNameLabel.setPrefSize(88.0, 50.4);
//        AnchorPane.setTopAnchor(itemNameLabel, 0.0);
//        AnchorPane.setRightAnchor(itemNameLabel, 0.0);
//        AnchorPane.setBottomAnchor(itemNameLabel, 0.0);
//        AnchorPane.setLeftAnchor(itemNameLabel, 0.0);
//        itemNamePane.getChildren().add(itemNameLabel);
//        HBox.setHgrow(itemNamePane, Priority.ALWAYS);
//
//        // Desired Quantity
//        AnchorPane desiredQtyPane = new AnchorPane();
//        Label desiredQtyLabel = new Label(desiredQty);
//        desiredQtyLabel.setAlignment(javafx.geometry.Pos.CENTER);
//        desiredQtyLabel.setPrefSize(127.2, 50.4);
//        AnchorPane.setTopAnchor(desiredQtyLabel, 0.0);
//        AnchorPane.setRightAnchor(desiredQtyLabel, 0.0);
//        AnchorPane.setBottomAnchor(desiredQtyLabel, 0.0);
//        AnchorPane.setLeftAnchor(desiredQtyLabel, 0.0);
//        desiredQtyPane.getChildren().add(desiredQtyLabel);
//        HBox.setHgrow(desiredQtyPane, Priority.ALWAYS);
//
//        // Actual Quantity
//        AnchorPane actualQtyPane = new AnchorPane();
//        Label actualQtyLabel = new Label(actualQty);
//        actualQtyLabel.setAlignment(javafx.geometry.Pos.CENTER);
//        actualQtyLabel.setPrefSize(112.8, 50.4);
//        AnchorPane.setTopAnchor(actualQtyLabel, 0.0);
//        AnchorPane.setRightAnchor(actualQtyLabel, 0.0);
//        AnchorPane.setBottomAnchor(actualQtyLabel, 0.0);
//        AnchorPane.setLeftAnchor(actualQtyLabel, 0.0);
//        actualQtyPane.getChildren().add(actualQtyLabel);
//        HBox.setHgrow(actualQtyPane, Priority.ALWAYS);
//
//        // Price
//        AnchorPane pricePane = new AnchorPane();
//        Label priceLabel = new Label(price);
//        priceLabel.setAlignment(javafx.geometry.Pos.CENTER);
//        priceLabel.setPrefSize(108.8, 50.4);
//        AnchorPane.setTopAnchor(priceLabel, 0.0);
//        AnchorPane.setRightAnchor(priceLabel, 0.0);
//        AnchorPane.setBottomAnchor(priceLabel, 0.0);
//        AnchorPane.setLeftAnchor(priceLabel, 0.0);
//        pricePane.getChildren().add(priceLabel);
//        HBox.setHgrow(pricePane, Priority.ALWAYS);
//
//        // Edit Button
//        AnchorPane editButtonPane = new AnchorPane();
//        editButtonPane.setMaxWidth(100.0);
//        editButtonPane.setMinWidth(100.0);
//        editButtonPane.setPrefWidth(100.0);
//        Button editButton = new Button("Edit");
//        editButton.setId("editbutton");
//        editButton.setLayoutX(25.0);
//        editButton.setLayoutY(9.0);
//        editButton.setMinWidth(50.0);
//        editButton.getStyleClass().add("edit-button");
//        editButton.setOnAction(event -> onEditButtonClick(event,id));
//        editButtonPane.getChildren().add(editButton);
//        HBox.setHgrow(editButtonPane, Priority.ALWAYS);
//
//        // Remove Button
//        AnchorPane removeButtonPane = new AnchorPane();
//        removeButtonPane.setMaxWidth(100.0);
//        removeButtonPane.setMinWidth(100.0);
//        removeButtonPane.setPrefWidth(100.0);
//        Button removeButton = new Button("Remove");
//        removeButton.setId("removebutton");
//        removeButton.setLayoutX(11.0);
//        removeButton.setLayoutY(9.0);
//        removeButton.setMinWidth(50.0);
//        removeButton.getStyleClass().add("remove-button");
//        removeButton.setOnAction(event -> onRemoveButtonClick(event,id));
//        removeButtonPane.getChildren().add(removeButton);
//        HBox.setHgrow(removeButtonPane, Priority.ALWAYS);
//
//        // Add all components to the HBox
//        hbox.getChildren().addAll(itemNamePane, desiredQtyPane, actualQtyPane, pricePane, editButtonPane, removeButtonPane);
//
//        // Add the HBox to the ListView
//        listView.getItems().add(hbox);
//    }

    // Placeholder methods for button actions
    private void onEditButtonClick(javafx.event.ActionEvent event,String id) {
        // Implement edit menu item logic here
    }

    private void onRemoveButtonClick(javafx.event.ActionEvent event,String id) {
        // Implement remove menu item logic here

        ObservableList<HBox> items = itemlist.getItems(); // Get the items of the ListView

        // Loop through the list to find the HBox with the matching ID
        for (int i = 0; i < items.size(); i++) {
            HBox hbox = items.get(i);

            if (id.equals(hbox.getId())) {  // Compare the ID of the HBox
                items.remove(i);  // Remove the HBox at the found index
                break;            // Exit the loop once the HBox is removed
            }
        }
    }
}
