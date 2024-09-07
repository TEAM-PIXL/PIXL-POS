package teampixl.com.pixlpos.controllers.waiterconsole;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import teampixl.com.pixlpos.constructs.Users;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.database.DataStore;

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
    private Button logout;
    @FXML
    private Text timeordered;
    @FXML
    private Text tablenum;
    @FXML
    private Text timedue;
    @FXML
    private Text ordernum;

    @FXML
    private GridPane orderSummaryGrid;

    /* These buttons are hardcoded and connected to database items for the prototype*/
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

    private int currentRow = 0;
    private Map<String, Integer> orderItems = new HashMap<>();
    private Label selectedItem = null;
    private Stack<Runnable> actionStack = new Stack<>();

    @FXML
    private void initialize() {
        classic.setOnAction(event -> addItemToOrder("Classic"));
        bbqbacon.setOnAction(event -> addItemToOrder("BBQ Bacon"));
        mushroomswiss.setOnAction(event -> addItemToOrder("Mushroom Swiss"));
        spicy.setOnAction(event -> addItemToOrder("Spicy Jalapeño"));
        hawaiian.setOnAction(event -> addItemToOrder("Hawaiian Pineapple"));
        beyond.setOnAction(event -> addItemToOrder("Veggie Bean"));
        mediterranean.setOnAction(event -> addItemToOrder("Mediterranean Falafel"));
        teriyaki.setOnAction(event -> addItemToOrder("Teriyaki Salmon"));
        breakfast.setOnAction(event -> addItemToOrder("Breakfast Burger"));
        coke.setOnAction(event -> addItemToOrder("Coke"));
        fanta.setOnAction(event -> addItemToOrder("Fanta"));
        sprite.setOnAction(event -> addItemToOrder("Sprite"));
        icedtea.setOnAction(event -> addItemToOrder("Iced tea"));
        icedcoffee.setOnAction(event -> addItemToOrder("Iced Coffee"));
        restart.setOnAction(event -> restartOrder());
    }

    private void addItemToOrder(String itemName) {
        if (orderItems.containsKey(itemName)) {
            orderItems.put(itemName, orderItems.get(itemName) + 1);
        } else {
            orderItems.put(itemName, 1);
        }
        actionStack.push(() -> {
            if (orderItems.get(itemName) == 1) {
                orderItems.remove(itemName);
            } else {
                orderItems.put(itemName, orderItems.get(itemName) - 1);
            }
            updateOrderSummary();
        });
        updateOrderSummary();
    }

    private void updateOrderSummary() {
        orderSummaryGrid.getChildren().clear();
        currentRow = 0;
        for (Map.Entry<String, Integer> entry : orderItems.entrySet()) {
            String itemName = entry.getKey();
            int quantity = entry.getValue();
            Label itemLabel = new Label("x" + quantity + " " + itemName);
            itemLabel.setOnMouseClicked(event -> selectItem(itemLabel));
            orderSummaryGrid.add(itemLabel, 0, currentRow);
            currentRow++;
        }
    }

    private void selectItem(Label itemLabel) {
        if (selectedItem != null) {
            selectedItem.setStyle(""); // Reset previous selection style
        }
        selectedItem = itemLabel;
        selectedItem.setStyle("-fx-background-color: lightblue;"); // Highlight selected item
    }

    @FXML
    private void voidSelectedItem() {
        if (selectedItem != null) {
            String itemText = selectedItem.getText();
            String itemName = itemText.substring(itemText.indexOf(" ") + 1);
            int quantity = orderItems.get(itemName);
            orderItems.remove(itemName);
            actionStack.push(() -> {
                orderItems.put(itemName, quantity);
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
    }

    @FXML
    private void correctLastItem() {
        if (!actionStack.isEmpty()) {
            actionStack.pop().run();
        }
    }
}
