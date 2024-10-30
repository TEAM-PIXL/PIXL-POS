package teampixl.com.pixlpos.database.interfaces;


import javafx.collections.ObservableList;
import teampixl.com.pixlpos.models.Ingredients;
import teampixl.com.pixlpos.models.MenuItem;

import java.util.Map;

/**
 * Interface for MenuItemStore
 */
public interface IMenuItemStore {

    /**
     * Read all menu items from the database
     * @return ObservableList of MenuItems
     */
    ObservableList<MenuItem> readMenuItems();

    /**
     * Create a new menu item
     * @param item MenuItem to be created
     */
    void createMenuItem(MenuItem item);

    /**
     * Delete an existing menu item
     * @param item MenuItem to be deleted
     */
    void deleteMenuItem(MenuItem item);

    /**
     * Get a menu item by its name
     * @param itemName Name of the menu item
     * @return MenuItem
     */
    MenuItem getMenuItem(String itemName);

    /**
     * Get a menu item by its id
     * @param itemId Id of the menu item
     * @return MenuItem
     */
    MenuItem getMenuItemById(String itemId);

    /**
     * Update an existing menu item
     * @param item MenuItem to be updated
     */
    void updateMenuItem(MenuItem item);

    /**
     * Read all ingredients of a menu item
     * @param menuItem MenuItem to read ingredients from
     * @return Map of ingredients and their quantities
     */
    Map<String, Object> readMenuItemIngredients(MenuItem menuItem);

    /**
     * Update an ingredient of a menu item
     * @param menuItem MenuItem to update ingredient
     * @param ingredient Ingredient to update
     * @param numeral New quantity of the ingredient
     */
    void updateMenuItemIngredient(MenuItem menuItem, Ingredients ingredient, Object numeral);

    /**
     * Delete an ingredient of a menu item
     * @param menuItem MenuItem to delete ingredient
     * @param ingredient Ingredient to delete
     */
    void deleteMenuItemIngredient(MenuItem menuItem, Ingredients ingredient);

    /**
     * Create an ingredient of a menu item
     * @param menuItem MenuItem to create ingredient
     * @param ingredient Ingredient to create
     * @param numeral Quantity of the ingredient
     */
    void createMenuItemIngredient(MenuItem menuItem, Ingredients ingredient, Object numeral);
}


