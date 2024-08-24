package teampixl.com.pixlpos.constructs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teampixl.com.pixlpos.database.MetadataWrapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;
    private MenuItem burger;
    private MenuItem fries;

    @BeforeEach
    void setUp() {
        // Create a new order with orderNumber 1
        order = new Order(1);

        // Create menu items to add to the order
        burger = new MenuItem("Burger", 5.99, MenuItem.ItemType.MAIN, true, "A delicious beef burger");
        fries = new MenuItem("Fries", 2.99, MenuItem.ItemType.ENTREE, true, "Crispy fries");

        // Add 2 burgers to the order
        order.addItem(burger, 2);
    }

    @Test
    void testInitialMetadata() {
        MetadataWrapper metadata = order.getMetadata();
        assertNotNull(metadata.metadata().get("orderId"));
        assertNotNull(metadata.metadata().get("orderTime"));
        assertNotNull(metadata.metadata().get("orderDate"));
        assertEquals(1, metadata.metadata().get("orderNumber"));
        assertEquals(Order.OrderStatus.NOT_STARTED, metadata.metadata().get("orderStatus"));
        assertFalse((Boolean) metadata.metadata().get("isCompleted"));
    }

    @Test
    void testAddItem() {
        // Add 1 order of fries to the order
        order.addItem(fries, 1);

        Map<MetadataWrapper, Integer> items = (Map<MetadataWrapper, Integer>) order.getData().get("items");
        assertEquals(2, items.size()); // 2 unique items (burger and fries) should be in the order
        assertEquals(2, items.get(burger.getMetadata()));
        assertEquals(1, items.get(fries.getMetadata()));

        // Validate the total price: 2 * 5.99 + 1 * 2.99 = 14.97
        assertEquals(14.97, order.getData().get("total"));
    }

    @Test
    void testRemoveItem() {
        // Remove 1 burger from the order
        order.removeItem(burger, 1);

        Map<MetadataWrapper, Integer> items = (Map<MetadataWrapper, Integer>) order.getData().get("items");
        assertEquals(1, items.size()); // Only 1 burger should remain
        assertEquals(1, items.get(burger.getMetadata()));

        // Validate the total price: 1 * 5.99 = 5.99
        assertEquals(5.99, order.getData().get("total"));
    }

    @Test
    void testRemoveItemComplete() {
        // Remove 2 burgers from the order (all burgers should be removed)
        order.removeItem(burger, 2);

        Map<MetadataWrapper, Integer> items = (Map<MetadataWrapper, Integer>) order.getData().get("items");
        assertFalse(items.containsKey(burger.getMetadata())); // No burgers should remain

        // Validate the total price: 0.0
        assertEquals(0.0, order.getData().get("total"));
    }

    @Test
    void testUpdateOrderStatus() {
        // Update the order status to ON_THE_WAY
        order.updateOrderStatus(Order.OrderStatus.ON_THE_WAY);
        assertEquals(Order.OrderStatus.ON_THE_WAY, order.getMetadata().metadata().get("orderStatus"));

        // Update the order status to COMPLETED. The isCompleted metadata should be true
        order.updateOrderStatus(Order.OrderStatus.COMPLETED);
        assertTrue((Boolean) order.getMetadata().metadata().get("isCompleted"));
    }

    @Test
    void testToString() {
        // The expected output string should reflect the TreeMap ordering in metadata and data
        String expected = "Order{Metadata: {isCompleted=false, orderDate=" + order.getMetadata().metadata().get("orderDate") +
                ", orderId=" + order.getMetadata().metadata().get("orderId") +
                ", orderNumber=1, orderStatus=NOT_STARTED, orderTime=" + order.getMetadata().metadata().get("orderTime") +
                "}, Data: Items:\n" +
                burger.getMetadata() + " x2\nTotal: $11.98}";

        assertEquals(expected, order.toString());
    }
}
