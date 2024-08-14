package com.example.pixlpos.controllers.waiterconsole;

import com.example.pixlpos.POSApplication;
import com.example.pixlpos.database.DataStore;
import com.example.pixlpos.constructs.Order;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.collections.ObservableList;

public class OrdersScreenController {

    @FXML
    private Button goBackButton;

    @FXML
    private ListView<String> ordersListView;

    @FXML
    private ListView<String> orderDetailsListView;

    @FXML
    private Label orderTotalLabel;

    @FXML
    private Button markAsCompletedButton;

    private ObservableList<Order> orders;

    @FXML
    public void initialize() {
        // Get the orders from the singleton
        orders = FXCollections.observableArrayList(DataStore.getInstance().getOrders());
        updateOrderListView();
        ordersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showOrderDetails(newValue));
    }

    private void updateOrderListView() {
        ordersListView.getItems().clear();
        for (Order order : orders) {
            ordersListView.getItems().add(order.toString());
        }
    }

    private void showOrderDetails(String selectedOrder) {
        orderDetailsListView.getItems().clear();
        if (selectedOrder != null) {
            Order order = orders.stream().filter(o -> o.toString().equals(selectedOrder)).findFirst().orElse(null);
            if (order != null) {
                for (Map.Entry<String, Integer> entry : order.getItems().entrySet()) {
                    orderDetailsListView.getItems().add(order.getItemWithQuantity(entry.getKey()));
                }
                orderTotalLabel.setText("Total: $" + String.format("%.2f", order.getTotal()));
            }
        }
    }

    @FXML
    protected void onGoBackButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(POSApplication.class.getResource("/fxml/waiterconsole/waiter-landing.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), POSApplication.WIDTH, POSApplication.HEIGHT);
            Stage stage = (Stage) goBackButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Waiter Landing");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onMarkAsCompletedButtonClick() {
        String selectedOrder = ordersListView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            orders.removeIf(order -> order.toString().equals(selectedOrder));
            DataStore.getInstance().getOrders().removeIf(order -> order.toString().equals(selectedOrder));
            updateOrderListView();
            orderDetailsListView.getItems().clear();
            orderTotalLabel.setText("Total: $0.00");
        }
    }
}
