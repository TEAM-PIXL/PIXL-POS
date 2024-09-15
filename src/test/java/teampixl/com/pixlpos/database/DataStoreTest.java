package teampixl.com.pixlpos.database;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teampixl.com.pixlpos.constructs.Ingredients;
import teampixl.com.pixlpos.constructs.MenuItem;
import teampixl.com.pixlpos.constructs.Order;
import teampixl.com.pixlpos.database.api.userapi.Users;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ALL")
class DataStoreTest {

    private static DataStore dataStore;
    private static MenuItem sampleMenuItem;
    private static Users sampleUser;
    private static Order sampleOrder;
    private static Ingredients sampleIngredient1;
    private static Ingredients sampleIngredient2;

    @BeforeEach
    void setup() {
        DatabaseHelper.initializeDatabase();
        dataStore = DataStore.getInstance();
        dataStore.clearData();

        sampleMenuItem = new MenuItem("Sample Item", 9.99, MenuItem.ItemType.MAIN, true, "Sample description", MenuItem.DietaryRequirement.NONE);

        sampleIngredient1 = new Ingredients("Ingredient 1", "Ingredient 1 Notes");
        sampleIngredient2 = new Ingredients("Ingredient 2", "Ingredient 2 Notes");

        sampleUser = new Users("John", "Doe", "sample_user", "password", "johndoe@example.com", Users.UserRole.ADMIN);

        sampleOrder = new Order(1, (String) sampleUser.getMetadata().metadata().get("id"));

        dataStore.addUser(sampleUser);
    }

    @Test
    void testAddMenuItem() {
        dataStore.addMenuItem(sampleMenuItem);
        assertEquals(1, dataStore.getMenuItems().size());
    }

    @Test
    void testGetMenuItem() {
        dataStore.addMenuItem(sampleMenuItem);
        MenuItem item = dataStore.getMenuItem("Sample Item");
        assertNotNull(item, "MenuItem should not be null");
        assertEquals("Sample Item", item.getMetadata().metadata().get("itemName"));
    }

    @Test
    void testUpdateMenuItem() {
        dataStore.addMenuItem(sampleMenuItem);
        sampleMenuItem.setDataValue("description", "Updated description");
        dataStore.updateMenuItem(sampleMenuItem);
        MenuItem updatedItem = dataStore.getMenuItems().get(0);
        assertEquals("Updated description", updatedItem.getData().get("description"));
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
        Users user = dataStore.getUser("sample_user");
        assertNotNull(user, "User should not be null");
        assertEquals("sample_user", user.getMetadata().metadata().get("username"));
    }

    @Test
    void testUpdateUser() {
        sampleUser.setDataValue("email", "updated_email@example.com");
        dataStore.updateUser(sampleUser);
        Users updatedUser = dataStore.getUsers().get(0);
        assertEquals("updated_email@example.com", updatedUser.getData().get("email"));
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

    @Test
    void testAddIngredient() {
        dataStore.addIngredient(sampleIngredient1);
        assertEquals(1, dataStore.getIngredients().size());
    }

    @Test
    void testUpdateIngredient() {
        dataStore.addIngredient(sampleIngredient1);
        sampleIngredient1.setDataValue("notes", "Updated notes");
        dataStore.updateIngredient(sampleIngredient1);
        Ingredients updatedIngredient = dataStore.getIngredients().get(0);
        assertEquals("Updated notes", updatedIngredient.getData().get("notes"));
    }

    @Test
    void testRemoveIngredient() {
        dataStore.addIngredient(sampleIngredient1);
        dataStore.removeIngredient(sampleIngredient1);
        assertEquals(0, dataStore.getIngredients().size());
    }

    @Test
    void testAddMenuItemIngredient() {
        dataStore.addMenuItem(sampleMenuItem);
        dataStore.addMenuItemIngredient(sampleMenuItem, sampleIngredient1, 1.0);
        assertTrue(sampleMenuItem.hasIngredient((String) sampleIngredient1.getMetadata().metadata().get("ingredient_id")));
    }

    @Test
    void testUpdateMenuItemIngredient() {
        dataStore.addMenuItem(sampleMenuItem);
        dataStore.addMenuItemIngredient(sampleMenuItem, sampleIngredient1, 1.0);
        dataStore.updateMenuItemIngredient(sampleMenuItem, sampleIngredient1, 2.0);
        Ingredients updatedIngredient = sampleMenuItem.getIngredients().get((String) sampleIngredient1.getMetadata().metadata().get("ingredient_id")).ingredient();
        Object updatedNumeral = sampleMenuItem.getIngredients().get((String) sampleIngredient1.getMetadata().metadata().get("ingredient_id")).numeral();
        assertEquals(2.0, updatedNumeral);
    }

    @Test
    void testAddMenuItemIngredientWithNegativeNumeral() {
        dataStore.addMenuItem(sampleMenuItem);
        assertThrows(IllegalArgumentException.class, () -> dataStore.addMenuItemIngredient(sampleMenuItem, sampleIngredient1, -1.0));
    }

    @Test
    void testUpdateMenuItemIngredientWithNegativeNumeral() {
        dataStore.addMenuItem(sampleMenuItem);
        dataStore.addMenuItemIngredient(sampleMenuItem, sampleIngredient1, 1.0);
        assertThrows(IllegalArgumentException.class, () -> dataStore.updateMenuItemIngredient(sampleMenuItem, sampleIngredient1, -1.0));
    }
}

