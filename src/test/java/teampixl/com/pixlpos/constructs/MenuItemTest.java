package teampixl.com.pixlpos.constructs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemTest {

    private MenuItem menuItem;
    private Ingredients flour;
    private Ingredients sugar;

    @BeforeEach
    void setUp() {
        // Create some ingredients
        flour = new Ingredients("Flour", Ingredients.StockStatus.INSTOCK, false, Ingredients.UnitType.KG, 5.0, "Organic white flour");
        sugar = new Ingredients("Sugar", Ingredients.StockStatus.LOWSTOCK, true, Ingredients.UnitType.KG, 2.5, "Brown sugar");

        // Create a menu item
        menuItem = new MenuItem("Cake", 10.0, MenuItem.ItemType.DESSERT, true, "Delicious chocolate cake", null);
    }

    @Test
    void testAddIngredient() {
        menuItem.addIngredient(flour);
        assertTrue(menuItem.hasIngredient((String) flour.getMetadata().metadata().get("uuid")));
    }

    @Test
    void testRemoveIngredient() {
        menuItem.addIngredient(flour);
        menuItem.removeIngredient(flour);
        assertFalse(menuItem.hasIngredient((String) flour.getMetadata().metadata().get("uuid")));
    }

    @Test
    void testUpdateIngredient() {
        menuItem.addIngredient(flour);
        menuItem.updateIngredient(String.valueOf(flour), sugar);
        assertFalse(menuItem.hasIngredient((String) flour.getMetadata().metadata().get("uuid")));
        assertTrue(menuItem.hasIngredient((String) sugar.getMetadata().metadata().get("uuid")));
    }

    @Test
    void testClearIngredients() {
        menuItem.addIngredient(flour);
        menuItem.addIngredient(sugar);
        menuItem.clearIngredients();
        assertTrue(menuItem.getIngredients().isEmpty());
    }

    @Test
    void testMenuItemWithoutIngredients() {
        assertTrue(menuItem.getIngredients().isEmpty());
    }

    @Test
    void testMenuItemToString() {
        menuItem.addIngredient(flour);
        String menuItemString = menuItem.toString();

        // Check if ingredient name and UUID are in the string representation
        assertTrue(menuItemString.contains("Flour"), "Ingredient name not found in MenuItem toString");
        assertTrue(menuItemString.contains((String) flour.getMetadata().metadata().get("uuid")), "Ingredient UUID not found in MenuItem toString");
    }
}

