package com.example.pixlpos.controllers.waiterconsole;

import com.example.pixlpos.POSApplication;
import com.example.pixlpos.constructs.MenuItem;
import com.example.pixlpos.database.DataStore;
import com.example.pixlpos.constructs.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import java.io.IOException;
import java.util.Map;

public class NewOrderScreenController {

    @FXML
    private Button goBackButton;

    @FXML
    private ListView<MenuItem> menuListView;

    @FXML
    private ListView<HBox> orderSummaryListView;

    @FXML
    private Label totalLabel;

    @FXML
    private VBox rightPane;

    @FXML
    private Button addOrderButton;

    private ObservableList<MenuItem> menuItems;
    private ObservableList<HBox> orderSummaryItems = FXCollections.observableArrayList();
    private Order currentOrder;

    @FXML
    public void initialize() {
        menuItems = FXCollections.observableArrayList(DataStore.getInstance().getMenuItems());
        updateMenuListView();
        orderSummaryListView.setItems(orderSummaryItems);
        currentOrder = new Order();
    }

    private void updateMenuListView() {
        menuListView.getItems().clear();
        menuListView.getItems().addAll(menuItems);
        menuListView.setCellFactory(new Callback<ListView<MenuItem>, ListCell<MenuItem>>() {
            @Override
            public ListCell<MenuItem> call(ListView<MenuItem> listView) {
                return new ListCell<MenuItem>() {
                    private CheckBox checkBox = new CheckBox();

                    @Override
                    protected void updateItem(MenuItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            checkBox.setText("$" + item.getPrice() + " - " + item.getItemName());
                            checkBox.setOnAction(event -> {
                                if (checkBox.isSelected()) {
                                    currentOrder.addItem(item.getItemName(), item.getPrice());
                                    addItemToOrderSummary(item.getItemName(), item.getPrice());
                                } else {
                                    int quantity = currentOrder.getItems().getOrDefault(item.getItemName(), 1);
                                    currentOrder.removeItem(item.getItemName(), item.getPrice() * quantity);
                                    removeItemFromOrderSummary(item.getItemName());
                                }
                                updateTotal();
                            });
                            setGraphic(checkBox);
                        }
                    }
                };
            }
        });
    }

    private void addItemToOrderSummary(String itemName, double price) {
        HBox hbox = new HBox();
        Label itemLabel = new Label(itemName);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        TextField quantityField = new TextField("1");
        quantityField.setPrefWidth(40);
        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int quantity = Integer.parseInt(newValue);
                if (quantity < 1) {
                    quantity = 1;
                    quantityField.setText(String.valueOf(quantity));
                }
                currentOrder.setItemQuantity(itemName, quantity, price);
                updateTotal();
            } catch (NumberFormatException e) {
                quantityField.setText(oldValue);
            }
        });
        Button increaseButton = new Button("+");
        Button decreaseButton = new Button("-");
        increaseButton.setOnAction(event -> {
            int quantity = Integer.parseInt(quantityField.getText());
            quantityField.setText(String.valueOf(quantity + 1));
            currentOrder.addItem(itemName, price);
            updateTotal();
        });
        decreaseButton.setOnAction(event -> {
            int quantity = Integer.parseInt(quantityField.getText());
            if (quantity > 1) {
                quantityField.setText(String.valueOf(quantity - 1));
                currentOrder.removeItem(itemName, price);
                updateTotal();
            }
        });
        hbox.getChildren().addAll(itemLabel, spacer, decreaseButton, quantityField, increaseButton);
        hbox.setSpacing(10);
        orderSummaryItems.add(hbox);
    }

    private void removeItemFromOrderSummary(String itemName) {
        orderSummaryItems.removeIf(hbox -> {
            Label label = (Label) hbox.getChildren().get(0);
            return label.getText().equals(itemName);
        });
    }

    private void updateOrderSummary() {
        orderSummaryItems.clear();
        for (Map.Entry<String, Integer> entry : currentOrder.getItems().entrySet()) {
            addItemToOrderSummary(entry.getKey(), getItemPrice(entry.getKey()));
        }
        updateTotal();
    }

    private void updateTotal() {
        totalLabel.setText("Total: $" + String.format("%.2f", currentOrder.getTotal()));
    }

    private double getItemPrice(String item) {
        for (MenuItem menuItem : menuItems) {
            if (menuItem.getItemName().equals(item)) {
                return menuItem.getPrice();
            }
        }
        return 0.0;
    }

    @FXML
    protected void onGoBackButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(POSApplication.class.getResource("/fxml/waitercontroller/waiter-landing.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), POSApplication.WIDTH, POSApplication.HEIGHT);
            Stage stage = (Stage) goBackButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Waiter Landing");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onAddOrderButtonClick() {
        DataStore.getInstance().addOrder(currentOrder);
        System.out.println("Order added: " + currentOrder);
        currentOrder = new Order();
        orderSummaryItems.clear();
        updateTotal();
    }
}
