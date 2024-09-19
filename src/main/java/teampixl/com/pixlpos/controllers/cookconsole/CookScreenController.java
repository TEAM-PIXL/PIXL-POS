package teampixl.com.pixlpos.controllers.cookconsole;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;
import teampixl.com.pixlpos.database.api.UsersAPI;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.database.api.UserStack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class CookScreenController extends GuiCommon {
    @FXML
    private Button refreshButton;
    @FXML
    private Button completeButton;
    @FXML
    private Button logoutButton;
    @FXML
    private ListView<VBox> orderview;
    private ObservableList<Order> orders;
    private DataStore datastore;
    private UserStack userStack = UserStack.getInstance();

    @FXML
    private void initialize() {
        datastore = DataStore.getInstance();
        orders = FXCollections.observableArrayList(datastore.getOrders());
        updateOrderListView();

        Users currentUser = userStack.getCurrentUser();
        if (currentUser != null) {
            System.out.println("Current User: " + currentUser.getMetadata().metadata().get("username"));
        } else {
            System.err.println("Error: Current user is not set.");
        }

        for (Order order : orders) {
            if (order.getMetadata().metadata().get("order_status") != Order.OrderStatus.COMPLETED && order.getMetadata().metadata().get("order_status") != Order.OrderStatus.PENDING) {
                Object totalObj = order.getData().get("total");
                if (totalObj != null) {
                    double total = (double) totalObj;
                    System.out.println("Order total: " + total);
                    order.getData().put("total", total);
                } else {
                    System.err.println("Order total is null for order ID: " + order.getMetadata().metadata().get("order_number"));
                }
            }
        }
    }

    @FXML
    private void onRefreshButtonClick() {
        orders.setAll(datastore.getOrders());
        updateOrderListView();
    }

    @FXML
    private void onCompleteButtonClick() {
        Order selectedOrder = getSelectedOrder();
        if (selectedOrder != null) {
            selectedOrder.updateOrderStatus(Order.OrderStatus.COMPLETED);
            Map<String, Object> orderData = selectedOrder.getData();
            if (orderData.get("total") != null) {
                datastore.updateOrder(selectedOrder);
                orders.setAll(datastore.getOrders());
                updateOrderListView();
            } else {
                System.err.println("Order total is null. Cannot complete order.");
            }
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
            if (order.getMetadata().metadata().get("order_status") != Order.OrderStatus.COMPLETED && order.getMetadata().metadata().get("order_status") != Order.OrderStatus.PENDING) {
                VBox orderVBox = new VBox();
                orderVBox.setPadding(new Insets(10));
                orderVBox.setSpacing(10);
                orderVBox.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

                Label orderNumLabel = new Label();
                String orderNumber = String.valueOf(order.getMetadata().metadata().get("order_number"));
                orderNumLabel.setText("Order#: " + orderNumber);
                orderNumLabel.setFont(new Font(30));

                Label userIdLabel = new Label();
                String userId = order.getMetadata().metadata().get("user_id").toString();
                try {
                    Users user = UsersAPI.getInstance().getUserById(userId);
                    String username = user.getMetadata().metadata().get("username").toString();
                    userIdLabel.setText("User: " + username);
                } catch (Exception e) {
                    userIdLabel.setText("User: Unknown");
                }

                Label timeOrderedLabel = new Label();
                long unixTime = (long) order.getMetadata().metadata().get("created_at");
                String humanReadableTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(unixTime));
                timeOrderedLabel.setText("Time Ordered: " + humanReadableTime);

                Label itemsLabel = new Label("Items");
                VBox itemsVBox = new VBox();
                Map<String, Object> orderItems = datastore.getOrderItems(order);
                try {
                    for (Map.Entry<String, Object> entry : orderItems.entrySet()) {
                        String itemKey = entry.getKey();
                        String itemName = datastore.getMenuItemById(itemKey).getMetadata().metadata().get("itemName").toString();
                        int quantity = (int) entry.getValue();
                        Label itemLabel = new Label(itemName + " x" + quantity);
                        itemsVBox.getChildren().add(itemLabel);
                    }
                } catch (Exception e) {
                    Label itemLabel = new Label("No items");
                    itemsVBox.getChildren().add(itemLabel);
                }
                Label totalLabel = new Label("Total: $" + String.format("%.2f", order.getData().get("total")));

                orderVBox.getChildren().addAll(orderNumLabel, userIdLabel, timeOrderedLabel, itemsLabel, itemsVBox, totalLabel);

                orderview.getItems().add(orderVBox);
            }
        }
    }

    private Order getSelectedOrder() {
        VBox selectedVBox = orderview.getSelectionModel().getSelectedItem();
        if (selectedVBox != null) {
            Label orderNumLabel = (Label) selectedVBox.getChildren().get(0);
            String orderNumText = orderNumLabel.getText().replace("Order#: ", "");
            int orderNumber = Integer.parseInt(orderNumText);
            for (Order order : orders) {
                if (order.getMetadata().metadata().get("order_number").equals(orderNumber)) {
                    return order;
                }
            }
        }
        return null;
    }
}