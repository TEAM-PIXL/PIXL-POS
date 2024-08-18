package com.example.pixlpos.database;

import com.example.pixlpos.constructs.MenuItem;
import com.example.pixlpos.constructs.Users;
import com.example.pixlpos.constructs.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

public class DataStore implements IMenuItemStore, IUserStore {

    private static DataStore instance = null;
    private ObservableList<Users> users;
    private ObservableList<MenuItem> menuItems;
    private ObservableList<Order> orders;

    private DataStore() {
        users = FXCollections.observableArrayList();
        menuItems = FXCollections.observableArrayList();
        orders = FXCollections.observableArrayList();

        // Sample users
        users.add(new Users("waiter", "waiter", "Waiter", "Waiter"));
        users.add(new Users("cook", "cook", "Cook", "Cook"));
        // Sample menu items
        menuItems.add(new MenuItem("Chicken Curry", "Delicious chicken curry", 15.49));
        menuItems.add(new MenuItem("Pizza", "Cheesy pizza with toppings", 18.99));
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    @Override
    public ObservableList<Users> getUsers() {
        return users;
    }

    @Override
    public void addUser(Users user) {
        users.add(user);
    }

    @Override
    public void removeUser(Users user) {
        users.remove(user);
    }

    @Override
    public Users getUser(String username) {
        return users.stream().filter(user -> user.getUsername().equals(username)).findFirst().orElse(null);
    }

    @Override
    public void updateUser(Users user) {
        int index = users.indexOf(user);
        if (index != -1) {
            users.set(index, user);
        }
    }

    @Override
    public void clearUsers() {
        users.clear();
    }

    @Override
    public ObservableList<MenuItem> getMenuItems() {
        return menuItems;
    }

    @Override
    public void addMenuItem(MenuItem item) {
        menuItems.add(item);
    }

    @Override
    public void removeMenuItem(MenuItem item) {
        menuItems.remove(item);
    }

    @Override
    public MenuItem getMenuItem(String itemName) {
        return menuItems.stream().filter(item -> item.getItemName().equals(itemName)).findFirst().orElse(null);
    }

    @Override
    public void updateMenuItem(MenuItem item) {
        int index = menuItems.indexOf(item);
        if (index != -1) {
            menuItems.set(index, item);
        }
    }

    @Override
    public void clearMenuItems() {
        menuItems.clear();
    }

    public void setMenuItems(List<MenuItem> items) {
        menuItems.setAll(items);
    }

    public ObservableList<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public void clearData() {
        clearMenuItems();
        clearUsers();
    }
}