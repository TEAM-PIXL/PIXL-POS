package teampixl.com.pixlpos;

import teampixl.com.pixlpos.constructs.MenuItem;
import teampixl.com.pixlpos.constructs.Order;
import teampixl.com.pixlpos.constructs.Users;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.DatabaseHelper;

public class Main {

    public static void main(String[] args) {
        // Initialize database but don't clear tables
        DatabaseHelper.initializeDatabase();
        System.out.println("Database initialized and tables created.");

        // Initialize DataStore
        DataStore dataStore = DataStore.getInstance();

        // Test Users
        Users user1 = new Users("admin", "admin", "admin@example.com", Users.UserRole.ADMIN);
        Users user2 = new Users("waiter", "waiter", "waiter@example.com", Users.UserRole.WAITER);

        dataStore.addUser(user1);
        System.out.println("User 1 added to database.");
        dataStore.addUser(user2);
        System.out.println("User 2 added to database.");

        System.out.println("Retrieving all users from the database:");
        dataStore.getUsers().forEach(user -> System.out.println(user));

        // Test MenuItems
        MenuItem item1 = new MenuItem("Chicken Curry", 15.49, MenuItem.ItemType.MAIN, true, "Delicious chicken curry", MenuItem.DietaryRequirement.SPICY);
        MenuItem item2 = new MenuItem("Pizza", 18.99, MenuItem.ItemType.MAIN, true, "Cheesy pizza with toppings", null);
        MenuItem item3 = new MenuItem("Vegan Salad", 12.99, MenuItem.ItemType.ENTREE, true, "Healthy vegan salad", MenuItem.DietaryRequirement.VEGAN);

        dataStore.addMenuItem(item1);
        System.out.println("MenuItem 1 added to database.");
        dataStore.addMenuItem(item2);
        System.out.println("MenuItem 2 added to database.");
        dataStore.addMenuItem(item3);
        System.out.println("MenuItem 3 added to database.");

        System.out.println("Retrieving all menu items from the database:");
        dataStore.getMenuItems().forEach(item -> System.out.println(item));

        // Test Orders
        Order order1 = new Order(1, (String) user1.getMetadata().metadata().get("id"));
        order1.addMenuItem(item1, 2);
        order1.addMenuItem(item3, 1);

        Order order2 = new Order(2, (String) user2.getMetadata().metadata().get("id"));
        order2.addMenuItem(item2, 3);

        dataStore.addOrder(order1);
        System.out.println("Order 1 added to database.");
        dataStore.addOrder(order2);
        System.out.println("Order 2 added to database.");

        System.out.println("Retrieving all orders from the database:");
        dataStore.getOrders().forEach(order -> System.out.println(order));

        // Simulate application exit and re-run
        System.out.println("\n--- Simulating Application Exit and Re-Run ---");
        // Re-initialize DataStore to simulate a new session
        dataStore = DataStore.getInstance();

        System.out.println("Retrieving all users from the database after re-initialization:");
        dataStore.getUsers().forEach(user -> System.out.println(user));

        System.out.println("Retrieving all menu items from the database after re-initialization:");
        dataStore.getMenuItems().forEach(item -> System.out.println(item));

        System.out.println("Retrieving all orders from the database after re-initialization:");
        dataStore.getOrders().forEach(order -> System.out.println(order));
    }
}



