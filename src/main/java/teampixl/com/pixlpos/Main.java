package teampixl.com.pixlpos;

import javafx.collections.ObservableList;
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

    /*===================================================================================================================================================================================================================================
    Code Description:
    This is the functionality testing class for the application. It tests the functionality of the application by creating test data and testing the functionality of the application.
    The aim is to ensure that the application is functioning as expected and that the data is being stored and retrieved correctly. All the classes and methods are tested in this class.
    ===================================================================================================================================================================================================================================*/

    public static void main(String[] args) {

        /*===================================================================================================================================================================================================================================
        Code Description:
        This section handles the initialization of the database and the creation of tables. It also clears the data from the database.

        Tested Methods:
        - initializeDatabase()
        - clearData()
        - getInstance() {Constructor}
        ===================================================================================================================================================================================================================================*/

        DatabaseHelper.initializeDatabase();
        System.out.println("Database initialized and tables created.");

        DataStore dataStore = DataStore.getInstance();
        dataStore.clearData();

        /*===================================================================================================================================================================================================================================
        Code Description:
        This section tests the functionality of the Users class. It tests the creation of users, retrieval of users, updating of users, and removal of users. It also tests the hashing and verification of passwords.
        All the methods of the Users class are tested in this section. As well as some of the methods of the AuthenticationManager class.

        Tested Methods:

            Authentication Methods:
                - register()
                - verifyPassword()
                - hashPassword()

            User Methods:
                - getData()
                - getMetadata()
                - getUsers()
                - getUser()
                - setDataValue()
                - updateMetadata()
                - updateUser()
                - updateUserPassword()
                - removeUser()
        ===================================================================================================================================================================================================================================*/

        boolean registerAdmin = AuthenticationManager.register("admin", "admin", "admin", "admin", "admin@example.com", Users.UserRole.ADMIN);
        boolean registerWaiter =  AuthenticationManager.register("waiter", "waiter","waiter", "waiter", "waiter@example.com", Users.UserRole.WAITER);
        boolean registerCook =  AuthenticationManager.register("cook", "cook","cook", "cook", "cook@example.com", Users.UserRole.COOK);

        if (registerAdmin && registerWaiter && registerCook) {
            System.out.println("Admin, Waiter, and Cook registered successfully.");
        } else {
            System.out.println("Admin, Waiter, and Cook registration failed.");
        }

        Users user1 = dataStore.getUser("admin");
        Users user2 = dataStore.getUser("waiter");
        Users user3 = dataStore.getUser("cook");

        if (user1 != null && user2 != null && user3 != null) {
            System.out.println("Admin, Waiter, and Cook retrieved successfully.");
        } else {
            System.out.println("Admin, Waiter, and Cook retrieval failed.");
        }

        boolean registerTest =  AuthenticationManager.register("test", "test","test", "test", "test123@example.com", Users.UserRole.WAITER);
        if (registerTest) {
            System.out.println("Test user registered successfully.");
        } else {
            System.out.println("Test user registration failed.");
        }

        Users user4 = dataStore.getUser("test");
        if (user4 != null) {
            System.out.println("Test user retrieved successfully.");
        } else {
            System.out.println("Test user retrieval failed.");
        }

        if (user4 != null) {
            System.out.println("User 4 has the following hashed-password: " + user4.getData().get("password"));
            dataStore.updateUserPassword(user4, "test123");

            if (PasswordUtils.verifyPassword("test123", user4.getData().get("password").toString())) {
                System.out.println("User 4 has the following hashed-password after update: " + user4.getData().get("password"));
                System.out.println("User 4 password updated successfully.");
            } else {
                System.out.println("User 4 password update failed.");
            }
        }

        if (user4 != null) {
            dataStore.updateUser(user4);

            user4.setDataValue("email", "test@example.com");
            dataStore.updateUser(user4);

            if (user4.getData().get("email").equals("test@example.com")) {
                System.out.println("User 4 email updated successfully.");
            } else {
                System.out.println("User 4 email update failed.");
            }

            user4.updateMetadata("role", Users.UserRole.ADMIN);
            dataStore.updateUser(user4);

            if (user4.getMetadata().metadata().get("role").equals(Users.UserRole.ADMIN)) {
                System.out.println("User 4 role updated successfully.");
            } else {
                System.out.println("User 4 role update failed.");
            }

            dataStore.updateUser(user4);
            System.out.println("User 4 has the following metadata & data:" + user4);

        }

        if (user4 != null) {
            dataStore.removeUser(user4);
            if (dataStore.getUser("test") == null) {
                System.out.println("User 4 removed successfully.");
            } else {
                System.out.println("User 4 removal failed.");
            }
        }

        System.out.println("Retrieving all users from the database:");
        dataStore.getUsers().forEach(System.out::println);

        /*===================================================================================================================================================================================================================================
        Code Description:
        This section tests the functionality of the Ingredients class. It tests the creation of ingredients, retrieval of ingredients, updating of ingredients, and removal of ingredients.
        It also tests the addition of stock to ingredients. All the methods of the Ingredients class are tested in this section.

        Tested Methods:
            - Ingredients()
            - getIngredients()
            - getIngredient()
            - addIngredient()
            - setDataValue()
            - updateIngredient()
            - updateMetadata()
            - removeIngredient()
        ===================================================================================================================================================================================================================================*/

        Ingredients ingredient1 = new Ingredients("Tomato Sauce", "Fresh tomato sauce");
        Ingredients ingredient2 = new Ingredients("Cheese", "Mozzarella cheese");
        Ingredients ingredient3 = new Ingredients("Fish", "Fresh fish fillet");
        Ingredients ingredient4 = new Ingredients("Basil", null);

        dataStore.addIngredient(ingredient1);
        dataStore.addIngredient(ingredient2);
        dataStore.addIngredient(ingredient3);
        dataStore.addIngredient(ingredient4);
        System.out.println("Ingredients added to the database:");

        ObservableList<Ingredients> checkIngredients = dataStore.getIngredients();
        if (checkIngredients.size() == 4) {
            System.out.println("Ingredients added successfully.");
        } else {
            System.out.println("Ingredients addition failed.");
        }

        if(dataStore.getIngredient("Basil") != null) {
            System.out.println("Basil retrieved successfully.");
        } else {
            System.out.println("Basil retrieval failed.");
        }

        ingredient4.updateMetadata("itemName", "Fresh Basil");
        dataStore.updateIngredient(ingredient4);
        if (dataStore.getIngredient("Fresh Basil") != null) {
            System.out.println("Basil updated successfully.");
        } else {
            System.out.println("Basil update failed.");
        }

        ingredient4.setDataValue("notes", "Fresh basil leaves");
        dataStore.updateIngredient(ingredient4);
        if (dataStore.getIngredient("Fresh Basil").getData().get("notes").equals("Fresh basil leaves")) {
            System.out.println("Basil notes updated successfully.");
        } else {
            System.out.println("Basil notes update failed.");
        }

        dataStore.removeIngredient(dataStore.getIngredient("Fresh Basil"));
        if (dataStore.getIngredient("Fresh Basil") == null) {
            System.out.println("Basil removed successfully.");
        } else {
            System.out.println("Basil removal failed.");
        }

        dataStore.getIngredients().forEach(System.out::println);

        /*===================================================================================================================================================================================================================================
        Code Description:
        This section tests the functionality of the Stock class. It tests the creation of stock, retrieval of stock, updating of stock, and removal of stock. It also tests the addition of stock to ingredients.
        All the methods of the Stock class are tested in this section. As well as the methods of the DataStore class that interact with the Stock class.

        Tested Methods:
            - Stock()
            - getStockItems()
            - getStockItem()
            - addStock()
            - setDataValue()
            - updateMetadata()
            - updateStock()
            - removeStock()
        ===================================================================================================================================================================================================================================*/

        Stock stock1 = new Stock(ingredient1, Stock.StockStatus.INSTOCK, Stock.UnitType.KG, 2.0, false);
        Stock stock2 = new Stock(ingredient2, Stock.StockStatus.LOWSTOCK, Stock.UnitType.KG, 1.5, true);
        Stock stock3 = new Stock(ingredient3, Stock.StockStatus.NOSTOCK, Stock.UnitType.KG, 0.0, false);
        dataStore.addStock(stock1);
        dataStore.addStock(stock2);
        dataStore.addStock(stock3);
        System.out.println("Stock added to the database:");

        ObservableList<Stock> checkStock = dataStore.getStockItems();
        if (checkStock.size() == 3) {
            System.out.println("Stock added successfully.");
        } else {
            System.out.println("Stock addition failed.");
        }

        if (dataStore.getStockItem("Tomato Sauce") != null) {
            System.out.println("Tomato Sauce retrieved successfully.");
        } else {
            System.out.println("Tomato Sauce retrieval failed.");
        }

        stock1.setDataValue("numeral", 3.0);
        dataStore.updateStock(stock1);
        if (dataStore.getStockItem("Tomato Sauce").getData().get("numeral").equals(3.0)) {
            System.out.println("Tomato Sauce updated successfully.");
        } else {
            System.out.println("Tomato Sauce update failed.");
        }

        stock2.updateMetadata("stockStatus", Stock.StockStatus.INSTOCK);
        dataStore.updateStock(stock2);
        if (dataStore.getStockItem("Cheese").getMetadata().metadata().get("stockStatus").equals(Stock.StockStatus.INSTOCK)) {
            System.out.println("Cheese updated successfully.");
        } else {
            System.out.println("Cheese update failed.");
        }

        stock2.setDataValue("numeral", 0.0);
        dataStore.updateStock(stock2);
        if (dataStore.getStockItem("Cheese").getMetadata().metadata().get("stockStatus").equals(Stock.StockStatus.NOSTOCK)) {
            System.out.println("Cheese updated successfully.");
        } else {
            System.out.println("Cheese update failed.");
        }

        dataStore.removeStock(dataStore.getStockItem("Fish"));
        if (dataStore.getStockItem("Fish") == null) {
            System.out.println("Fish removed successfully.");
        } else {
            System.out.println("Fish removal failed.");
        }

        dataStore.getStockItems().forEach(System.out::println);

        /*===================================================================================================================================================================================================================================
        Code Description:
        This section tests the functionality of the MenuItem class. It tests the creation of menu items, retrieval of menu items, updating of menu items, and removal of menu items. It also tests the addition of ingredients to menu items.
        All the methods of the MenuItem class are tested in this section. As well as the methods of the DataStore class that interact with the MenuItem class.

        Tested Methods:
            - MenuItem()
            - getMenuItems()
            - getMenuItem()
            - addMenuItem()
            - setDataValue()
            - updateMetadata()
            - updateMenuItem()
            - removeMenuItem()
            - addMenuItemIngredient()
            - updateMenuItemIngredient()
            - removeMenuItemIngredient()
            - getMenuItemIngredients()
        ===================================================================================================================================================================================================================================*/

        MenuItem item1 = new MenuItem("Pizza", 18.99, MenuItem.ItemType.MAIN, true, "Cheesy pizza with toppings", null);
        MenuItem item2 = new MenuItem("Vegan Salad", 12.99, MenuItem.ItemType.ENTREE, true, "Healthy vegan salad", MenuItem.DietaryRequirement.VEGAN);
        MenuItem item3 = new MenuItem("Fish & Chips", 22.99, MenuItem.ItemType.MAIN, true, "Fresh fish fillet with chips", null);
        MenuItem item4 = new MenuItem("Basil Pasta", 15.99, MenuItem.ItemType.MAIN, true, "Pasta with fresh basil", MenuItem.DietaryRequirement.VEGETARIAN);

        dataStore.addMenuItem(item1);
        dataStore.addMenuItem(item2);
        dataStore.addMenuItem(item3);
        dataStore.addMenuItem(item4);
        System.out.println("Menu items added to the database:");

        ObservableList<MenuItem> checkMenuItems = dataStore.getMenuItems();
        if (checkMenuItems.size() == 4) {
            System.out.println("Menu items added successfully.");
        } else {
            System.out.println("Menu items addition failed.");
        }

        if (dataStore.getMenuItem("Pizza") != null) {
            System.out.println("Pizza retrieved successfully.");
        } else {
            System.out.println("Pizza retrieval failed.");
        }

        item1.setDataValue("price", 19.99);
        dataStore.updateMenuItem(item1);
        if (dataStore.getMenuItem("Pizza").getData().get("price").equals(19.99)) {
            System.out.println("Pizza updated successfully.");
        } else {
            System.out.println("Pizza update failed.");
        }

        item2.updateMetadata("itemName", "Vegan Caesar Salad");
        dataStore.updateMenuItem(item2);
        if (dataStore.getMenuItem("Vegan Caesar Salad") != null) {
            System.out.println("Vegan Caesar Salad updated successfully.");
        } else {
            System.out.println("Vegan Caesar Salad update failed.");
        }

        item2.setDataValue("dietaryRequirement", MenuItem.DietaryRequirement.VEGETARIAN);
        dataStore.updateMenuItem(item2);
        if (dataStore.getMenuItem("Vegan Caesar Salad").getData().get("dietaryRequirement").equals(MenuItem.DietaryRequirement.VEGETARIAN)) {
            System.out.println("Vegan Caesar Salad dietary requirement updated successfully.");
        } else {
            System.out.println("Vegan Caesar Salad dietary requirement update failed.");
        }

        dataStore.removeMenuItem(dataStore.getMenuItem("Vegan Caesar Salad"));
        if (dataStore.getMenuItem("Vegan Caesar Salad") == null) {
            System.out.println("Vegan Caesar Salad removed successfully.");
        } else {
            System.out.println("Vegan Caesar Salad removal failed.");
        }

        dataStore.addMenuItemIngredient(item1, ingredient1, 1.0);
        dataStore.addMenuItemIngredient(item1, ingredient2, 0.5);
        dataStore.updateMenuItem(item1);
        if (dataStore.getMenuItem("Pizza").hasIngredient(ingredient1.getMetadata().metadata().get("ingredient_id").toString()) && dataStore.getMenuItem("Pizza").hasIngredient(ingredient2.getMetadata().metadata().get("ingredient_id").toString())) {
            System.out.println("Pizza ingredients added successfully.");
        } else {
            System.out.println("Pizza ingredients addition failed.");
        }

        dataStore.updateMenuItemIngredient(item1, ingredient1, 2.0);
        dataStore.updateMenuItem(item1);
        System.out.println(dataStore.getMenuItemIngredients(item1));
        if (dataStore.getMenuItemIngredients(item1).get(ingredient1.getMetadata().metadata().get("itemName")).equals(2.0)) {
            System.out.println("Pizza ingredient updated successfully.");
        } else {
            System.out.println("Pizza ingredient update failed.");
        }

        dataStore.removeMenuItemIngredient(item1, ingredient1);
        dataStore.updateMenuItem(item1);
        if (dataStore.getMenuItem("Pizza").hasIngredient(ingredient1.getMetadata().metadata().get("ingredient_id").toString())) {
            System.out.println("Pizza ingredient removal failed.");
        } else {
            System.out.println("Pizza ingredient removed successfully.");
        }

        dataStore.getMenuItems().forEach(System.out::println);

        /*===================================================================================================================================================================================================================================
        Code Description:
        This section tests the functionality of the Order class. It tests the creation of orders, retrieval of orders, updating of orders, and removal of orders. It also tests the addition of menu items to orders.
        All the methods of the Order class are tested in this section. As well as the methods of the DataStore class that interact with the Order class.

        Tested Methods:
            - Order()
            - getOrders()
            - getOrder()
            - addOrder()
            - setDataValue()
            - updateMetadata()
            - updateOrder()
            - removeOrder()
            - addOrderItem()
            - updateOrderItem()
            - removeOrderItem()
            - getOrderItems()
        ===================================================================================================================================================================================================================================*/

        assert user1 != null;
        Order order1 = new Order(1, user1.getMetadata().metadata().get("id").toString());
        assert user2 != null;
        Order order2 = new Order(2, user2.getMetadata().metadata().get("id").toString());
        assert user3 != null;
        Order order3 = new Order(3, user3.getMetadata().metadata().get("id").toString());

        dataStore.addOrder(order1);
        dataStore.addOrder(order2);
        dataStore.addOrder(order3);
        System.out.println("Orders added to the database:");

        ObservableList<Order> checkOrders = dataStore.getOrders();
        if (checkOrders.size() == 3) {
            System.out.println("Orders added successfully.");
        } else {
            System.out.println("Orders addition failed.");
        }

        order1.setDataValue("status", Order.OrderStatus.PENDING);
        dataStore.updateOrder(order1);
        if (dataStore.getOrder(1).getData().get("status").equals(Order.OrderStatus.PENDING)) {
            System.out.println("Order 1 updated successfully.");
        } else {
            System.out.println("Order 1 update failed.");
        }

        order2.updateMetadata("tableNumber", 3);
        dataStore.updateOrder(order2);
        if (dataStore.getOrder(2).getMetadata().metadata().get("tableNumber").equals(3)) {
            System.out.println("Order 2 updated successfully.");
        } else {
            System.out.println("Order 2 update failed.");
        }

        order3.setDataValue("status", Order.OrderStatus.COMPLETED);
        dataStore.updateOrder(order3);
        if (dataStore.getOrder(3).getData().get("status").equals(Order.OrderStatus.COMPLETED)) {
            System.out.println("Order 3 updated successfully.");
        } else {
            System.out.println("Order 3 update failed.");
        }

        dataStore.removeOrder(dataStore.getOrder(3));
        if (dataStore.getOrder(3) == null) {
            System.out.println("Order 3 removed successfully.");
        } else {
            System.out.println("Order 3 removal failed.");
        }

        dataStore.addOrderItem(order1, item1, 1);
        dataStore.updateOrder(order1);
        dataStore.addOrderItem(order1, item3, 2);
        dataStore.updateOrder(order1);

        System.out.println(dataStore.getOrderItems(order1));
        DataStore finalDataStore = dataStore;
        dataStore.getOrderItems(order1).forEach((key, value) -> {
            System.out.println("Key: " + key + " Value: " + value);
            System.out.println(finalDataStore.getMenuItemById(key));
        });

        if (dataStore.getOrderItems(order1).size() == 2) {
            System.out.println("Order 1 items added successfully.");
        } else {
            System.out.println("Order 1 items addition failed.");
        }

        if (dataStore.getOrderItems(order1).get(item1.getMetadata().metadata().get("id")).equals(1) && dataStore.getOrderItems(order1).get(item3.getMetadata().metadata().get("id")).equals(2)) {
            System.out.println("Order 1 items added successfully.");
        } else {
            System.out.println("Order 1 items addition failed.");
        }



        dataStore.updateOrderItem(order1, item1, 2);
        dataStore.updateOrder(order1);
        if (dataStore.getOrderItems(order1).get(item1.getMetadata().metadata().get("id")).equals(2)) {
            System.out.println("Order 1 item updated successfully.");
            System.out.println(dataStore.getOrderItem(order1, item1.getMetadata().metadata().get("id").toString()));
        } else {
            System.out.println("Order 1 item update failed.");
        }

        dataStore.removeOrderItem(order1, item1, 2);
        dataStore.updateOrder(order1);
        if (dataStore.getOrderItems(order1).get(item1.getMetadata().metadata().get("id")) == null) {
            System.out.println("Order 1 item removed successfully.");
        } else {
            System.out.println("Order 1 item removal failed.");
        }

        System.out.println(dataStore.getOrderItems(order1));
        DataStore tempDatastore = dataStore;
        dataStore.getOrderItems(order1).forEach((key, value) -> {
            System.out.println("Key: " + key + " Value: " + value);
            System.out.println(tempDatastore.getMenuItemById(key));
        });

        dataStore.getOrders().forEach(System.out::println);

        /*===================================================================================================================================================================================================================================
        Code Description:
        This section tests the functionality of the AuthenticationManager class. It tests the login and logout functionality of the application.
        All the methods of the AuthenticationManager class are tested in this section.

        Tested Methods:
            - login()
            - verifyPassword()
        ===================================================================================================================================================================================================================================*/

        System.out.println("Testing Authentication...");

        Users user = dataStore.getUser("admin");
        if (user != null) {
            System.out.println("User retrieved successfully.");
        } else {
            System.out.println("User retrieval failed.");
        }

        if (user != null) {
            if (PasswordUtils.verifyPassword("admin", user.getData().get("password").toString())) {
                System.out.println("Password verification successful.");
            } else {
                System.out.println("Password verification failed.");
            }
        }

        if (user != null) {
            if (AuthenticationManager.login("admin", "admin")) {
                System.out.println("Login successful.");
            } else {
                System.out.println("Login failed.");
            }
        }

        /*===================================================================================================================================================================================================================================
        Code Description:
        This section tests the functionality of the DataStore class. It tests the retrieval of all users, menu items, orders, ingredients, and stock from the database

        Methods Tested:
            - getInstance() {Constructor}
        ===================================================================================================================================================================================================================================*/



        System.out.println("\n--- Simulating Application Exit and Re-Run ---");
        dataStore = DataStore.getInstance();

        System.out.println("Retrieving all users from the database after re-initialization:");
        dataStore.getUsers().forEach(System.out::println);

        System.out.println("Retrieving all menu items from the database after re-initialization:");
        dataStore.getMenuItems().forEach(System.out::println);

        System.out.println("Retrieving all orders from the database after re-initialization:");
        dataStore.getOrders().forEach(System.out::println);
    }
}







