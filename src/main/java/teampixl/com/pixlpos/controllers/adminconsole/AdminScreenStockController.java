package teampixl.com.pixlpos.controllers.adminconsole;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.control.ListView;
import javafx.animation.AnimationTimer;
import teampixl.com.pixlpos.database.api.IngredientsAPI;
import teampixl.com.pixlpos.database.api.MenuAPI;
import teampixl.com.pixlpos.database.api.StockAPI;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.Ingredients;
import teampixl.com.pixlpos.models.Stock;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.UserStack;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class AdminScreenStockController
{
    /*===================================================================================================================================================================================
    Code Description:
    This class is the controller for the home admin screen of the application.
    ====================================================================================================================================================================================*/
    private final UserStack userStack = UserStack.getInstance();
    Users currentuser = userStack.getCurrentUser();
    String firstName = currentuser.getMetadata().metadata().get("first_name").toString();
    private StockAPI stockAPI;
    private DataStore dataStore;
    private MenuAPI menuAPI;
    private IngredientsAPI ingredientsAPI;
    private String loadedStockID;
    /*
    Shared Components
     */

    @FXML
    private Label greeting;
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
    private TextField thresholdquantityfield;
    @FXML
    private TextField actualquantityfield;
    @FXML
    private TextField orderstatusfield;
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
            time.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }
    };

    @FXML
    public void initialize() {
        datetime.start();
        adding_counter = 0;
        itemlist.getItems().clear();
        greeting.setText("Hello, " + firstName);
        stockAPI = StockAPI.getInstance();
        menuAPI = MenuAPI.getInstance();
        dataStore = DataStore.getInstance();
        ingredientsAPI = IngredientsAPI.getInstance();
        populateStockGrid();

        searchbar.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchStock();
            }
        });

        searchbar.setOnAction(event -> searchStock());
    }


    @FXML
    protected void onSettingsButtonClick() {
        // Handle exit button click
    }
    @FXML
    protected void onSubmitButtonClick(){
        try {
            Double thresholdQuantity = Double.parseDouble(thresholdquantityfield.getText());
            Double actualQuantity = Double.parseDouble(actualquantityfield.getText());
            Boolean orderStatus = orderstatusfield.getText().equals("true");
            String ingredientName = itemnamefield.getText();
            String ingredientDescription = itemdescriptionfield.getText();

            if (thresholdQuantity < 0 || actualQuantity < 0) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Price and Quantities cannot be negative");
                return;
            }

            if (ingredientName.isEmpty() || orderStatus == null || thresholdQuantity == null || actualQuantity == null) {
                showAlert(Alert.AlertType.ERROR, "EmptyField", "Item Name, Item Price, Desired Quantity and Actual Quantity are required");
            } else {
                List<StatusCode> statusCodes = ingredientsAPI.putIngredientName(ingredientsAPI.reverseKeySearch(loadedStockID), ingredientName);
                initialize();
                String IngredientID = ingredientsAPI.keySearch(ingredientName);
                List<StatusCode> statusCodesNotes = ingredientsAPI.putIngredientNotes(ingredientName, ingredientDescription);
                statusCodes.addAll(statusCodesNotes);
                initialize();
                List<StatusCode> statusCodesStock = stockAPI.putStockOnOrder(IngredientID, orderStatus);
                statusCodes.addAll(statusCodesStock);
                initialize();
                List<StatusCode> statusCodesNumeral = stockAPI.putStockNumeral(IngredientID, actualQuantity);
                statusCodes.addAll(statusCodesNumeral);
                initialize();
//                List<StatusCode> statusCodesThreshold = stockAPI.putStockLowStockThreshold(IngredientID, thresholdQuantity);
//                statusCodes.addAll(statusCodesThreshold);
                initialize();
                if (Exceptions.isSuccessful(statusCodes)) {
                    initialize();
                    showAlert(Alert.AlertType.CONFIRMATION, "New Stock Item", "Stock Item has been updated");
                } else {
                    showAlert(Alert.AlertType.ERROR, "New Stock Item", "Stock Item update failed with the error codes: " + statusCodes);
                }
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Please enter valid numerals");
            return;
        }
    }
    @FXML
    protected void onAddItemButtonClick(){
        try {
            Double thresholdQuantity = Double.parseDouble(thresholdquantityfield.getText());
            Double actualQuantity = Double.parseDouble(actualquantityfield.getText());
            Boolean orderStatus = orderstatusfield.getText().equals("true");
            String ingredientName = itemnamefield.getText();
            String ingredientDescription = itemdescriptionfield.getText();

            if (thresholdQuantity < 0 || actualQuantity < 0) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Price and Quantities cannot be negative");
                return;
            }

            if (ingredientName.isEmpty() || orderStatus == null || thresholdQuantity == null || actualQuantity == null) {
                showAlert(Alert.AlertType.ERROR, "EmptyField", "Item Name, Item Price, Desired Quantity and Actual Quantity are required");
            } else {
                List<StatusCode> statusCodes = ingredientsAPI.postIngredient(ingredientName, ingredientDescription);
                String IngredientID = ingredientsAPI.keySearch(ingredientName);
                initialize();
                List<StatusCode> statusCodesStock = stockAPI.postStock(IngredientID, Stock.StockStatus.INSTOCK, Stock.UnitType.KG, actualQuantity, orderStatus);
                statusCodes.addAll(statusCodesStock);
                initialize();
//                List<StatusCode> statusCodesThreshold = stockAPI.putStockLowStockThreshold(IngredientID, thresholdQuantity);
//                statusCodes.addAll(statusCodesThreshold);
                initialize();
                if (Exceptions.isSuccessful(statusCodes)) {
                    initialize();
                    showAlert(Alert.AlertType.CONFIRMATION, "New Stock Item", "New Stock Item has been created");
                } else {
                    showAlert(Alert.AlertType.ERROR, "New Stock Item", "Stock Item creation failed with the error codes: " + statusCodes);
                }
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Please enter valid numerals");
            return;
        }
        adding_counter++;
    }

    @FXML
    protected void onCancelButtonClick(){
        itemnamefield.clear();
        thresholdquantityfield.clear();
        actualquantityfield.clear();
        orderstatusfield.clear();
        itemdescriptionfield.clear();
    }

    @FXML
    protected void onRemoveButtonClick(javafx.event.ActionEvent event, String id){
        ObservableList<HBox> items = itemlist.getItems(); // Get the items of the ListView

        for (int i = 0; i < items.size(); i++) {
            HBox hbox = items.get(i);

            if (id.equals(hbox.getId())) {
                try {
                    items.remove(i);
                    stockAPI.deleteStock(id);
                    initialize();
                    showAlert(Alert.AlertType.CONFIRMATION, "Stock Item", "Stock Item has been removed");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Stock Item", "Stock Item removal failed");
                }
                break;
            }
        }
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
        GuiCommon.logout(logoutbutton);
    }

    /**
     * Adds an item to the specified ListView. The item is represented as an HBox containing four AnchorPanes
     * that display the name, price, type, and dietary information of the item.
     *
     * @param listView the ListView to which the new menu item will be added. Each menu item will be displayed as an HBox.
     * @param itemName the name of the item (e.g., the patty), displayed in the first column of the HBox.
     * @param lastupdated the last time the item was updated
     * @param actualQty the actual number of items in stock
     * @param stocklvl the stock level of the item
     * @param orderstatus the order status of the item
     */
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

            if(Objects.equals(stocklvl, "INSTOCK")) {
                stocklvlfield.setText("In Stock");
                stocklvlfield.getStyleClass().add("stockalert-level-in");
            } else if(Objects.equals(stocklvl, "LOWSTOCK")) {
                stocklvlfield.setText("Low Stock");
                stocklvlfield.getStyleClass().add("stockalert-level-low");
            } else if(Objects.equals(stocklvl, "NOSTOCK")) {
                stocklvlfield.setText("No Stock");
                stocklvlfield.getStyleClass().add("stockalert-level-no");

            }

            if(Objects.equals(orderstatus, "true")) {
                orderstatfield.setText("ON-ORDER");
                orderstatfield.getStyleClass().add("stockalert-status-on");
            } else {
                orderstatfield.setText("NOT-ORDERED");
                orderstatfield.getStyleClass().add("stockalert-status-not");
            }

            // Set action handlers for buttons (if they exist in your FXML)
            Button editbutton = (Button) hbox.lookup("#editbutton");
            editbutton.setOnAction(event -> onEditButtonClick(event, id));
            Button removebutton = (Button) hbox.lookup("#removebutton");
            removebutton.setOnAction(event -> onRemoveButtonClick(event, id));
            Tooltip tooltip = new Tooltip("Edit Item");
            Tooltip tooltip2 = new Tooltip("Remove Item");
            editbutton.setTooltip(tooltip);
            removebutton.setTooltip(tooltip2);

            // Add the HBox to the ListView
            listView.getItems().add(hbox);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateStockGrid() {
        ObservableList<Stock> listOfStockItems = dataStore.readStock();
        for (Stock stock : listOfStockItems) {
            String actualQuantity = stock.getDataValue("numeral").toString();
            String ingredientID = stock.getMetadataValue("ingredient_id").toString();
            String ingredientName = ingredientsAPI.reverseKeySearch(ingredientID);
            String orderStatus = stock.getMetadataValue("onOrder").toString();

            String lastUpdated = stock.getMetadataValue("lastUpdated").toString().substring(0, 10);
            String stockLevel = stock.getMetadataValue("stockStatus").toString();
            addInventoryItemToListView(
                    itemlist,
                    ingredientID,
                    ingredientName,
                    actualQuantity,
                    orderStatus,
                    lastUpdated,
                    stockLevel
            );
        }
    }

    private void populateInputFields(String id) {
        Stock stock = stockAPI.getStockByIngredientID(id);
        Ingredients ingredient = ingredientsAPI.getIngredient(ingredientsAPI.reverseKeySearch(id));
        itemnamefield.setText(ingredientsAPI.reverseKeySearch(stock.getMetadataValue("ingredient_id").toString()));
        thresholdquantityfield.setText(stock.getDataValue("low_stock_threshold").toString());
        actualquantityfield.setText(stock.getDataValue("numeral").toString());
        orderstatusfield.setText(stock.getMetadataValue("onOrder").toString());
        itemdescriptionfield.setText(ingredient.getDataValue("notes").toString());
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Placeholder methods for button actions
    private void onEditButtonClick(javafx.event.ActionEvent event,String id) {
        loadedStockID = id;
        populateInputFields(id);
    }

    private void searchStock() {
        String searchQuery = searchbar.getText();
        if (searchQuery.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Please specify a term you want to query");
        } else {
            List<Ingredients> ingredientsList = ingredientsAPI.searchIngredients(searchQuery);
            if (ingredientsList.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Failed", "No results found for the search term: " + searchQuery);
            } else {
                if (ingredientsList.size() > 1) {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Multiple results found for the search term: " + searchQuery);
                } else {
                    populateInputFields(ingredientsList.get(0).getMetadataValue("ingredient_id").toString());
                }
            }
        }
    }
}
