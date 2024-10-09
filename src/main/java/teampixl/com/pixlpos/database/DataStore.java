package teampixl.com.pixlpos.database;

import teampixl.com.pixlpos.controllers.adminconsole.Notes;
import teampixl.com.pixlpos.models.*;
import teampixl.com.pixlpos.database.interfaces.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import teampixl.com.pixlpos.models.logs.UserLogs;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

public class DataStore implements IUserStore, IMenuItemStore, IOrderStore, IIngredientsStore, IStockStore {

    private static volatile DataStore instance;

    private final ObservableList<MenuItem> menuItems;
    private final ObservableList<Order> orders;
    private final ObservableList<Users> users;
    private final ObservableList<Ingredients> ingredients;
    private final ObservableList<Stock> stockItems;
    private final ObservableList<UserSettings> userSettings;
    private final ObservableList<Notes> notes;
    private final ObservableList<UserLogs> userLogs;

    private DataStore() {
        menuItems = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        orders = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        users = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        ingredients = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        stockItems = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        userSettings = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        notes = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        userLogs = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

        loadDataFromDatabase();
    }

    public static DataStore getInstance() {
        if (instance == null) {
            synchronized (DataStore.class) {
                if (instance == null) {
                    instance = new DataStore();
                }
            }
        }
        return instance;
    }

    private void loadDataFromDatabase() {
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        try {
            // List to hold futures
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            // Load data asynchronously and add futures to the list
            futures.add(loadMenuItemsFromDatabase());
            futures.add(loadIngredientsFromDatabase());
            futures.add(loadStockFromDatabase());
            futures.add(loadUsersFromDatabase());
            futures.add(loadOrdersFromDatabase());
            futures.add(loadUserSettingsFromDatabase());
            futures.add(loadNotesFromDatabase());
//            futures.add(loadUserLogsFromDatabase());

            // Wait for all futures to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } finally {
            executorService.shutdown();
        }
    }

    /* ===================== MenuItem Methods ===================== */

    @Override
    public synchronized void createMenuItem(MenuItem item) {
        menuItems.add(item);
        DatabaseManager.saveMenuItemToDatabase(item).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public ObservableList<MenuItem> readMenuItems() {
        return menuItems;
    }

    @Override
    public synchronized void updateMenuItem(MenuItem item) {
        DatabaseManager.updateMenuItemInDatabase(item).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public synchronized void deleteMenuItem(MenuItem item) {
        menuItems.remove(item);
        DatabaseManager.deleteMenuItemFromDatabase(item).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public void createMenuItemIngredient(MenuItem menuItem, Ingredients ingredient, Object numeral) {
        menuItem.addIngredient(ingredient, numeral);
        DatabaseManager.saveMenuItemIngredientsToDatabase(menuItem).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public Map<String, Object> readMenuItemIngredients(MenuItem menuItem) {
        CompletableFuture<Map<String, Object>> future = DatabaseManager.getMenuItemIngredientsFromDatabase(menuItem);
        try {
            return future.get(); // Blocking call to get the result
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    @Override
    public void updateMenuItemIngredient(MenuItem menuItem, Ingredients ingredient, Object numeral) {
        menuItem.updateIngredient((String) ingredient.getMetadata().metadata().get("ingredient_id"), ingredient, numeral);
        DatabaseManager.updateMenuItemIngredientsInDatabase(menuItem).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public void deleteMenuItemIngredient(MenuItem menuItem, Ingredients ingredient) {
        menuItem.removeIngredient(ingredient);
        DatabaseManager.updateMenuItemIngredientsInDatabase(menuItem).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public MenuItem getMenuItem(String itemName) {
        synchronized (menuItems) {
            for (MenuItem item : menuItems) {
                if (item.getMetadata().metadata().get("itemName").equals(itemName)) {
                    return item;
                }
            }
        }
        return null;
    }

    public MenuItem getMenuItemById(String itemId) {
        synchronized (menuItems) {
            for (MenuItem item : menuItems) {
                if (item.getMetadata().metadata().get("id").equals(itemId)) {
                    return item;
                }
            }
        }
        return null;
    }

    /* ===================== Order Methods ===================== */

    @Override
    public synchronized void createOrder(Order order) {
        orders.add(order);
        DatabaseManager.saveOrderToDatabase(order).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public ObservableList<Order> readOrders() {
        return orders;
    }

    @Override
    public synchronized void updateOrder(Order order) {
        DatabaseManager.updateOrderInDatabase(order).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
        int index = orders.indexOf(order);
        if (index != -1) {
            orders.set(index, order);
        } else {
            orders.add(order);
        }
    }

    @Override
    public synchronized void deleteOrder(Order order) {
        orders.remove(order);
        DatabaseManager.deleteOrderFromDatabase(order).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public synchronized void createOrderItem(Order order, MenuItem item, int quantity) {
        order.addMenuItem(item, quantity);
        DatabaseManager.saveOrderItemsToDatabase(order).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
        deductIngredientsFromStock(item, quantity);
    }

    @Override
    public Map<String, Integer> readOrderItems(Order order) {
        CompletableFuture<Map<String, Integer>> future = DatabaseManager.getOrderItemsFromDatabase((String) order.getMetadata().metadata().get("order_id"));
        try {
            return future.get(); // Blocking call to get the result
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    @Override
    public synchronized void updateOrderItem(Order order, MenuItem menuItem, int quantity) {
        order.updateMenuItem(menuItem, quantity);
        DatabaseManager.updateOrderItemsInDatabase(order).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
        deductIngredientsFromStock(menuItem, quantity);
    }

    @Override
    public synchronized void deleteOrderItem(Order order, MenuItem item, int quantity) {
        order.removeMenuItem(item, quantity);
        DatabaseManager.updateOrderItemsInDatabase(order).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
        restoreIngredientsToStock(item, quantity);
    }

    public Order getOrder(int orderNumber) {
        synchronized (orders) {
            for (Order order : orders) {
                if (order.getMetadata().metadata().get("order_number").equals(orderNumber)) {
                    return order;
                }
            }
        }
        return null;
    }

    public Map<String, Integer> getOrderItem(Order order, String menuItemId) {
        Map<String, Integer> orderItems = readOrderItems(order);
        if (orderItems.containsKey(menuItemId)) {
            Map<String, Integer> orderItem = new HashMap<>();
            orderItem.put(menuItemId, orderItems.get(menuItemId));
            return orderItem;
        }
        return null;
    }

    public synchronized void reloadOrdersFromDatabase() {
        orders.clear();
        loadOrdersFromDatabase().join();
    }

    /* ===================== User Methods ===================== */

    @Override
    public synchronized void createUser(Users user) {
        users.add(user);
        DatabaseManager.saveUserToDatabase(user).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public ObservableList<Users> readUsers() {
        return users;
    }

    @Override
    public synchronized void updateUser(Users user) {
        DatabaseManager.updateUserInDatabase(user).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public synchronized void deleteUser(Users user) {
        users.remove(user);
        DatabaseManager.deleteUserFromDatabase(user).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    /* ===================== Ingredient Methods ===================== */

    @Override
    public synchronized void createIngredient(Ingredients ingredient) {
        ingredients.add(ingredient);
        DatabaseManager.saveIngredientToDatabase(ingredient).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public ObservableList<Ingredients> readIngredients() {
        return ingredients;
    }

    @Override
    public synchronized void updateIngredient(Ingredients ingredient) {
        DatabaseManager.updateIngredientInDatabase(ingredient).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public synchronized void deleteIngredient(Ingredients ingredient) {
        ingredients.remove(ingredient);
        DatabaseManager.deleteIngredientFromDatabase(ingredient).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public Ingredients getIngredient(String ingredientName) {
        synchronized (ingredients) {
            for (Ingredients ingredient : ingredients) {
                if (ingredient.getMetadata().metadata().get("itemName").equals(ingredientName)) {
                    return ingredient;
                }
            }
        }
        return null;
    }

    public Ingredients getIngredientById(String ingredientId) {
        synchronized (ingredients) {
            for (Ingredients ingredient : ingredients) {
                if (ingredient.getMetadata().metadata().get("ingredient_id").equals(ingredientId)) {
                    return ingredient;
                }
            }
        }
        return null;
    }

    /* ===================== Stock Methods ===================== */

    @Override
    public synchronized void createStock(Stock stock) {
        stockItems.add(stock);
        DatabaseManager.saveStockToDatabase(stock).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public ObservableList<Stock> readStock() {
        return stockItems;
    }

    @Override
    public synchronized void updateStock(Stock stock) {
        DatabaseManager.updateStockInDatabase(stock).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public synchronized void deleteStock(Stock stock) {
        stockItems.remove(stock);
        DatabaseManager.deleteStockFromDatabase(stock).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public Stock getStockItem(String itemName) {
        String ingredientId = getIngredientIdByIngredientName(itemName);
        return getStockItemByIngredientId(ingredientId);
    }

    /* ===================== User Settings Methods ===================== */

    public synchronized void createUserSettings(UserSettings settings) {
        userSettings.add(settings);
        DatabaseManager.saveUserSettingsToDatabase(settings).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public ObservableList<UserSettings> readUserSettings() {
        return userSettings;
    }

    public synchronized void updateUserSettings(UserSettings settings) {
        DatabaseManager.updateUserSettingsInDatabase(settings).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public synchronized void deleteUserSettings(UserSettings settings) {
        userSettings.remove(settings);
        DatabaseManager.deleteUserSettingsFromDatabase(settings).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    /* ===================== Notes Methods ===================== */

    public synchronized void createNotesApp(Notes note) {
        notes.add(note);
        DatabaseManager.saveNotesToDatabase(note).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public ObservableList<Notes> readNotesApp() {
        return notes;
    }

    public synchronized void updateNotesApp(Notes note) {
        DatabaseManager.updateNotesInDatabase(note).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public synchronized void deleteNotesApp(Notes note) {
        notes.remove(note);
        DatabaseManager.deleteNotesFromDatabase(note).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    /* ===================== User Logs Methods ===================== */

    public synchronized void createUserLogs(UserLogs userLog) {
        userLogs.add(userLog);
        DatabaseManager.saveUserLogsToDatabase(userLog).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public ObservableList<UserLogs> readUserLogs() {
        return userLogs;
    }

    public synchronized void deleteUserLogs(UserLogs userLog) {
        userLogs.remove(userLog);
        DatabaseManager.deleteUserLogsFromDatabase(userLog).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    /* ===================== Helper Methods ===================== */

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

    public Stock getStockItemByIngredientId(String ingredientId) {
        synchronized (stockItems) {
            for (Stock stockItem : stockItems) {
                if (stockItem.getMetadata().metadata().get("ingredient_id").equals(ingredientId)) {
                    return stockItem;
                }
            }
        }
        return null;
    }

    private String getIngredientIdByIngredientName(String ingredientName) {
        synchronized (ingredients) {
            for (Ingredients ingredient : ingredients) {
                if (ingredient.getMetadata().metadata().get("itemName").equals(ingredientName)) {
                    return (String) ingredient.getMetadata().metadata().get("ingredient_id");
                }
            }
        }
        return null;
    }

    /* ===================== Data Loading Methods ===================== */

    private CompletableFuture<Void> loadMenuItemsFromDatabase() {
        return DatabaseManager.loadMenuItemsFromDatabase().thenAccept(items -> {
            synchronized (menuItems) {
                menuItems.addAll(items);
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private CompletableFuture<Void> loadOrdersFromDatabase() {
        return DatabaseManager.loadOrdersFromDatabase().thenAccept(orderList -> {
            synchronized (orders) {
                orders.addAll(orderList);
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private CompletableFuture<Void> loadUsersFromDatabase() {
        return DatabaseManager.loadUsersFromDatabase().thenAccept(userList -> {
            synchronized (users) {
                users.addAll(userList);
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private CompletableFuture<Void> loadIngredientsFromDatabase() {
        return DatabaseManager.loadIngredientsFromDatabase().thenAccept(ingredientList -> {
            synchronized (ingredients) {
                ingredients.addAll(ingredientList);
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private CompletableFuture<Void> loadStockFromDatabase() {
        return DatabaseManager.loadStockFromDatabase().thenAccept(stockList -> {
            synchronized (stockItems) {
                stockItems.addAll(stockList);
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private CompletableFuture<Void> loadUserSettingsFromDatabase() {
        return DatabaseManager.loadUserSettingsFromDatabase().thenAccept(settingsList -> {
            synchronized (userSettings) {
                userSettings.addAll(settingsList);
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private CompletableFuture<Void> loadNotesFromDatabase() {
        return DatabaseManager.loadNotesFromDatabase().thenAccept(notesList -> {
            synchronized (notes) {
                notes.addAll(notesList);
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private CompletableFuture<Void> loadUserLogsFromDatabase() {
        return DatabaseManager.loadUserLogsFromDatabase().thenAccept(logsList -> {
            synchronized (userLogs) {
                userLogs.addAll(logsList);
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    /* ===================== Clear Data Method ===================== */

    public synchronized void clearData() {
        menuItems.clear();
        orders.clear();
        users.clear();
        ingredients.clear();
        stockItems.clear();
        userSettings.clear();
        notes.clear();
        userLogs.clear();

        DatabaseManager.clearDatabase().exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }
}

