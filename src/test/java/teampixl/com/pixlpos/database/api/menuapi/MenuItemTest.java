package teampixl.com.pixlpos.database.api.menuapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teampixl.com.pixlpos.database.api.ingredientsapi.Ingredients;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemTest {

    private MenuItem menuItem;
    private Ingredients tomatoSauce;

    @BeforeEach
    void setUp() {
        tomatoSauce = new Ingredients("Tomato Sauce", "Organic tomato sauce");
        menuItem = new MenuItem("Pizza", 14.99, MenuItem.ItemType.MAIN, true, "Delicious pizza with tomato sauce and cheese", MenuItem.DietaryRequirement.NONE);
    }

    @Test
    void testAddIngredient() {
        menuItem.addIngredient(tomatoSauce, 2.5);
        assertTrue(menuItem.hasIngredient((String) tomatoSauce.getMetadata().metadata().get("ingredient_id")));
        MenuItem.IngredientAmount addedIngredientAmount = menuItem.getIngredients().get((String) tomatoSauce.getMetadata().metadata().get("ingredient_id"));
        assertNotNull(addedIngredientAmount);
        assertEquals("Tomato Sauce", addedIngredientAmount.ingredient().getMetadata().metadata().get("itemName"));
        assertEquals(2.5, addedIngredientAmount.numeral());
    }

    @Test
    void testRemoveIngredient() {
        menuItem.addIngredient(tomatoSauce, 2.5);
        menuItem.removeIngredient(tomatoSauce);
        assertFalse(menuItem.hasIngredient((String) tomatoSauce.getMetadata().metadata().get("ingredient_id")));
    }

    @Test
    void testUpdateIngredient() {
        menuItem.addIngredient(tomatoSauce, 2.5);
        Ingredients updatedTomatoSauce = new Ingredients("Tomato Sauce", "Organic tomato sauce - updated");
        menuItem.updateIngredient((String) tomatoSauce.getMetadata().metadata().get("ingredient_id"), updatedTomatoSauce, 3.0);
        MenuItem.IngredientAmount updatedIngredientAmount = menuItem.getIngredients().get((String) updatedTomatoSauce.getMetadata().metadata().get("ingredient_id"));
        assertNotNull(updatedIngredientAmount);
        assertEquals("Organic tomato sauce - updated", updatedIngredientAmount.ingredient().getData().get("notes"));
        assertEquals(3.0, updatedIngredientAmount.numeral());
    }

    @Test
    void testMenuItemToString() {
        String expected = String.format("MenuItem{Metadata: %s, Data: %s, Ingredients: %s}", new HashMap<>(menuItem.getMetadata().metadata()), new HashMap<>(menuItem.getData()), menuItem.getIngredients());
        assertEquals(expected, menuItem.toString());
    }

    @Test
    void testTimestampUpdateOnIngredientModification() throws InterruptedException {
        menuItem.addIngredient(tomatoSauce, 2.5);
        long timestampBeforeUpdate = (long) menuItem.getMetadata().metadata().get("updated_at");
        Thread.sleep(10);
        Ingredients updatedTomatoSauce = new Ingredients("Tomato Sauce", "Organic tomato sauce - updated");
        menuItem.updateIngredient((String) tomatoSauce.getMetadata().metadata().get("ingredient_id"), updatedTomatoSauce, 3.0);
        long timestampAfterUpdate = (long) menuItem.getMetadata().metadata().get("updated_at");
        assertTrue(timestampAfterUpdate > timestampBeforeUpdate);
    }
}




