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
import teampixl.com.pixlpos.database.api.MenuAPI;
import teampixl.com.pixlpos.models.MenuItem;
import javafx.animation.AnimationTimer;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.database.api.UsersAPI;
import javafx.scene.layout.Priority;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.models.Users;
import javafx.scene.layout.HBox;
import teampixl.com.pixlpos.database.DataStore;

public class AdminScreenMenuController
{
    /*===================================================================================================================================================================================
    Code Description:
    This class is the controller for the home admin screen of the application.
    ====================================================================================================================================================================================*/
    private final UserStack userStack = UserStack.getInstance();
    Users currentuser = userStack.getCurrentUser();
    String firstName = currentuser.getMetadata().metadata().get("first_name").toString();
    private MenuItem loadedMenuItem;
    private MenuAPI menuAPI;
    private DataStore datastore;
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
        menuitemlist.getItems().clear();
        greeting.setText("Hello, " + firstName);
        menuAPI = MenuAPI.getInstance();
        datastore = DataStore.getInstance();
    }



    @FXML
    protected void onSubmitButtonClick(){
        if (loadedMenuItem == null) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Please select a Menu Item");
            return;
        }
        try{
            String itemName = menuitemnamefield.getText();
            Double price;
            MenuItem.ItemType itemType = itemtypefield.getSelectionModel().getSelectedItem();
            MenuItem.DietaryRequirement dietaryRequirement = dietaryrequirementsfield.getSelectionModel().getSelectedItem();
            String description = itemdescriptionfield.getText();
            if (itemName.isEmpty() || itemType == null || description.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Empty Field", "Item Name, Item Type, Description and Price are required");
            }else {
                try{
                    price = Double.parseDouble(pricefield.getText());
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Please enter a valid price");
                    return;
                }
                if (price < 0){
                    showAlert(Alert.AlertType.ERROR, "Failed", "Price cannot be negative");
                    return;
                }
                try {
                    loadedMenuItem.updateMetadata("itemName", itemName);
                    loadedMenuItem.updateMetadata("price", price);
                    loadedMenuItem.setDataValue("description", description);
                    loadedMenuItem.updateMetadata("itemType", itemType);
                    loadedMenuItem.updateMetadata("dietaryRequirement", dietaryRequirement);
                    datastore.updateMenuItem(loadedMenuItem);
                    showAlert(Alert.AlertType.CONFIRMATION, "Updated Menu Item", "Menu Item has been updated");
                    initialize();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Updated User", "Unexpected error occured while updating Menu Item: " + e.getMessage());
                }
            }
        }
        catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Empty Field", "Unexpected error occured: " + e.getMessage());
        }
    }
    @FXML
    protected void onAddMenuItemButtonClick(){
        try {
            Double price;
            String itemName = menuitemnamefield.getText();
            MenuItem.ItemType itemType = itemtypefield.getSelectionModel().getSelectedItem();
            MenuItem.DietaryRequirement dietaryRequirement = dietaryrequirementsfield.getValue();
            String description = itemdescriptionfield.getText();

            if (itemName.isEmpty() || itemType == null || description.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Empty Field", "Item Name, Item Type, Description and Price are required");
            } else {
                try {
                    price = Double.parseDouble(pricefield.getText());
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Please enter a valid price");
                    return;
                }
                if (price < 0){
                    showAlert(Alert.AlertType.ERROR, "Failed", "Price cannot be negative");
                    return;
                }
                if (datastore.getMenuItem(itemName) == null) {
                    MenuItem newMenuItem = new MenuItem(itemName, price, MenuItem.ItemType.MAIN, true, description, null);
                    datastore.createMenuItem(newMenuItem);
                    if (datastore.getMenuItem(itemName) != null) {
                        initialize();
                        showAlert(Alert.AlertType.CONFIRMATION, "New Menu Item", "New Menu Item has been created");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "New Menu Item", "Menu Item creation failed");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "New Menu Item", "Menu Item already exists");
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "New Menu Item", "Unexpected error occurred: " + e.getMessage());
        }
    }
    @FXML
    protected void onCancelButtonClick(){
        // Handle clear button click
        pricefield.clear();
        menuitemnamefield.clear();
        itemtypefield.setValue(null);
        dietaryrequirementsfield.setValue(null);
        itemdescriptionfield.clear();
        loadedMenuItem = null;
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




    // Placeholder methods for button actions
    private void onEditButtonClick(javafx.event.ActionEvent event,String id) {
        // Implement edit menu item logic here
        try{
            loadedMenuItem = menuAPI.keyTransform(id);
            if (loadedMenuItem == null) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Please select a user from the table");
            } else{
                populateMenuParam(loadedMenuItem);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Unexpected error occured: " + e.getMessage());
        }
    }

    private void onRemoveButtonClick(javafx.event.ActionEvent event,String id) {
        // Implement remove menu item logic here
        ObservableList<HBox> items = menuitemlist.getItems(); // Get the items of the ListView

        // Loop through the list to find the HBox with the matching ID
        for (int i = 0; i < items.size(); i++) {
            HBox hbox = items.get(i);

            if (id.equals(hbox.getId())) {  // Compare the ID of the HBox
                try{
                    items.remove(i);  // Remove the HBox at the found index
                    // Handle delete user button click
                    datastore.deleteMenuItem(menuAPI.getMenuItem(menuAPI.reverseKeySearch(id)));
                    initialize();
                    showAlert(Alert.AlertType.CONFIRMATION, "Deleted Menu Item", "Menu Item has been deleted");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Deleted Menu Item", "Unexpected error occured: " + e.getMessage());
                }
                break;            // Exit the loop once the HBox is removed
            }
        }
    }

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
    public void addMenuItemToListView(ListView<HBox> listView, String id, String name, String price, String type, String dietary) {
        HBox hbox = new HBox();
        hbox.setPrefHeight(50.0);
        hbox.setPrefWidth(200.0);
        hbox.setId(id);

        // Name
        AnchorPane namePane = new AnchorPane();
        Label nameLabel = new Label(name);
        nameLabel.setAlignment(javafx.geometry.Pos.CENTER);
        nameLabel.setPrefSize(111.2, 50.4);
        AnchorPane.setTopAnchor(nameLabel, 0.0);
        AnchorPane.setRightAnchor(nameLabel, 0.0);
        AnchorPane.setBottomAnchor(nameLabel, 0.0);
        AnchorPane.setLeftAnchor(nameLabel, 0.0);
        namePane.getChildren().add(nameLabel);
        HBox.setHgrow(namePane, Priority.ALWAYS);

        // Price
        AnchorPane pricePane = new AnchorPane();
        Label priceLabel = new Label(price);
        priceLabel.setAlignment(javafx.geometry.Pos.CENTER);
        priceLabel.setPrefSize(86.4, 50.4);
        AnchorPane.setTopAnchor(priceLabel, 0.0);
        AnchorPane.setRightAnchor(priceLabel, 0.0);
        AnchorPane.setBottomAnchor(priceLabel, 0.0);
        AnchorPane.setLeftAnchor(priceLabel, 0.0);
        pricePane.getChildren().add(priceLabel);
        HBox.setHgrow(pricePane, Priority.ALWAYS);

        // Type
        AnchorPane typePane = new AnchorPane();
        Label typeLabel = new Label(type);
        typeLabel.setAlignment(javafx.geometry.Pos.CENTER);
        typeLabel.setPrefSize(72.8, 50.4);
        AnchorPane.setTopAnchor(typeLabel, 0.0);
        AnchorPane.setRightAnchor(typeLabel, 0.0);
        AnchorPane.setBottomAnchor(typeLabel, 0.0);
        AnchorPane.setLeftAnchor(typeLabel, 0.0);
        typePane.getChildren().add(typeLabel);
        HBox.setHgrow(typePane, Priority.ALWAYS);

        // Dietary
        AnchorPane dietaryPane = new AnchorPane();
        Label dietaryLabel = new Label(dietary);
        dietaryLabel.setAlignment(javafx.geometry.Pos.CENTER);
        dietaryLabel.setPrefSize(80.0, 50.4);
        AnchorPane.setTopAnchor(dietaryLabel, 0.0);
        AnchorPane.setRightAnchor(dietaryLabel, 0.0);
        AnchorPane.setBottomAnchor(dietaryLabel, 0.0);
        AnchorPane.setLeftAnchor(dietaryLabel, 0.0);
        dietaryPane.getChildren().add(dietaryLabel);
        HBox.setHgrow(dietaryPane, Priority.ALWAYS);

        // Edit Button
        AnchorPane editButtonPane = new AnchorPane();
        editButtonPane.setMaxWidth(100.0);
        editButtonPane.setMinWidth(100.0);
        Button editButton = new Button("Edit");
        editButton.setId("editbutton");
        editButton.setLayoutX(35.0);
        editButton.setLayoutY(12.0);
        editButton.setMinHeight(26.4);
        editButton.setMaxHeight(26.4);
        editButton.setPrefHeight(26.4);
        editButton.setMinWidth(50.0);
        editButton.getStyleClass().add("edit-button");
        editButton.setOnAction(event -> onEditButtonClick(event,id));
        editButtonPane.getChildren().add(editButton);
        HBox.setHgrow(editButtonPane, Priority.ALWAYS);

        // Remove Button
        AnchorPane removeButtonPane = new AnchorPane();
        removeButtonPane.setMaxWidth(100.0);
        removeButtonPane.setMinWidth(100.0);
        Button removeButton = new Button("Remove");
        removeButton.setId("removebutton");
        removeButton.setLayoutX(11.0);
        removeButton.setLayoutY(12.0);
        removeButton.setMinHeight(26.4);
        removeButton.setPrefHeight(26.4);
        removeButton.setMinWidth(50.0);
        removeButton.getStyleClass().add("remove-button");
        removeButton.setOnAction(event -> onRemoveButtonClick(event,id));
        removeButtonPane.getChildren().add(removeButton);
        HBox.setHgrow(removeButtonPane, Priority.ALWAYS);

        // Add all components to the HBox
        hbox.getChildren().addAll(namePane, pricePane, typePane, dietaryPane, editButtonPane, removeButtonPane);

        // Add the HBox to the ListView
        listView.getItems().add(hbox);
    }

    private void populateMenuParam(MenuItem menuItem) {
        Object price = menuItem.getMetadataValue("price");
        Object itemName = menuItem.getMetadataValue("itemName");
        Object itemType = menuItem.getData().get("itemTame");
        pricefield.setText(Double.toString((double) price));
        menuitemnamefield.setText(itemName.toString());
        itemtypefield.setValue(MenuItem.ItemType.valueOf(itemType.toString()));
        try {
            Object dietaryRequirement = menuItem.getMetadataValue("dietaryRequirement");
            dietaryrequirementsfield.setValue(MenuItem.DietaryRequirement.valueOf(dietaryRequirement.toString()));
        } catch (Exception e) {
            System.out.println(("No Dietary Requirement"));
        }
        try {
            Object description = menuItem.getData().get("description");
            itemdescriptionfield.setText(description.toString());
        } catch (Exception e) {
            System.out.println(("No Description"));
        }
        loadedMenuItem = menuItem;
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
