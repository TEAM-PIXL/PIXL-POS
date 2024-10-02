package teampixl.com.pixlpos.database;

import teampixl.com.pixlpos.models.*;
import teampixl.com.pixlpos.database.interfaces.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for managing the data in the application. It acts as a facade for the database operations. It provides methods for adding, updating, and removing data from the database.
 * This is the application programming interface (API) for the database.
 * It implements the IUserStore, IMenuItemStore, IOrderStore, IIngredientsStore, and IStockStore interfaces.
 */
public class DataStore implements IUserStore, IMenuItemStore, IOrderStore, IIngredientsStore, IStockStore {



/*====================================================================================================================================================================================

    ------------>    THIS IS THE APPLICATION PROGRAMMING INTERFACE (API) FOR THE DATABASE. IT PROVIDES METHODS FOR ADDING, UPDATING, AND REMOVING DATA FROM THE DATABASE.  <------------

====================================================================================================================================================================================*/



    /*====================================================================================================================================================================
    Code Description:
    This class is responsible for managing the data in the application. It acts as a facade for the database operations.

    Class Variables:
        - instance: DataStore - A singleton instance of the DataStore class.
        - menuItems: ObservableList<MenuItem> - A list of all menu items.
        - orders: ObservableList<Order> - A list of all orders.
        - users: ObservableList<Users> - A list of all users.
        - ingredients: ObservableList<Ingredients> - A list of all ingredients.
        - stockItems: ObservableList<Stock> - A list of all stock items.

    Constructor:
        - DataStore() - Initializes the DataStore object and loads data from the database.

    Methods:
        - getInstance(): DataStore - Returns the singleton instance of the DataStore class.
    ====================================================================================================================================================================*/



    private static DataStore instance = null;
    private final ObservableList<MenuItem> menuItems;
    private final ObservableList<Order> orders;
    private final ObservableList<Users> users;
    private final ObservableList<Ingredients> ingredients;
    private final ObservableList<Stock> stockItems;

    private DataStore() {
        menuItems = FXCollections.observableArrayList();
        orders = FXCollections.observableArrayList();
        users = FXCollections.observableArrayList();
        ingredients = FXCollections.observableArrayList();
        stockItems = FXCollections.observableArrayList();

        loadMenuItemsFromDatabase();
        loadOrdersFromDatabase();
        loadUsersFromDatabase();
        loadIngredientsFromDatabase();
        loadStockFromDatabase();
    }

    /**
     * Returns the singleton instance of the DataStore class.
     * @return DataStore - The singleton instance of the DataStore class.
     */
    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }



    /*====================================================================================================================================================================
    Code Description:
    This section of code handles the implementation of the IMenuItemStore interface. It provides methods for creating, reading, updating, and deleting menu items and ingredients.
    Methods:
        Methods for lists of menu items:
            - createMenuItem(MenuItem item): void - Adds a new menu item to the list of menu items.
            - readMenuItems(): ObservableList<MenuItem> - Returns a list of all menu items.
            - updateMenuItem(MenuItem item): void - Updates an existing menu item in the list of menu items.
            - deleteMenuItem(MenuItem item): void - Removes an existing menu item from the list of menu items.

        Methods for Ingredient-MenuItem relationships:
            - createMenuItemIngredient(MenuItem menuItem, Ingredients ingredient, Object numeral): void - Adds an ingredient to a menu item.
            - readMenuItemIngredients(MenuItem menuItem): Map - Returns a map of all ingredients for a menu item.
            - updateMenuItemIngredient(MenuItem menuItem, Ingredients ingredient, Object numeral): void - Updates an ingredient in a menu item.
            - deleteMenuItemIngredient(MenuItem menuItem, Ingredients ingredient): void - Removes an ingredient from a menu item.

        Methods for singular items (DEPRECATED):
            - getMenuItem(String itemName): MenuItem - Returns a menu item with the specified name.
            - getMenuItemById(String itemId): MenuItem - Returns a menu item with the specified ID.
    ====================================================================================================================================================================*/


    
    /**
     * Adds a new menu item to the list of menu items.
     * @param item MenuItem - The menu item to add.
     */
    public void createMenuItem(MenuItem item) {
        menuItems.add(item);
        saveMenuItemToDatabase(item);
    }


    /**
     * Returns a list of all menu items.
     * @return ObservableList<MenuItem> - A list of all menu items.
     */
    public ObservableList<MenuItem> readMenuItems() {
        return menuItems;
    }

    /**
     * Updates an existing menu item in the list of menu items.
     * @param item MenuItem - The menu item to update.
     */
    public void updateMenuItem(MenuItem item) {
        updateMenuItemInDatabase(item);
    }

    /**
     * Removes an existing menu item from the list of menu items.
     * @param item MenuItem - The menu item to remove.
     */
    public void deleteMenuItem(MenuItem item) {
        menuItems.remove(item);
        deleteMenuItemFromDatabase(item);
    }

    /**
     * Adds an ingredient to a menu item.
     * @param menuItem MenuItem - The menu item to add the ingredient to.
     * @param ingredient Ingredients - The ingredient to add.
     * @param numeral Object - The quantity of the ingredient to add.
     */
    public void createMenuItemIngredient(MenuItem menuItem, Ingredients ingredient, Object numeral) {
        menuItem.addIngredient(ingredient, numeral);
        saveMenuItemIngredientsToDatabase(menuItem, ingredient, numeral);
    }

    /**
     * Returns a map of all ingredients for a menu item.
     * @param menuItem MenuItem - The menu item to get ingredients for.
     * @return Map - A map of all ingredients for the menu item.
     */
    public Map<String, Object> readMenuItemIngredients(MenuItem menuItem) {
        return getMenuItemIngredientsFromDatabase(menuItem);
    }

    /**
     * Updates an ingredient in a menu item.
     * @param menuItem MenuItem - The menu item to update the ingredient in.
     * @param ingredient Ingredients - The ingredient to update.
     * @param numeral Object - The new quantity of the ingredient.
     */
    public void updateMenuItemIngredient(MenuItem menuItem, Ingredients ingredient, Object numeral) {
        menuItem.updateIngredient((String) ingredient.getMetadata().metadata().get("ingredient_id"), ingredient, numeral);
        updateMenuItemIngredientsInDatabase(menuItem);
    }

    /**
     * Removes an ingredient from a menu item.
     * @param menuItem MenuItem - The menu item to remove the ingredient from.
     * @param ingredient Ingredients - The ingredient to remove.
     */
    public void deleteMenuItemIngredient(MenuItem menuItem, Ingredients ingredient) {
        menuItem.removeIngredient(ingredient);
        updateMenuItemIngredientsInDatabase(menuItem);
    }

    /* ---------------->   METHODS WILL BE DEPRECATED IN FUTURE RELEASES REPLACED BY MENUITEM API   <------------------------- */

    /**
     * Returns a menu item with the specified name.
     * METHODS WILL BE DEPRECATED IN FUTURE RELEASES REPLACED BY MENUITEM API
     * @param itemName String - The name of the menu item to get.
     * @return MenuItem - The menu item with the specified name.
     */
    @Override
    public MenuItem getMenuItem(String itemName) {
        for (MenuItem item : menuItems) {
            if (item.getMetadata().metadata().get("itemName").equals(itemName)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Returns a menu item with the specified ID.
     * METHODS WILL BE DEPRECATED IN FUTURE RELEASES REPLACED BY MENUITEM API
     * @param itemId String - The ID of the menu item to get.
     * @return MenuItem - The menu item with the specified ID.
     */
    public MenuItem getMenuItemById(String itemId) {
        for (MenuItem item : menuItems) {
            if (item.getMetadata().metadata().get("id").equals(itemId)) {
                return item;
            }
        }
        return null;
    }



    /*====================================================================================================================================================================
     Code Description:
     This section of code handles the implementation of the IOrderStore interface. It provides methods for creating, reading, updating, and deleting orders and order items.

     Methods:
        Methods for orders:
            - createOrder(Order order): void - Adds a new order to the list of orders.
            - readOrders(): ObservableList<Order> - Returns a list of all orders.
            - updateOrder(Order order): void - Updates an existing order in the list of orders.
            - deleteOrder(Order order): void - Removes an existing order from the list of orders.

        Methods for order items:
            - createOrderItem(Order order, MenuItem item, int quantity): void - Adds a new item to an existing order.
            - readOrderItems(Order order): Map - Returns a map of all items in an order.
            - updateOrderItem(Order order, MenuItem menuItem, int quantity): void - Updates an existing item in an order.
            - deleteOrderItem(Order order, MenuItem item, int quantity): void - Removes an existing item from an order.

        Methods for getting orders and order items (DEPRECATED):
            - getOrder(int orderNumber): Order - Returns an order with the specified order number.
            - getOrderItem(Order order, String menuItemId): Map - Returns an item in an order with the specified menu item ID.
            - reloadOrdersFromDatabase(): void - Reloads orders from the database. (SPECIAL METHOD FOR RELOADING ORDERS FROM DATABASE, EXPERIMENTAL FOR CURRENT RELEASE)
    ====================================================================================================================================================================*/



    /**
     * Adds a new order to the list of orders.
     * @param order Order - The order to add.
     */
    public void createOrder(Order order) {
        orders.add(order);
        saveOrderToDatabase(order);
    }

    /**
     * Returns a list of all orders.
     * @return ObservableList<Order> - A list of all orders.
     */
    public ObservableList<Order> readOrders() {
        return orders;
    }

    /**
     * Updates an existing order in the list of orders.
     * @param order Order - The order to update.
     */
    public void updateOrder(Order order) {
        updateOrderInDatabase(order);
        int index = orders.indexOf(order);
        if (index != -1) {
            orders.set(index, order);
        } else {
            orders.add(order);
        }
    }

    /**
     * Removes an existing order from the list of orders.
     * @param order Order - The order to remove.
     */
    public void deleteOrder(Order order) {
        orders.remove(order);
        deleteOrderFromDatabase(order);
    }

    /**
     * Adds a new item to an existing order.
     * @param order Order - The order to add the item to.
     * @param item MenuItem - The item to add.
     * @param quantity int - The quantity of the item to add.
     */
    public void createOrderItem(Order order, MenuItem item, int quantity) {
        order.addMenuItem(item, quantity);
        saveOrderItemToDatabase(order, item, quantity);
        deductIngredientsFromStock(item, quantity);
    }

    /**
     * Returns a map of all items in an order.
     * @param order Order - The order to get items for.
     * @return Map - A map of all items in the order.
     */
    public Map<String, Object> readOrderItems(Order order) {
        return getOrderItemsFromDatabase((String) order.getMetadata().metadata().get("order_id"));
    }

    /**
     * Updates an existing item in an order.
     * @param order Order - The order to update the item in.
     * @param menuItem MenuItem - The item to update.
     * @param quantity int - The new quantity of the item.
     */
    public void updateOrderItem(Order order, MenuItem menuItem, int quantity) {
        order.updateMenuItem(menuItem, quantity);
        updateOrderItemInDatabase(order, menuItem, quantity);
        deductIngredientsFromStock(menuItem, quantity);
    }

    /**
     * Removes an existing item from an order.
     * @param order Order - The order to remove the item from.
     * @param item MenuItem - The item to remove.
     * @param quantity int - The quantity of the item to remove.
     */
    public void deleteOrderItem(Order order, MenuItem item, int quantity) {
        order.removeMenuItem(item, quantity);
        deleteOrderItemFromDatabase(order, item, quantity);
        restoreIngredientsToStock(item, quantity);
    }

    /* ---------------->   METHODS WILL BE DEPRECATED IN FUTURE RELEASES REPLACED BY ORDER API   <------------------------- */

    /**
     * Returns an order with the specified order number.
     * @param orderNumber int - The order number to get.
     * @return Order - The order with the specified order number.
     */
    public Order getOrder(int orderNumber) {
        for (Order order : orders) {
            if (order.getMetadata().metadata().get("order_number").equals(orderNumber)) {
                return order;
            }
        }
        return null;
    }

    /**
     * Returns an item in an order with the specified menu item ID.
     * @param order Order - The order to get the item from.
     * @param menuItemId String - The menu item ID to get.
     * @return Map - The item in the order with the specified menu item ID.
     */
    public Map<String, Object> getOrderItem(Order order, String menuItemId) {
        Map<String, Object> orderItems = getOrderItemsFromDatabase((String) order.getMetadata().metadata().get("order_id"));
        for (Map.Entry<String, Object> entry : orderItems.entrySet()) {
            if (entry.getKey().equals(menuItemId)) {
                Map<String, Object> orderItem = new HashMap<>();
                orderItem.put("Item", getMenuItemById(entry.getKey()));
                orderItem.put("Quantity", entry.getValue());
                return orderItem;
            }
        }
        return null;
    }

    /**
     * Reloads orders from the database.
     * SPECIAL METHOD FOR RELOADING ORDERS FROM DATABASE, EXPERIMENTAL FOR CURRENT RELEASE.
     * METHOD MAY BE DEPRECATED IN FUTURE RELEASES.
     */
    public void reloadOrdersFromDatabase() {
        orders.clear();
        loadOrdersFromDatabase();
    }



    /*====================================================================================================================================================================
    Code Description:
    This section of code handles the implementation of the IUserStore interface. It provides methods for creating, reading, updating, and deleting users.

    Methods:
        Methods for users:
            - createUser(Users user): void - Adds a new user to the list of users.
            - readUsers(): ObservableList<Users> - Returns a list of all users.
            - updateUser(Users user): void - Updates an existing user in the list of users.
            - deleteUser(Users user): void - Removes an existing user from the list of users.

        Methods for user operations (DEPRECATED):
            - updateUserPassword(Users user, String newPassword): void - Updates a user's password.
            - usernameExists(String username): boolean - Checks if a username already exists in the database.
            - getUser(String username): Users - Returns a user with the specified username.
    ====================================================================================================================================================================*/



    /**
     * Adds a new user to the list of users.
     * @param user Users - The user to add.
     */
    public void createUser(Users user) {
        users.add(user);
        saveUserToDatabase(user);
    }

    /**
     * Returns a list of all users.
     * @return ObservableList<Users> - A list of all users.
     */
    public ObservableList<Users> readUsers() {
        return users;
    }

    /**
     * Updates an existing user in the list of users.
     * @param user Users - The user to update.
     */
    public void updateUser(Users user) {
        updateUserInDatabase(user);
    }

    /**
     * Removes an existing user from the list of users.
     * @param user Users - The user to remove.
     */
    public void deleteUser(Users user) {
        users.remove(user);
        deleteUserFromDatabase(user);
    }



    /*====================================================================================================================================================================
    Code Description:
    This section of code handles the implementation of the IIngredientStore interface. It provides methods for creating, reading, updating, and deleting ingredients.

    Methods:
        Methods for ingredients:
            - createIngredient(Ingredients ingredient): void - Adds a new ingredient to the list of ingredients.
            - readIngredients(): ObservableList<Ingredients> - Returns a list of all ingredients.
            - updateIngredient(Ingredients ingredient): void - Updates an existing ingredient in the list of ingredients.
            - deleteIngredient(Ingredients ingredient): void - Removes an existing ingredient from the list of ingredients.

        Methods for getting ingredients (DEPRECATED):
            - getIngredient(String ingredientName): Ingredients - Returns an ingredient with the specified name.
    ====================================================================================================================================================================*/



    /**
     * Adds a new ingredient to the list of ingredients.
     * @param ingredient Ingredients - The ingredient to add.
     */
    public void createIngredient(Ingredients ingredient) {
        ingredients.add(ingredient);
        saveIngredientToDatabase(ingredient);
    }

    /**
     * Returns a list of all ingredients.
     * @return ObservableList<Ingredients> - A list of all ingredients.
     */
    public ObservableList<Ingredients> readIngredients() {
        return ingredients;
    }

    /**
     * Updates an existing ingredient in the list of ingredients.
     * @param ingredient Ingredients - The ingredient to update.
     */
    public void updateIngredient(Ingredients ingredient) {
        updateIngredientInDatabase(ingredient);
    }

    /**
     * Removes an existing ingredient from the list of ingredients.
     * @param ingredient Ingredients - The ingredient to remove.
     */
    public void deleteIngredient(Ingredients ingredient) {
        ingredients.remove(ingredient);
        deleteIngredientFromDatabase(ingredient);
    }

    /* ---------------->   METHODS WILL BE DEPRECATED IN FUTURE RELEASES REPLACED BY INGREDIENT API   <------------------------- */

    /**
     * Returns an ingredient with the specified name.
     * @param ingredientName String - The name of the ingredient to get.
     * @return Ingredients - The ingredient with the specified name.
     */
    public Ingredients getIngredient(String ingredientName) {
        for (Ingredients ingredient : ingredients) {
            if (ingredient.getMetadata().metadata().get("itemName").equals(ingredientName)) {
                return ingredient;
            }
        }
        return null;
    }



    /*====================================================================================================================================================================
    Code Description:
    This section of code outlines the methods used to interact with the database for Stock. It includes methods for creating, reading, updating, and deleting stock data.

    Methods:
        Methods for lists of stock items:
            - createStock(Stock stock): void - Adds a new stock item to the list of stock items.
            - readStock(): ObservableList<Stock> - Returns a list of all stock items.
            - updateStock(Stock stock): void - Updates an existing stock item in the list of stock items.
            - deleteStock(Stock stock): void - Removes an existing stock item from the list of stock items.

        Methods for getting stock items (DEPRECATED):
            - getStockItem(String itemName): Stock - Returns a stock item with the specified ingredient name.
    ====================================================================================================================================================================*/



    /**
     * Adds a new stock item to the list of stock items.
     * @param stock Stock - The stock item to add.
     */
    public void createStock(Stock stock) {
        stockItems.add(stock);
        saveStockToDatabase(stock);
    }

    /**
     * Returns a list of all stock items.
     * @return ObservableList<Stock> - A list of all stock items.
     */
    public ObservableList<Stock> readStock() {
        return stockItems;
    }

    /**
     * Updates an existing stock item in the list of stock items.
     * @param stock Stock - The stock item to update.
     */
    public void updateStock(Stock stock) {
        updateStockInDatabase(stock);
    }

    /**
     * Removes an existing stock item from the list of stock items.
     * @param stock Stock - The stock item to remove.
     */
    public void deleteStock(Stock stock) {
        stockItems.remove(stock);
        deleteStockFromDatabase(stock);
    }

    /* ---------------->   METHODS WILL BE DEPRECATED IN FUTURE RELEASES REPLACED BY STOCK API   <------------------------- */

    /**
     * Returns a stock item with the specified ingredient name.
     * @param itemName String - The ingredient name of the stock item to get.
     * @return Stock - The stock item with the specified ingredient name.
     */
    public Stock getStockItem(String itemName) {
        String checkIngredientId = getIngredientIdByIngredientName(itemName);
        return getStockItemByIngredientId(checkIngredientId);
    }



/*====================================================================================================================================================================================

    -------------------------------------------------------------------------->    END OF API IMPLEMENTATION  <-------------------------------------------------------------------

====================================================================================================================================================================================*/



    /*====================================================================================================================================================================
    Code Description:
    This section of code outlines the methods used to interact with the database for MenuItems. It includes methods for loading, saving, updating, and deleting data.

    Methods (INTERNAL):
        Methods for lists of menu items:
        - loadMenuItemsFromDatabase(): void - Loads menu items from the database.
        - saveMenuItemToDatabase(MenuItem item): void - Saves a menu item to the database.
        - updateMenuItemInDatabase(MenuItem item): void - Updates a menu item in the database.
        - deleteMenuItemFromDatabase(MenuItem item): void - Deletes a menu item from the database.

        Methods for Ingredient-MenuItem relationships:
        - saveMenuItemIngredientsToDatabase(MenuItem menuItem): void - Saves MenuItem-Ingredient relationships to the database.
        - updateMenuItemIngredientsInDatabase(MenuItem menuItem): void - Updates MenuItem-Ingredient relationships in the database.
        - deleteMenuItemIngredientsFromDatabase(MenuItem menuItem): void - Deletes MenuItem-Ingredient relationships from the database.
        - menuItemIngredientExists(String menuItemId, String ingredientId, Connection conn): boolean - Checks if a MenuItem-Ingredient relationship exists in the database.
        - loadMenuItemIngredientsFromDatabase(MenuItem menuItem): void - Loads MenuItem-Ingredient relationships from the database.
    ====================================================================================================================================================================*/



    private void loadMenuItemsFromDatabase() {
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM menu_items")) {

            while (rs.next()) {
                String id = rs.getString("id");
                String itemName = rs.getString("item_name");
                double price = rs.getDouble("price");
                MenuItem.ItemType itemType = MenuItem.ItemType.valueOf(rs.getString("item_type"));
                boolean activeItem = rs.getInt("active_item") == 1;
                String dietaryRequirementStr = rs.getString("dietary_requirement");
                MenuItem.DietaryRequirement dietaryRequirement = dietaryRequirementStr != null ? MenuItem.DietaryRequirement.valueOf(dietaryRequirementStr) : null;
                String description = rs.getString("description");

                MenuItem item = new MenuItem(itemName, price, itemType, activeItem, description, dietaryRequirement);
                item.updateMetadata("id", id);
                menuItems.add(item);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    private void saveMenuItemToDatabase(MenuItem item) {
        String sql = "INSERT INTO menu_items(id, item_name, price, item_type, active_item, dietary_requirement, description, notes, amount_ordered) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) item.getMetadata().metadata().get("id"));
            pstmt.setString(2, (String) item.getMetadata().metadata().get("itemName"));
            pstmt.setDouble(3, (Double) item.getMetadata().metadata().get("price"));
            pstmt.setString(4, item.getMetadata().metadata().get("itemType").toString());
            pstmt.setInt(5, (Boolean) item.getMetadata().metadata().get("activeItem") ? 1 : 0);
            pstmt.setString(6, item.getMetadata().metadata().get("dietaryRequirement") != null ? item.getMetadata().metadata().get("dietaryRequirement").toString() : null);
            pstmt.setString(7, (String) item.getData().get("description"));
            pstmt.setString(8, (String) item.getData().get("notes"));
            pstmt.setInt(9, (Integer) item.getData().get("amountOrdered"));

            pstmt.executeUpdate();
            System.out.println("MenuItem saved to database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateMenuItemInDatabase(MenuItem item) {
        String sql = "UPDATE menu_items SET item_name = ?, price = ?, item_type = ?, active_item = ?, dietary_requirement = ?, description = ?, notes = ?, amount_ordered = ? WHERE id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) item.getMetadata().metadata().get("itemName"));
            pstmt.setDouble(2, (Double) item.getMetadata().metadata().get("price"));
            pstmt.setString(3, item.getMetadata().metadata().get("itemType").toString());
            pstmt.setInt(4, (Boolean) item.getMetadata().metadata().get("activeItem") ? 1 : 0);
            pstmt.setString(5, item.getMetadata().metadata().get("dietaryRequirement") != null ? item.getMetadata().metadata().get("dietaryRequirement").toString() : null);
            pstmt.setString(6, (String) item.getData().get("description"));
            pstmt.setString(7, (String) item.getData().get("notes"));
            pstmt.setInt(8, (Integer) item.getData().get("amountOrdered"));
            pstmt.setString(9, (String) item.getMetadata().metadata().get("id"));

            pstmt.executeUpdate();
            System.out.println("MenuItem updated in database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteMenuItemFromDatabase(MenuItem item) {
        String sql = "DELETE FROM menu_items WHERE id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) item.getMetadata().metadata().get("id"));
            pstmt.executeUpdate();
            System.out.println("MenuItem deleted from database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    private void loadMenuItemIngredientsFromDatabase(MenuItem menuItem) {
        String sql = "SELECT * FROM menu_item_ingredients WHERE menu_item_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) menuItem.getMetadata().metadata().get("id"));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String ingredientId = rs.getString("ingredient_id");
                double numeral = rs.getDouble("numeral");
                Ingredients ingredient = getIngredientById(ingredientId);

                menuItem.addIngredient(ingredient, numeral);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private Map<String, Object> getMenuItemIngredientsFromDatabase(MenuItem menuItem) {
        Map<String, Object> ingredientsMap = new HashMap<>();
        String sql = "SELECT * FROM menu_item_ingredients WHERE menu_item_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) menuItem.getMetadata().metadata().get("id"));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String ingredientId = rs.getString("ingredient_id");
                double numeral = rs.getDouble("numeral");  // Assuming that numeral is stored as REAL in SQLite
                Ingredients ingredient = getIngredientById(ingredientId);  // You'll need a method to fetch ingredient by ID

                assert ingredient != null;
                ingredientsMap.put(ingredient.getMetadata().metadata().get("itemName").toString(), numeral);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return ingredientsMap;
    }

    private void saveMenuItemIngredientsToDatabase(MenuItem menuItem, Ingredients ingredient, Object numeral) {
        String sql = "INSERT INTO menu_item_ingredients(menu_item_id, ingredient_id, numeral) VALUES(?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) menuItem.getMetadata().metadata().get("id"));
            pstmt.setString(2, (String) ingredient.getMetadata().metadata().get("ingredient_id"));
            if (numeral instanceof Integer) {
                pstmt.setInt(3, (Integer) numeral);
            } else if (numeral instanceof Double) {
                pstmt.setDouble(3, (Double) numeral);
            }

            pstmt.executeUpdate();
            System.out.println("MenuItem-Ingredients relationships saved to database with numeral.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateMenuItemIngredientsInDatabase(MenuItem menuItem) {
        deleteMenuItemIngredientsFromDatabase(menuItem);
        for (Map.Entry<String, MenuItem.IngredientAmount> entry : menuItem.getIngredients().entrySet()) {
            saveMenuItemIngredientsToDatabase(menuItem, entry.getValue().ingredient(), entry.getValue().numeral());
        }
    }

    private void deleteMenuItemIngredientsFromDatabase(MenuItem menuItem) {
        String sql = "DELETE FROM menu_item_ingredients WHERE menu_item_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) menuItem.getMetadata().metadata().get("id"));
            pstmt.executeUpdate();

            System.out.println("MenuItem-Ingredients relationships deleted from database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



    /*====================================================================================================================================================================
    Code Description:
    This section of code outlines the methods used to interact with the database for Orders. It includes methods for loading, saving, updating, and deleting data.

    Methods (INTERNAL):
            Methods for lists of orders:
                - loadOrdersFromDatabase(): void - Loads orders from the database.
                - saveOrderToDatabase(Order order): void - Saves an order to the database.
                - updateOrderInDatabase(Order order): void - Updates an order in the database.
                - deleteOrderFromDatabase(Order order): void - Deletes an order from the database.

            Methods for singular items:
                - saveOrderItemToDatabase(Order order, MenuItem item, int quantity): void - Saves an order item to the database.
                - updateOrderItemInDatabase(Order order, MenuItem item, int newQuantity): void - Updates an order item in the database.
                - deleteOrderItemFromDatabase(Order order, MenuItem item, int quantity): void - Deletes an order item from the database.

            Methods for order items:
                - loadOrderItems(String orderId): ObservableList<Map<String, Object>> - Loads order items from the database.
                - saveOrderItemsToDatabase(Order order): void - Saves order items to the database.
                - updateOrderItemsInDatabase(Order order): void - Updates order items in the database.
                - deleteOrderItemsFromDatabase(Order order): void - Deletes order items from the database.
    ====================================================================================================================================================================*/



    private void loadOrdersFromDatabase() {
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM orders")) {

            while (rs.next()) {
                String orderId = rs.getString("order_id");
                int orderNumber = rs.getInt("order_number");
                String userId = rs.getString("user_id");
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(rs.getString("order_status"));
                boolean isCompleted = rs.getInt("is_completed") == 1;
                Order.OrderType orderType = Order.OrderType.valueOf(rs.getString("order_type"));
                int tableNumber = rs.getInt("table_number");
                long createdAt = rs.getLong("created_at");
                long updatedAt = rs.getLong("updated_at");
                double total = rs.getDouble("total");
                String specialRequests = rs.getString("special_requests");

                Order order = new Order(orderNumber, userId);
                order.updateMetadata("order_id", orderId);
                order.updateMetadata("order_status", orderStatus);
                order.updateMetadata("is_completed", isCompleted);
                order.updateMetadata("order_type", orderType);
                order.updateMetadata("table_number", tableNumber);
                order.updateMetadata("created_at", createdAt);
                order.updateMetadata("updated_at", updatedAt);
                order.setDataValue("total", total);
                order.setDataValue("specialRequests", specialRequests);
                order.setDataValue("paymentMethod", rs.getString("payment_method"));

                orders.add(order);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void loadOrderItems(String orderId, Order order) {
        ObservableList<Map<String, Object>> orderItemsList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String menuItemId = rs.getString("menu_item_id");
                int quantity = rs.getInt("quantity");

                Map<String, Object> orderItemMap = new HashMap<>();
                orderItemMap.put("Item", menuItemId);
                orderItemMap.put("Quantity", quantity);

                orderItemsList.add(orderItemMap);
            }

            if (order != null) {
                @SuppressWarnings("unchecked")
                Map<String, Integer> menuItems = (Map<String, Integer>) order.getData().get("menuItems");
                if (menuItems == null) {
                    menuItems = new HashMap<>();
                    order.setDataValue("menuItems", menuItems);
                }
                for (Map<String, Object> orderItem : orderItemsList) {
                    String menuItemId = (String) orderItem.get("Item");
                    int quantity = (int) orderItem.get("Quantity");
                    menuItems.put(menuItemId, quantity);
                }
            } else {
                System.out.println("Order not found for orderId: " + orderId);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateOrderItem(Order order, String itemId, int newQuantity) {
        String sql = "UPDATE order_items SET quantity = ? WHERE order_id = ? AND menu_item_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newQuantity);
            pstmt.setString(2, (String) order.getMetadata().metadata().get("order_id"));
            pstmt.setString(3, itemId);

            pstmt.executeUpdate();
            System.out.println("Order item updated in the database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private Map<String, Object> getOrderItemsFromDatabase(String orderId) {
        Map<String, Object> orderItemsMap = new HashMap<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String menuItemId = rs.getString("menu_item_id");
                int quantity = rs.getInt("quantity");

                orderItemsMap.put(menuItemId, quantity);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return orderItemsMap;
    }

    private void saveOrderToDatabase(Order order) {
        String sql = "INSERT INTO orders(order_id, order_number, user_id, order_status, is_completed, order_type, table_number, total, special_requests, payment_method, created_at, updated_at) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) order.getMetadata().metadata().get("order_id"));
            pstmt.setInt(2, (Integer) order.getMetadata().metadata().get("order_number"));
            pstmt.setString(3, (String) order.getMetadata().metadata().get("user_id"));
            pstmt.setString(4, order.getMetadata().metadata().get("order_status").toString());
            pstmt.setInt(5, (Boolean) order.getMetadata().metadata().get("is_completed") ? 1 : 0);
            pstmt.setString(6, order.getMetadata().metadata().get("order_type").toString());
            pstmt.setInt(7, (Integer) order.getMetadata().metadata().get("table_number"));
            pstmt.setDouble(8, (Double) order.getData().get("total"));
            pstmt.setString(9, (String) order.getData().get("specialRequests"));
            pstmt.setString(10, (String) order.getData().get("paymentMethod"));
            pstmt.setLong(11, (Long) order.getMetadata().metadata().get("created_at"));
            pstmt.setLong(12, (Long) order.getMetadata().metadata().get("updated_at"));

            pstmt.executeUpdate();
            System.out.println("Order saved to database.");

            saveOrderItemsToDatabase(order);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void saveOrderItemsToDatabase(Order order) {
        String sql = "INSERT INTO order_items(order_id, menu_item_id, quantity) VALUES(?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            Map<String, Integer> menuItems = (Map<String, Integer>) order.getData().get("menuItems");

            for (Map.Entry<String, Integer> entry : menuItems.entrySet()) {
                pstmt.setString(1, (String) order.getMetadata().metadata().get("order_id"));
                pstmt.setString(2, entry.getKey());
                pstmt.setInt(3, entry.getValue());
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void saveOrderItemToDatabase(Order order, MenuItem item, int quantity) {
        String sql = "INSERT INTO order_items(order_id, menu_item_id, quantity) VALUES(?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) order.getMetadata().metadata().get("order_id"));
            pstmt.setString(2, (String) item.getMetadata().metadata().get("id"));
            pstmt.setInt(3, quantity);

            pstmt.executeUpdate();
            System.out.println("Order item saved to database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateOrderItemInDatabase(Order order, MenuItem item, int newQuantity) {
        String sql = "UPDATE order_items SET quantity = ? WHERE order_id = ? AND menu_item_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newQuantity);
            pstmt.setString(2, (String) order.getMetadata().metadata().get("order_id"));
            pstmt.setString(3, (String) item.getMetadata().metadata().get("id"));

            pstmt.executeUpdate();
            System.out.println("Order item updated in database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateOrderInDatabase(Order order) {
        String sql = "UPDATE orders SET order_status = ?, is_completed = ?, order_type = ?, table_number = ?, total = ?, special_requests = ?, payment_method = ?, updated_at = ? WHERE order_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, order.getMetadata().metadata().get("order_status").toString());
            pstmt.setInt(2, (Boolean) order.getMetadata().metadata().get("is_completed") ? 1 : 0);
            pstmt.setString(3, order.getMetadata().metadata().get("order_type").toString());
            pstmt.setInt(4, (Integer) order.getMetadata().metadata().get("table_number"));
            pstmt.setDouble(3, (Double) order.getData().get("total"));
            pstmt.setString(4, (String) order.getData().get("specialRequests"));
            pstmt.setString(5, (String) order.getData().get("paymentMethod"));
            pstmt.setLong(4, (Long) order.getMetadata().metadata().get("updated_at"));
            pstmt.setString(5, (String) order.getMetadata().metadata().get("order_id"));

            pstmt.executeUpdate();
            System.out.println("Order updated in database.");

            updateOrderItemsInDatabase(order);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateOrderItemsInDatabase(Order order) {
        deleteOrderItemsFromDatabase(order);
        saveOrderItemsToDatabase(order);
    }

    private void deleteOrderItemsFromDatabase(Order order) {
        String sql = "DELETE FROM order_items WHERE order_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) order.getMetadata().metadata().get("order_id"));
            pstmt.executeUpdate();

            System.out.println("Order items deleted from database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteOrderItemFromDatabase(Order order, MenuItem item, int quantity) {
        String sql = "DELETE FROM order_items WHERE order_id = ? AND menu_item_id = ? AND quantity = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) order.getMetadata().metadata().get("order_id"));
            pstmt.setString(2, (String) item.getMetadata().metadata().get("id"));
            pstmt.setInt(3, quantity);

            pstmt.executeUpdate();
            System.out.println("Order item deleted from database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteOrderFromDatabase(Order order) {
        String sql = "DELETE FROM orders WHERE order_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) order.getMetadata().metadata().get("order_id"));
            pstmt.executeUpdate();

            System.out.println("Order deleted from database.");

            deleteOrderItemsFromDatabase(order);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



    /*====================================================================================================================================================================
    Code Description:
    This section of code outlines the methods used to interact with the database for Users. It includes methods for loading, saving, updating, and deleting data.

    Methods (INTERNAL):
        - loadUsersFromDatabase(): void - Loads users from the database.
        - saveUserToDatabase(Users user): void - Saves a user to the database.
        - updateUserInDatabase(Users user): void - Updates a user in the database.
        - deleteUserFromDatabase(Users user): void - Deletes a user from the database.
    ====================================================================================================================================================================*/



    private void loadUsersFromDatabase() {
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            while (rs.next()) {
                String id = rs.getString("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String username = rs.getString("username");
                String roleStr = rs.getString("role");
                Users.UserRole role = Users.UserRole.valueOf(roleStr);
                String email = rs.getString("email");
                String password = rs.getString("password");
                String additionalInfo = rs.getString("additional_info");
                Boolean isActive = rs.getInt("is_active") == 1;
                Users user = new Users(firstName, lastName,username, password, email, role);
                user.updateMetadata("id", id);
                user.updateMetadata("is_active", isActive);
                user.setDataValue("additional_info", additionalInfo);
                users.add(user);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void saveUserToDatabase(Users user) {
        String sql = "INSERT INTO users(id, first_name, last_name, username, password, email, role, created_at, updated_at, is_active, additional_info) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) user.getMetadata().metadata().get("id"));
            pstmt.setString(2, (String) user.getMetadata().metadata().get("first_name"));
            pstmt.setString(3, (String) user.getMetadata().metadata().get("last_name"));
            pstmt.setString(4, (String) user.getMetadata().metadata().get("username"));
            pstmt.setString(5, (String) user.getData().get("password"));
            pstmt.setString(6, (String) user.getData().get("email"));
            pstmt.setString(7, user.getMetadata().metadata().get("role").toString());
            pstmt.setLong(8, (Long) user.getMetadata().metadata().get("created_at"));
            pstmt.setLong(9, (Long) user.getMetadata().metadata().get("updated_at"));
            pstmt.setBoolean(10, (Boolean) user.getMetadata().metadata().get("is_active"));
            pstmt.setString(11, (String) user.getData().get("additional_info"));

            pstmt.executeUpdate();
            System.out.println("User saved to database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateUserInDatabase(Users user) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, username = ?, password = ?, email = ?, role = ?, updated_at = ?, is_active = ?, additional_info = ? WHERE id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) user.getMetadata().metadata().get("first_name"));
            pstmt.setString(2, (String) user.getMetadata().metadata().get("last_name"));
            pstmt.setString(3, (String) user.getMetadata().metadata().get("username"));
            pstmt.setString(4, (String) user.getData().get("password"));
            pstmt.setString(5, (String) user.getData().get("email"));
            pstmt.setString(6, user.getMetadata().metadata().get("role").toString());
            pstmt.setLong(7, (Long) user.getMetadata().metadata().get("updated_at"));
            pstmt.setBoolean(8, (Boolean) user.getMetadata().metadata().get("is_active"));
            pstmt.setString(9, (String) user.getData().get("additional_info"));
            pstmt.setString(10, (String) user.getMetadata().metadata().get("id"));

            pstmt.executeUpdate();
            System.out.println("User updated in database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteUserFromDatabase(Users user) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) user.getMetadata().metadata().get("id"));
            pstmt.executeUpdate();
            System.out.println("User deleted from database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



    /*====================================================================================================================================================================
    Code Description:
    This section of code outlines the methods used to interact with the database for Ingredients. It includes methods for loading, saving, updating, and deleting data.

    Methods (INTERNAL):
        - loadIngredientsFromDatabase(): void - Loads ingredients from the database.
        - saveIngredientToDatabase(Ingredients ingredient): void - Saves an ingredient to the database.
        - updateIngredientInDatabase(Ingredients ingredient): void - Updates an ingredient in the database.
        - deleteIngredientFromDatabase(Ingredients ingredient): void - Deletes an ingredient from the database.
    ====================================================================================================================================================================*/



    private void loadIngredientsFromDatabase() {
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ingredients")) {

            while (rs.next()) {
                String ingredientId = rs.getString("ingredient_id");
                String itemName = rs.getString("item_name");
                String notes = rs.getString("notes");

                Ingredients ingredient = new Ingredients(itemName, notes);
                ingredient.updateMetadata("ingredient_id", ingredientId); // Set the ingredient ID from the database

                ingredients.add(ingredient);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void saveIngredientToDatabase(Ingredients ingredient) {
        String sql = "INSERT INTO ingredients(ingredient_id, item_name, notes) VALUES(?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) ingredient.getMetadata().metadata().get("ingredient_id"));
            pstmt.setString(2, (String) ingredient.getMetadata().metadata().get("itemName"));
            pstmt.setString(3, (String) ingredient.getData().get("notes"));

            pstmt.executeUpdate();
            System.out.println("Ingredient saved to database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateIngredientInDatabase(Ingredients ingredient) {
        String sql = "UPDATE ingredients SET item_name = ?, notes = ? WHERE ingredient_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) ingredient.getMetadata().metadata().get("itemName"));
            pstmt.setString(2, (String) ingredient.getData().get("notes"));
            pstmt.setString(3, (String) ingredient.getMetadata().metadata().get("ingredient_id"));

            pstmt.executeUpdate();
            System.out.println("Ingredient updated in database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteIngredientFromDatabase(Ingredients ingredient) {
        String sql = "DELETE FROM ingredients WHERE ingredient_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) ingredient.getMetadata().metadata().get("ingredient_id"));
            pstmt.executeUpdate();
            System.out.println("Ingredient deleted from database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }




    /*====================================================================================================================================================================
    Code Description:
    This section of code outlines the methods used to interact with the database for Stock. It includes methods for loading, saving, updating, and deleting data.

    Methods (INTERNAL):
        - loadStockFromDatabase(): void - Loads stock from the database.
        - saveStockToDatabase(Stock stock): void - Saves a stock item to the database.
        - updateStockInDatabase(Stock stock): void - Updates a stock item in the database.
        - deleteStockFromDatabase(Stock stock): void - Deletes a stock item from the database.

        Methods for Ingredient-Stock relationships:
        - getIngredientById(String ingredientId): Ingredients - Returns an ingredient with the specified ID.
    ====================================================================================================================================================================*/



    private void loadStockFromDatabase() {
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM stock")) {

            while (rs.next()) {
                String ingredientId = rs.getString("ingredient_id");
                Ingredients ingredient = getIngredientById(ingredientId);  // Fetch the ingredient by its ID

                if (ingredient != null) {
                    Stock.StockStatus stockStatus = Stock.StockStatus.valueOf(rs.getString("stock_status"));
                    Stock.UnitType unitType = Stock.UnitType.valueOf(rs.getString("unit_type"));
                    Object numeral;
                    if (unitType == Stock.UnitType.QTY) {
                        numeral = rs.getInt("numeral");
                    } else {
                        numeral = rs.getDouble("numeral");
                    }
                    boolean onOrder = rs.getInt("on_order") == 1;

                    Stock stock = new Stock(ingredient, stockStatus, unitType, numeral, onOrder);
                    stockItems.add(stock);
                } else {
                    System.out.println("Ingredient not found for ID: " + ingredientId);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void saveStockToDatabase(Stock stock) {
        String sql = "INSERT INTO stock(stock_id, ingredient_id, stock_status, on_order, created_at, last_updated, unit_type, numeral) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) stock.getMetadata().metadata().get("stock_id"));
            pstmt.setString(2, (String) stock.getMetadata().metadata().get("ingredient_id"));
            pstmt.setString(3, stock.getMetadata().metadata().get("stockStatus").toString());
            pstmt.setInt(4, (Boolean) stock.getMetadata().metadata().get("onOrder") ? 1 : 0);
            pstmt.setString(5, stock.getMetadata().metadata().get("created_at").toString());
            pstmt.setString(6, stock.getMetadata().metadata().get("lastUpdated").toString());
            pstmt.setString(7, stock.getData().get("unit").toString());
            pstmt.setObject(8, stock.getData().get("numeral"));

            pstmt.executeUpdate();
            System.out.println("Stock saved to database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateStockInDatabase(Stock stock) {
        String sql = "UPDATE stock SET stock_status = ?, on_order = ?, last_updated = ?, unit_type = ?, numeral = ? WHERE stock_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, stock.getMetadata().metadata().get("stockStatus").toString());
            pstmt.setInt(2, (Boolean) stock.getMetadata().metadata().get("onOrder") ? 1 : 0);
            pstmt.setString(3, stock.getMetadata().metadata().get("lastUpdated").toString());

            String unit = stock.getData().get("unit") != null ? stock.getData().get("unit").toString() : "UNKNOWN";
            Object numeral = stock.getData().get("numeral");

            pstmt.setString(4, unit);
            if (numeral instanceof Integer) {
                pstmt.setInt(5, (Integer) numeral);
            } else if (numeral instanceof Double) {
                pstmt.setDouble(5, (Double) numeral);
            } else {
                pstmt.setNull(5, Types.NULL);
            }

            pstmt.setString(6, (String) stock.getMetadata().metadata().get("stock_id"));

            pstmt.executeUpdate();
            System.out.println("Stock updated in database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteStockFromDatabase(Stock stock) {
        String sql = "DELETE FROM stock WHERE stock_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) stock.getMetadata().metadata().get("stock_id"));
            pstmt.executeUpdate();
            System.out.println("Stock deleted from database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private Ingredients getIngredientById(String ingredientId) {
        for (Ingredients ingredient : ingredients) {
            if (ingredient.getMetadata().metadata().get("ingredient_id").equals(ingredientId)) {
                return ingredient;
            }
        }
        return null;
    }



    /*====================================================================================================================================================================
    Code Description:
    This section of code outlines the methods used to manage stock levels based on orders.

    Methods (INTERNAL):
        - deductIngredientsFromStock(MenuItem item, int quantity): void - Deducts the appropriate amount of each ingredient from the stock based on the order.
        - restoreIngredientsToStock(MenuItem item, int quantity): void - Restores the appropriate amount of each ingredient to the stock if an order is canceled or modified.
        - getStockItemByIngredientId(String ingredientId): Stock - Returns the stock item for the specified ingredient ID.
        - getIngredientIdByIngredientName(String ingredientName): String - Returns the ingredient ID for the specified ingredient name.
    ====================================================================================================================================================================*/



    private void deductIngredientsFromStock(MenuItem item, int quantity) {
        for (Map.Entry<String, MenuItem.IngredientAmount> entry : item.getIngredients().entrySet()) {
            Ingredients ingredient = entry.getValue().ingredient();
            Object numeral = entry.getValue().numeral();

            Stock stockItem = getStockItemByIngredientId((String) ingredient.getMetadata().metadata().get("ingredient_id"));

            if (stockItem != null) {
                if (stockItem.getData().get("unit").equals(Stock.UnitType.QTY)) {
                    int currentStock = (int) stockItem.getData().get("numeral");
                    int newStock = currentStock - ((Integer) numeral * quantity);
                    stockItem.setDataValue("numeral", newStock);
                } else {
                    double currentStock = (double) stockItem.getData().get("numeral");
                    double newStock = currentStock - ((Double) numeral * quantity);
                    stockItem.setDataValue("numeral", newStock);
                }
                updateStock(stockItem);
            }
        }
    }

    private void restoreIngredientsToStock(MenuItem item, int quantity) {
        for (Map.Entry<String, MenuItem.IngredientAmount> entry : item.getIngredients().entrySet()) {
            Ingredients ingredient = entry.getValue().ingredient();
            Object numeral = entry.getValue().numeral();

            Stock stockItem = getStockItemByIngredientId((String) ingredient.getMetadata().metadata().get("ingredient_id"));

            if (stockItem != null) {
                if (stockItem.getData().get("unit").equals(Stock.UnitType.QTY)) {
                    int currentStock = (int) stockItem.getData().get("numeral");
                    int newStock = currentStock + ((Integer) numeral * quantity);
                    stockItem.setDataValue("numeral", newStock);
                } else {
                    double currentStock = (double) stockItem.getData().get("numeral");
                    double newStock = currentStock + ((Double) numeral * quantity);
                    stockItem.setDataValue("numeral", newStock);
                }
                updateStock(stockItem);
            }
        }
    }

    /**
     * This method is used to get the stock item by ingredient id
     * @param ingredientId - The ingredient id
     * @return - The stock item
     */
    public Stock getStockItemByIngredientId(String ingredientId) {
        for (Stock stockItem : stockItems) {
            if (stockItem.getMetadata().metadata().get("ingredient_id").equals(ingredientId)) {
                return stockItem;
            }
        }
        return null;
    }

    private String getIngredientIdByIngredientName(String ingredientName) {
        for (Ingredients ingredient : ingredients) {
            if (ingredient.getMetadata().metadata().get("itemName").equals(ingredientName)) {
                return (String) ingredient.getMetadata().metadata().get("ingredient_id");
            }
        }
        return null;
    }



    /*====================================================================================================================================================================
    Code Description:
    This section of code outlines the method used to clear all data from the database.

    Methods:
        - clearData(): void - Clears all data from the database.

        Methods for clearing individual tables (INTERNAL):
            - clearUsers(): void - Clears all users from the database.
            - clearIngredient(): void - Clears all ingredient relationships from the database.
            - clearIngredients(): void - Clears all ingredients from the database.
            - clearStock(): void - Clears all stock from the database.
            - clearMenuItems(): void - Clears all menu items from the database.
            - clearOrders(): void - Clears all orders from the database.
            - clearOrderItems(): void - Clears all order items from the database.
    ====================================================================================================================================================================*/



    /**
     * This method is used to clear all data from the database.
     * THIS METHOD SHOULD BE USED WITH CAUTION.
     */
    public void clearData() {
        clearUsers();
        clearIngredient();
        clearIngredients();
        clearMenuItems();
        clearOrders();
        clearOrderItems();
        clearStock();
    }

    private void clearUsers() {
        users.clear();
        String sql = "DELETE FROM users";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Users table cleared.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void clearIngredient() {
        String sql = "DELETE FROM menu_item_ingredients";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("MenuItem-Ingredients relationships table cleared.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void clearIngredients() {
        ingredients.clear();
        String sql = "DELETE FROM ingredients";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Ingredients table cleared.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void clearStock() {
        stockItems.clear();
        String sql = "DELETE FROM stock";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Stock table cleared.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void clearMenuItems() {
        menuItems.clear();
        String sql = "DELETE FROM menu_items";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("MenuItems table cleared.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void clearOrders() {
        orders.clear();
        String sql = "DELETE FROM orders";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Orders table cleared.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void clearOrderItems() {
        String sql = "DELETE FROM order_items";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("OrderItems table cleared.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}






