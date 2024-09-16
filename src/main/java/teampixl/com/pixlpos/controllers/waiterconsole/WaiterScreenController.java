package teampixl.com.pixlpos.controllers.waiterconsole;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import teampixl.com.pixlpos.common.GuiCommon;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.userapi.UserStack;
import teampixl.com.pixlpos.database.api.userapi.UsersAPI;

import java.util.HashMap;
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
    private Integer orderNumber = 1;
    private Double orderTotal = 0.00;

    public WaiterScreenController() {
        this.dataStore = DataStore.getInstance();
        this.userStack = UserStack.getInstance();
    }

    private void saveOrder(Order order) {
        try {
            dataStore.addOrder(order);
            System.out.println("Order saved to database");
            System.out.println("Order items:");
            Map<String, Integer> testOrder = (Map<String, Integer>) order.getData().get("menuItems");
            testOrder.forEach((key, value) -> System.out.println(dataStore.getMenuItemById(key).getMetadata().metadata().get("itemName") + " : " + value));
            System.out.println("Order total: $" + order.getData().get("total"));
        } catch (Exception e) {
            System.out.println("Error saving order to database");
        }
    }

    @FXML
    private void initialize() {
        // Set the order number
        ordernum.setText(orderNumber.toString());
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

    private void addItemToOrder(String itemName) {
        String itemNameID = (String)dataStore.getMenuItem(itemName).getMetadata().metadata().get("id");
        if (itemNameID != null) {
            if (orderItems.containsKey(itemNameID)) {
                orderItems.put(itemNameID, orderItems.get(itemNameID) + 1);
            } else {
                orderItems.put(itemNameID, 1);
                orderNotes.put(itemNameID, ""); // Initialize note for new item
            }

            actionStack.push(() -> {
                if (orderItems.get(itemNameID) == 1) {
                    orderItems.remove(itemNameID);
                    orderNotes.remove(itemNameID); // Remove note for removed item
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
        orderTotal = 0.00; // Reset total before recalculation
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
            selectedItem.setStyle(""); // Reset previous selection style
        }
        selectedItem = itemLabel;
        selectedItem.setStyle("-fx-background-color: lightblue;"); // Highlight selected item

        // Extract item name and display the associated note in the TextField
        String itemText = selectedItem.getText();
        String itemName = itemText.substring(itemText.indexOf(" ") + 1).split(" - Note:")[0]; // Extract item name correctly
        String note = orderNotes.getOrDefault(itemName, ""); // Get the note or an empty string if no note exists
        notes.setText(note);
    }

    @FXML
    private void applyNoteToSelectedItem() {
        if (selectedItem != null) {
            String itemText = selectedItem.getText();
            String itemName = itemText.substring(itemText.indexOf(" ") + 1).split(" - Note:")[0]; // Extract item name correctly
            String itemNameID = (String)dataStore.getMenuItem(itemName).getMetadata().metadata().get("id");
            String currentNote = orderNotes.getOrDefault(itemNameID, ""); // Get the current note or an empty string if no note exists
            String newNote = notes.getText();

            // Save the current state before updating the note
            actionStack.push(() -> {
                orderNotes.put(itemNameID, currentNote); // Restore the previous note
                updateOrderSummary();
            });

            orderNotes.put(itemNameID, newNote); // Update the note in the map
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
        // Save the current state before clearing
        Map<String, Integer> currentOrderItems = new HashMap<>(orderItems);
        actionStack.push(() -> {
            orderItems.clear();
            orderItems.putAll(currentOrderItems);
            updateOrderSummary();
        });

        // Clear the order
        orderItems.clear();
        orderSummaryGrid.getChildren().clear();
        currentRow = 0;
        orderTotal = 0.00;
        totalprice.setText("$" + String.format("%.2f", orderTotal));
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
        } else {
            String userID = userStack.getCurrentUserId();
            Order order = new Order(orderNumber, userID);
            orderNumber++;
            ordernum.setText(orderNumber.toString());
            for (Map.Entry<String, Integer> entry : orderItems.entrySet()) {
                menuItem = dataStore.getMenuItemById(entry.getKey());
                order.addMenuItem(menuItem, entry.getValue());
            }
            saveOrder(order);
            restartOrder();
        }
    }
}
