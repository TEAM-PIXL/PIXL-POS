// src/main/java/teampixl/com/pixlpos/controllers/cookconsole/CookScreenController.java
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
import teampixl.com.pixlpos.constructs.Order;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.constructs.Users;

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

    @FXML
    private void initialize() {
        datastore = DataStore.getInstance();
        orders = FXCollections.observableArrayList(datastore.getOrders());
        updateOrderListView();
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
            datastore.updateOrder(selectedOrder);
            orders.setAll(datastore.getOrders());
            updateOrderListView();
        }
    }

    @FXML
    private void onLogoutButtonClick() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE, logoutButton);
    }

    private void updateOrderListView() {
        orderview.getItems().clear();
        for (Order order : orders) {
            VBox orderVBox = new VBox();
            orderVBox.setPadding(new Insets(10));
            orderVBox.setSpacing(10);
            orderVBox.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

            // Order Number
            Label orderNumLabel = new Label("Order#");
            Text orderNumText = new Text(String.valueOf(order.getMetadata().metadata().get("order_number")));
            orderNumText.setFont(new Font(25));
            orderNumLabel.setGraphic(orderNumText);
            orderNumLabel.setFont(new Font(30));

            // Time Ordered
            Label timeOrderedLabel = new Label("Time Ordered");
            long unixTime = (long) order.getMetadata().metadata().get("created_at");
            String humanReadableTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(unixTime));
            Text timeOrderedText = new Text(humanReadableTime);
            timeOrderedLabel.setGraphic(timeOrderedText);

            // User ID to Username
            Label userIdLabel = new Label("User");
            String userId = order.getMetadata().metadata().get("user_id").toString();
            try {
                Users user = datastore.getUser(userId);
                String username = user.getMetadata().metadata().get("username").toString();
                Text userIdText = new Text(username);
                userIdLabel.setGraphic(userIdText);
            } catch (Exception e) {
                Text userIdText = new Text("Unknown");
                userIdLabel.setGraphic(userIdText);
            }

            // Order Items and Total
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
            Label totalLabel = new Label("Total: " + order.getData().get("total"));

            // Add all elements to the VBox
            orderVBox.getChildren().addAll(orderNumLabel, timeOrderedLabel, userIdLabel, itemsLabel, itemsVBox, totalLabel);

            // Add the VBox to the ListView
            orderview.getItems().add(orderVBox);
        }
    }

    private Order getSelectedOrder() {
        VBox selectedVBox = orderview.getSelectionModel().getSelectedItem();
        if (selectedVBox != null) {
            Text orderNumText = (Text) ((Label) selectedVBox.getChildren().getFirst()).getGraphic();
            int orderNumber = Integer.parseInt(orderNumText.getText());
            for (Order order : orders) {
                if (order.getMetadata().metadata().get("order_number").equals(orderNumber)) {
                    return order;
                }
            }
        }
        return null;
    }
}