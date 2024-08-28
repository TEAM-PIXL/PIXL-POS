package teampixl.com.pixlpos.constructs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;


class MenuItemTest {

    private MenuItem menuItem;
    private Ingredients tomatoSauce;
    private Ingredients cheese;

    @BeforeEach
    void setUp() {
        // Create ingredients
        tomatoSauce = new Ingredients("Tomato Sauce", Ingredients.StockStatus.INSTOCK, false, Ingredients.UnitType.KG, 1.5, "Organic tomato sauce");
        cheese = new Ingredients("Cheese", Ingredients.StockStatus.INSTOCK, false, Ingredients.UnitType.KG, 0.5, "Mozzarella cheese");

        // Create a menu item
        menuItem = new MenuItem("Pizza", 14.99, MenuItem.ItemType.MAIN, true, "Delicious pizza with tomato sauce and cheese", MenuItem.DietaryRequirement.NONE);
    }

    @Test
    void testAddIngredient() {
        menuItem.addIngredient(tomatoSauce);

        // Check if ingredient is added
        assertTrue(menuItem.hasIngredient((String) tomatoSauce.getMetadata().metadata().get("uuid")));

        // Verify that the ingredient is in the ingredients map
        Ingredients addedIngredient = menuItem.getIngredients().get((String) tomatoSauce.getMetadata().metadata().get("uuid"));
        assertNotNull(addedIngredient);
        assertEquals("Tomato Sauce", addedIngredient.getMetadata().metadata().get("itemName"));
    }

    @Test
    void testRemoveIngredient() {
        menuItem.addIngredient(tomatoSauce);

        // Remove ingredient and check if it's removed
        menuItem.removeIngredient(tomatoSauce);
        assertFalse(menuItem.hasIngredient((String) tomatoSauce.getMetadata().metadata().get("uuid")));
    }

    @Test
    void testUpdateIngredient() {
      menuItem.addIngredient(tomatoSauce);

        // Update the ingredient
        Ingredients updatedTomatoSauce = new Ingredients("Tomato Sauce", Ingredients.StockStatus.INSTOCK, false, Ingredients.UnitType.KG, 2.0, "Organic tomato sauce - updated");
        menuItem.updateIngredient((String) tomatoSauce.getMetadata().metadata().get("uuid"), updatedTomatoSauce);

        // Check if the ingredient is updated
        Ingredients updatedIngredient = menuItem.getIngredients().get((String) tomatoSauce.getMetadata().metadata().get("uuid"));
        assertNotNull(updatedIngredient);
        assertEquals(2.0, updatedIngredient.getData().get("numeral"));
    }


    @Test
    void testMenuItemToString() {
        String expected = String.format("MenuItem{Metadata: %s, Data: %s}", new HashMap<>(menuItem.getMetadata().metadata()), new HashMap<>(menuItem.getData()));
        assertEquals(expected, menuItem.toString());
    }

    @Test
    void testTimestampUpdateOnIngredientModification() throws InterruptedException {
        menuItem.addIngredient(tomatoSauce);

        // Get the timestamp before updating the ingredient
        long timestampBeforeUpdate = (long) menuItem.getMetadata().metadata().get("updated_at");

        // Add a small delay to ensure the timestamp difference is noticeable
        Thread.sleep(10);

        // Update the ingredient
        Ingredients updatedTomatoSauce = new Ingredients("Tomato Sauce", Ingredients.StockStatus.INSTOCK, false, Ingredients.UnitType.KG, 2.0, "Organic tomato sauce - updated");
        menuItem.updateIngredient((String) tomatoSauce.getMetadata().metadata().get("uuid"), updatedTomatoSauce);

        // Get the timestamp after updating the ingredient
        long timestampAfterUpdate = (long) menuItem.getMetadata().metadata().get("updated_at");

        // Check if the timestamp is updated
        assertTrue(timestampAfterUpdate > timestampBeforeUpdate);
    }
}


