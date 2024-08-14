package com.example.pixlpos.database;

import com.example.pixlpos.constructs.MenuItem;
import javafx.collections.ObservableList;

public interface IMenuItemStore {
    ObservableList<MenuItem> getMenuItems();
    void addMenuItem(MenuItem item);
    void removeMenuItem(MenuItem item);
    MenuItem getMenuItem(String itemName);
    void updateMenuItem(MenuItem item);
    void clearMenuItems();
}
