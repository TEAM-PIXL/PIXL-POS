package teampixl.com.pixlpos.database.api.orderapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teampixl.com.pixlpos.database.api.menuapi.MenuItem;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

class OrderTest {

    private Order order;
    private MenuItem pizza;
    private MenuItem salad;

    @BeforeEach
    void setUp() {
        pizza = new MenuItem("Pizza", 10.99, MenuItem.ItemType.MAIN, true, "Cheesy pizza with toppings", MenuItem.DietaryRequirement.VEGETARIAN);
        salad = new MenuItem("Salad", 7.99, MenuItem.ItemType.ENTREE, true, "Fresh salad with vinaigrette", MenuItem.DietaryRequirement.VEGAN);
        order = new Order(1, "user123");
        order.addMenuItem(pizza, 2);
        order.addMenuItem(salad, 1);
    }

    @Test
    void testOrderCreation() {
        assertNotNull(order.getMetadata().metadata().get("order_id"));
        assertEquals(1, order.getMetadata().metadata().get("order_number"));
        assertEquals("user123", order.getMetadata().metadata().get("user_id"));
        assertEquals(Order.OrderStatus.PENDING, order.getMetadata().metadata().get("order_status"));
    }

    @SuppressWarnings({"unchecked", "SuspiciousMethodCalls"})
    @Test
    void testAddMenuItemToOrder() {
        Map<String, Integer> menuItems = (Map<String, Integer>) order.getData().get("menuItems");
        assertEquals(2, menuItems.size());
        assertEquals(2, menuItems.get(pizza.getMetadata().metadata().get("id")));
        assertEquals(1, menuItems.get(salad.getMetadata().metadata().get("id")));
    }

    @Test
    void testTotalOrderCost() {
        double total = (double) order.getData().get("total");
        assertEquals(29.97, total);
    }

    @Test
    void testOrderCompletion() {
        order.completeOrder();
        assertEquals(Order.OrderStatus.COMPLETED, order.getMetadata().metadata().get("order_status"));
        assertTrue((Boolean) order.getMetadata().metadata().get("is_completed"));
    }

    @Test
    void testUpdateOrderStatus() {
        order.updateOrderStatus(Order.OrderStatus.IN_PROGRESS);
        assertEquals(Order.OrderStatus.IN_PROGRESS, order.getMetadata().metadata().get("order_status"));
    }
}


