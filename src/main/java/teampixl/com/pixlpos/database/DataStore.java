package teampixl.com.pixlpos.database;

import teampixl.com.pixlpos.constructs.MenuItem;
import teampixl.com.pixlpos.constructs.Order;
import teampixl.com.pixlpos.constructs.Users;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataStore {

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

    // MenuItem-related methods
    public ObservableList<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void addMenuItem(MenuItem item) {
        menuItems.add(item);
        saveMenuItemToDatabase(item);
    }

    public void removeMenuItem(MenuItem item) {
        menuItems.remove(item);
        deleteMenuItemFromDatabase(item);
    }

    // Load MenuItems from the SQLite database
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

    // Save a MenuItem to the SQLite database
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

    // Delete a MenuItem from the SQLite database
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

    // Order-related methods
    public ObservableList<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order order) {
        orders.add(order);
        saveOrderToDatabase(order);
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        deleteOrderFromDatabase(order);
    }

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
                order.updateOrderStatus(orderStatus);
                if (isCompleted) {
                    order.completeOrder();
                }
                order.setDataValue("total", total);
                orders.add(order);
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

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Users-related methods
    public ObservableList<Users> getUsers() {
        return users;
    }

    public void addUser(Users user) {
        users.add(user);
        saveUserToDatabase(user);
    }

    public void removeUser(Users user) {
        users.remove(user);
        deleteUserFromDatabase(user);
    }

    private void loadUsersFromDatabase() {
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            while (rs.next()) {
                String id = rs.getString("id");
                String username = rs.getString("username");
                Users.UserRole role = Users.UserRole.valueOf(rs.getString("role"));
                String email = rs.getString("email");

                Users user = new Users(username, "placeholder", email, role);
                user.updateMetadata("id", id);  // Set the ID from the database
                users.add(user);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void saveUserToDatabase(Users user) {
        String sql = "INSERT INTO users(id, username, password, email, role, created_at, updated_at) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, (String) user.getMetadata().metadata().get("id"));
            pstmt.setString(2, (String) user.getMetadata().metadata().get("username"));
            pstmt.setString(3, (String) user.getData().get("password")); // Placeholder, should be encrypted
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

    public void updateOrder(Order order) {
        // Find the existing order by ID and update it
        Order existingOrder = orders.stream()
                .filter(o -> o.getMetadata().metadata().get("order_id").equals(order.getMetadata().metadata().get("order_id")))
                .findFirst()
                .orElse(null);

        if (existingOrder != null) {
            orders.set(orders.indexOf(existingOrder), order);
            // Update in database
            saveOrderToDatabase(order);
        }
    }

    public void updateMenuItem(MenuItem menuItem) {
        // Find the existing menu item by ID and update it
        MenuItem existingItem = menuItems.stream()
                .filter(item -> item.getMetadata().metadata().get("id").equals(menuItem.getMetadata().metadata().get("id")))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            menuItems.set(menuItems.indexOf(existingItem), menuItem);
            // Update in database
            saveMenuItemToDatabase(menuItem);
        }
    }

    public void clearData() {
        // Clear all data in memory
        menuItems.clear();
        users.clear();
        orders.clear();

        // Optionally, clear the data from the database
        // clearDatabaseTables();
    }

    // Optional method to clear the database tables
    private void clearDatabaseTables() {
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DELETE FROM menu_items");
            stmt.executeUpdate("DELETE FROM users");
            stmt.executeUpdate("DELETE FROM orders");
            System.out.println("All data cleared from the database.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}





