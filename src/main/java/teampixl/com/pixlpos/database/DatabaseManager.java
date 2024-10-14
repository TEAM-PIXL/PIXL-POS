package teampixl.com.pixlpos.database;

import teampixl.com.pixlpos.controllers.adminconsole.Notes;
import teampixl.com.pixlpos.models.*;
import teampixl.com.pixlpos.models.logs.*;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The DatabaseManager class handles all database operations for the application.
 * It provides asynchronous methods for loading, saving, updating, and deleting data from the database.
 */
public class DatabaseManager {

    /* ===================== Generic Database Operations ===================== */

    private static <T> CompletableFuture<List<T>> loadEntities(String sql, Function<ResultSet, T> mapper) {
        return CompletableFuture.supplyAsync(() -> {
            List<T> entities = new ArrayList<>();
            try (Connection conn = DatabaseHelper.connect();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    T entity = mapper.apply(rs);
                    if (entity != null) {
                        entities.add(entity);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error loading entities: " + e.getMessage());
            }
            return entities;
        });
    }

    private static CompletableFuture<Void> executeUpdate(String sql, PreparedStatementSetter setter) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = DatabaseHelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                setter.setValues(pstmt);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error executing update: " + e.getMessage());
            }
        });
    }

    /* ===================== MenuItem Database Operations ===================== */

    public static CompletableFuture<List<MenuItem>> loadMenuItemsFromDatabase() {
        String sql = "SELECT * FROM menu_items";
        return loadEntities(sql, rs -> {
            try {
                String id = rs.getString("id");
                String itemName = rs.getString("item_name");
                double price = rs.getDouble("price");
                MenuItem.ItemType itemType = MenuItem.ItemType.valueOf(rs.getString("item_type"));
                boolean activeItem = rs.getInt("active_item") == 1;
                String dietaryRequirementStr = rs.getString("dietary_requirement");
                MenuItem.DietaryRequirement dietaryRequirement = dietaryRequirementStr != null ? MenuItem.DietaryRequirement.valueOf(dietaryRequirementStr) : null;
                String description = rs.getString("description");
                String notes = rs.getString("notes");
                Integer amountOrdered = rs.getInt("amount_ordered");

                MenuItem item = new MenuItem(itemName, price, itemType, activeItem, description, dietaryRequirement);
                item.updateMetadata("id", id);
                item.setDataValue("notes", notes);
                item.setDataValue("amountOrdered", amountOrdered);

                getMenuItemIngredientsFromDatabase(item).thenAccept(ingredients -> {
                    for (Map.Entry<String, Object> entry : ingredients.entrySet()) {
                        Ingredients ingredient = DataStore.getInstance().getIngredientById(entry.getKey());
                        Object numeral = entry.getValue();
                        item.addIngredient(ingredient, numeral);
                    }
                });

                return item;
            } catch (SQLException e) {
                System.err.println("Error mapping MenuItem: " + e.getMessage());
                return null;
            }
        });
    }

    public static CompletableFuture<Void> saveMenuItemToDatabase(MenuItem item) {
        String sql = "INSERT INTO menu_items(id, item_name, price, item_type, active_item, dietary_requirement, description, notes, amount_ordered) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return executeUpdate(sql, pstmt -> {
            pstmt.setString(1, (String) item.getMetadata().metadata().get("id"));
            pstmt.setString(2, (String) item.getMetadata().metadata().get("itemName"));
            pstmt.setDouble(3, (Double) item.getMetadata().metadata().get("price"));
            pstmt.setString(4, item.getMetadata().metadata().get("itemType").toString());
            pstmt.setInt(5, (Boolean) item.getMetadata().metadata().get("activeItem") ? 1 : 0);
            pstmt.setString(6, item.getMetadata().metadata().get("dietaryRequirement") != null ? item.getMetadata().metadata().get("dietaryRequirement").toString() : null);
            pstmt.setString(7, (String) item.getData().get("description"));
            pstmt.setString(8, (String) item.getData().get("notes"));
            pstmt.setInt(9, (Integer) item.getData().get("amountOrdered"));
        }).thenCompose(v -> saveMenuItemIngredientsToDatabase(item));
    }

    public static CompletableFuture<Void> updateMenuItemInDatabase(MenuItem item) {
        String sql = "UPDATE menu_items SET item_name = ?, price = ?, item_type = ?, active_item = ?, dietary_requirement = ?, description = ?, notes = ?, amount_ordered = ? WHERE id = ?";
        return executeUpdate(sql, pstmt -> {
            pstmt.setString(1, (String) item.getMetadata().metadata().get("itemName"));
            pstmt.setDouble(2, (Double) item.getMetadata().metadata().get("price"));
            pstmt.setString(3, item.getMetadata().metadata().get("itemType").toString());
            pstmt.setInt(4, (Boolean) item.getMetadata().metadata().get("activeItem") ? 1 : 0);
            pstmt.setString(5, item.getMetadata().metadata().get("dietaryRequirement") != null ? item.getMetadata().metadata().get("dietaryRequirement").toString() : null);
            pstmt.setString(6, (String) item.getData().get("description"));
            pstmt.setString(7, (String) item.getData().get("notes"));
            pstmt.setInt(8, (Integer) item.getData().get("amountOrdered"));
            pstmt.setString(9, (String) item.getMetadata().metadata().get("id"));
        }).thenCompose(v -> updateMenuItemIngredientsInDatabase(item));
    }

    public static CompletableFuture<Void> deleteMenuItemFromDatabase(MenuItem item) {
        return deleteMenuItemIngredientsFromDatabase(item)
                .thenCompose(v -> {
                    String sql = "DELETE FROM menu_items WHERE id = ?";
                    return executeUpdate(sql, pstmt -> pstmt.setString(1, (String) item.getMetadata().metadata().get("id")));
                });
    }

    /* ===================== MenuItem-Ingredient Relationship Methods ===================== */

    public static CompletableFuture<Void> saveMenuItemIngredientsToDatabase(MenuItem menuItem) {
        String sql = "INSERT INTO menu_item_ingredients(menu_item_id, ingredient_id, numeral) VALUES(?, ?, ?)";
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = DatabaseHelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                for (Map.Entry<String, MenuItem.IngredientAmount> entry : menuItem.getIngredients().entrySet()) {
                    Ingredients ingredient = entry.getValue().ingredient();
                    Object numeral = entry.getValue().numeral();

                    pstmt.setString(1, (String) menuItem.getMetadata().metadata().get("id"));
                    pstmt.setString(2, (String) ingredient.getMetadata().metadata().get("ingredient_id"));
                    if (numeral instanceof Integer) {
                        pstmt.setInt(3, (Integer) numeral);
                    } else if (numeral instanceof Double) {
                        pstmt.setDouble(3, (Double) numeral);
                    }

                    pstmt.addBatch();
                }

                pstmt.executeBatch();
            } catch (SQLException e) {
                System.err.println("Error saving MenuItem ingredients: " + e.getMessage());
            }
        });
    }

    public static CompletableFuture<Void> updateMenuItemIngredientsInDatabase(MenuItem menuItem) {
        return deleteMenuItemIngredientsFromDatabase(menuItem)
                .thenCompose(v -> saveMenuItemIngredientsToDatabase(menuItem));
    }

    public static CompletableFuture<Void> deleteMenuItemIngredientsFromDatabase(MenuItem menuItem) {
        String sql = "DELETE FROM menu_item_ingredients WHERE menu_item_id = ?";
        return executeUpdate(sql, pstmt -> pstmt.setString(1, (String) menuItem.getMetadata().metadata().get("id")));
    }

    public static CompletableFuture<Map<String, Object>> getMenuItemIngredientsFromDatabase(MenuItem menuItem) {
        String sql = "SELECT * FROM menu_item_ingredients WHERE menu_item_id = ?";
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> ingredientsMap = new HashMap<>();
            try (Connection conn = DatabaseHelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, (String) menuItem.getMetadata().metadata().get("id"));
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    String ingredientId = rs.getString("ingredient_id");
                    Object numeral = rs.getObject("numeral");
                    ingredientsMap.put(ingredientId, numeral);
                }

            } catch (SQLException e) {
                System.err.println("Error getting MenuItem ingredients: " + e.getMessage());
            }
            return ingredientsMap;
        });
    }

    /* ===================== Order Database Operations ===================== */

    public static CompletableFuture<List<Order>> loadOrdersFromDatabase() {
        long twentyFourHoursAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        String sql = "SELECT * FROM orders WHERE updated_at >= " + twentyFourHoursAgo;

        return loadEntities(sql, rs -> {
            try {
                String orderId = rs.getString("order_id");
                int orderNumber = rs.getInt("order_number");
                String userId = rs.getString("user_id");
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(rs.getString("order_status"));
                boolean isCompleted = rs.getInt("is_completed") == 1;
                Order.OrderType orderType = Order.OrderType.valueOf(rs.getString("order_type"));
                int tableNumber = rs.getInt("table_number");
                int customers = rs.getInt("customers");
                long createdAt = rs.getLong("created_at");
                long updatedAt = rs.getLong("updated_at");
                double total = rs.getDouble("total");
                String specialRequests = rs.getString("special_requests");
                Order.PaymentMethod paymentMethod = Order.PaymentMethod.valueOf(rs.getString("payment_method"));

                Order order = new Order(orderNumber, userId);
                order.updateMetadata("order_id", orderId);
                order.updateMetadata("order_status", orderStatus);
                order.updateMetadata("is_completed", isCompleted);
                order.updateMetadata("order_type", orderType);
                order.updateMetadata("table_number", tableNumber);
                order.updateMetadata("customers", customers);
                order.updateMetadata("created_at", createdAt);
                order.updateMetadata("updated_at", updatedAt);
                order.setDataValue("total", total);
                order.setDataValue("special_requests", specialRequests);
                order.setDataValue("payment_method", paymentMethod);

                return order;
            } catch (SQLException e) {
                System.err.println("Error mapping Order: " + e.getMessage());
                return null;
            }
        }).thenCompose(orders -> loadAllOrderItems(orders).thenApply(v -> orders));
    }

    private static CompletableFuture<Void> loadAllOrderItems(List<Order> orders) {

        List<String> orderIds = orders.stream()
                .map(order -> (String) order.getMetadataValue("order_id"))
                .collect(Collectors.toList());

        return getAllOrderItemsFromDatabase(orderIds).thenAccept(orderItemsMap -> {
            for (Order order : orders) {
                String orderId = (String) order.getMetadataValue("order_id");
                Map<String, Integer> orderItems = orderItemsMap.get(orderId);
                order.setDataValue("menuItems", Objects.requireNonNullElse(orderItems, Collections.emptyMap()));
            }
        });
    }

    public static CompletableFuture<Map<String, Map<String, Integer>>> getAllOrderItemsFromDatabase(List<String> orderIds) {
        if (orderIds.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyMap());
        }

        String placeholders = String.join(",", Collections.nCopies(orderIds.size(), "?"));
        String sql = "SELECT * FROM order_items WHERE order_id IN (" + placeholders + ")";

        return CompletableFuture.supplyAsync(() -> {
            Map<String, Map<String, Integer>> allOrderItemsMap = new HashMap<>();

            try (Connection conn = DatabaseHelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                for (int i = 0; i < orderIds.size(); i++) {
                    pstmt.setString(i + 1, orderIds.get(i));
                }

                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    String orderId = rs.getString("order_id");
                    String menuItemId = rs.getString("menu_item_id");
                    int quantity = rs.getInt("quantity");

                    allOrderItemsMap
                            .computeIfAbsent(orderId, k -> new HashMap<>())
                            .put(menuItemId, quantity);
                }

            } catch (SQLException e) {
                System.err.println("Error getting all Order items: " + e.getMessage());
            }
            return allOrderItemsMap;
        });
    }



    public static CompletableFuture<Void> saveOrderToDatabase(Order order) {
        String sql = "INSERT INTO orders(order_id, order_number, user_id, order_status, is_completed, order_type, table_number, customers, total, special_requests, payment_method, created_at, updated_at) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return executeUpdate(sql, pstmt -> {
            pstmt.setString(1, (String) order.getMetadata().metadata().get("order_id"));
            pstmt.setInt(2, (Integer) order.getMetadata().metadata().get("order_number"));
            pstmt.setString(3, (String) order.getMetadata().metadata().get("user_id"));
            pstmt.setString(4, order.getMetadata().metadata().get("order_status").toString());
            pstmt.setInt(5, (Boolean) order.getMetadata().metadata().get("is_completed") ? 1 : 0);
            pstmt.setString(6, order.getMetadata().metadata().get("order_type").toString());
            pstmt.setInt(7, (Integer) order.getMetadata().metadata().get("table_number"));
            pstmt.setInt(8, (Integer) order.getMetadata().metadata().get("customers"));
            pstmt.setDouble(9, (Double) order.getData().get("total"));
            pstmt.setString(10, (String) order.getData().get("special_requests"));
            pstmt.setString(11, order.getData().get("payment_method").toString());
            pstmt.setLong(12, (Long) order.getMetadata().metadata().get("created_at"));
            pstmt.setLong(13, (Long) order.getMetadata().metadata().get("updated_at"));
        }).thenCompose(v -> saveOrderItemsToDatabase(order));
    }

    public static CompletableFuture<Void> updateOrderInDatabase(Order order) {
        String sql = "UPDATE orders SET order_status = ?, is_completed = ?, order_type = ?, table_number = ?, total = ?, special_requests = ?, payment_method = ?, customers = ?, updated_at = ? WHERE order_id = ?";
        return executeUpdate(sql, pstmt -> {
            pstmt.setString(1, order.getMetadata().metadata().get("order_status").toString());
            pstmt.setInt(2, (Boolean) order.getMetadata().metadata().get("is_completed") ? 1 : 0);
            pstmt.setString(3, order.getMetadata().metadata().get("order_type").toString());
            pstmt.setInt(4, (Integer) order.getMetadata().metadata().get("table_number"));
            pstmt.setDouble(5, (Double) order.getData().get("total"));
            pstmt.setString(6, (String) order.getData().get("special_requests"));
            pstmt.setString(7, order.getData().get("payment_method").toString());
            pstmt.setInt(8, (Integer) order.getMetadata().metadata().get("customers"));
            pstmt.setLong(9, (Long) order.getMetadata().metadata().get("updated_at"));
            pstmt.setString(10, (String) order.getMetadata().metadata().get("order_id"));
        }).thenCompose(v -> updateOrderItemsInDatabase(order));
    }

    public static CompletableFuture<Void> deleteOrderFromDatabase(Order order) {
        return deleteOrderItemsFromDatabase(order)
                .thenCompose(v -> {
                    String sql = "DELETE FROM orders WHERE order_id = ?";
                    return executeUpdate(sql, pstmt -> pstmt.setString(1, (String) order.getMetadata().metadata().get("order_id")));
                });
    }

    /* ===================== Order Items Methods ===================== */

    public static CompletableFuture<Void> saveOrderItemsToDatabase(Order order) {
        String sql = "INSERT INTO order_items(order_id, menu_item_id, quantity) VALUES(?, ?, ?)";
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = DatabaseHelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                @SuppressWarnings("unchecked")
                Map<String, Integer> menuItems = (Map<String, Integer>) order.getData().get("menuItems");

                for (Map.Entry<String, Integer> entry : menuItems.entrySet()) {
                    pstmt.setString(1, (String) order.getMetadata().metadata().get("order_id"));
                    pstmt.setString(2, entry.getKey());
                    pstmt.setInt(3, entry.getValue());
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
            } catch (SQLException e) {
                System.err.println("Error saving Order items: " + e.getMessage());
            }
        });
    }

    public static CompletableFuture<Void> updateOrderItemsInDatabase(Order order) {
        return deleteOrderItemsFromDatabase(order)
                .thenCompose(v -> saveOrderItemsToDatabase(order));
    }

    public static CompletableFuture<Void> deleteOrderItemsFromDatabase(Order order) {
        String sql = "DELETE FROM order_items WHERE order_id = ?";
        return executeUpdate(sql, pstmt -> pstmt.setString(1, (String) order.getMetadata().metadata().get("order_id")));
    }

    public static CompletableFuture<Map<String, Integer>> getOrderItemsFromDatabase(String orderId) {
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Integer> orderItemsMap = new HashMap<>();
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
                System.err.println("Error getting Order items: " + e.getMessage());
            }
            return orderItemsMap;
        });
    }

    /* ===================== User Database Operations ===================== */

    public static CompletableFuture<List<Users>> loadUsersFromDatabase() {
        String sql = "SELECT * FROM users";
        return loadEntities(sql, rs -> {
            try {
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
                Users user = new Users(firstName, lastName, username, password, email, role);
                user.updateMetadata("id", id);
                user.updateMetadata("is_active", isActive);
                user.setDataValue("additional_info", additionalInfo);
                return user;
            } catch (SQLException e) {
                System.err.println("Error mapping Users: " + e.getMessage());
                return null;
            }
        });
    }

    public static CompletableFuture<Void> saveUserToDatabase(Users user) {
        String sql = "INSERT INTO users(id, first_name, last_name, username, password, email, role, created_at, updated_at, is_active, additional_info) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return executeUpdate(sql, pstmt -> {
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
        });
    }

    public static CompletableFuture<Void> updateUserInDatabase(Users user) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, username = ?, password = ?, email = ?, role = ?, updated_at = ?, is_active = ?, additional_info = ? WHERE id = ?";
        return executeUpdate(sql, pstmt -> {
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
        });
    }

    public static CompletableFuture<Void> deleteUserFromDatabase(Users user) {
        String sql = "DELETE FROM users WHERE id = ?";
        return executeUpdate(sql, pstmt -> pstmt.setString(1, (String) user.getMetadata().metadata().get("id")));
    }

    /* ===================== Ingredient Database Operations ===================== */

    public static CompletableFuture<List<Ingredients>> loadIngredientsFromDatabase() {
        String sql = "SELECT * FROM ingredients";
        return loadEntities(sql, rs -> {
            try {
                String ingredientId = rs.getString("ingredient_id");
                String itemName = rs.getString("item_name");
                String notes = rs.getString("notes");

                Ingredients ingredient = new Ingredients(itemName, notes);
                ingredient.updateMetadata("ingredient_id", ingredientId);

                return ingredient;
            } catch (SQLException e) {
                System.err.println("Error mapping Ingredients: " + e.getMessage());
                return null;
            }
        });
    }

    public static CompletableFuture<Void> saveIngredientToDatabase(Ingredients ingredient) {
        String sql = "INSERT INTO ingredients(ingredient_id, item_name, notes) VALUES(?, ?, ?)";
        return executeUpdate(sql, pstmt -> {
            pstmt.setString(1, (String) ingredient.getMetadata().metadata().get("ingredient_id"));
            pstmt.setString(2, (String) ingredient.getMetadata().metadata().get("itemName"));
            pstmt.setString(3, (String) ingredient.getData().get("notes"));
        });
    }

    public static CompletableFuture<Void> updateIngredientInDatabase(Ingredients ingredient) {
        String sql = "UPDATE ingredients SET item_name = ?, notes = ? WHERE ingredient_id = ?";
        return executeUpdate(sql, pstmt -> {
            pstmt.setString(1, (String) ingredient.getMetadata().metadata().get("itemName"));
            pstmt.setString(2, (String) ingredient.getData().get("notes"));
            pstmt.setString(3, (String) ingredient.getMetadata().metadata().get("ingredient_id"));
        });
    }

    public static CompletableFuture<Void> deleteIngredientFromDatabase(Ingredients ingredient) {
        String sql = "DELETE FROM ingredients WHERE ingredient_id = ?";
        return executeUpdate(sql, pstmt -> pstmt.setString(1, (String) ingredient.getMetadata().metadata().get("ingredient_id")));
    }

    /* ===================== Stock Database Operations ===================== */

    public static CompletableFuture<List<Stock>> loadStockFromDatabase() {
        String sql = "SELECT * FROM stock";
        return loadEntities(sql, rs -> {
            try {
                String stockId = rs.getString("stock_id");
                String ingredientId = rs.getString("ingredient_id");
                Stock.StockStatus stockStatus = Stock.StockStatus.valueOf(rs.getString("stock_status"));
                Stock.UnitType unitType = Stock.UnitType.valueOf(rs.getString("unit_type"));
                Object numeral = unitType == Stock.UnitType.QTY ? rs.getInt("numeral") : rs.getDouble("numeral");
                boolean onOrder = rs.getInt("on_order") == 1;
                String createdAt = rs.getString("created_at");
                String lastUpdated = rs.getString("last_updated");
                double pricePerUnit = rs.getDouble("price_per_unit");
                double lowStockThreshold = rs.getDouble("low_stock_threshold");
                double desiredQuantity = rs.getDouble("desired_quantity");

                Ingredients ingredient = DataStore.getInstance().getIngredientById(ingredientId);

                if (ingredient == null) {
                    System.err.println("Ingredient not found for ID: " + ingredientId);
                    return null;
                }

                Stock stock = new Stock(ingredient, stockStatus, unitType, numeral, onOrder);
                stock.updateMetadata("stock_id", stockId);
                stock.updateMetadata("created_at", createdAt);
                stock.updateMetadata("lastUpdated", lastUpdated);
                stock.setDataValue("pricePerUnit", pricePerUnit);
                stock.setDataValue("lowStockThreshold", lowStockThreshold);
                stock.setDataValue("desiredQuantity", desiredQuantity);

                return stock;
            } catch (SQLException e) {
                System.err.println("Error mapping Stock: " + e.getMessage());
                return null;
            }
        });
    }

    public static CompletableFuture<Void> saveStockToDatabase(Stock stock) {
        String sql = "INSERT INTO stock(stock_id, ingredient_id, stock_status, on_order, created_at, last_updated, unit_type, numeral, low_stock_threshold, desired_quantity, price_per_unit) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return executeUpdate(sql, pstmt -> {
            pstmt.setString(1, (String) stock.getMetadata().metadata().get("stock_id"));
            pstmt.setString(2, (String) stock.getMetadata().metadata().get("ingredient_id"));
            pstmt.setString(3, stock.getMetadata().metadata().get("stockStatus").toString());
            pstmt.setInt(4, (Boolean) stock.getMetadata().metadata().get("onOrder") ? 1 : 0);
            pstmt.setString(5, stock.getMetadata().metadata().get("created_at").toString());
            pstmt.setString(6, stock.getMetadata().metadata().get("lastUpdated").toString());
            pstmt.setString(7, stock.getData().get("unit").toString());
            Object numeral = stock.getData().get("numeral");
            if (numeral instanceof Integer) {
                pstmt.setInt(8, (Integer) numeral);
            } else if (numeral instanceof Double) {
                pstmt.setDouble(8, (Double) numeral);
            } else {
                pstmt.setNull(8, Types.NULL);
            }
            Object lowStockThreshold = stock.getData().get("low_stock_threshold");
            if (lowStockThreshold instanceof Double) {
                pstmt.setDouble(9, (Double) lowStockThreshold);
            } else {
                pstmt.setNull(9, Types.NULL);
            }
            Object desiredQuantity = stock.getData().get("desired_quantity");
            if (desiredQuantity instanceof Double) {
                pstmt.setDouble(10, (Double) desiredQuantity);
            } else {
                pstmt.setNull(10, Types.NULL);
            }
            pstmt.setDouble(11, (Double) stock.getData().get("price_per_unit"));
        });
    }

    public static CompletableFuture<Void> updateStockInDatabase(Stock stock) {
        String sql = "UPDATE stock SET stock_status = ?, on_order = ?, last_updated = ?, unit_type = ?, numeral = ?, low_stock_threshold = ?, desired_quantity = ?, price_per_unit = ? WHERE stock_id = ?";
        return executeUpdate(sql, pstmt -> {
            pstmt.setString(1, stock.getMetadata().metadata().get("stockStatus").toString());
            pstmt.setInt(2, (Boolean) stock.getMetadata().metadata().get("onOrder") ? 1 : 0);
            pstmt.setString(3, stock.getMetadata().metadata().get("lastUpdated").toString());
            pstmt.setString(4, stock.getData().get("unit").toString());
            Object numeral = stock.getData().get("numeral");
            if (numeral instanceof Integer) {
                pstmt.setInt(5, (Integer) numeral);
            } else if (numeral instanceof Double) {
                pstmt.setDouble(5, (Double) numeral);
            } else {
                pstmt.setNull(5, Types.NULL);
            }
            Object lowStockThreshold = stock.getData().get("low_stock_threshold");
            if (lowStockThreshold instanceof Double) {
                pstmt.setDouble(6, (Double) lowStockThreshold);
            } else {
                pstmt.setNull(6, Types.NULL);
            }
            Object desiredQuantity = stock.getData().get("desired_quantity");
            if (desiredQuantity instanceof Double) {
                pstmt.setDouble(7, (Double) desiredQuantity);
            } else {
                pstmt.setNull(7, Types.NULL);
            }
            pstmt.setDouble(8, (Double) stock.getData().get("price_per_unit"));
            pstmt.setString(9, (String) stock.getMetadata().metadata().get("stock_id"));
        });
    }

    public static CompletableFuture<Void> deleteStockFromDatabase(Stock stock) {
        String sql = "DELETE FROM stock WHERE stock_id = ?";
        return executeUpdate(sql, pstmt -> pstmt.setString(1, (String) stock.getMetadata().metadata().get("stock_id")));
    }

    /* ===================== UserSettings Database Operations ===================== */

    public static CompletableFuture<List<UserSettings>> loadUserSettingsFromDatabase() {
        String sql = "SELECT * FROM user_settings";
        return loadEntities(sql, rs -> {
            try {
                String userId = rs.getString("user_id");
                UserSettings.Theme theme = UserSettings.Theme.valueOf(rs.getString("theme"));
                UserSettings.Currency currency = UserSettings.Currency.valueOf(rs.getString("currency"));
                UserSettings.Resolution resolution = UserSettings.Resolution.valueOf(rs.getString("resolution"));
                UserSettings.Timezone timezone = UserSettings.Timezone.valueOf(rs.getString("timezone"));
                UserSettings.AccessLevel access_level = UserSettings.AccessLevel.valueOf(rs.getString("access_level"));
                UserSettings.Language language = UserSettings.Language.valueOf(rs.getString("language"));

                UserSettings userSettings = new UserSettings(userId);
                userSettings.updateMetadata("theme", theme);
                userSettings.updateMetadata("currency", currency);
                userSettings.updateMetadata("resolution", resolution);
                userSettings.updateMetadata("timezone", timezone);
                userSettings.setDataValue("access_level", access_level);
                userSettings.setDataValue("language", language);

                return userSettings;
            } catch (SQLException e) {
                System.err.println("Error mapping UserSettings: " + e.getMessage());
                return null;
            }
        });
    }

    public static CompletableFuture<Void> saveUserSettingsToDatabase(UserSettings settings) {
        String sql = "INSERT INTO user_settings(user_id, theme, language, currency, resolution, timezone, access_level) VALUES(?, ?, ?, ?, ?, ?, ?)";
        return executeUpdate(sql, pstmt -> {
            pstmt.setString(1, (String) settings.getMetadata().metadata().get("user_id"));
            pstmt.setString(2, settings.getMetadata().metadata().get("theme").toString());
            pstmt.setString(3, settings.getData().get("language").toString());
            pstmt.setString(4, settings.getMetadata().metadata().get("currency").toString());
            pstmt.setString(5, settings.getMetadata().metadata().get("resolution").toString());
            pstmt.setString(6, settings.getMetadata().metadata().get("timezone").toString());
            pstmt.setString(7, settings.getData().get("access_level").toString());
        });
    }

    public static CompletableFuture<Void> updateUserSettingsInDatabase(UserSettings settings) {
        String sql = "UPDATE user_settings SET theme = ?, language = ?, currency = ?, resolution = ?, timezone = ?, access_level = ? WHERE user_id = ?";
        return executeUpdate(sql, pstmt -> {
            pstmt.setString(1, settings.getMetadata().metadata().get("theme").toString());
            pstmt.setString(2, settings.getData().get("language").toString());
            pstmt.setString(3, settings.getMetadata().metadata().get("currency").toString());
            pstmt.setString(4, settings.getMetadata().metadata().get("resolution").toString());
            pstmt.setString(5, settings.getMetadata().metadata().get("timezone").toString());
            pstmt.setString(6, settings.getData().get("access_level").toString());
            pstmt.setString(7, (String) settings.getMetadata().metadata().get("user_id"));
        });
    }

    public static CompletableFuture<Void> deleteUserSettingsFromDatabase(UserSettings settings) {
        String sql = "DELETE FROM user_settings WHERE user_id = ?";
        return executeUpdate(sql, pstmt -> pstmt.setString(1, (String) settings.getMetadata().metadata().get("user_id")));
    }

    /* ===================== Notes Database Operations ===================== */

    public static CompletableFuture<List<Notes>> loadNotesFromDatabase() {
        String sql = "SELECT * FROM global_notes";
        return loadEntities(sql, rs -> {
            try {
                String noteId = rs.getString("note_id");
                String userId = rs.getString("user_id");
                long timestamp = rs.getLong("timestamp");
                String noteTitle = rs.getString("note_title");
                String noteContent = rs.getString("note_content");

                Notes note = new Notes(noteContent, noteTitle);
                note.updateMetadata("note_id", noteId);
                note.updateMetadata("user_id", userId);
                note.updateMetadata("timestamp", timestamp);
                note.setDataValue("note_title", noteTitle);
                note.setDataValue("note_content", noteContent);

                return note;
            } catch (SQLException e) {
                System.err.println("Error mapping Notes: " + e.getMessage());
                return null;
            }
        });
    }

    public static CompletableFuture<Void> saveNotesToDatabase(Notes notes) {
        String sql = "INSERT INTO global_notes(note_id, user_id, timestamp, note_title, note_content) VALUES(?, ?, ?, ?, ?)";
        return executeUpdate(sql, pstmt -> {
            pstmt.setString(1, (String) notes.getMetadata().metadata().get("note_id"));
            pstmt.setString(2, (String) notes.getMetadata().metadata().get("user_id"));
            pstmt.setLong(3, (Long) notes.getMetadata().metadata().get("timestamp"));
            pstmt.setString(4, (String) notes.getData().get("note_title"));
            pstmt.setString(5, (String) notes.getData().get("note_content"));
        });
    }

    public static CompletableFuture<Void> updateNotesInDatabase(Notes notes) {
        String sql = "UPDATE global_notes SET note_title = ?, note_content = ? WHERE note_id = ?";
        return executeUpdate(sql, pstmt -> {
            pstmt.setString(1, (String) notes.getData().get("note_title"));
            pstmt.setString(2, (String) notes.getData().get("note_content"));
            pstmt.setString(3, (String) notes.getMetadata().metadata().get("note_id"));
        });
    }

    public static CompletableFuture<Void> deleteNotesFromDatabase(Notes notes) {
        String sql = "DELETE FROM global_notes WHERE note_id = ?";
        return executeUpdate(sql, pstmt -> pstmt.setString(1, (String) notes.getMetadata().metadata().get("note_id")));
    }

    /* ===================== UserLogs Database Operations ===================== */

    public static CompletableFuture<List<UserLogs>> loadUserLogsFromDatabase() {
        String sql = "SELECT * FROM user_logs";
        return loadEntities(sql, rs -> {
            try {
                String id = rs.getString("id");
                String userId = rs.getString("user_id");
                String logTypeStr = rs.getString("log_type");
                long timestamp = rs.getLong("created_at");

                UserLogs.LogType logType = UserLogs.LogType.valueOf(logTypeStr);
                UserLogs userLog = new UserLogs(logType);
                userLog.updateMetadata("id", id);
                userLog.updateMetadata("user_id", userId);
                userLog.updateMetadata("created_at", timestamp);

                return userLog;
            } catch (SQLException e) {
                System.err.println("Error mapping UserLogs: " + e.getMessage());
                return null;
            }
        });
    }

    public static CompletableFuture<Void> saveUserLogsToDatabase(UserLogs userLog) {
        String sql = "INSERT INTO user_logs(id, user_id, log_type, created_at) VALUES(?, ?, ?, ?)";
        return executeUpdate(sql, pstmt -> {
            pstmt.setString(1, userLog.getMetadata().metadata().get("id").toString());
            pstmt.setString(2, (String) userLog.getMetadata().metadata().get("user_id"));
            pstmt.setString(3, userLog.getMetadata().metadata().get("log_type").toString());
            pstmt.setLong(4, (Long) userLog.getMetadata().metadata().get("created_at"));
        });
    }

    public static CompletableFuture<Void> deleteUserLogsFromDatabase(UserLogs userLog) {
        String sql = "DELETE FROM user_logs WHERE id = ?";
        return executeUpdate(sql, pstmt -> pstmt.setString(1, userLog.getMetadata().metadata().get("id").toString()));
    }

    /* ===================== Clear Database ===================== */

    @SuppressWarnings("all")
    public static CompletableFuture<Void> clearDatabase() {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = DatabaseHelper.connect();
                 Statement stmt = conn.createStatement()) {

                conn.setAutoCommit(false);

                stmt.executeUpdate("DELETE FROM user_logs");
                stmt.executeUpdate("DELETE FROM global_notes");
                stmt.executeUpdate("DELETE FROM user_settings");
                stmt.executeUpdate("DELETE FROM order_items");
                stmt.executeUpdate("DELETE FROM orders");
                stmt.executeUpdate("DELETE FROM menu_item_ingredients");
                stmt.executeUpdate("DELETE FROM menu_items");
                stmt.executeUpdate("DELETE FROM stock");
                stmt.executeUpdate("DELETE FROM ingredients");
                stmt.executeUpdate("DELETE FROM users");

                conn.commit();
                System.out.println("All tables cleared.");

            } catch (SQLException e) {
                System.err.println("Error clearing database: " + e.getMessage());
            }
        });
    }

    /* ===================== Interface for PreparedStatement Setting ===================== */

    @FunctionalInterface
    interface PreparedStatementSetter {
        void setValues(PreparedStatement pstmt) throws SQLException;
    }
}

