package teampixl.com.pixlpos.database;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teampixl.com.pixlpos.constructs.Ingredients;
import teampixl.com.pixlpos.constructs.MenuItem;
import teampixl.com.pixlpos.constructs.Order;
import teampixl.com.pixlpos.constructs.Users;

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
        dataStore.clearData();  // Clear all data before each test

        // Sample MenuItem
        sampleMenuItem = new MenuItem("Sample Item", 9.99, MenuItem.ItemType.MAIN, true, "Sample description", MenuItem.DietaryRequirement.NONE);

        // Sample Ingredients
        sampleIngredient1 = new Ingredients("Ingredient 1", Ingredients.StockStatus.INSTOCK, false, Ingredients.UnitType.KG, 2.5, "Ingredient 1 Notes");
        sampleIngredient2 = new Ingredients("Ingredient 2", Ingredients.StockStatus.LOWSTOCK, true, Ingredients.UnitType.L, 1.5, "Ingredient 2 Notes");

        // Sample User
        sampleUser = new Users("sample_user", "password", "user@example.com", Users.UserRole.ADMIN);

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
        Ingredients ingredient = new Ingredients("Ingredient 1", Ingredients.StockStatus.INSTOCK, false, Ingredients.UnitType.KG, 1.5, "Ingredient 1 Notes");
        dataStore.addIngredient(ingredient);

        ingredient.setDataValue("notes", "Updated notes");
        dataStore.updateIngredient(ingredient);

        Ingredients updatedIngredient = dataStore.getIngredients().get(0);  // Assuming the first ingredient in the list
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
        dataStore.addMenuItemIngredient(sampleMenuItem, sampleIngredient1);
        assertTrue(sampleMenuItem.hasIngredient((String) sampleIngredient1.getMetadata().metadata().get("uuid")));
    }

    @Test
    void testUpdateMenuItemIngredient() {
        dataStore.addMenuItem(sampleMenuItem);
        dataStore.addMenuItemIngredient(sampleMenuItem, sampleIngredient1);
        sampleIngredient1.setDataValue("notes", "Updated ingredient notes");
        dataStore.updateMenuItemIngredient(sampleMenuItem);
        Ingredients updatedIngredient = sampleMenuItem.getIngredients().get((String) sampleIngredient1.getMetadata().metadata().get("uuid"));
        assertEquals("Updated ingredient notes", updatedIngredient.getData().get("notes"));
    }

    @Test
    void testRemoveMenuItemIngredient() {
        dataStore.addMenuItem(sampleMenuItem);
        dataStore.addMenuItemIngredient(sampleMenuItem, sampleIngredient1);
        dataStore.removeMenuItemIngredient(sampleMenuItem, sampleIngredient1);
        assertFalse(sampleMenuItem.hasIngredient((String) sampleIngredient1.getMetadata().metadata().get("uuid")));
    }
}