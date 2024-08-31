package teampixl.com.pixlpos.database.interfaces;


import javafx.collections.ObservableList;
import teampixl.com.pixlpos.constructs.Ingredients;
import teampixl.com.pixlpos.constructs.MenuItem;

import java.util.Map;

public interface IMenuItemStore {
    ObservableList<MenuItem> getMenuItems();
    void addMenuItem(MenuItem item);
    void removeMenuItem(MenuItem item);
    MenuItem getMenuItem(String itemName);
    void updateMenuItem(MenuItem item);
    Map<String, Object> getMenuItemIngredients(MenuItem menuItem);
    void updateMenuItemIngredient(MenuItem menuItem, Ingredients ingredient, Object numeral);
    void removeMenuItemIngredient(MenuItem menuItem, Ingredients ingredient);
    void addMenuItemIngredient(MenuItem menuItem, Ingredients ingredient, Object numeral);
}


