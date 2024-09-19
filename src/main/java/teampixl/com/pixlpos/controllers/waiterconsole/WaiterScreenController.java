package teampixl.com.pixlpos.controllers.waiterconsole;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import teampixl.com.pixlpos.common.GuiCommon;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.database.api.UsersAPI;
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.database.api.util.Exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class WaiterScreenController extends GuiCommon {
    /*===================================================================================================================================================================================
    Code Description:
    This class is the controller for the waiter screen of the application. It handles creating menu orders which can be sent to the cook screen.
    ====================================================================================================================================================================================*/

    @FXML
    private TextField notes;
    @FXML
    private Button applynotes;
    @FXML
    private Button send;
    @FXML
    private Button restart;
    @FXML
    private Button voiditem;
    @FXML
    private Button itemcorrect;
    @FXML
    private Button logoutButton;
    @FXML
    private Text timeordered;
    @FXML
    private Text tablenum;
    @FXML
    private Text totalprice;
    @FXML
    private Text ordernum;

    @FXML
    private GridPane orderSummaryGrid;

    /* These buttons are hardcoded and connected to database items for the prototype*/
    /* Buttons for the different menu items */
    @FXML
    private Button classic;
    @FXML
    private Button bbqbacon;
    @FXML
    private Button mushroomswiss;
    @FXML
    private Button spicy;
    @FXML
    private Button hawaiian;
    @FXML
    private Button veggie;
    @FXML
    private Button beyond;
    @FXML
    private Button mediterranean;
    @FXML
    private Button teriyaki;
    @FXML
    private Button breakfast;
    @FXML
    private Button coke;
    @FXML
    private Button fanta;
    @FXML
    private Button sprite;
    @FXML
    private Button icedtea;
    @FXML
    private Button icedcoffee;
    /* End of hardcoded buttons */

    private int currentRow = 0;
    private Map<String, Integer> orderItems = new HashMap<>();
    private Label selectedItem = null;
    private Stack<Runnable> actionStack = new Stack<>();
    private Map<String, String> orderNotes = new HashMap<>();
    private DataStore dataStore;
    private MenuItem menuItem;
    private UserStack userStack;
    private OrderAPI orderAPI;
    private Integer orderNumber = 0;
    private Double orderTotal = 0.00;

    public WaiterScreenController() {
        this.dataStore = DataStore.getInstance();
        this.orderAPI = OrderAPI.getInstance();
        this.userStack = UserStack.getInstance();
    }

    @FXML
    private void initialize() {
        initializeOrder();
        totalprice.setText("$" + String.format("%.2f", orderTotal));
        classic.setOnAction(event -> addItemToOrder("Classic Cheeseburger"));
        bbqbacon.setOnAction(event -> addItemToOrder("BBQ Bacon Cheeseburger"));
        mushroomswiss.setOnAction(event -> addItemToOrder("Mushroom Swiss Burger"));
        spicy.setOnAction(event -> addItemToOrder("Spicy Jalapeño Burger"));
        hawaiian.setOnAction(event -> addItemToOrder("Hawaiian Pineapple Burger"));
        veggie.setOnAction(event -> addItemToOrder("Veggie Bean Burger"));
        beyond.setOnAction(event -> addItemToOrder("Beyond Burger"));
        mediterranean.setOnAction(event -> addItemToOrder("Mediterranean Falafel Burger"));
        teriyaki.setOnAction(event -> addItemToOrder("Teriyaki Salmon Burger"));
        breakfast.setOnAction(event -> addItemToOrder("Breakfast Burger"));
        coke.setOnAction(event -> addItemToOrder("Coke"));
        fanta.setOnAction(event -> addItemToOrder("Fanta"));
        sprite.setOnAction(event -> addItemToOrder("Sprite"));
        icedtea.setOnAction(event -> addItemToOrder("Iced Tea"));
        icedcoffee.setOnAction(event -> addItemToOrder("Iced Coffee"));

        restart.setOnAction(event -> restartOrder());
        applynotes.setOnAction(event -> applyNoteToSelectedItem());
        logoutButton.setOnAction(event -> onLogoutButtonClick());
    }

    private void initializeOrder() {
        Order ORDER = orderAPI.initializeOrder();
        if (ORDER == null) {
            System.out.println("Failed to initialize order");
            return;
        }
        orderNumber = ORDER.getMetadata().metadata().get("order_number") != null ? (Integer) ORDER.getMetadata().metadata().get("order_number") : 0;
        ordernum.setText(orderNumber.toString());
    }

    private void addItemToOrder(String itemName) {
        String itemNameID = (String)dataStore.getMenuItem(itemName).getMetadata().metadata().get("id");
        if (itemNameID != null) {
            if (orderItems.containsKey(itemNameID)) {
                orderItems.put(itemNameID, orderItems.get(itemNameID) + 1);
            } else {
                orderItems.put(itemNameID, 1);
                orderNotes.put(itemNameID, "");
            }

            actionStack.push(() -> {
                if (orderItems.get(itemNameID) == 1) {
                    orderItems.remove(itemNameID);
                    orderNotes.remove(itemNameID);
                } else {
                    orderItems.put(itemNameID, orderItems.get(itemNameID) - 1);
                }
                updateOrderSummary();
            });
            updateOrderSummary();
        }
    }

    private void updateOrderSummary() {
        orderSummaryGrid.getChildren().clear();
        currentRow = 0;
        orderTotal = 0.00;
        for (Map.Entry<String, Integer> entry : orderItems.entrySet()) {
            String itemNameID = entry.getKey();
            int quantity = entry.getValue();
            String note = orderNotes.get(itemNameID);
            String itemName = dataStore.getMenuItemById(itemNameID).getMetadata().metadata().get("itemName").toString();
            Label itemLabel = new Label("x" + quantity + " " + itemName + (note.isEmpty() ? "" : " - Note: " + note));
            itemLabel.setOnMouseClicked(event -> selectItem(itemLabel));
            orderSummaryGrid.add(itemLabel, 0, currentRow);
            orderTotal += (Double)dataStore.getMenuItemById(itemNameID).getMetadata().metadata().get("price") * quantity;
            currentRow++;
        }
        totalprice.setText("$" + String.format("%.2f", orderTotal));
    }

    private void selectItem(Label itemLabel) {
        if (selectedItem != null) {
            selectedItem.setStyle("");
        }
        selectedItem = itemLabel;
        selectedItem.setStyle("-fx-background-color: lightblue;");

        String itemText = selectedItem.getText();
        String itemName = itemText.substring(itemText.indexOf(" ") + 1).split(" - Note:")[0];
        String note = orderNotes.getOrDefault(itemName, "");
        notes.setText(note);
    }

    @FXML
    private void applyNoteToSelectedItem() {
        if (selectedItem != null) {
            String itemText = selectedItem.getText();
            String itemName = itemText.substring(itemText.indexOf(" ") + 1).split(" - Note:")[0];
            String itemNameID = (String)dataStore.getMenuItem(itemName).getMetadata().metadata().get("id");
            String currentNote = orderNotes.getOrDefault(itemNameID, "");
            String newNote = notes.getText();

            actionStack.push(() -> {
                orderNotes.put(itemNameID, currentNote);
                updateOrderSummary();
            });

            orderNotes.put(itemNameID, newNote);
            updateOrderSummary();
        }
    }

    @FXML
    private void voidSelectedItem() {
        if (selectedItem != null) {
            String itemText = selectedItem.getText();
            String itemName = itemText.substring(itemText.indexOf(" ") + 1);
            String itemNameID = (String)dataStore.getMenuItem(itemName).getMetadata().metadata().get("id");
            int quantity = orderItems.get(itemNameID);
            orderItems.remove(itemNameID);
            actionStack.push(() -> {
                orderItems.put(itemNameID, quantity);
                updateOrderSummary();
            });
            updateOrderSummary();
        }
    }

    @FXML
    private void restartOrder() {
        Map<String, Integer> currentOrderItems = new HashMap<>(orderItems);
        actionStack.push(() -> {
            orderItems.clear();
            orderItems.putAll(currentOrderItems);
            updateOrderSummary();
        });

        orderItems.clear();
        String ORDER_ID = orderAPI.getOrderByNumber(orderNumber);
        Order ORDER = orderAPI.getOrderById(ORDER_ID);
        ORDER.getData().clear();
        ORDER.setDataValue("total", 0.00);
        orderSummaryGrid.getChildren().clear();
        currentRow = 0;
        orderTotal = 0.00;
        updateOrderSummary();
    }

    @FXML
    private void correctLastItem() {
        if (!actionStack.isEmpty()) {
            actionStack.pop().run();
        }
    }

    @FXML
    private void onLogoutButtonClick() {
        GuiCommon.loadScene(GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE, logoutButton);
    }

    @FXML
    private void sendOrder() {
        if (orderItems.isEmpty()) {
            System.out.println("No items in order");
            return;
        }

        String ORDER_ID = orderAPI.getOrderByNumber(orderNumber);
        Order ORDER;

        if (ORDER_ID == null) {
            ORDER = orderAPI.initializeOrder();
            orderNumber = ORDER.getMetadata().metadata().get("order_number") != null ? (Integer) ORDER.getMetadata().metadata().get("order_number") : 0;
            ordernum.setText(String.valueOf(orderNumber));
        } else {
            ORDER = orderAPI.getOrderById(ORDER_ID);
        }

        for (Map.Entry<String, Integer> entry : orderItems.entrySet()) {
            String menuItemID = entry.getKey();
            MenuItem menuItem = dataStore.getMenuItemById(menuItemID);
            if (menuItem != null) {
                String ITEM_NAME = menuItem.getMetadata().metadata().get("itemName").toString();
                int QUANTITY = entry.getValue();
                orderAPI.putOrderByItem(orderNumber, ITEM_NAME, QUANTITY);
            } else {
                System.err.println("Menu item not found for ID: " + menuItemID);
            }
        }

        List<StatusCode> STATUS = orderAPI.postOrder(ORDER);
        if (Exceptions.isSuccessful(STATUS)) {
            System.out.println("Order placed successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Order Creation Failed", Exceptions.returnStatus("Order could not be placed with the following errors:", STATUS));
        }
        restartOrder();
        initializeOrder();
    }
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
