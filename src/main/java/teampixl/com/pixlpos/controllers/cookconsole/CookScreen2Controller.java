package teampixl.com.pixlpos.controllers.cookconsole;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import teampixl.com.pixlpos.common.GuiCommon;
import teampixl.com.pixlpos.common.OrderUtil;
import teampixl.com.pixlpos.database.api.MenuAPI;
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Order;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public class CookScreen2Controller {

    @FXML
    private Button logoutbutton;
    @FXML
    private Button settingsbutton;
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

    private final ObservableList<VBox> orderObservableList = FXCollections.observableArrayList();
    private final Map<String, VBox> orderMap = new LinkedHashMap<>();
    private final Map<String, Integer> orderOriginalPositions = new HashMap<>();

    private DynamicLabelManager dynamicLabelManager;

    private ScheduledExecutorService scheduler;

    protected void addtooltips() {
        Tooltip hometooltip = new Tooltip("Settings");
        hometooltip.setShowDelay(javafx.util.Duration.millis(250));
        Tooltip.install(settingsbutton, hometooltip);

        Tooltip userstooltip = new Tooltip("Logout");
        userstooltip.setShowDelay(javafx.util.Duration.millis(250));
        Tooltip.install(logoutbutton, userstooltip);
    }

    @FXML
    public void initialize() {
        AnimationTimer datetime = new AnimationTimer() {
            @Override
            public void handle(long now) {
                date.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                time.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
        };
        datetime.start();
        addtooltips();

        orderList.setItems(orderObservableList);
        dynamicLabelManager = new DynamicLabelManager(completedOrders);

        loadOrdersFromDatabase();

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> Platform.runLater(this::loadOrdersFromDatabase), 10, 10, TimeUnit.SECONDS);
    }

    private void loadOrdersFromDatabase() {
        Task<Void> loadOrdersTask = new Task<>() {
            @Override
            protected Void call() {
                orderAPI.reloadOrders();
                List<Order> allOrders = orderAPI.getOrders();

                List<Order> activeOrders = new ArrayList<>();
                List<Order> completedOrdersList = new ArrayList<>();

                for (Order order : allOrders) {
                    Object statusObj = order.getMetadataValue("order_status");
                    if (statusObj instanceof Order.OrderStatus) {
                        Order.OrderStatus status = (Order.OrderStatus) statusObj;
                        if (status == Order.OrderStatus.SENT) {
                            activeOrders.add(order);
                        } else if (status == Order.OrderStatus.COMPLETED) {
                            completedOrdersList.add(order);
                        }
                    }
                }

                Platform.runLater(() -> {
                    orderObservableList.clear();
                    orderMap.clear();
                    orderOriginalPositions.clear();

                    for (int i = 0; i < activeOrders.size(); i++) {
                        Order order = activeOrders.get(i);
                        addOrderToList(order, i);
                    }

                    orders.setText(orderObservableList.size() + " Orders");

                    dynamicLabelManager.clearCompletedLabels();
                    for (Order order : completedOrdersList) {
                        Object updatedAtObj = order.getMetadataValue("updated_at");
                        if (updatedAtObj instanceof Long) {
                            long updatedAt = (Long) updatedAtObj;
                            LocalDateTime updatedAtDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(updatedAt), ZoneId.systemDefault());
                            dynamicLabelManager.addCompletedLabel("Order #" + order.getOrderNumber() + " @ " + updatedAtDateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                        }
                    }
                });

                return null;
            }
        };
        new Thread(loadOrdersTask).start();
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
        Order.OrderType orderType = (Order.OrderType) order.getMetadataValue("order_type");
        double totalPrice = calculateTotalPrice(order);

        VBox orderVBox = new VBox();
        orderVBox.setMaxWidth(300.0);
        orderVBox.setPrefWidth(300.0);
        orderVBox.setMinWidth(300.0);
        orderVBox.getStyleClass().add("order-container");
        orderVBox.setSpacing(5);
        VBox.setVgrow(orderVBox, Priority.NEVER);

        HBox orderNumberHBox = new HBox();
        orderNumberHBox.setAlignment(Pos.CENTER_LEFT);
        orderNumberHBox.setPadding(new Insets(5, 10, 0, 14));

        Label orderLabel = new Label("Order #");
        orderLabel.getStyleClass().add("order-label");

        Label orderNumberLabel = new Label(String.format("%05d", orderNumber));
        orderNumberLabel.getStyleClass().add("order-number");

        orderNumberHBox.getChildren().addAll(orderLabel, orderNumberLabel);
        orderNumberHBox.setSpacing(5);

        AnchorPane infoPane = new AnchorPane();
        infoPane.setPrefHeight(60.0);
        infoPane.getStyleClass().add("order-highlevel-container");

        Label orderTypeLabel = new Label(orderType.name());
        orderTypeLabel.getStyleClass().add("amount-label");
        Label orderTypeInnerLabel = new Label("Type:");
        orderTypeInnerLabel.getStyleClass().add("table-label");
        ImageView orderTypeIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/teampixl/com/pixlpos/images/cookicons/user_plus_icon.png"))));
        orderTypeIcon.setFitHeight(22);
        orderTypeIcon.setFitWidth(22);
        orderTypeIcon.setPreserveRatio(true);
        orderTypeInnerLabel.setGraphic(orderTypeIcon);
        orderTypeLabel.setGraphic(orderTypeInnerLabel);
        AnchorPane.setLeftAnchor(orderTypeLabel, 14.0);
        AnchorPane.setTopAnchor(orderTypeLabel, 5.0);

        Label customerLabel = new Label(String.format("%02d", customerCount));
        customerLabel.getStyleClass().add("amount-label");
        Label customerInnerLabel = new Label("Customers:");
        customerInnerLabel.getStyleClass().add("customers-label");
        ImageView customerIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/teampixl/com/pixlpos/images/cookicons/users_group_icon.png"))));
        customerIcon.setFitHeight(22);
        customerIcon.setFitWidth(22);
        customerIcon.setPreserveRatio(true);
        customerInnerLabel.setGraphic(customerIcon);
        customerLabel.setGraphic(customerInnerLabel);
        AnchorPane.setLeftAnchor(customerLabel, 14.0);
        AnchorPane.setTopAnchor(customerLabel, 35.0);

        Label tableLabel = new Label(String.format("%02d", tableNumber));
        tableLabel.getStyleClass().add("amount-label");
        Label tableInnerLabel = new Label("Table:");
        tableInnerLabel.getStyleClass().add("table-label");
        ImageView tableIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/teampixl/com/pixlpos/images/cookicons/hash_icon.png"))));
        tableIcon.setFitHeight(22);
        tableIcon.setFitWidth(22);
        tableIcon.setPreserveRatio(true);
        tableInnerLabel.setGraphic(tableIcon);
        tableLabel.setGraphic(tableInnerLabel);
        AnchorPane.setLeftAnchor(tableLabel, 166.0);
        AnchorPane.setTopAnchor(tableLabel, 35.0);

        infoPane.getChildren().addAll(orderTypeLabel, customerLabel, tableLabel);

        ListView<Label> orderItemsListView = new ListView<>();
        orderItemsListView.getStyleClass().add("list-pane");
        orderItemsListView.setPrefHeight(375);
        orderItemsListView.setMaxHeight(375);
        orderItemsListView.setMinHeight(375);
        VBox.setVgrow(orderItemsListView, Priority.NEVER);

        DynamicLabelManager internaldynamicLabelManager = new DynamicLabelManager(orderItemsListView);

        Map<MenuItem, Integer> orderItems = orderAPI.getOrderItemsById(orderId);
        Map<MenuItem, List<String>> itemNotes = OrderUtil.deserializeItemNotes((String) order.getDataValue("special_requests"), menuAPI);
        System.out.println("Order #" + orderNumber + " has " + orderItems.size() + " items");
        for (Map.Entry<MenuItem, Integer> entry : orderItems.entrySet()) {
            MenuItem menuItem = entry.getKey();
            int quantity = entry.getValue();
            List<String> notes = itemNotes.getOrDefault(menuItem, new ArrayList<>());
            internaldynamicLabelManager.addOrderLabel(quantity + "x " + menuItem.getMetadataValue("itemName"));

            for (String note : notes) {
                internaldynamicLabelManager.addOrderLabel("    * " + note);
            }
        }

        HBox priceHBox = new HBox();
        priceHBox.setAlignment(Pos.CENTER);
        priceHBox.setPadding(new Insets(0, 10, 0, 14));

        Label totalPriceLabel = new Label(String.format("%.2f", totalPrice));
        totalPriceLabel.getStyleClass().add("amount-label");
        totalPriceLabel.setStyle("-fx-font-size: 18px;");

        ImageView priceIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/teampixl/com/pixlpos/images/cookicons/dollar_sign_icon.png"))));
        priceIcon.setFitHeight(18.0);
        priceIcon.setFitWidth(18.0);
        priceIcon.setPreserveRatio(true);
        totalPriceLabel.setGraphic(priceIcon);

        priceHBox.getChildren().add(totalPriceLabel);


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
            ImageView buttonIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath))));
            buttonIcon.setFitHeight(22);
            buttonIcon.setFitWidth(22);
            buttonIcon.setPreserveRatio(true);
            button.setGraphic(buttonIcon);

            String iconName = iconPath.substring(iconPath.lastIndexOf("/") + 1);

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

        orderVBox.getChildren().addAll(
                orderNumberHBox,
                infoPane,
                orderItemsListView,
                priceHBox,
                actionButtonsHBox
        );

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
            if (orderObservableList.indexOf(orderVBox) != 0) {
                orderObservableList.remove(orderVBox);
                orderObservableList.addFirst(orderVBox);
            } else {
                int originalPosition = orderOriginalPositions.get(orderId);
                orderObservableList.remove(orderVBox);
                orderObservableList.add(originalPosition, orderVBox);
            }
        }
    }

    private void handleSendAction(Order order) {
        System.out.println("Send action for Order #" + order.getOrderNumber());
        List<StatusCode> statusCodes = orderAPI.putOrderStatus((String) order.getMetadataValue("order_id"), Order.OrderStatus.COMPLETED);
        if (Exceptions.isSuccessful(statusCodes)) {
            removeOrderFromList(order);
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
    protected void onSettingsButtonClick() {
    }

    @FXML
    protected void onLogoutButtonClick() {
        GuiCommon.logout(logoutbutton);
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
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

        public void clearCompletedLabels() {
            labelListView.getItems().clear();
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