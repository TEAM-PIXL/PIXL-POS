package teampixl.com.pixlpos.constructs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        menuItem.addIngredient(tomatoSauce);
        assertTrue(menuItem.hasIngredient((String) tomatoSauce.getMetadata().metadata().get("ingredient_id")));
        Ingredients addedIngredient = menuItem.getIngredients().get((String) tomatoSauce.getMetadata().metadata().get("ingredient_id"));
        assertNotNull(addedIngredient);
        assertEquals("Tomato Sauce", addedIngredient.getMetadata().metadata().get("itemName"));
    }

    @Test
    void testRemoveIngredient() {
        menuItem.addIngredient(tomatoSauce);
        menuItem.removeIngredient(tomatoSauce);
        assertFalse(menuItem.hasIngredient((String) tomatoSauce.getMetadata().metadata().get("ingredient_id")));
    }

    @Test
    void testUpdateIngredient() {
        menuItem.addIngredient(tomatoSauce);
        Ingredients updatedTomatoSauce = new Ingredients("Tomato Sauce", "Organic tomato sauce - updated");
        menuItem.updateIngredient((String) tomatoSauce.getMetadata().metadata().get("ingredient_id"), updatedTomatoSauce);
        Ingredients updatedIngredient = menuItem.getIngredients().get((String) updatedTomatoSauce.getMetadata().metadata().get("ingredient_id"));
        assertNotNull(updatedIngredient);
        assertEquals("Organic tomato sauce - updated", updatedIngredient.getData().get("notes"));
    }


    @Test
    void testMenuItemToString() {
        String expected = String.format("MenuItem{Metadata: %s, Data: %s}", new HashMap<>(menuItem.getMetadata().metadata()), new HashMap<>(menuItem.getData()));
        assertEquals(expected, menuItem.toString());
    }

    @Test
    void testTimestampUpdateOnIngredientModification() throws InterruptedException {
        menuItem.addIngredient(tomatoSauce);
        long timestampBeforeUpdate = (long) menuItem.getMetadata().metadata().get("updated_at");
        Thread.sleep(10);
        Ingredients updatedTomatoSauce = new Ingredients("Tomato Sauce", "Organic tomato sauce - updated");
        menuItem.updateIngredient((String) tomatoSauce.getMetadata().metadata().get("ingredient_id"), updatedTomatoSauce);
        long timestampAfterUpdate = (long) menuItem.getMetadata().metadata().get("updated_at");
        assertTrue(timestampAfterUpdate > timestampBeforeUpdate);
    }
}




