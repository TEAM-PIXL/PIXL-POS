package teampixl.com.pixlpos.controllers.cookconsole;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;
import teampixl.com.pixlpos.common.OrderUtil;
import teampixl.com.pixlpos.database.api.MenuAPI;
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Order;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CookScreen2Controller {

    @FXML
    private Button logoutbutton;
    @FXML
    private TextField searchbar;
    @FXML
    private Label date;
    @FXML
    private Label time;
    @FXML
    private Label orders;
    @FXML
    private ListView<Label> completedOrders;
    @FXML
    private ListView<VBox> orderList;

    private final OrderAPI orderAPI = OrderAPI.getInstance();
    private final MenuAPI menuAPI = MenuAPI.getInstance();

    private ObservableList<VBox> orderObservableList = FXCollections.observableArrayList();
    private Map<String, VBox> orderMap = new LinkedHashMap<>(); // Use LinkedHashMap to maintain order
    private List<Order> activeOrders = new ArrayList<>();
    private Map<String, Integer> orderOriginalPositions = new HashMap<>();

    private DynamicLabelManager dynamicLabelManager;

    AnimationTimer datetime = new AnimationTimer() {
        @Override
        public void handle(long now) {
            date.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            time.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    };

    @FXML
    public void initialize() {
        datetime.start();

        orderList.setItems(orderObservableList);
        dynamicLabelManager = new DynamicLabelManager(completedOrders);

        // Load orders from the database
        loadOrdersFromDatabase();

        // Update the orders label
        orders.setText(orderObservableList.size() + " Orders");

        // Optional: Periodic refresh of orders
        // scheduleOrderRefresh();
    }

    private void loadOrdersFromDatabase() {
        // Fetch orders with status SENT
        activeOrders = orderAPI.getOrders().stream().filter(order -> order.getMetadataValue("order_status") == Order.OrderStatus.SENT).toList();
        if (activeOrders == null) {
            activeOrders = new ArrayList<>();
        }

        // Clear existing orders
        orderObservableList.clear();
        orderMap.clear();
        orderOriginalPositions.clear();

        // Add orders to the list
        for (int i = 0; i < activeOrders.size(); i++) {
            Order order = activeOrders.get(i);
            addOrderToList(order, i);
        }

        orders.setText(orderObservableList.size() + " Orders");
    }

    private void addOrderToList(Order order, int position) {
        VBox orderVBox = createOrderVBox(order);
        orderObservableList.add(orderVBox);
        String orderId = (String) order.getMetadataValue("order_id");
        orderMap.put(orderId, orderVBox);
        orderOriginalPositions.put(orderId, position);
    }

    private VBox createOrderVBox(Order order) {
        String orderId = (String) order.getMetadataValue("order_id");
        int orderNumber = order.getOrderNumber();
        int customerCount = (int) order.getMetadataValue("customers");
        int tableNumber = (int) order.getMetadataValue("table_number");
        double totalPrice = calculateTotalPrice(order);

        VBox orderVBox = new VBox();
        orderVBox.setMaxWidth(300.0);
        orderVBox.setPrefWidth(300.0);
        orderVBox.setMinWidth(300.0);
        orderVBox.getStyleClass().add("order-container");
        VBox.setVgrow(orderVBox, Priority.ALWAYS);

        // Order number HBox
        HBox orderNumberHBox = new HBox();
        orderNumberHBox.setAlignment(Pos.CENTER);

        AnchorPane anchorPane = new AnchorPane();
        HBox.setHgrow(anchorPane, Priority.ALWAYS);

        Label orderLabel = new Label("Order #");
        orderLabel.getStyleClass().add("order-label");
        AnchorPane.setLeftAnchor(orderLabel, 14.0);
        AnchorPane.setTopAnchor(orderLabel, 3.0);
        anchorPane.getChildren().add(orderLabel);

        Label orderNumberLabel = new Label(String.format("%05d", orderNumber));
        orderNumberLabel.getStyleClass().add("order-number");

        orderNumberHBox.getChildren().addAll(anchorPane, orderNumberLabel);
        orderNumberHBox.setPadding(new Insets(0, 10, 0, 0));

        // Customer and Table info
        AnchorPane infoPane = new AnchorPane();
        infoPane.setPrefHeight(60.0);
        infoPane.getStyleClass().add("order-highlevel-container");

        Label customerLabel = new Label(String.format("%02d", customerCount));
        customerLabel.getStyleClass().add("amount-label");
        Label customerInnerLabel = new Label("Customers:");
        customerInnerLabel.getStyleClass().add("customers-label");
        ImageView customerIcon = new ImageView(new Image(getClass().getResourceAsStream("/teampixl/com/pixlpos/images/cookicons/user_plus_icon.png")));
        customerIcon.setFitHeight(22);
        customerIcon.setFitWidth(22);
        customerIcon.setPreserveRatio(true);
        customerInnerLabel.setGraphic(customerIcon);
        customerLabel.setGraphic(customerInnerLabel);
        AnchorPane.setLeftAnchor(customerLabel, 14.0);
        AnchorPane.setTopAnchor(customerLabel, 15.0);

        Label tableLabel = new Label(String.format("%02d", tableNumber));
        tableLabel.getStyleClass().add("amount-label");
        Label tableInnerLabel = new Label("Table:");
        tableInnerLabel.getStyleClass().add("table-label");
        ImageView tableIcon = new ImageView(new Image(getClass().getResourceAsStream("/teampixl/com/pixlpos/images/cookicons/hash_icon.png")));
        tableIcon.setFitHeight(22);
        tableIcon.setFitWidth(22);
        tableIcon.setPreserveRatio(true);
        tableInnerLabel.setGraphic(tableIcon);
        tableLabel.setGraphic(tableInnerLabel);
        AnchorPane.setLeftAnchor(tableLabel, 166.0);
        AnchorPane.setTopAnchor(tableLabel, 15.0);

        infoPane.getChildren().addAll(customerLabel, tableLabel);

        // Order items ListView
        ListView<Label> orderItemsListView = new ListView<>();
        orderItemsListView.getStyleClass().add("list-pane");
        VBox.setVgrow(orderItemsListView, Priority.ALWAYS);
        DynamicLabelManager internaldynamicLabelManager = new DynamicLabelManager(orderItemsListView);

        // Get order items from the order
        Map<MenuItem, Integer> orderItems = orderAPI.getOrderItemsById(orderId);
        Map<MenuItem, List<String>> itemNotes = OrderUtil.deserializeItemNotes((String) order.getDataValue("special_requests"), menuAPI);

        // Add items to the list view
        for (Map.Entry<MenuItem, Integer> entry : orderItems.entrySet()) {
            MenuItem menuItem = entry.getKey();
            int quantity = entry.getValue();
            List<String> notes = itemNotes.getOrDefault(menuItem, new ArrayList<>());
            internaldynamicLabelManager.addOrderLabel(quantity + "x " + menuItem.getMetadataValue("itemName"));

            // Add notes under the item
            for (String note : notes) {
                internaldynamicLabelManager.addOrderLabel("    * " + note);
            }
        }

        // Price HBox
        HBox priceHBox = new HBox();
        priceHBox.setAlignment(Pos.CENTER);
        Label totalPriceLabel = new Label("$ " + String.format("%.2f", totalPrice));
        totalPriceLabel.getStyleClass().add("amount-label");
        Label priceLabel = new Label("Price:");
        priceLabel.getStyleClass().add("customers-label");
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/teampixl/com/pixlpos/images/cookicons/dollar_sign_icon.png")));
        imageView.setFitHeight(22.0);
        imageView.setFitWidth(22.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        priceLabel.setGraphic(imageView);
        totalPriceLabel.setGraphic(priceLabel);
        priceHBox.getChildren().add(totalPriceLabel);

        // Action buttons
        HBox actionButtonsHBox = new HBox();
        actionButtonsHBox.setAlignment(Pos.CENTER);
        actionButtonsHBox.setSpacing(30.0);

        String[] iconPaths = {
                "/teampixl/com/pixlpos/images/cookicons/trash_icon.png",
                "/teampixl/com/pixlpos/images/cookicons/flag_icon.png",
                "/teampixl/com/pixlpos/images/cookicons/send_icon.png"
        };

        for (String iconPath : iconPaths) {
            Button button = new Button();
            button.setMnemonicParsing(false);
            button.getStyleClass().add("icon-button");
            ImageView buttonIcon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
            buttonIcon.setFitHeight(22);
            buttonIcon.setFitWidth(22);
            buttonIcon.setPreserveRatio(true);
            button.setGraphic(buttonIcon);

            // Extract icon name from path
            String iconName = iconPath.substring(iconPath.lastIndexOf("/") + 1);

            // Set action for the button based on the icon name
            switch (iconName) {
                case "trash_icon.png":
                    button.setOnAction(event -> handleTrashAction(order));
                    break;
                case "flag_icon.png":
                    button.setOnAction(event -> handleFlagAction(order));
                    break;
                case "send_icon.png":
                    button.setOnAction(event -> handleSendAction(order));
                    break;
            }

            actionButtonsHBox.getChildren().add(button);
        }

        orderVBox.getChildren().addAll(orderNumberHBox, infoPane, orderItemsListView, priceHBox, actionButtonsHBox);

        return orderVBox;
    }

    private double calculateTotalPrice(Order order) {
        double totalPrice = 0.0;
        Map<MenuItem, Integer> orderItems = orderAPI.getOrderItemsById((String) order.getMetadataValue("order_id"));
        for (Map.Entry<MenuItem, Integer> entry : orderItems.entrySet()) {
            MenuItem menuItem = entry.getKey();
            int quantity = entry.getValue();
            double price = (double) menuItem.getMetadataValue("price");
            totalPrice += price * quantity;
        }
        return totalPrice;
    }

    private void handleTrashAction(Order order) {
        System.out.println("Trash action for Order #" + order.getOrderNumber());
        // Remove the order from the database
        List<StatusCode> statusCodes = orderAPI.deleteOrder((String) order.getMetadataValue("order_id"));
        if (Exceptions.isSuccessful(statusCodes)) {
            removeOrderFromList(order);
        } else {
            showErrorDialog(Exceptions.returnStatus("Failed to delete order:", statusCodes));
        }
    }

    private void handleFlagAction(Order order) {
        System.out.println("Flag action for Order #" + order.getOrderNumber());
        String orderId = (String) order.getMetadataValue("order_id");
        VBox orderVBox = orderMap.get(orderId);

        if (orderObservableList.contains(orderVBox)) {
            // Check if the order is already at the front
            if (orderObservableList.indexOf(orderVBox) != 0) {
                // Move order to the front
                orderObservableList.remove(orderVBox);
                orderObservableList.add(0, orderVBox);
            } else {
                // Move order back to its original position
                int originalPosition = orderOriginalPositions.get(orderId);
                orderObservableList.remove(orderVBox);
                orderObservableList.add(originalPosition, orderVBox);
            }
        }
    }

    private void handleSendAction(Order order) {
        System.out.println("Send action for Order #" + order.getOrderNumber());
        // Update order status to COMPLETED
        List<StatusCode> statusCodes = orderAPI.putOrderStatus((String) order.getMetadataValue("order_id"), Order.OrderStatus.COMPLETED);
        if (Exceptions.isSuccessful(statusCodes)) {
            // Remove from current view
            removeOrderFromList(order);
            // Add to completed orders list
            dynamicLabelManager.addCompletedLabel("Order #" + order.getOrderNumber() + " @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            showErrorDialog(Exceptions.returnStatus("Failed to complete order:", statusCodes));
        }
    }

    private void removeOrderFromList(Order order) {
        String orderId = (String) order.getMetadataValue("order_id");
        VBox orderVBox = orderMap.remove(orderId);
        if (orderVBox != null) {
            orderObservableList.remove(orderVBox);
            orderOriginalPositions.remove(orderId);
            System.out.println("Removed Order: " + orderId);
            orders.setText(orderObservableList.size() + " Orders");
        } else {
            System.out.println("Order not found: " + orderId);
        }
    }

    @FXML
    protected void onLogoutButtonClick() {
        // Handle logout button click
        GuiCommon.logout(logoutbutton);
    }

    /**
     * This class is used to manage dynamic labels for completed orders
     */
    public static class DynamicLabelManager {
        private final ListView<Label> labelListView;

        public DynamicLabelManager(ListView<Label> labelListView) {
            this.labelListView = labelListView;
        }

        public void addCompletedLabel(String text) {
            Label newLabel = new Label(text);
            newLabel.getStyleClass().add("docket-label");
            labelListView.getItems().add(newLabel);
            System.out.println("Added completed order label with text: " + text);
        }

        public void addOrderLabel(String itemName) {
            Label newLabel = new Label(itemName);
            newLabel.getStyleClass().add("order-label");
            labelListView.getItems().add(newLabel);
            System.out.println("Added order label with text: " + itemName);
        }
    }

    private void showErrorDialog(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(logoutbutton.getScene().getWindow());
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}

