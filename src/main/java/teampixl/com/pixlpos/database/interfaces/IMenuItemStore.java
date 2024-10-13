package teampixl.com.pixlpos.database.interfaces;


import javafx.collections.ObservableList;
import teampixl.com.pixlpos.models.Ingredients;
import teampixl.com.pixlpos.models.MenuItem;

import java.util.Map;

public interface IMenuItemStore {
    ObservableList<MenuItem> readMenuItems();
    void createMenuItem(MenuItem item);
    void deleteMenuItem(MenuItem item);
    MenuItem getMenuItem(String itemName);
    MenuItem getMenuItemById(String itemId);
    void updateMenuItem(MenuItem item);
    Map<String, Object> readMenuItemIngredients(MenuItem menuItem);
    void updateMenuItemIngredient(MenuItem menuItem, Ingredients ingredient, Object numeral);
    void deleteMenuItemIngredient(MenuItem menuItem, Ingredients ingredient);
    void createMenuItemIngredient(MenuItem menuItem, Ingredients ingredient, Object numeral);
}


