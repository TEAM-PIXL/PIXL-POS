package teampixl.com.pixlpos.controllers.adminconsole;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.IngredientsAPI;
import teampixl.com.pixlpos.database.api.MenuAPI;
import teampixl.com.pixlpos.database.api.StockAPI;
import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.Ingredients;
import teampixl.com.pixlpos.models.Stock;
import teampixl.com.pixlpos.models.Users;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdminScreenStockController {

    private final UserStack userStack = UserStack.getInstance();
    private final Users currentUser = userStack.getCurrentUser();
    private final String firstName = currentUser.getMetadata().metadata().get("first_name").toString();

    private final StockAPI stockAPI = StockAPI.getInstance();
    private final IngredientsAPI ingredientsAPI = IngredientsAPI.getInstance();

    private String loadedStockID;

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
    private ListView<HBox> itemlist;

    private final AnimationTimer datetime = new AnimationTimer() {
        @Override
        public void handle(long now) {
            date.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            time.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }
    };

    @FXML
    public void initialize() {
        datetime.start();
        greeting.setText("Hello, " + firstName);
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
        // Handle settings button click
    }

    @FXML
    protected void onSubmitButtonClick() {
        try {
            String ingredientName = itemnamefield.getText().trim();
            String ingredientDescription = itemdescriptionfield.getText().trim();
            String thresholdQuantityStr = thresholdquantityfield.getText().trim();
            String actualQuantityStr = actualquantityfield.getText().trim();
            String orderStatusStr = orderstatusfield.getText().trim();

            if (ingredientName.isEmpty() || thresholdQuantityStr.isEmpty() || actualQuantityStr.isEmpty() || orderStatusStr.isEmpty()) {
                showAlert(AlertType.ERROR, "Empty Field", "All fields are required");
                return;
            }

            double thresholdQuantity = Double.parseDouble(thresholdQuantityStr);
            double actualQuantity = Double.parseDouble(actualQuantityStr);
            boolean orderStatus = Boolean.parseBoolean(orderStatusStr);

            if (thresholdQuantity < 0 || actualQuantity < 0) {
                showAlert(AlertType.ERROR, "Invalid Input", "Quantities cannot be negative");
                return;
            }

            // Update Ingredient
            List<StatusCode> statusCodes = new ArrayList<>();
            statusCodes.addAll(ingredientsAPI.putIngredientName(ingredientsAPI.reverseKeySearch(loadedStockID), ingredientName));
            statusCodes.addAll(ingredientsAPI.putIngredientNotes(ingredientName, ingredientDescription));

            // Update Stock
            String ingredientID = ingredientsAPI.keySearch(ingredientName);
            statusCodes.addAll(stockAPI.putStockOnOrder(ingredientID, orderStatus));
            statusCodes.addAll(stockAPI.putStockNumeral(ingredientID, actualQuantity));
            statusCodes.addAll(stockAPI.putStockLowStockThreshold(ingredientID, thresholdQuantity));

            if (Exceptions.isSuccessful(statusCodes)) {
                showAlert(AlertType.CONFIRMATION, "Stock Item Updated", "Stock item has been updated successfully");
                populateStockGrid();
                clearInputFields();
            } else {
                showAlert(AlertType.ERROR, "Update Failed", "Failed to update stock item: " + statusCodes);
            }

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Input", "Please enter valid numbers for quantities");
        }
    }

    @FXML
    protected void onAddItemButtonClick() {
        try {
            String ingredientName = itemnamefield.getText().trim();
            String ingredientDescription = itemdescriptionfield.getText().trim();
            String thresholdQuantityStr = thresholdquantityfield.getText().trim();
            String actualQuantityStr = actualquantityfield.getText().trim();
            String orderStatusStr = orderstatusfield.getText().trim();

            if (ingredientName.isEmpty() || thresholdQuantityStr.isEmpty() || actualQuantityStr.isEmpty() || orderStatusStr.isEmpty()) {
                showAlert(AlertType.ERROR, "Empty Field", "All fields are required");
                return;
            }

            double thresholdQuantity = Double.parseDouble(thresholdQuantityStr);
            double actualQuantity = Double.parseDouble(actualQuantityStr);
            boolean orderStatus = Boolean.parseBoolean(orderStatusStr);

            if (thresholdQuantity < 0 || actualQuantity < 0) {
                showAlert(AlertType.ERROR, "Invalid Input", "Quantities cannot be negative");
                return;
            }

            // Create Ingredient
            List<StatusCode> statusCodes = new ArrayList<>();
            statusCodes.addAll(ingredientsAPI.postIngredient(ingredientName, ingredientDescription));

            // Create Stock
            String ingredientID = ingredientsAPI.keySearch(ingredientName);
            statusCodes.addAll(stockAPI.postStock(ingredientID, Stock.StockStatus.INSTOCK, Stock.UnitType.KG, actualQuantity, orderStatus));
            statusCodes.addAll(stockAPI.putStockLowStockThreshold(ingredientID, thresholdQuantity));

            if (Exceptions.isSuccessful(statusCodes)) {
                showAlert(AlertType.CONFIRMATION, "Stock Item Created", "New stock item has been created successfully");
                populateStockGrid();
                clearInputFields();
            } else {
                showAlert(AlertType.ERROR, "Creation Failed", "Failed to create stock item: " + statusCodes);
            }

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Input", "Please enter valid numbers for quantities");
        }
    }

    @FXML
    protected void onCancelButtonClick() {
        clearInputFields();
    }

    private void clearInputFields() {
        itemnamefield.clear();
        thresholdquantityfield.clear();
        actualquantityfield.clear();
        orderstatusfield.clear();
        itemdescriptionfield.clear();
    }

    @FXML
    protected void onRemoveButtonClick(String id) {
        try {
            List<StatusCode> statusCodes = new ArrayList<>();
            statusCodes.addAll(stockAPI.deleteStock(id));
            statusCodes.addAll(ingredientsAPI.deleteIngredient(ingredientsAPI.reverseKeySearch(id)));

            if (Exceptions.isSuccessful(statusCodes)) {
                showAlert(AlertType.CONFIRMATION, "Stock Item Removed", "Stock item has been removed successfully");
                populateStockGrid();
            } else {
                showAlert(AlertType.ERROR, "Removal Failed", "Failed to remove stock item: " + statusCodes);
            }

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "An error occurred while removing the stock item");
        }
    }

    @FXML
    protected void onUsersButtonClick() {
        Stage stage = (Stage) usersbutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_USERS_FXML, GuiCommon.ADMIN_SCREEN_USERS_TITLE, stage);
    }

    @FXML
    protected void onMenuButtonClick() {
        Stage stage = (Stage) menubutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_MENU_FXML, GuiCommon.ADMIN_SCREEN_MENU_TITLE, stage);
    }

    @FXML
    protected void onHomeButtonClick() {
        Stage stage = (Stage) homebutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_HOME_FXML, GuiCommon.ADMIN_SCREEN_HOME_TITLE, stage);
    }

    @FXML
    protected void onStockButtonClick() {
        Stage stage = (Stage) stockbutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_STOCK_FXML, GuiCommon.ADMIN_SCREEN_STOCK_TITLE, stage);
    }

    @FXML
    protected void onAnalyticsButtonClick() {
        Stage stage = (Stage) analyticsbutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.ADMIN_SCREEN_ANALYTICS_FXML, GuiCommon.ADMIN_SCREEN_ANALYTICS_TITLE, stage);
    }

    @FXML
    protected void onLogoutButtonClick() {
        GuiCommon.logout(logoutbutton);
    }

    public void addInventoryItemToListView(ListView<HBox> listView, String id, String itemName, String actualQty, String orderStatus, String lastUpdated, String stockLevel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/teampixl/com/pixlpos/fxml/adminconsole/dynamics/stockdynamic.fxml"));
            HBox hbox = loader.load();

            hbox.setId(id);

            Label nameField = (Label) hbox.lookup("#namefield");
            Label qtyField = (Label) hbox.lookup("#qtyfield");
            Label orderStatField = (Label) hbox.lookup("#orderstatfield");
            Label lastUpField = (Label) hbox.lookup("#lastupfield");
            Label stockLvlField = (Label) hbox.lookup("#stocklvlfield");

            nameField.setText(itemName);
            qtyField.setText(actualQty);
            lastUpField.setText(lastUpdated);

            // Set stock level text and style
            switch (stockLevel) {
                case "INSTOCK" -> {
                    stockLvlField.setText("In Stock");
                    stockLvlField.getStyleClass().add("stockalert-level-in");
                }
                case "LOWSTOCK" -> {
                    stockLvlField.setText("Low Stock");
                    stockLvlField.getStyleClass().add("stockalert-level-low");
                }
                case "NOSTOCK" -> {
                    stockLvlField.setText("No Stock");
                    stockLvlField.getStyleClass().add("stockalert-level-no");
                }
                default -> stockLvlField.setText("Unknown");
            }

            // Set order status text and style
            if (orderStatus.equalsIgnoreCase("true")) {
                orderStatField.setText("ON-ORDER");
                orderStatField.getStyleClass().add("stockalert-status-on");
            } else {
                orderStatField.setText("NOT-ORDERED");
                orderStatField.getStyleClass().add("stockalert-status-not");
            }

            // Set action handlers for buttons
            Button editButton = (Button) hbox.lookup("#editbutton");
            editButton.setOnAction(event -> onEditButtonClick(id));
            Button removeButton = (Button) hbox.lookup("#removebutton");
            removeButton.setOnAction(event -> onRemoveButtonClick(id));

            editButton.setTooltip(new Tooltip("Edit Item"));
            removeButton.setTooltip(new Tooltip("Remove Item"));

            listView.getItems().add(hbox);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateStockGrid() {
        itemlist.getItems().clear();

        List<Stock> stockItems = stockAPI.getStock();
        for (Stock stock : stockItems) {
            String ingredientID = stock.getMetadataValue("ingredient_id").toString();
            String ingredientName = ingredientsAPI.reverseKeySearch(ingredientID);
            String actualQuantity = stock.getDataValue("numeral").toString();
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
        Ingredients ingredient = ingredientsAPI.keyTransform(id);

        itemnamefield.setText(ingredient.getMetadataValue("itemName").toString());
        thresholdquantityfield.setText(stock.getDataValue("low_stock_threshold").toString());
        actualquantityfield.setText(stock.getDataValue("numeral").toString());
        orderstatusfield.setText(stock.getMetadataValue("onOrder").toString());
        itemdescriptionfield.setText(ingredient.getDataValue("notes").toString());
    }

    private void showAlert(AlertType alertType, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    private void onEditButtonClick(String id) {
        loadedStockID = id;
        populateInputFields(id);
    }

    private void searchStock() {
        String searchQuery = searchbar.getText().trim();
        if (searchQuery.isEmpty()) {
            showAlert(AlertType.ERROR, "Search Error", "Please enter a term to search");
            return;
        }

        List<Ingredients> ingredientsList = ingredientsAPI.searchIngredients(searchQuery);
        if (ingredientsList.isEmpty()) {
            showAlert(AlertType.INFORMATION, "No Results", "No stock items found matching: " + searchQuery);
        } else if (ingredientsList.size() > 1) {
            showAlert(AlertType.INFORMATION, "Multiple Results", "Multiple items found, please refine your search");
        } else {
            String ingredientID = ingredientsList.get(0).getMetadataValue("ingredient_id").toString();
            populateInputFields(ingredientID);
        }
    }
}
