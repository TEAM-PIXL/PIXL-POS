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
            List<StatusCode> statusCodes = orderAPI.putOrderStatus(selectedOrder.getMetadata().metadata().get("order_id").toString(), Order.OrderStatus.COMPLETED);
            if (statusCodes.contains(StatusCode.SUCCESS)) {
                System.out.println("Order marked as completed.");
                onRefreshButtonClick();
            } else {
                showAlert("Failed to complete order. Status: " + statusCodes);
            }
        } else {
            showAlert("No order selected.");
        }
    }

    @FXML
    private void onLogoutButtonClick() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        GuiCommon.loadRoot(GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE, logoutButton);
    }

    private void updateOrderListView() {
        orderview.getItems().clear();
        for (Order order : orders) {
            Order.OrderStatus status = Order.OrderStatus.valueOf(order.getMetadata().metadata().get("order_status").toString());
            if (status != Order.OrderStatus.COMPLETED && status != Order.OrderStatus.PENDING) {
                VBox orderVBox = createOrderVBox(order);
                orderview.getItems().add(orderVBox);
            }
        }
    }

    private VBox createOrderVBox(Order order) {
        VBox orderVBox = new VBox();
        orderVBox.getStyleClass().add("vbox-order");

        Label orderNumLabel = new Label("Order#: " + order.getMetadata().metadata().get("order_number"));
        orderNumLabel.getStyleClass().add("label-order-number");

        Label userIdLabel = new Label();
        String userId = order.getMetadata().metadata().get("user_id").toString();
        Users user = usersAPI.keyTransform(userId);
        if (user != null) {
            userIdLabel.setText("User: " + usersAPI.reverseKeySearch(userId));
        } else {
            userIdLabel.setText("User: Unknown");
        }
        userIdLabel.getStyleClass().add("label-user-id");

        Label timeOrderedLabel = new Label();
        long unixTime = order.getMetadata().metadata().get("created_at") instanceof Long ? (Long) order.getMetadata().metadata().get("created_at") : 0;
        String humanReadableTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(unixTime));
        timeOrderedLabel.setText("Time Ordered: " + humanReadableTime);
        timeOrderedLabel.getStyleClass().add("label-time-ordered");

        Label itemsLabel = new Label("Items");
        itemsLabel.getStyleClass().add("label-items");

        VBox itemsVBox = new VBox();
        itemsVBox.getStyleClass().add("vbox-items");
        Map<MenuItem, Integer> orderItems = orderAPI.getOrderItemsById(order.getMetadata().metadata().get("order_id").toString());
        for (Map.Entry<MenuItem, Integer> entry : orderItems.entrySet()) {
            String itemName = entry.getKey().getMetadata().metadata().get("itemName").toString();
            int quantity = entry.getValue();
            Label itemLabel = new Label(itemName + " x" + quantity);
            itemLabel.getStyleClass().add("label-item");
            itemsVBox.getChildren().add(itemLabel);
        }

        double total = order.getData().get("total") instanceof Double ? (Double) order.getData().get("total") : 0.0;
        Label totalLabel = new Label("Total: $" + String.format("%.2f", total));
        totalLabel.getStyleClass().add("label-total");

        orderVBox.getChildren().addAll(orderNumLabel, userIdLabel, timeOrderedLabel, itemsLabel, itemsVBox, totalLabel);

        return orderVBox;
    }

    private Order getSelectedOrder() {
        VBox selectedVBox = orderview.getSelectionModel().getSelectedItem();
        if (selectedVBox != null) {
            Label orderNumLabel = (Label) selectedVBox.getChildren().getFirst();
            String orderNumText = orderNumLabel.getText().replace("Order#: ", "");
            int orderNumber = Integer.parseInt(orderNumText);
            for (Order order : orders) {
                if (order.getMetadata().metadata().get("orderNumber").equals(orderNumber)) {
                    return order;
                }
            }
        }
        return null;
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Order Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
