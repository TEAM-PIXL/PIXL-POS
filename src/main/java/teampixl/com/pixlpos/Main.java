package teampixl.com.pixlpos;

import teampixl.com.pixlpos.constructs.Ingredients;
import teampixl.com.pixlpos.constructs.MenuItem;
import teampixl.com.pixlpos.constructs.Order;
import teampixl.com.pixlpos.constructs.Users;
import teampixl.com.pixlpos.constructs.Stock;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.DatabaseHelper;
import teampixl.com.pixlpos.authentication.PasswordUtils;
import teampixl.com.pixlpos.authentication.AuthenticationManager;

public class Main {

    public static void main(String[] args) {
        // Initialize database but don't clear tables
        DatabaseHelper.initializeDatabase();
        System.out.println("Database initialized and tables created.");

        // Initialize DataStore
        DataStore dataStore = DataStore.getInstance();
        dataStore.clearData();  // Clear all data before adding new data

        // Test Users
        AuthenticationManager.register("admin", "admin", "admin@example.com", Users.UserRole.ADMIN);
        Users user2 = new Users("waiter", "waiter", "waiter@example.com", Users.UserRole.WAITER);

        Users user1 = dataStore.getUser("admin");

        System.out.println("User 1 added to database.");
        dataStore.addUser(user2);
        System.out.println("User 2 added to database.");

        System.out.println("Retrieving all users from the database:");
        dataStore.getUsers().forEach(user -> System.out.println(user));

        // Test Ingredients
        Ingredients ingredient1 = new Ingredients("Tomato Sauce", "Fresh tomato sauce");
        Ingredients ingredient2 = new Ingredients("Cheese", "Mozzarella cheese");

        dataStore.addIngredient(ingredient1);
        dataStore.addIngredient(ingredient2);

        System.out.println("Ingredients added to the database:");
        dataStore.getIngredients().forEach(ingredient -> System.out.println(ingredient));

        // Test Stock
        Stock stock1 = new Stock(ingredient1, Stock.StockStatus.INSTOCK, Stock.UnitType.KG, 2.0, false);
        Stock stock2 = new Stock(ingredient2, Stock.StockStatus.LOWSTOCK, Stock.UnitType.KG, 1.5, true);

        dataStore.addStock(stock1);
        dataStore.addStock(stock2);

        System.out.println("Stock added to the database:");
        dataStore.getStockItems().forEach(stock -> System.out.println(stock));

        // Test MenuItems
        MenuItem item1 = new MenuItem("Pizza", 18.99, MenuItem.ItemType.MAIN, true, "Cheesy pizza with toppings", null);
        MenuItem item2 = new MenuItem("Vegan Salad", 12.99, MenuItem.ItemType.ENTREE, true, "Healthy vegan salad", MenuItem.DietaryRequirement.VEGAN);

        dataStore.addMenuItem(item1);
        System.out.println("MenuItem 1 added to database.");
        dataStore.addMenuItem(item2);
        System.out.println("MenuItem 2 added to database.");

        // Add ingredients to menu items with amounts
        dataStore.addMenuItemIngredient(item1, ingredient1, 1.5);  // Add 1.5 KG of Tomato Sauce to Pizza
        dataStore.addMenuItemIngredient(item1, ingredient2, 0.5);  // Add 0.5 KG of Cheese to Pizza

        System.out.println("Retrieving all menu items from the database:");
        dataStore.getMenuItems().forEach(item -> System.out.println(item));

        // Test Orders
        Order order1 = new Order(1, (String) user1.getMetadata().metadata().get("id"));
        order1.addMenuItem(item1, 2);  // Add 2 Pizzas to order
        order1.addMenuItem(item2, 1);  // Add 1 Vegan Salad to order

        dataStore.addOrder(order1);
        System.out.println("Order 1 added to database.");

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

        System.out.println("Testing Authentication...");

        // Creating a new user with a plain password
        Users user = new Users("testUser", "testPassword123", "test@example.com", Users.UserRole.WAITER);

        // Hash the password and verify it
        String plainPassword = "testPassword123";
        String hashedPassword = PasswordUtils.hashPassword(plainPassword);
        boolean isPasswordValid = PasswordUtils.verifyPassword(plainPassword, hashedPassword);

        // Output the results
        System.out.println("Plain Password: " + plainPassword);
        System.out.println("Hashed Password: " + hashedPassword);
        System.out.println("Is Password Valid: " + isPasswordValid);

        // Test with wrong password
        boolean isWrongPasswordValid = PasswordUtils.verifyPassword("wrongPassword", hashedPassword);
        System.out.println("Is Wrong Password Valid: " + isWrongPasswordValid);

        if (isPasswordValid && !isWrongPasswordValid) {
            System.out.println("Authentication tests passed.");
        } else {
            System.out.println("Authentication tests failed.");
        }
    }
}







