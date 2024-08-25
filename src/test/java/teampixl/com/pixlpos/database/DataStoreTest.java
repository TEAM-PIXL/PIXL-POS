package teampixl.com.pixlpos.database;

import teampixl.com.pixlpos.constructs.MenuItem;
import teampixl.com.pixlpos.constructs.Order;
import teampixl.com.pixlpos.constructs.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.collections.ObservableList;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DataStoreTest {

    private static DataStore dataStore;
    private static MenuItem sampleMenuItem;
    private static Users sampleUser;
    private static Order sampleOrder;

    @BeforeEach
    void setup() {
        dataStore = DataStore.getInstance();
        dataStore.clearData();  // Clear all data before each test

        // Sample MenuItem
        sampleMenuItem = new MenuItem("Burger", 5.99, MenuItem.ItemType.MAIN, true, "A juicy burger", MenuItem.DietaryRequirement.GLUTEN_FREE);

        // Sample User
        sampleUser = new Users("john_doe", "password123", "john@example.com", Users.UserRole.WAITER);

        // Sample Order
        sampleOrder = new Order(1, (String) sampleUser.getMetadata().metadata().get("id"));

        dataStore.addUser(sampleUser);  // Add the sample user to the database
    }

    @Test
    void testAddMenuItem() {
        dataStore.addMenuItem(sampleMenuItem);
        assertEquals(1, dataStore.getMenuItems().size());
    }

    @Test
    void testGetMenuItem() {
        dataStore.addMenuItem(sampleMenuItem);
        MenuItem item = dataStore.getMenuItem("Burger");
        assertNotNull(item, "MenuItem should not be null");
        assertEquals("Burger", item.getMetadata().metadata().get("itemName"));
    }

    @Test
    void testUpdateMenuItem() {
        dataStore.addMenuItem(sampleMenuItem);
        sampleMenuItem.setDataValue("description", "A very juicy burger");
        dataStore.updateMenuItem(sampleMenuItem);
        MenuItem updatedItem = dataStore.getMenuItems().get(0);
        assertEquals("A very juicy burger", updatedItem.getData().get("description"));
    }

    @Test
    void testRemoveMenuItem() {
        dataStore.addMenuItem(sampleMenuItem);
        dataStore.removeMenuItem(sampleMenuItem);
        assertEquals(0, dataStore.getMenuItems().size());
    }

    @Test
    void testAddUser() {
        assertEquals(1, dataStore.getUsers().size());
    }

    @Test
    void testGetUser() {
        Users user = dataStore.getUser("john_doe");
        assertNotNull(user, "User should not be null");
        assertEquals("john_doe", user.getMetadata().metadata().get("username"));
    }

    @Test
    void testUpdateUser() {
        sampleUser.setDataValue("email", "john_new@example.com");
        dataStore.updateUser(sampleUser);
        Users updatedUser = dataStore.getUsers().get(0);
        assertEquals("john_new@example.com", updatedUser.getData().get("email"));
    }

    @Test
    void testRemoveUser() {
        dataStore.removeUser(sampleUser);
        assertEquals(0, dataStore.getUsers().size());
    }

    @Test
    void testAddOrder() {
        dataStore.addOrder(sampleOrder);
        assertEquals(1, dataStore.getOrders().size());
    }

    @Test
    void testGetOrders() {
        dataStore.addOrder(sampleOrder);
        ObservableList<Order> orders = dataStore.getOrders();
        assertEquals(1, orders.size(), "There should be exactly 1 order");
    }

    @Test
    void testUpdateOrder() {
        dataStore.addOrder(sampleOrder);
        sampleOrder.updateOrderStatus(Order.OrderStatus.IN_PROGRESS);
        dataStore.updateOrder(sampleOrder);
        Order updatedOrder = dataStore.getOrders().get(0);
        assertEquals(Order.OrderStatus.IN_PROGRESS, updatedOrder.getMetadata().metadata().get("order_status"));
    }

    @Test
    void testRemoveOrder() {
        dataStore.addOrder(sampleOrder);
        dataStore.removeOrder(sampleOrder);
        assertEquals(0, dataStore.getOrders().size());
    }

    @Test
    void testAddOrderItem() {
        dataStore.addOrder(sampleOrder);
        dataStore.addOrderItem(sampleOrder, sampleMenuItem, 2);
        Order updatedOrder = dataStore.getOrders().get(0);
        Map<String, Integer> menuItems = (Map<String, Integer>) updatedOrder.getData().get("menuItems");
        assertEquals(2, menuItems.get(sampleMenuItem.getMetadata().metadata().get("id")));
    }

    @Test
    void testUpdateOrderItem() {
        dataStore.addOrder(sampleOrder);
        dataStore.addOrderItem(sampleOrder, sampleMenuItem, 2);
        dataStore.updateOrderItem(sampleOrder, sampleMenuItem, 3);
        Order updatedOrder = dataStore.getOrders().get(0);
        Map<String, Integer> menuItems = (Map<String, Integer>) updatedOrder.getData().get("menuItems");
        assertEquals(3, menuItems.get(sampleMenuItem.getMetadata().metadata().get("id")));
    }

    @Test
    void testRemoveOrderItem() {
        dataStore.addOrder(sampleOrder);
        dataStore.addOrderItem(sampleOrder, sampleMenuItem, 2);
        dataStore.removeOrderItem(sampleOrder, sampleMenuItem, 2);
        Order updatedOrder = dataStore.getOrders().get(0);
        Map<String, Integer> menuItems = (Map<String, Integer>) updatedOrder.getData().get("menuItems");
        assertNull(menuItems.get(sampleMenuItem.getMetadata().metadata().get("id")));
    }

    @Test
    void testClearData() {
        dataStore.addMenuItem(sampleMenuItem);
        dataStore.addOrder(sampleOrder);
        dataStore.clearData();
        assertEquals(0, dataStore.getMenuItems().size());
        assertEquals(0, dataStore.getOrders().size());
        assertEquals(0, dataStore.getUsers().size());
    }
}




