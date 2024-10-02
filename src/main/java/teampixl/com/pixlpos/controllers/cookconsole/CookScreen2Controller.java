package teampixl.com.pixlpos.controllers.cookconsole;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import teampixl.com.pixlpos.common.GuiCommon;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CookScreen2Controller
{
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
        ObservableList<VBox> ordernum = FXCollections.observableArrayList();
        ListProperty<VBox> listProperty = new SimpleListProperty<>(ordernum);
        orderList.itemsProperty().bindBidirectional(listProperty);
        listProperty.addListener((observable, oldList, newList) -> {
            orders.setText(orderList.getItems().size() + " Orders");

        });
        DynamicLabelManager dynamicLabelManager = new DynamicLabelManager(completedOrders);
        OrderManager orderManager = new OrderManager(orderList,dynamicLabelManager);

        orderManager.addOrder("00001", 2, 3, 20.0);
        orderManager.addOrder("00002", 4, 5, 40.0);
        orderManager.addOrder("00003", 6, 7, 60.0);
        orderManager.addOrder("00004", 8, 9, 80.0);
        orderManager.addOrder("00005", 10, 11, 100.0);



    }

    @FXML
    protected void onLogoutButtonClick() {
        // Handle exit button click
        Stage stage = (Stage) logoutbutton.getScene().getWindow();
        GuiCommon.loadScene(GuiCommon.LOGIN_SCREEN_FXML, GuiCommon.LOGIN_SCREEN_TITLE, stage);
    }






    /**
     * This class is used to manage dynamic labels
     */
    public static class DynamicLabelManager {
        private final ListView<Label> labelListView;

        public DynamicLabelManager(ListView<Label> labelListView) {
            this.labelListView = labelListView;

        }

        public void addLabel(String name) {
            Label newLabel = new Label(name);
            // Apply a dummy style class
            newLabel.getStyleClass().add("docket-label");
            labelListView.getItems().add(newLabel);
            System.out.println("Added Label with text: " + name);
        }

        public void removeLabelByIndex(int index) {
            if (index >= 0 && index < labelListView.getItems().size()) {
                Label removedLabel = labelListView.getItems().remove(index);
                System.out.println("Removed Label: " + removedLabel.getText());
            } else {
                System.out.println("Invalid index: " + index);
            }
        }

        public void removeLabelByText(String text) {
            for (Label label : labelListView.getItems()) {
                if (label.getText().equals(text)) {
                    labelListView.getItems().remove(label);
                    System.out.println("Removed Label: " + text);
                    return;
                }
            }
            System.out.println("Label with text '" + text + "' not found.");
        }

        public void clearAllLabels() {
            labelListView.getItems().clear();
            System.out.println("All labels cleared.");
        }

        public Label getLabelByIndex(int index) {
            if (index >= 0 && index < labelListView.getItems().size()) {
                return labelListView.getItems().get(index);
            }
            return null;
        }

        public int getLabelCount() {
            return labelListView.getItems().size();
        }
    }








    /**
     * =================================================================================================
     * This class is used to manage dynamic orders
     * =================================================================================================
     */
    public static class OrderManager {
        private final ListView<VBox> orderListView;
        private final Map<String, VBox> orderMap;
        private final DynamicLabelManager dynamicLabelManager;


        public OrderManager(ListView<VBox> orderListView,DynamicLabelManager dynamicLabelManager) {
            this.orderListView = orderListView;
            this.orderMap = new HashMap<>();
            this.dynamicLabelManager = dynamicLabelManager;
        }

        public void addOrder(String orderNumber, int customerCount, int tableNumber,double totalPrice) {
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

            Label orderNumberLabel = new Label(orderNumber);
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

            // Price Hbox
            // Create the main HBox
            HBox price = new HBox();
            price.setAlignment(Pos.CENTER);
            Label totalPriceLabel = new Label("$ " + String.valueOf(totalPrice));
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
            price.getChildren().add(totalPriceLabel);

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
                        button.setOnAction(event -> handleTrashAction(orderNumber));
                        break;
                    case "flag_icon.png":
                        button.setOnAction(event -> handleFlagAction(orderNumber));
                        break;
                    case "send_icon.png":
                        button.setOnAction(event -> handleSendAction(orderNumber));
                        break;
                }

                actionButtonsHBox.getChildren().add(button);
            }

            orderVBox.getChildren().addAll(orderNumberHBox, infoPane, orderItemsListView,price, actionButtonsHBox);
            orderListView.heightProperty().addListener((obs, oldVal, newVal) -> {
                orderVBox.setMaxHeight(newVal.doubleValue()-30);
            });
            orderListView.getItems().add(orderVBox);
            orderMap.put(orderNumber, orderVBox);
            System.out.println("Added Order: " + orderNumber);
        }

        private void handleTrashAction(String orderNumber) {
            System.out.println("Trash action for Order #" + orderNumber);
            removeOrderByNumber(orderNumber);
        }

        private void handleFlagAction(String orderNumber) {
            System.out.println("Flag action for Order #" + orderNumber);
            // Add your flag action logic here
        }

        private void handleSendAction(String orderNumber) {
            System.out.println("Send action for Order #" + orderNumber);
            dynamicLabelManager.addLabel("Order #" + orderNumber + " @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
            removeOrderByNumber(orderNumber);
        }

        public void removeOrderByNumber(String orderNumber) {
            VBox orderVBox = orderMap.remove(orderNumber);
            if (orderVBox != null) {
                orderListView.getItems().remove(orderVBox);
                System.out.println("Removed Order: " + orderNumber);
            } else {
                System.out.println("Order not found: " + orderNumber);
            }
        }

        public void clearAllOrders() {
            orderListView.getItems().clear();
            orderMap.clear();
            System.out.println("All orders cleared.");
        }

        public VBox getOrderByNumber(String orderNumber) {
            return orderMap.get(orderNumber);
        }

        public int getOrderCount() {
            return orderMap.size();
        }

        public void addItemToOrder(String orderNumber, String itemName) {
            VBox orderVBox = orderMap.get(orderNumber);
            if (orderVBox != null) {
                ListView<Label> orderItemsListView = (ListView<Label>) orderVBox.getChildren().get(2);
                Label newItem = new Label("1x " + itemName);
                newItem.getStyleClass().add("docket-label");
                orderItemsListView.getItems().add(newItem);
                System.out.println("Added item to Order " + orderNumber + ": " + itemName);
            } else {
                System.out.println("Order not found: " + orderNumber);
            }
        }

        public void removeItemFromOrder(String orderNumber, int itemIndex) {
            VBox orderVBox = orderMap.get(orderNumber);
            if (orderVBox != null) {
                ListView<Label> orderItemsListView = (ListView<Label>) orderVBox.getChildren().get(2);
                if (itemIndex >= 0 && itemIndex < orderItemsListView.getItems().size()) {
                    Label removedItem = orderItemsListView.getItems().remove(itemIndex);
                    System.out.println("Removed item from Order " + orderNumber + ": " + removedItem.getText());
                } else {
                    System.out.println("Invalid item index for Order " + orderNumber);
                }
            } else {
                System.out.println("Order not found: " + orderNumber);
            }
        }
    }
}
