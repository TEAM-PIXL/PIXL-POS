package teampixl.com.pixlpos.constructs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teampixl.com.pixlpos.constructs.MenuItem.ItemType;
import teampixl.com.pixlpos.constructs.MenuItem.DietaryRequirement;
import teampixl.com.pixlpos.database.MetadataWrapper;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemTest {

    private MenuItem menuItem;

    @BeforeEach
    void setUp() {
        menuItem = new MenuItem("Burger", 5.99, ItemType.MAIN, true, "A delicious beef burger");
    }

    @Test
    void testInitialMetadata() {
        MetadataWrapper metadata = menuItem.getMetadata();
        assertEquals("Burger", metadata.metadata().get("itemName"));
        assertEquals(5.99, metadata.metadata().get("price"));
        assertEquals(ItemType.MAIN, metadata.metadata().get("itemType"));
        assertTrue((Boolean) metadata.metadata().get("activeItem"));
        assertNull(metadata.metadata().get("dietaryRequirement"));
    }

    @Test
    void testInitialData() {
        assertEquals("A delicious beef burger", menuItem.getData().get("description"));
        assertNull(menuItem.getData().get("notes"));
        assertEquals(0, menuItem.getData().get("amountOrdered"));
    }

    @Test
    void testUpdateMetadata() {
        menuItem.updateMetadata("price", 6.49);
        menuItem.updateMetadata("dietaryRequirement", DietaryRequirement.VEGETARIAN);

        MetadataWrapper metadata = menuItem.getMetadata();
        assertEquals(6.49, metadata.metadata().get("price"));
        assertEquals(DietaryRequirement.VEGETARIAN, metadata.metadata().get("dietaryRequirement"));
    }

    @Test
    void testSetDataValue() {
        menuItem.setDataValue("notes", "No onions, please");
        menuItem.setDataValue("amountOrdered", 3);

        assertEquals("No onions, please", menuItem.getData().get("notes"));
        assertEquals(3, menuItem.getData().get("amountOrdered"));
    }

    @Test
    void testToString() {
        String expected = "MenuItem{Metadata: " + menuItem.getMetadata().toString() +
                ", Data: {description=A delicious beef burger, notes=null, amountOrdered=0}}";
        assertEquals(expected, menuItem.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        MenuItem menuItem2 = new MenuItem("Burger", 5.99, ItemType.MAIN, true, "A delicious beef burger");

        // Ensure different IDs result in different MetadataWrapper objects
        assertNotEquals(menuItem.getMetadata(), menuItem2.getMetadata());
        assertNotEquals(menuItem.getMetadata().hashCode(), menuItem2.getMetadata().hashCode());

        // Simulate identical metadata by using updateMetadata
        menuItem2.updateMetadata("id", menuItem.getMetadata().metadata().get("id"));

        assertEquals(menuItem.getMetadata(), menuItem2.getMetadata());
        assertEquals(menuItem.getMetadata().hashCode(), menuItem2.getMetadata().hashCode());
    }
}