package com.example.pixlpos.controllers.cookconsole;

import com.example.pixlpos.POSApplication;
import com.example.pixlpos.constructs.Order;
import com.example.pixlpos.database.DataStore;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.util.Map;
import java.util.List;

public class DocketsScreenController {

    @FXML
    private HBox ordersContainer;

    @FXML
    private Button goBackButton;

    @FXML
    private Button refreshButton;

    @FXML
    protected void onGoBackButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/cookconsole/cook-landing.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), POSApplication.WIDTH, POSApplication.HEIGHT);
            Stage stage = (Stage) goBackButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Cook Landing");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        updateOrders();
    }

    private void updateOrders() {
        ordersContainer.getChildren().clear();
        List<Order> orders = DataStore.getInstance().getOrders();
        for (Order order : orders) {
            VBox orderVBox = new VBox();
            orderVBox.setPadding(new Insets(10));
            orderVBox.setSpacing(10);
            orderVBox.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
            for (Map.Entry<String, Integer> entry : order.getItems().entrySet()) {
                Label itemLabel = new Label(order.getItemWithQuantity(entry.getKey()));
                orderVBox.getChildren().add(itemLabel);
            }
            Label totalLabel = new Label("Total: $" + String.format("%.2f", order.getTotal()));
            orderVBox.getChildren().add(totalLabel);
            ordersContainer.getChildren().add(orderVBox);
        }
    }

    @FXML
    protected void onRefreshButtonClick() {
        updateOrders();
    }
}