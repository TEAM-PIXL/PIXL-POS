package teampixl.com.pixlpos.database;

import teampixl.com.pixlpos.constructs.MenuItem;
import teampixl.com.pixlpos.constructs.Order;
import teampixl.com.pixlpos.constructs.Users;
import teampixl.com.pixlpos.database.interfaces.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.HashMap;

import java.sql.*;
import java.util.Map;

public class DataStore implements IUserStore, IMenuItemStore, IOrderStore {
    /*====================================================================================================================================================================
      This class is responsible for managing the data in the application. It acts as a facade for the database operations.
    ====================================================================================================================================================================*/


    private static DataStore instance = null;
    private ObservableList<MenuItem> menuItems;
    private ObservableList<Order> orders;
    private ObservableList<Users> users;

    private DataStore() {
        menuItems = FXCollections.observableArrayList();
        orders = FXCollections.observableArrayList();
        users = FXCollections.observableArrayList();

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
      This section of code handles the implementation of the IMenuItemStore interface. It provides methods for adding, updating, and removing menu items.
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
     This section of code handles the implementation of the IOrderStore interface. It provides methods for adding, updating, and removing orders.
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

    public void updateOrderItem(Order order, MenuItem item, int newQuantity) {
        order.addMenuItem(item, newQuantity); // update or add item with new quantity
        updateOrderItemInDatabase(order, item, newQuantity);
    }

    public void removeOrderItem(Order order, MenuItem item, int quantity) {
        order.removeMenuItem(item, quantity);
        deleteOrderItemFromDatabase(order, item, quantity);
    }



    /*====================================================================================================================================================================
      This section of code handles the implementation of the IUserStore interface. It provides methods for adding, updating, and removing users.
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
        return null;
    }




    /*====================================================================================================================================================================
     This section of code outlines the methods used to interact with the database for MenuItems. It includes methods for loading, saving, updating, and deleting data.
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
      This section of code outlines the methods used to interact with the database for Orders. It includes methods for loading, saving, updating, and deleting data.
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

                // Load associated menu items for the order
                loadOrderItems((String) order.getMetadata().metadata().get("order_id"));

                orders.add(order);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private ObservableList<Map<String, Object>> loadOrderItems(String orderId) {
        ObservableList<Map<String, Object>> orderItemsList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String menuItemId = rs.getString("menu_item_id");
                int quantity = rs.getInt("quantity");

                // Construct a map for each order item
                Map<String, Object> orderItemMap = new HashMap<>();
                orderItemMap.put("menu_item_id", menuItemId);
                orderItemMap.put("quantity", quantity);

                orderItemsList.add(orderItemMap);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return orderItemsList;
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

            // Save associated menu items
            saveOrderItemsToDatabase(order);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

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

            // Update associated menu items
            updateOrderItemsInDatabase(order);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateOrderItemsInDatabase(Order order) {
        // First, delete existing order items
        deleteOrderItemsFromDatabase(order);

        // Then, save the new ones
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

            // Also delete the associated menu items
            deleteOrderItemsFromDatabase(order);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /*====================================================================================================================================================================
      This section of code outlines the methods used to interact with the database for Users. It includes methods for loading, saving, updating, and deleting data.
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
            pstmt.setString(2, (String) user.getData().get("password"));
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
        This section of code outlines the method used to clear all data from the database.
    ====================================================================================================================================================================*/



    public void clearData() {
        clearMenuItems();
        clearOrders();
        clearUsers();
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





