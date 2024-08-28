package teampixl.com.pixlpos.database;

import teampixl.com.pixlpos.constructs.MenuItem;
import teampixl.com.pixlpos.constructs.Order;
import teampixl.com.pixlpos.constructs.Users;
import teampixl.com.pixlpos.constructs.Ingredients;
import teampixl.com.pixlpos.database.interfaces.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.HashMap;
import teampixl.com.pixlpos.authentication.PasswordUtils;

import java.sql.*;
import java.util.Map;

public class DataStore implements IUserStore, IMenuItemStore, IOrderStore, IIngredientsStore {

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

    private DataStore() {
        menuItems = FXCollections.observableArrayList();
        orders = FXCollections.observableArrayList();
        users = FXCollections.observableArrayList();
        ingredients = FXCollections.observableArrayList();

        // Load data from database on initialization
        loadMenuItemsFromDatabase();
        loadOrdersFromDatabase();
        loadUsersFromDatabase();
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }



    /*====================================================================================================================================================================
    Code Description:
    This section of code handles the implementation of the IMenuItemStore interface. It provides methods for adding, updating, and removing menu items.

    Methods:
        Methods for lists of menu items:
            - getMenuItems(): ObservableList<MenuItem> - Returns a list of all menu items.
            - addMenuItem(MenuItem item): void - Adds a new menu item to the list of menu items.
            - updateMenuItem(MenuItem item): void - Updates an existing menu item in the list of menu items.
            - removeMenuItem(MenuItem item): void - Removes an existing menu item from the list of menu items.

        Methods for singular items:
            - getMenuItem(String itemName): MenuItem - Returns a menu item with the specified name.
    ====================================================================================================================================================================*/



    public ObservableList<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void addMenuItem(MenuItem item) {
        menuItems.add(item);
        saveMenuItemToDatabase(item);
    }

    public void updateMenuItem(MenuItem item) {
        updateMenuItemInDatabase(item);
    }

    public void removeMenuItem(MenuItem item) {
        menuItems.remove(item);
        deleteMenuItemFromDatabase(item);
    }

    @Override
    public MenuItem getMenuItem(String itemName) {
        for (MenuItem item : menuItems) {
            if (item.getMetadata().metadata().get("itemName").equals(itemName)) {
                return item;
            }
        }
        return null;
    }



    /*====================================================================================================================================================================
     Code Description:
     This section of code handles the implementation of the IOrderStore interface. It provides methods for adding, updating, and removing orders.

     Methods:
        Methods for lists of orders:
            - getOrders(): ObservableList<Order> - Returns a list of all orders.
            - addOrder(Order order): void - Adds a new order to the list of orders.
            - updateOrder(Order order): void - Updates an existing order in the list of orders.
            - removeOrder(Order order): void - Removes an existing order from the list of orders.

        Methods for singular items:
            - addOrderItem(Order order, MenuItem item, int quantity): void - Adds a new item to an existing order.
            - updateOrderItem(Order order, MenuItem menuItem, int quantity): void - Updates an existing item in an order.
            - removeOrderItem(Order order, MenuItem item, int quantity): void - Removes an existing item from an order.
    ====================================================================================================================================================================*/



    public ObservableList<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order order) {
        orders.add(order);
        saveOrderToDatabase(order);
    }

    public void updateOrder(Order order) {
        updateOrderInDatabase(order);
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        deleteOrderFromDatabase(order);
    }

    // CRUD operations for singular items.

    public void addOrderItem(Order order, MenuItem item, int quantity) {
        order.addMenuItem(item, quantity);
        saveOrderItemToDatabase(order, item, quantity);
    }

    public void updateOrderItem(Order order, MenuItem menuItem, int quantity) {
        order.updateMenuItem(menuItem, quantity);
        updateOrderItemInDatabase(order, menuItem, quantity);
    }

    public void removeOrderItem(Order order, MenuItem item, int quantity) {
        order.removeMenuItem(item, quantity);
        deleteOrderItemFromDatabase(order, item, quantity);
    }



    /*====================================================================================================================================================================
    Code Description:
    This section of code handles the implementation of the IUserStore interface. It provides methods for adding, updating, and removing users.

    Methods:
        Methods for lists of users:
            - getUsers(): ObservableList<Users> - Returns a list of all users.
            - addUser(Users user): void - Adds a new user to the list of users.
            - updateUser(Users user): void - Updates an existing user in the list of users.
            - removeUser(Users user): void - Removes an existing user from the list of users.

        Methods for singular items:
            - getUser(String username): Users - Returns a user with the specified username.
    ====================================================================================================================================================================*/



    public ObservableList<Users> getUsers() {
        return users;
    }

    public void addUser(Users user) {
        if (usernameExists(user.getMetadata().metadata().get("username").toString())) {
            throw new IllegalArgumentException("Username already exists.");
        }

        users.add(user);
        saveUserToDatabase(user);
    }


    public void updateUser(Users user) {
        updateUserInDatabase(user);
    }

    public void removeUser(Users user) {
        users.remove(user);
        deleteUserFromDatabase(user);
    }

    @Override
    public Users getUser(String username) {
        for (Users user : users) {
            if (user.getMetadata().metadata().get("username").equals(username)) {
                return user;
            }
        }
        return null; // User not found
    }

    public void updateUserPassword(Users user, String newPassword) {
        String hashedPassword = PasswordUtils.hashPassword(newPassword);
        user.setDataValue("password", hashedPassword);
        updateUserInDatabase(user);
    }



    /*====================================================================================================================================================================
    Code Description:
    This section of code handles the implementation of the IIngredientStore interface. It provides methods for adding, updating, and removing ingredients.

    Methods:
        - getIngredients(): ObservableList<Ingredients> - Returns a list of all ingredients.
        - addIngredient(Ingredients ingredient): void - Adds a new ingredient to the list of ingredients.
        - updateIngredient(Ingredients ingredient): void - Updates an existing ingredient in the list of ingredients.
        - removeIngredient(Ingredients ingredient): void - Removes an existing ingredient from the list of ingredients.
    ====================================================================================================================================================================*/



    public ObservableList<Ingredients> getIngredients() {
        return ingredients;
    }

    public void addIngredient(Ingredients ingredient) {

    }

    public void updateIngredient(Ingredients ingredient) {

    }

    public void removeIngredient(Ingredients ingredient) {

    }



/*====================================================================================================================================================================

---------------------------------------------------------------->    END OF API IMPLEMENTATION    <-------------------------------------------------------------------

====================================================================================================================================================================*/



    /*====================================================================================================================================================================
    Code Description:
    This section of code outlines the methods used to interact with the database for MenuItems. It includes methods for loading, saving, updating, and deleting data.

    Methods (INTERNAL):
        - loadMenuItemsFromDatabase(): void - Loads menu items from the database.
        - saveMenuItemToDatabase(MenuItem item): void - Saves a menu item to the database.
        - updateMenuItemInDatabase(MenuItem item): void - Updates a menu item in the database.
        - deleteMenuItemFromDatabase(MenuItem item): void - Deletes a menu item from the database.
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
                item.updateMetadata("id", id);  // Set the ID from the database
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
                double total = rs.getDouble("total");

                Order order = new Order(orderNumber, userId);
                order.updateMetadata("order_id", orderId);
                order.updateMetadata("order_status", orderStatus);
                order.updateMetadata("is_completed", isCompleted);
                order.setDataValue("total", total);

                loadOrderItems((String) order.getMetadata().metadata().get("order_id"));

                orders.add(order);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadOrderItems(String orderId) {
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
                orderItemMap.put("menu_item_id", menuItemId);
                orderItemMap.put("quantity", quantity);

                orderItemsList.add(orderItemMap);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    private void saveOrderToDatabase(Order order) {
        String sql = "INSERT INTO orders(order_id, order_number, user_id, order_status, is_completed, total, created_at, updated_at) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) order.getMetadata().metadata().get("order_id"));
            pstmt.setInt(2, (Integer) order.getMetadata().metadata().get("order_number"));
            pstmt.setString(3, (String) order.getMetadata().metadata().get("user_id"));
            pstmt.setString(4, order.getMetadata().metadata().get("order_status").toString());
            pstmt.setInt(5, (Boolean) order.getMetadata().metadata().get("is_completed") ? 1 : 0);
            pstmt.setDouble(6, (Double) order.getData().get("total"));
            pstmt.setLong(7, (Long) order.getMetadata().metadata().get("created_at"));
            pstmt.setLong(8, (Long) order.getMetadata().metadata().get("updated_at"));

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
        String sql = "UPDATE orders SET order_status = ?, is_completed = ?, total = ?, updated_at = ? WHERE order_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, order.getMetadata().metadata().get("order_status").toString());
            pstmt.setInt(2, (Boolean) order.getMetadata().metadata().get("is_completed") ? 1 : 0);
            pstmt.setDouble(3, (Double) order.getData().get("total"));
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
                String username = rs.getString("username");
                String roleStr = rs.getString("role");
                Users.UserRole role = Users.UserRole.valueOf(roleStr);
                String email = rs.getString("email");
                String password = rs.getString("password");

                Users user = new Users(username, password, email, role);
                user.updateMetadata("id", id);
                users.add(user);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // If count > 0, the username exists
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private void saveUserToDatabase(Users user) {
        String sql = "INSERT INTO users(id, username, password, email, role, created_at, updated_at) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) user.getMetadata().metadata().get("id"));
            pstmt.setString(2, (String) user.getMetadata().metadata().get("username"));
            pstmt.setString(3, (String) user.getData().get("password"));
            pstmt.setString(4, (String) user.getData().get("email"));
            pstmt.setString(5, user.getMetadata().metadata().get("role").toString());
            pstmt.setLong(6, (Long) user.getMetadata().metadata().get("created_at"));
            pstmt.setLong(7, (Long) user.getMetadata().metadata().get("updated_at"));

            pstmt.executeUpdate();
            System.out.println("User saved to database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateUserInDatabase(Users user) {
        String sql = "UPDATE users SET username = ?, password = ?, email = ?, role = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) user.getMetadata().metadata().get("username"));
            pstmt.setString(2, (String) user.getData().get("password")); // Ensure password is stored as a hash
            pstmt.setString(3, (String) user.getData().get("email"));
            pstmt.setString(4, user.getMetadata().metadata().get("role").toString());
            pstmt.setLong(5, (Long) user.getMetadata().metadata().get("updated_at"));
            pstmt.setString(6, (String) user.getMetadata().metadata().get("id"));

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


    /*====================================================================================================================================================================
    Code Description:
    This section of code outlines the method used to clear all data from the database.

    Methods:
        - clearData(): void - Clears all data from the database.

        Methods for clearing individual tables (INTERNAL):
            - clearMenuItems(): void - Clears all menu items from the database.
            - clearOrders(): void - Clears all orders from the database.
            - clearUsers(): void - Clears all users from the database.
            - clearOrderItems(): void - Clears all order items from the database.
    ====================================================================================================================================================================*/



    public void clearData() {
        clearMenuItems();
        clearOrders();
        clearUsers();
        clearOrderItems();
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
}





