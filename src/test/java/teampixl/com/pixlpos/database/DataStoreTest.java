package teampixl.com.pixlpos.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teampixl.com.pixlpos.constructs.MenuItem;
import teampixl.com.pixlpos.constructs.Order;
import teampixl.com.pixlpos.constructs.Users;
import teampixl.com.pixlpos.constructs.MenuItem.ItemType;
import teampixl.com.pixlpos.constructs.Users.UserRole;

import static org.junit.jupiter.api.Assertions.*;

public class DataStoreTest {

    private DataStore dataStore;

    @BeforeEach
    public void setUp() {
        dataStore = DataStore.getInstance();
        dataStore.clearData(); // Clear the datastore before each test
    }

    @Test
    public void testAddAndRetrieveMenuItem() {
        MenuItem menuItem = new MenuItem("Burger", 10.99, ItemType.MAIN, true, "Delicious beef burger", MenuItem.DietaryRequirement.NONE);
        dataStore.addMenuItem(menuItem);

        MenuItem retrievedItem = dataStore.getMenuItems().stream()
                .filter(item -> item.getMetadata().metadata().get("itemName").equals("Burger"))
                .findFirst()
                .orElse(null);

        assertNotNull(retrievedItem, "MenuItem should be found in DataStore");
        assertEquals(menuItem.getMetadata().metadata().get("price"), retrievedItem.getMetadata().metadata().get("price"), "Prices should match");
    }

    @Test
    public void testUpdateMenuItem() {
        MenuItem menuItem = new MenuItem("Salad", 5.99, ItemType.ENTREE, true, "Fresh garden salad", MenuItem.DietaryRequirement.VEGAN);
        dataStore.addMenuItem(menuItem);

        // Update price
        menuItem.updateMetadata("price", 6.99);
        dataStore.updateMenuItem(menuItem);

        MenuItem updatedItem = dataStore.getMenuItems().stream()
                .filter(item -> item.getMetadata().metadata().get("itemName").equals("Salad"))
                .findFirst()
                .orElse(null);

        assertNotNull(updatedItem, "Updated MenuItem should be found in DataStore");
        assertEquals(6.99, updatedItem.getMetadata().metadata().get("price"), "Updated price should match");
    }

    @Test
    public void testRemoveMenuItem() {
        MenuItem menuItem = new MenuItem("Pizza", 8.99, ItemType.MAIN, true, "Cheesy pizza", MenuItem.DietaryRequirement.NONE);
        dataStore.addMenuItem(menuItem);

        dataStore.removeMenuItem(menuItem);

        MenuItem removedItem = dataStore.getMenuItems().stream()
                .filter(item -> item.getMetadata().metadata().get("itemName").equals("Pizza"))
                .findFirst()
                .orElse(null);

        assertNull(removedItem, "MenuItem should be removed from DataStore");
    }

    @Test
    public void testAddAndRetrieveOrder() {
        Users user = new Users("waiter", "password", "waiter@example.com", UserRole.WAITER);
        dataStore.addUser(user);

        Order order = new Order(1, user.getMetadata().metadata().get("id").toString());
        dataStore.addOrder(order);

        Order retrievedOrder = dataStore.getOrders().stream()
                .filter(o -> o.getMetadata().metadata().get("order_number").equals(1))
                .findFirst()
                .orElse(null);

        assertNotNull(retrievedOrder, "Order should be found in DataStore");
        assertEquals(order.getMetadata().metadata().get("order_number"), retrievedOrder.getMetadata().metadata().get("order_number"), "Order numbers should match");
    }

    @Test
    public void testCompleteOrder() {
        Users user = new Users("waiter", "password", "waiter@example.com", UserRole.WAITER);
        dataStore.addUser(user);

        Order order = new Order(2, user.getMetadata().metadata().get("id").toString());
        dataStore.addOrder(order);

        order.completeOrder();
        dataStore.updateOrder(order);

        Order completedOrder = dataStore.getOrders().stream()
                .filter(o -> o.getMetadata().metadata().get("order_number").equals(2))
                .findFirst()
                .orElse(null);

        assertNotNull(completedOrder, "Completed Order should be found in DataStore");
        assertTrue((Boolean) completedOrder.getMetadata().metadata().get("is_completed"), "Order should be marked as completed");
    }

    @Test
    public void testAddAndRetrieveUser() {
        Users user = new Users("cook", "password123", "cook@example.com", UserRole.COOK);
        dataStore.addUser(user);

        Users retrievedUser = dataStore.getUsers().stream()
                .filter(u -> u.getMetadata().metadata().get("username").equals("cook"))
                .findFirst()
                .orElse(null);

        assertNotNull(retrievedUser, "User should be found in DataStore");
        assertEquals(user.getMetadata().metadata().get("email"), retrievedUser.getMetadata().metadata().get("email"), "Emails should match");
    }
}

