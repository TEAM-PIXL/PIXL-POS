package com.example.pixlpos.controllers.waiterconsole;

import com.example.pixlpos.constructs.MenuItem;
import com.example.pixlpos.database.DataStore;
import com.example.pixlpos.constructs.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.Map;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.util.Callback;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

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

    private void updateOrderSummary() {
        orderSummaryItems.clear();
        updateTotal();
    }

    private void addItemToOrderSummary(String itemName, double price) {
        for (HBox hBox : orderSummaryItems) {
            Label nameLabel = (Label) hBox.getChildren().get(0);
            Label quantityLabel = (Label) hBox.getChildren().get(1);
            Label priceLabel = (Label) hBox.getChildren().get(2);
            if (nameLabel.getText().equals(itemName)) {
                int quantity = Integer.parseInt(quantityLabel.getText().substring(1));
                quantityLabel.setText("x" + (quantity + 1));
                priceLabel.setText("$" + price * (quantity + 1));
                return;
            }
        }
        HBox hBox = new HBox();
        Label nameLabel = new Label(itemName);
        Label quantityLabel = new Label("x1");
        Label priceLabel = new Label("$" + price);
        hBox.getChildren().addAll(nameLabel, quantityLabel, priceLabel);
        HBox.setHgrow(hBox, Priority.ALWAYS);
        orderSummaryItems.add(hBox);
    }

    private void removeItemFromOrderSummary(String itemName) {
        orderSummaryItems.removeIf(hBox -> {
            Label nameLabel = (Label) hBox.getChildren().get(0);
            return nameLabel.getText().equals(itemName);
        });
    }

    private double getItemPrice(MenuItem item) {
        for (MenuItem menuItem : menuItems) {
            if (menuItem.equals(item)) {
                return menuItem.getPrice();
            }
        }
        return 0.0;
    }

    private void updateTotal() {
        double total = 0.0;
        for (HBox hBox : orderSummaryItems) {
            Label priceLabel = (Label) hBox.getChildren().get(2);
            total += Double.parseDouble(priceLabel.getText().substring(1));
        }
        totalLabel.setText("Total: $" + total);
    }

    @FXML
    protected void onAddOrderButtonClick() {
        DataStore.getInstance().addOrder(currentOrder);
        currentOrder = new Order();
        updateOrderSummary();
    }

    @FXML
    protected void onGoBackButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/waiter-landing.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage stage = (Stage) goBackButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Waiter Landing");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
