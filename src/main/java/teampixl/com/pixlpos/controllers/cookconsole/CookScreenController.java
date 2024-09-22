package teampixl.com.pixlpos.controllers.cookconsole;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;
import teampixl.com.pixlpos.database.api.UsersAPI;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.database.api.OrderAPI;

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

    private final DataStore DATASTORE = DataStore.getInstance();
    private final UserStack USER_STACK = UserStack.getInstance();
    private final UsersAPI USERS_API = UsersAPI.getInstance();
    private final OrderAPI ORDER_API = OrderAPI.getInstance();
    private ObservableList<Order> ORDERS;

    @FXML
    private void initialize() throws InterruptedException {
        DATASTORE.reloadOrdersFromDatabase();
        ORDERS = OrderAPI.getOrders();
        orderview.getStylesheets().add(getClass().getResource("/teampixl/com/pixlpos/fxml/cookconsole/stylesheets/dyanmics.css").toExternalForm());
        updateOrderListView();

        Users CURRENT_USER = USER_STACK.getCurrentUser();
        if (CURRENT_USER != null) {
            System.out.println("Current User: " + CURRENT_USER.getMetadata().metadata().get("username"));
        } else {
            System.err.println("Error: Current user is not set.");
        }

        for (Order ORDER : ORDERS) {
            if (ORDER.getMetadata().metadata().get("order_status") != Order.OrderStatus.COMPLETED &&
                    ORDER.getMetadata().metadata().get("order_status") != Order.OrderStatus.PENDING) {
                Double TOTAL = (Double) ORDER.getData().get("total");
                if (TOTAL != null) {
                    System.out.println("Order total: " + TOTAL);
                } else {
                    System.err.println("Order total is null for order ID: " + ORDER.getMetadata().metadata().get("order_number"));
                }
            }
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
        DATASTORE.reloadOrdersFromDatabase();
        ORDERS.setAll(DATASTORE.readOrders());
        updateOrderListView();
    }


    @FXML
    private void onCompleteButtonClick() {
        Order SELECTED_ORDER = getSelectedOrder();
        if (SELECTED_ORDER != null) {
            SELECTED_ORDER.updateOrderStatus(Order.OrderStatus.COMPLETED);
            Map<String, Object> ORDER_DATA = SELECTED_ORDER.getData();
            if (ORDER_DATA.get("total") != null) {
                DATASTORE.updateOrder(SELECTED_ORDER);
                ORDERS.setAll(DATASTORE.readOrders());
                updateOrderListView();
            } else {
                System.err.println("Order total is null. Cannot complete order.");
            }
        }
    }

    @FXML
    private void onLogoutButtonClick() {
        Stage STAGE = (Stage) logoutButton.getScene().getWindow();
        GuiCommon.loadRoot(GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE, logoutButton);
    }

    private void updateOrderListView() {
        orderview.getItems().clear();
        for (Order ORDER : ORDERS) {
            if (ORDER.getMetadata().metadata().get("order_status") != Order.OrderStatus.COMPLETED && ORDER.getMetadata().metadata().get("order_status") != Order.OrderStatus.PENDING) {
                VBox ORDER_VBOX = new VBox();
                ORDER_VBOX.getStyleClass().add("vbox-order");

                Label ORDER_NUM_LABEL = new Label();
                String ORDER_NUMBER = String.valueOf(ORDER.getMetadata().metadata().get("order_number"));
                ORDER_NUM_LABEL.setText("Order#: " + ORDER_NUMBER);
                ORDER_NUM_LABEL.getStyleClass().add("label-order-number");

                Label USER_ID_LABEL = new Label();
                String USER_ID = ORDER.getMetadata().metadata().get("user_id").toString();
                try {
                    Users USER = USERS_API.getUsersById(USER_ID);
                    String USERNAME = USER.getMetadata().metadata().get("username").toString();
                    USER_ID_LABEL.setText("User: " + USERNAME);
                } catch (Exception e) {
                    USER_ID_LABEL.setText("User: Unknown");
                }
                USER_ID_LABEL.getStyleClass().add("label-user-id");

                Label TIME_ORDERED_LABEL = new Label();
                long UNIX_TIME = (long) ORDER.getMetadata().metadata().get("created_at");
                String HUMAN_READABLE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(UNIX_TIME));
                TIME_ORDERED_LABEL.setText("Time Ordered: " + HUMAN_READABLE_TIME);
                TIME_ORDERED_LABEL.getStyleClass().add("label-time-ordered");

                Label ITEMS_LABEL = new Label("Items");
                ITEMS_LABEL.getStyleClass().add("label-items");

                VBox ITEMS_VBOX = new VBox();
                ITEMS_VBOX.getStyleClass().add("vbox-items");
                Map<String, Object> ORDER_ITEMS = DATASTORE.readOrderItems(ORDER);
                try {
                    for (Map.Entry<String, Object> ENTRY : ORDER_ITEMS.entrySet()) {
                        String ITEM_KEY = ENTRY.getKey();
                        String ITEM_NAME = DATASTORE.getMenuItemById(ITEM_KEY).getMetadata().metadata().get("itemName").toString();
                        int QUANTITY = (int) ENTRY.getValue();
                        Label ITEM_LABEL = new Label(ITEM_NAME + " x" + QUANTITY);
                        ITEM_LABEL.getStyleClass().add("label-item");
                        ITEMS_VBOX.getChildren().add(ITEM_LABEL);
                    }
                } catch (Exception e) {
                    System.err.println("Error: Could not get order items.");
                }

                double TOTAL = (double) ORDER.getData().get("total");
                Label TOTAL_LABEL = new Label("Total: $" + String.format("%.2f", TOTAL));
                TOTAL_LABEL.getStyleClass().add("label-total");

                ORDER_VBOX.getChildren().addAll(ORDER_NUM_LABEL, USER_ID_LABEL, TIME_ORDERED_LABEL, ITEMS_LABEL, ITEMS_VBOX, TOTAL_LABEL);

                orderview.getItems().add(ORDER_VBOX);
            }
        }
    }

    private Order getSelectedOrder() {
        VBox SELECTED_VBOX = orderview.getSelectionModel().getSelectedItem();
        if (SELECTED_VBOX != null) {
            Label ORDER_NUM_LABEL = (Label) SELECTED_VBOX.getChildren().get(0);
            String ORDER_NUM_TEXT = ORDER_NUM_LABEL.getText().replace("Order#: ", "");
            int ORDER_NUMBER = Integer.parseInt(ORDER_NUM_TEXT);
            for (Order ORDER : ORDERS) {
                if (ORDER.getMetadata().metadata().get("order_number").equals(ORDER_NUMBER)) {
                    return ORDER;
                }
            }
        }
        return null;
    }
}