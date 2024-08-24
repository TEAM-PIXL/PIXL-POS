package teampixl.com.pixlpos.constructs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teampixl.com.pixlpos.database.MetadataWrapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemTest {

    private MenuItem menuItem;

    @BeforeEach
    void setUp() {
        menuItem = new MenuItem("Burger", 5.99, MenuItem.ItemType.MAIN, true, "A delicious beef burger");
    }

    @Test
    void testInitialMetadata() {
        MetadataWrapper metadata = menuItem.getMetadata();
        assertEquals("Burger", metadata.metadata().get("itemName"));
        assertEquals(5.99, metadata.metadata().get("price"));
        assertEquals(MenuItem.ItemType.MAIN, metadata.metadata().get("itemType"));
        assertTrue((Boolean) metadata.metadata().get("activeItem"));
        assertNull(metadata.metadata().get("dietaryRequirement"));
        assertNotNull(metadata.metadata().get("id"));  // Ensure id is initialized
    }

    @Test
    void testInitialData() {
        Map<String, Object> data = menuItem.getData();
        assertEquals("A delicious beef burger", data.get("description"));
        assertNull(data.get("notes"));
        assertEquals(0, data.get("amountOrdered"));
    }

    @Test
    void testUpdateMetadata() {
        menuItem.updateMetadata("price", 6.49);
        menuItem.updateMetadata("dietaryRequirement", MenuItem.DietaryRequirement.VEGETARIAN);

        MetadataWrapper metadata = menuItem.getMetadata();
        assertEquals(6.49, metadata.metadata().get("price"));
        assertEquals(MenuItem.DietaryRequirement.VEGETARIAN, metadata.metadata().get("dietaryRequirement"));
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
        // The keys in both Metadata and Data should be sorted alphabetically
        String expected = "MenuItem{Metadata: {activeItem=true, id=" + menuItem.getMetadata().metadata().get("id") +
                ", itemName=Burger, itemType=MAIN, price=5.99}, Data: {amountOrdered=0, description=A delicious beef burger, notes=null}}";
        assertEquals(expected, menuItem.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        MenuItem menuItem2 = new MenuItem("Burger", 5.99, MenuItem.ItemType.MAIN, true, "A delicious beef burger");

        // Different objects should have different IDs
        assertNotEquals(menuItem.getMetadata(), menuItem2.getMetadata());
        assertNotEquals(menuItem.getMetadata().hashCode(), menuItem2.getMetadata().hashCode());

        // Make metadata identical by copying the ID
        menuItem2.updateMetadata("id", menuItem.getMetadata().metadata().get("id"));

        // Now they should be equal
        assertEquals(menuItem.getMetadata(), menuItem2.getMetadata());
        assertEquals(menuItem.getMetadata().hashCode(), menuItem2.getMetadata().hashCode());
    }
}