package teampixl.com.pixlpos.constructs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MenuItemTest {

    private MenuItem menuItem;

    @BeforeEach
    void setUp() {
        menuItem = new MenuItem("Pizza", 10.99, MenuItem.ItemType.MAIN, true, "Cheesy pizza with toppings", MenuItem.DietaryRequirement.VEGETARIAN);
    }

    @Test
    void testMenuItemCreation() {
        assertNotNull(menuItem.getMetadata().metadata().get("id"));
        assertEquals("Pizza", menuItem.getMetadata().metadata().get("itemName"));
        assertEquals(10.99, menuItem.getMetadata().metadata().get("price"));
        assertEquals(MenuItem.ItemType.MAIN, menuItem.getMetadata().metadata().get("itemType"));
    }

    @Test
    void testUpdateMenuItemMetadata() {
        menuItem.updateMetadata("itemName", "Vegan Pizza");
        assertEquals("Vegan Pizza", menuItem.getMetadata().metadata().get("itemName"));
    }

    @Test
    void testUpdateMenuItemDescription() {
        menuItem.setDataValue("description", "Delicious vegan pizza with toppings");
        assertEquals("Delicious vegan pizza with toppings", menuItem.getData().get("description"));
    }

    @Test
    void testDietaryRequirement() {
        assertEquals(MenuItem.DietaryRequirement.VEGETARIAN, menuItem.getMetadata().metadata().get("dietaryRequirement"));
        menuItem.updateMetadata("dietaryRequirement", MenuItem.DietaryRequirement.VEGAN);
        assertEquals(MenuItem.DietaryRequirement.VEGAN, menuItem.getMetadata().metadata().get("dietaryRequirement"));
    }
}
