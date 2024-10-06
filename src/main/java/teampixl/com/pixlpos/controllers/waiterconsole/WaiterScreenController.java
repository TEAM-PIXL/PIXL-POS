package teampixl.com.pixlpos.controllers.waiterconsole;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import teampixl.com.pixlpos.common.GuiCommon;
import teampixl.com.pixlpos.database.api.MenuAPI;
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Order;

import java.util.*;

public class WaiterScreenController extends GuiCommon {
    private static final String ORDER_ID_KEY = "order_id";
    private static final String ORDER_NUMBER_KEY = "order_number";
    private static final String USER_ID_KEY = "user_id";
    private static final String ORDER_STATUS_KEY = "order_status";
    private static final String CREATED_AT_KEY = "created_at";

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
    /* End of menu item buttons */

    private int currentRow = 0;
    private final Map<String, Integer> orderItems = new HashMap<>();
    private Label selectedItem = null;
    private final Stack<Runnable> actionStack = new Stack<>();
    private final Map<String, String> orderNotes = new HashMap<>();
    private final OrderAPI orderAPI = OrderAPI.getInstance();
    private final MenuAPI menuAPI = MenuAPI.getInstance();
    private final UserStack userStack = UserStack.getInstance();
    private Order currentOrder;
    private Integer orderNumber = 0;
    private Double orderTotal = 0.00;

    public WaiterScreenController() {
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
        voiditem.setOnAction(event -> voidSelectedItem());
        itemcorrect.setOnAction(event -> correctLastItem());
        send.setOnAction(event -> sendOrder());
    }

    private void initializeOrder() {
        String userId = userStack.getCurrentUserId();
        currentOrder = orderAPI.initializeOrder();
        if (currentOrder == null) {
            System.out.println("Failed to initialize order");
            return;
        }
        orderNumber = currentOrder.getOrderNumber();
        ordernum.setText(orderNumber.toString());

        Map<MenuItem, Integer> existingItems = orderAPI.getOrderItemsById(currentOrder.getMetadata().metadata().get(ORDER_ID_KEY).toString());
        for (Map.Entry<MenuItem, Integer> entry : existingItems.entrySet()) {
            String menuItemId = menuAPI.keySearch(entry.getKey().getMetadata().metadata().get("itemName").toString());
            orderItems.put(menuItemId, entry.getValue());
            orderNotes.put(menuItemId, "");
        }
        updateOrderSummary();
    }

    private void addItemToOrder(String itemName) {
        String menuItemId = menuAPI.keySearch(itemName);
        if (menuItemId != null) {
            if (orderItems.containsKey(menuItemId)) {
                orderItems.put(menuItemId, orderItems.get(menuItemId) + 1);
            } else {
                orderItems.put(menuItemId, 1);
                orderNotes.put(menuItemId, "");
            }

            actionStack.push(() -> {
                if (orderItems.get(menuItemId) == 1) {
                    orderItems.remove(menuItemId);
                    orderNotes.remove(menuItemId);
                } else {
                    orderItems.put(menuItemId, orderItems.get(menuItemId) - 1);
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
            String menuItemId = entry.getKey();
            int quantity = entry.getValue();
            String note = orderNotes.get(menuItemId);
            MenuItem menuItem = menuAPI.keyTransform(menuItemId);
            String itemName = menuItem.getMetadata().metadata().get("itemName").toString();
            Label itemLabel = new Label("x" + quantity + " " + itemName + (note.isEmpty() ? "" : " - Note: " + note));
            itemLabel.setOnMouseClicked(event -> selectItem(itemLabel));
            orderSummaryGrid.add(itemLabel, 0, currentRow);
            orderTotal += menuItem.getPrice() * quantity;
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
        String itemNameWithQuantity = itemText.substring(itemText.indexOf(" ") + 1);
        String itemName = itemNameWithQuantity.split(" - Note:")[0];
        String menuItemId = menuAPI.keySearch(itemName);
        String note = orderNotes.getOrDefault(menuItemId, "");
        notes.setText(note);
    }

    @FXML
    private void applyNoteToSelectedItem() {
        if (selectedItem != null) {
            String itemText = selectedItem.getText();
            String itemNameWithQuantity = itemText.substring(itemText.indexOf(" ") + 1);
            String itemName = itemNameWithQuantity.split(" - Note:")[0];
            String menuItemId = menuAPI.keySearch(itemName);
            String currentNote = orderNotes.getOrDefault(menuItemId, "");
            String newNote = notes.getText();

            actionStack.push(() -> {
                orderNotes.put(menuItemId, currentNote);
                updateOrderSummary();
            });

            orderNotes.put(menuItemId, newNote);
            updateOrderSummary();
        }
    }

    @FXML
    private void voidSelectedItem() {
        if (selectedItem != null) {
            String itemText = selectedItem.getText();
            String itemNameWithQuantity = itemText.substring(itemText.indexOf(" ") + 1).split(" - Note:")[0];
            String menuItemId = menuAPI.keySearch(itemNameWithQuantity);
            int quantity = orderItems.get(menuItemId);
            orderItems.remove(menuItemId);
            orderNotes.remove(menuItemId);
            actionStack.push(() -> {
                orderItems.put(menuItemId, quantity);
                orderNotes.put(menuItemId, notes.getText());
                updateOrderSummary();
            });
            updateOrderSummary();
        }
    }

    @FXML
    private void restartOrder() {
        Map<String, Integer> currentOrderItems = new HashMap<>(orderItems);
        Map<String, String> currentOrderNotes = new HashMap<>(orderNotes);

        actionStack.push(() -> {
            orderItems.putAll(currentOrderItems);
            orderNotes.putAll(currentOrderNotes);
            updateOrderSummary();
        });

        orderItems.clear();
        orderNotes.clear();
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
        GuiCommon.loadRoot(GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE, logoutButton);
    }

    @FXML
    private void sendOrder() {
        if (orderItems.isEmpty()) {
            System.out.println("No items in order");
            return;
        }

        String orderId = currentOrder.getMetadata().metadata().get(ORDER_ID_KEY).toString();

        orderAPI.clearOrderItems(orderId);

        for (Map.Entry<String, Integer> entry : orderItems.entrySet()) {
            String menuItemId = entry.getKey();
            int quantity = entry.getValue();

            List<StatusCode> statusCodes = orderAPI.putOrderItem(orderId, menuItemId, quantity);

            if (!Exceptions.isSuccessful(statusCodes)) {
                System.err.println("Failed to add item to order: " + menuItemId + " Status: " + statusCodes);
                showAlert(Exceptions.returnStatus("Failed to add item to order:", statusCodes));
                return;
            }
        }

        List<StatusCode> status = orderAPI.putOrderStatus(orderId, Order.OrderStatus.SENT);
        Order ORDER = orderAPI.keyTransform(orderId);
        List<StatusCode> status2 = orderAPI.postOrder(ORDER);
        if (Exceptions.isSuccessful(status2)) {
            System.out.println("Order placed successfully.");
            restartOrder();
            initializeOrder();
        } else {
            showAlert(Exceptions.returnStatus("Order could not be placed with the following errors:", status));
        }
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Order Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

