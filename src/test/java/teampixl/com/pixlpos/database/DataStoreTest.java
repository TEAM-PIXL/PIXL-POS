package teampixl.com.pixlpos.database;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teampixl.com.pixlpos.models.Ingredients;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.models.Users;

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

        dataStore.createUser(sampleUser);
    }

    @Test
    void testCreateMenuItem() {
        dataStore.createMenuItem(sampleMenuItem);
        assertEquals(1, dataStore.readMenuItems().size());
    }

    @Test
    void testGetMenuItem() {
        dataStore.createMenuItem(sampleMenuItem);
        MenuItem item = dataStore.getMenuItem("Sample Item");
        assertNotNull(item, "MenuItem should not be null");
        assertEquals("Sample Item", item.getMetadata().metadata().get("itemName"));
    }

    @Test
    void testUpdateMenuItem() {
        dataStore.createMenuItem(sampleMenuItem);
        sampleMenuItem.setDataValue("description", "Updated description");
        dataStore.updateMenuItem(sampleMenuItem);
        MenuItem updatedItem = dataStore.readMenuItems().get(0);
        assertEquals("Updated description", updatedItem.getData().get("description"));
    }

    @Test
    void testDeleteMenuItem() {
        dataStore.createMenuItem(sampleMenuItem);
        dataStore.deleteMenuItem(sampleMenuItem);
        assertEquals(0, dataStore.readMenuItems().size());
    }

    @Test
    void testCreateUser() {
        assertEquals(1, dataStore.readUsers().size());
    }

    @Test
    void testUpdateUser() {
        sampleUser.setDataValue("email", "updated_email@example.com");
        dataStore.updateUser(sampleUser);
        Users updatedUser = dataStore.readUsers().get(0);
        assertEquals("updated_email@example.com", updatedUser.getData().get("email"));
    }

    @Test
    void testDeleteUser() {
        dataStore.deleteUser(sampleUser);
        assertEquals(0, dataStore.readUsers().size());
    }

    @Test
    void testCreateOrder() {
        dataStore.createOrder(sampleOrder);
        assertEquals(1, dataStore.readOrders().size());
    }

    @Test
    void testReadOrders() {
        dataStore.createOrder(sampleOrder);
        ObservableList<Order> orders = dataStore.readOrders();
        assertEquals(1, orders.size(), "There should be exactly 1 order");
    }

    @Test
    void testUpdateOrder() {
        dataStore.createOrder(sampleOrder);
        sampleOrder.updateOrderStatus(Order.OrderStatus.IN_PROGRESS);
        dataStore.updateOrder(sampleOrder);
        Order updatedOrder = dataStore.readOrders().get(0);
        assertEquals(Order.OrderStatus.IN_PROGRESS, updatedOrder.getMetadata().metadata().get("order_status"));
    }

    @Test
    void testDeleteOrder() {
        dataStore.createOrder(sampleOrder);
        dataStore.deleteOrder(sampleOrder);
        assertEquals(0, dataStore.readOrders().size());
    }

    @Test
    void testCreateOrderItem() {
        dataStore.createOrder(sampleOrder);
        dataStore.createOrderItem(sampleOrder, sampleMenuItem, 2);
        Order updatedOrder = dataStore.readOrders().get(0);
        Map<String, Integer> menuItems = (Map<String, Integer>) updatedOrder.getData().get("menuItems");
        assertEquals(2, menuItems.get(sampleMenuItem.getMetadata().metadata().get("id")));
    }

    @Test
    void testUpdateOrderItem() {
        dataStore.createOrder(sampleOrder);
        dataStore.createOrderItem(sampleOrder, sampleMenuItem, 2);
        dataStore.updateOrderItem(sampleOrder, sampleMenuItem, 3);
        Order updatedOrder = dataStore.readOrders().get(0);
        Map<String, Integer> menuItems = (Map<String, Integer>) updatedOrder.getData().get("menuItems");
        assertEquals(3, menuItems.get(sampleMenuItem.getMetadata().metadata().get("id")));
    }

    @Test
    void testDeleteOrderItem() {
        dataStore.createOrder(sampleOrder);
        dataStore.createOrderItem(sampleOrder, sampleMenuItem, 2);
        dataStore.deleteOrderItem(sampleOrder, sampleMenuItem, 2);
        Order updatedOrder = dataStore.readOrders().get(0);
        Map<String, Integer> menuItems = (Map<String, Integer>) updatedOrder.getData().get("menuItems");
        assertNull(menuItems.get(sampleMenuItem.getMetadata().metadata().get("id")));
    }

    @Test
    void testClearData() {
        dataStore.createMenuItem(sampleMenuItem);
        dataStore.createOrder(sampleOrder);
        dataStore.clearData();
        assertEquals(0, dataStore.readMenuItems().size());
        assertEquals(0, dataStore.readOrders().size());
        assertEquals(0, dataStore.readUsers().size());
    }

    @Test
    void testCreateIngredient() {
        dataStore.createIngredient(sampleIngredient1);
        assertEquals(1, dataStore.readIngredients().size());
    }

    @Test
    void testUpdateIngredient() {
        dataStore.createIngredient(sampleIngredient1);
        sampleIngredient1.setDataValue("notes", "Updated notes");
        dataStore.updateIngredient(sampleIngredient1);
        Ingredients updatedIngredient = dataStore.readIngredients().get(0);
        assertEquals("Updated notes", updatedIngredient.getData().get("notes"));
    }

    @Test
    void testDeleteIngredient() {
        dataStore.createIngredient(sampleIngredient1);
        dataStore.deleteIngredient(sampleIngredient1);
        assertEquals(0, dataStore.readIngredients().size());
    }

    @Test
    void testCreateMenuItemIngredient() {
        dataStore.createMenuItem(sampleMenuItem);
        dataStore.createMenuItemIngredient(sampleMenuItem, sampleIngredient1, 1.0);
        assertTrue(sampleMenuItem.hasIngredient((String) sampleIngredient1.getMetadata().metadata().get("ingredient_id")));
    }

    @Test
    void testUpdateMenuItemIngredient() {
        dataStore.createMenuItem(sampleMenuItem);
        dataStore.createMenuItemIngredient(sampleMenuItem, sampleIngredient1, 1.0);
        dataStore.updateMenuItemIngredient(sampleMenuItem, sampleIngredient1, 2.0);
        Ingredients updatedIngredient = sampleMenuItem.getIngredients().get((String) sampleIngredient1.getMetadata().metadata().get("ingredient_id")).ingredient();
        Object updatedNumeral = sampleMenuItem.getIngredients().get((String) sampleIngredient1.getMetadata().metadata().get("ingredient_id")).numeral();
        assertEquals(2.0, updatedNumeral);
    }

    @Test
    void testCreateMenuItemIngredientWithNegativeNumeral() {
        dataStore.createMenuItem(sampleMenuItem);
        assertThrows(IllegalArgumentException.class, () -> dataStore.createMenuItemIngredient(sampleMenuItem, sampleIngredient1, -1.0));
    }

    @Test
    void testUpdateMenuItemIngredientWithNegativeNumeral() {
        dataStore.createMenuItem(sampleMenuItem);
        dataStore.createMenuItemIngredient(sampleMenuItem, sampleIngredient1, 1.0);
        assertThrows(IllegalArgumentException.class, () -> dataStore.updateMenuItemIngredient(sampleMenuItem, sampleIngredient1, -1.0));
    }
}

