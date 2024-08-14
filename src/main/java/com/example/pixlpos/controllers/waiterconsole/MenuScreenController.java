package com.example.pixlpos.controllers.waiterconsole;

import com.example.pixlpos.POSApplication;
import com.example.pixlpos.constructs.MenuItem;
import com.example.pixlpos.database.DataStore;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.collections.ObservableList;
import java.io.IOException;

public class MenuScreenController {

    @FXML
    private ListView<String> menuListView;

    @FXML
    private Button goBackButton;

    private ObservableList<MenuItem> menuItems;

    @FXML
    public void initialize() {
        // Get the menu items from the singleton
        menuItems = DataStore.getInstance().getMenuItems();
        updateMenuListView();
    }

    private void updateMenuListView() {
        menuListView.getItems().clear();
        for (MenuItem item : menuItems) {
            menuListView.getItems().add(item.getItemName() + " - " + item.getDescription() + " - $" + item.getPrice());
        }
    }

    @FXML
    protected void onGoBackButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(POSApplication.class.getResource("/fxml/waiter-landing.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), POSApplication.WIDTH, POSApplication.HEIGHT);
            Stage stage = (Stage) goBackButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Waiter Landing");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
