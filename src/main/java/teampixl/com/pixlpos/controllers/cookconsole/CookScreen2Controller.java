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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private ObservableList<VBox> orderObservableList = FXCollections.observableArrayList();
    private Map<String, VBox> orderMap = new LinkedHashMap<>();
    private Map<String, Integer> orderOriginalPositions = new HashMap<>();

    private DynamicLabelManager dynamicLabelManager;

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private AnimationTimer datetime = new AnimationTimer() {
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

        loadOrdersFromDatabaseAsync();
    }

    private void loadOrdersFromDatabaseAsync() {
        Task<Void> loadOrdersTask = new Task<>() {
            @Override
            protected Void call() {
                OrderAPI orderAPI = OrderAPI.getInstance();
                MenuAPI menuAPI = MenuAPI.getInstance();

                orderAPI.reloadOrders();
                List<Order> activeOrders = orderAPI.getOrders().stream()
                        .filter(order -> order.getMetadataValue("order_status") == Order.OrderStatus.SENT)
                        .toList();

                Platform.runLater(() -> {
                    orderObservableList.clear();
                    orderMap.clear();
                    orderOriginalPositions.clear();
                });

                for (int i = 0; i < activeOrders.size(); i++) {
                    Order order = activeOrders.get(i);
                    int position = i;
                    Platform.runLater(() -> addOrderToList(order, position, menuAPI, orderAPI));
                }

                Platform.runLater(() -> orders.setText(orderObservableList.size() + " Orders"));

                List<Order> completedOrdersList = orderAPI.getOrders().stream()
                        .filter(order -> order.getMetadataValue("order_status") == Order.OrderStatus.COMPLETED)
                        .toList();

                for (Order order : completedOrdersList) {
                    long updatedAt = (long) order.getMetadataValue("updated_at");
                    LocalDateTime updatedAtDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(updatedAt), ZoneId.systemDefault());
                    String labelText = "Order #" + order.getOrderNumber() + " @ " + updatedAtDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                    Platform.runLater(() -> dynamicLabelManager.addCompletedLabel(labelText));
                }

                return null;
            }
        };

        executorService.submit(loadOrdersTask);
    }

    private void addOrderToList(Order order, int position, MenuAPI menuAPI, OrderAPI orderAPI) {
        VBox orderVBox = createOrderVBox(order, menuAPI, orderAPI);
        orderObservableList.add(orderVBox);
        String orderId = (String) order.getMetadataValue("order_id");
        orderMap.put(orderId, orderVBox);
        orderOriginalPositions.put(orderId, position);
    }

    private VBox createOrderVBox(Order order, MenuAPI menuAPI, OrderAPI orderAPI) {
        String orderId = (String) order.getMetadataValue("order_id");
        int orderNumber = order.getOrderNumber();
        int customerCount = (int) order.getMetadataValue("customers");
        int tableNumber = (int) order.getMetadataValue("table_number");

        VBox orderVBox = new VBox();
        orderVBox.setMaxWidth(300.0);
        orderVBox.setPrefWidth(300.0);
        orderVBox.setMinWidth(300.0);
        orderVBox.getStyleClass().add("order-container");
        VBox.setVgrow(orderVBox, Priority.ALWAYS);

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

        AnchorPane infoPane = new AnchorPane();
        infoPane.setPrefHeight(60.0);
        infoPane.getStyleClass().add("order-highlevel-container");

        Label customerLabel = new Label(String.format("%02d", customerCount));
        customerLabel.getStyleClass().add("amount-label");
        Label customerInnerLabel = new Label("Customers:");
        customerInnerLabel.getStyleClass().add("customers-label");
        ImageView customerIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/teampixl/com/pixlpos/images/cookicons/user_plus_icon.png"))));
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
        ImageView tableIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/teampixl/com/pixlpos/images/cookicons/hash_icon.png"))));
        tableIcon.setFitHeight(22);
        tableIcon.setFitWidth(22);
        tableIcon.setPreserveRatio(true);
        tableInnerLabel.setGraphic(tableIcon);
        tableLabel.setGraphic(tableInnerLabel);
        AnchorPane.setLeftAnchor(tableLabel, 166.0);
        AnchorPane.setTopAnchor(tableLabel, 15.0);

        infoPane.getChildren().addAll(customerLabel, tableLabel);

        ListView<Label> orderItemsListView = new ListView<>();
        orderItemsListView.getStyleClass().add("list-pane");
        VBox.setVgrow(orderItemsListView, Priority.ALWAYS);
        DynamicLabelManager internaldynamicLabelManager = new DynamicLabelManager(orderItemsListView);

        Task<Void> loadOrderItemsTask = new Task<>() {
            @Override
            protected Void call() {
                Map<MenuItem, Integer> orderItems = orderAPI.getOrderItemsById(orderId);
                Map<MenuItem, List<String>> itemNotes = OrderUtil.deserializeItemNotes((String) order.getDataValue("special_requests"), menuAPI);

                double totalPrice = 0.0;

                for (Map.Entry<MenuItem, Integer> entry : orderItems.entrySet()) {
                    MenuItem menuItem = entry.getKey();
                    int quantity = entry.getValue();
                    double price = (double) menuItem.getMetadataValue("price");
                    totalPrice += price * quantity;

                    List<String> notes = itemNotes.getOrDefault(menuItem, new ArrayList<>());
                    Platform.runLater(() -> internaldynamicLabelManager.addOrderLabel(quantity + "x " + menuItem.getMetadataValue("itemName")));

                    for (String note : notes) {
                        Platform.runLater(() -> internaldynamicLabelManager.addOrderLabel("    * " + note));
                    }
                }

                double finalTotalPrice = totalPrice;
                Platform.runLater(() -> {
                    HBox priceHBox = new HBox();
                    priceHBox.setAlignment(Pos.CENTER);
                    Label totalPriceLabel = new Label("$ " + String.format("%.2f", finalTotalPrice));
                    totalPriceLabel.getStyleClass().add("amount-label");
                    Label priceLabel = new Label("Price:");
                    priceLabel.getStyleClass().add("customers-label");
                    ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/teampixl/com/pixlpos/images/cookicons/dollar_sign_icon.png"))));
                    imageView.setFitHeight(22.0);
                    imageView.setFitWidth(22.0);
                    imageView.setPickOnBounds(true);
                    imageView.setPreserveRatio(true);
                    priceLabel.setGraphic(imageView);
                    totalPriceLabel.setGraphic(priceLabel);
                    priceHBox.getChildren().add(totalPriceLabel);

                    orderVBox.getChildren().add(priceHBox);
                });

                return null;
            }
        };

        executorService.submit(loadOrderItemsTask);

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
                    button.setOnAction(event -> handleTrashAction(order, orderAPI));
                    break;
                case "flag_icon.png":
                    button.setOnAction(event -> handleFlagAction(order));
                    break;
                case "send_icon.png":
                    button.setOnAction(event -> handleSendAction(order, orderAPI));
                    break;
            }

            actionButtonsHBox.getChildren().add(button);
        }

        orderVBox.getChildren().addAll(orderNumberHBox, infoPane, orderItemsListView, actionButtonsHBox);

        return orderVBox;
    }

    private void handleTrashAction(Order order, OrderAPI orderAPI) {
        Task<Void> deleteOrderTask = new Task<>() {
            @Override
            protected Void call() {
                List<StatusCode> statusCodes = orderAPI.deleteOrder((String) order.getMetadataValue("order_id"));
                if (Exceptions.isSuccessful(statusCodes)) {
                    Platform.runLater(() -> removeOrderFromList(order));
                } else {
                    Platform.runLater(() -> showErrorDialog(Exceptions.returnStatus("Failed to delete order:", statusCodes)));
                }
                return null;
            }
        };

        executorService.submit(deleteOrderTask);
    }

    private void handleFlagAction(Order order) {
        String orderId = (String) order.getMetadataValue("order_id");
        VBox orderVBox = orderMap.get(orderId);

        if (orderObservableList.contains(orderVBox)) {
            if (orderObservableList.indexOf(orderVBox) != 0) {
                orderObservableList.remove(orderVBox);
                orderObservableList.add(0, orderVBox);
            } else {
                int originalPosition = orderOriginalPositions.get(orderId);
                orderObservableList.remove(orderVBox);
                orderObservableList.add(originalPosition, orderVBox);
            }
        }
    }

    private void handleSendAction(Order order, OrderAPI orderAPI) {
        Task<Void> completeOrderTask = new Task<>() {
            @Override
            protected Void call() {
                List<StatusCode> statusCodes = orderAPI.putOrderStatus((String) order.getMetadataValue("order_id"), Order.OrderStatus.COMPLETED);
                if (Exceptions.isSuccessful(statusCodes)) {
                    Platform.runLater(() -> {
                        removeOrderFromList(order);
                        dynamicLabelManager.addCompletedLabel("Order #" + order.getOrderNumber() + " @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
                    });
                } else {
                    Platform.runLater(() -> showErrorDialog(Exceptions.returnStatus("Failed to complete order:", statusCodes)));
                }
                return null;
            }
        };

        executorService.submit(completeOrderTask);
    }

    private void removeOrderFromList(Order order) {
        String orderId = (String) order.getMetadataValue("order_id");
        VBox orderVBox = orderMap.remove(orderId);
        if (orderVBox != null) {
            orderObservableList.remove(orderVBox);
            orderOriginalPositions.remove(orderId);
            orders.setText(orderObservableList.size() + " Orders");
        }
    }

    @FXML
    protected void onLogoutButtonClick() {
        GuiCommon.logout(logoutbutton);
        executorService.shutdown();
    }

    public static class DynamicLabelManager {
        private final ListView<Label> labelListView;

        public DynamicLabelManager(ListView<Label> labelListView) {
            this.labelListView = labelListView;
        }

        public void addCompletedLabel(String text) {
            Label newLabel = new Label(text);
            newLabel.getStyleClass().add("docket-label");
            labelListView.getItems().add(newLabel);
        }

        public void addOrderLabel(String itemName) {
            Label newLabel = new Label(itemName);
            newLabel.getStyleClass().add("order-label");
            labelListView.getItems().add(newLabel);
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

