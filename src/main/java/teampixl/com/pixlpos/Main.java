package teampixl.com.pixlpos;

import teampixl.com.pixlpos.constructs.MenuItem;
import teampixl.com.pixlpos.constructs.Order;
import teampixl.com.pixlpos.constructs.Users;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.DatabaseHelper;

public class Main {
    public static void main(String[] args) {
        // Initialize the database
        DatabaseHelper.initializeDatabase();
        DataStore dataStore = DataStore.getInstance();

        // Clear existing data
        dataStore.clearData();

        // Create some test users
        Users user1 = new Users("waiter", "waiterpass", "waiter@example.com", Users.UserRole.WAITER);
        Users user2 = new Users("cook", "cookpass", "cook@example.com", Users.UserRole.COOK);

        // Add users to the datastore
        dataStore.addUser(user1);
        dataStore.addUser(user2);

        // Create some test menu items
        MenuItem menuItem1 = new MenuItem("Chicken Curry", 15.49, MenuItem.ItemType.MAIN, true, "Delicious chicken curry", MenuItem.DietaryRequirement.SPICY);
        MenuItem menuItem2 = new MenuItem("Vegan Salad", 12.99, MenuItem.ItemType.ENTREE, true, "Healthy vegan salad", MenuItem.DietaryRequirement.VEGAN);
        MenuItem menuItem3 = new MenuItem("Cheese Pizza", 9.99, MenuItem.ItemType.MAIN, true, "Cheesy pizza", null);

        // Add menu items to the datastore
        dataStore.addMenuItem(menuItem1);
        dataStore.addMenuItem(menuItem2);
        dataStore.addMenuItem(menuItem3);

        // Create a test order
        Order order = new Order(1, (String) user1.getMetadata().metadata().get("id"));
        order.addMenuItem(menuItem1, 2);
        order.addMenuItem(menuItem3, 1);

        // Add the order to the datastore
        dataStore.addOrder(order);

        // Retrieve and display data from the datastore
        System.out.println("Users in DataStore:");
        for (Users user : dataStore.getUsers()) {
            System.out.println(user);
        }

        System.out.println("\nMenu Items in DataStore:");
        for (MenuItem item : dataStore.getMenuItems()) {
            System.out.println(item);
        }

        System.out.println("\nOrders in DataStore:");
        for (Order ord : dataStore.getOrders()) {
            System.out.println(ord);
        }

        // Testing clearing the database
        dataStore.clearData();
        System.out.println("\nData cleared.");

        System.out.println("Users in DataStore after clearing:");
        for (Users user : dataStore.getUsers()) {
            System.out.println(user);
        }

        System.out.println("Menu Items in DataStore after clearing:");
        for (MenuItem item : dataStore.getMenuItems()) {
            System.out.println(item);
        }

        System.out.println("Orders in DataStore after clearing:");
        for (Order ord : dataStore.getOrders()) {
            System.out.println(ord);
        }
    }
}

