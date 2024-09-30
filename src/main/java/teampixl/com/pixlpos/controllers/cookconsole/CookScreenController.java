package teampixl.com.pixlpos.controllers.cookconsole;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.database.api.UsersAPI;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.models.Users;

import java.text.SimpleDateFormat;
import java.util.*;

public class CookScreenController extends GuiCommon {
    private static final String ORDER_ID_KEY = "order_id";
    private static final String ORDER_NUMBER_KEY = "order_number";
    private static final String USER_ID_KEY = "user_id";
    private static final String ORDER_STATUS_KEY = "order_status";
    private static final String CREATED_AT_KEY = "created_at";

    @FXML
    private Button refreshButton;
    @FXML
    private Button completeButton;
    @FXML
    private Button logoutButton;
    @FXML
    private ListView<VBox> orderview;

    private final UserStack userStack = UserStack.getInstance();
    private final UsersAPI usersAPI = UsersAPI.getInstance();
    private final OrderAPI orderAPI = OrderAPI.getInstance();
    private List<Order> orders;

    @FXML
    private void initialize() {
        orders = orderAPI.getOrders();
        orderview.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/teampixl/com/pixlpos/fxml/cookconsole/stylesheets/dyanmics.css")).toExternalForm());
        updateOrderListView();

        Users currentUser = userStack.getCurrentUser();
        if (currentUser != null) {
            System.out.println("Current User: " + currentUser.getMetadata().metadata().get("username"));
        } else {
            System.err.println("Error: Current user is not set.");
        }
        orderview.setOnMouseClicked(event -> {
            VBox selectedVBox = orderview.getSelectionModel().getSelectedItem();
            if (selectedVBox != null) {
                for (VBox vbox : orderview.getItems()) {
                    vbox.getStyleClass().remove("vbox-order-selected");
                }
                selectedVBox.getStyleClass().add("vbox-order-selected");
            }
        });
    }

    @FXML
    private void onRefreshButtonClick() {
        orders = orderAPI.getOrders();
        updateOrderListView();
    }

    @FXML
    private void onCompleteButtonClick() {
        Order selectedOrder = getSelectedOrder();
        if (selectedOrder != null) {
            List<StatusCode> statusCodes = orderAPI.putOrderStatus(selectedOrder.getMetadata().metadata().get(ORDER_ID_KEY).toString(), Order.OrderStatus.COMPLETED);
            if (statusCodes.contains(StatusCode.SUCCESS)) {
                System.out.println("Order marked as completed.");
                orders.removeIf(order -> order.getOrderNumber() == selectedOrder.getOrderNumber());
                updateOrderListView();
            } else {
                showAlert("Failed to complete order. Status: " + statusCodes);
            }
        } else {
            showAlert("No order selected.");
        }
    }

    private void updateOrderListView() {
        orderview.getItems().clear();
        for (Order order : orders) {
            Order.OrderStatus status = Order.OrderStatus.valueOf(order.getMetadata().metadata().get(ORDER_STATUS_KEY).toString()); //Clear and works
            if (status == Order.OrderStatus.SENT) {
                VBox orderVBox = createOrderVBox(order);
                orderview.getItems().add(orderVBox);
            }
        }
    }

    private VBox createOrderVBox(Order order) {
        VBox orderVBox = new VBox();
        orderVBox.getStyleClass().add("vbox-order");

        Label orderNumLabel = new Label("Order#: " + order.getOrderNumber());
        orderNumLabel.getStyleClass().add("label-order-number");

        Label userIdLabel = new Label();
        String userId = order.getMetadata().metadata().get(USER_ID_KEY).toString();
        Users user = usersAPI.keyTransform(userId);
        if (user != null) {
            userIdLabel.setText("User: " + user.getMetadata().metadata().get("username"));
        } else {
            userIdLabel.setText("User: Unknown");
        }
        userIdLabel.getStyleClass().add("label-user-id");

        Label timeOrderedLabel = new Label();
        long unixTime = (long) order.getMetadata().metadata().get(CREATED_AT_KEY);
        String humanReadableTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(unixTime));
        timeOrderedLabel.setText("Time Ordered: " + humanReadableTime);
        timeOrderedLabel.getStyleClass().add("label-time-ordered");

        Label itemsLabel = new Label("Items");
        itemsLabel.getStyleClass().add("label-items");

        VBox itemsVBox = new VBox();
        itemsVBox.getStyleClass().add("vbox-items");
        Map<MenuItem, Integer> orderItems = orderAPI.getOrderItemsById(order.getMetadata().metadata().get(ORDER_ID_KEY).toString());
        System.out.println("Order Items: " + orderItems);
        for (Map.Entry<MenuItem, Integer> entry : orderItems.entrySet()) {
            String itemName = entry.getKey().getMetadata().metadata().get("itemName").toString();
            int quantity = entry.getValue();
            Label itemLabel = new Label(itemName + " x" + quantity);
            itemLabel.getStyleClass().add("label-item");
            itemsVBox.getChildren().add(itemLabel);
        }

        double total = order.getTotal();
        Label totalLabel = new Label("Total: $" + String.format("%.2f", total));
        totalLabel.getStyleClass().add("label-total");

        orderVBox.getChildren().addAll(orderNumLabel, userIdLabel, timeOrderedLabel, itemsLabel, itemsVBox, totalLabel);

        return orderVBox;
    }

    private Order getSelectedOrder() {
        VBox selectedVBox = orderview.getSelectionModel().getSelectedItem();
        if (selectedVBox != null) {
            Label orderNumLabel = (Label) selectedVBox.getChildren().get(0);
            String orderNumText = orderNumLabel.getText().replace("Order#: ", "");
            int orderNumber = Integer.parseInt(orderNumText);
            for (Order order : orders) {
                if (order.getOrderNumber() == orderNumber) {
                    return order;
                }
            }
        }
        return null;
    }

    /* ------->HELPER FUNCTIONS<--------- */
    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Order Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void onLogoutButtonClick() {
        GuiCommon.loadRoot(GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE, logoutButton);
    }
}

