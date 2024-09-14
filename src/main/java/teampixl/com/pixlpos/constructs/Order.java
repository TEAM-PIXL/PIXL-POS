package teampixl.com.pixlpos.constructs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.MetadataWrapper;
import teampixl.com.pixlpos.constructs.interfaces.IDataManager;

/**
 * The Order class is a data structure that holds the information of an order.
 * <p>
 * Metadata:
 * - order_id: UUID
 * - order_number: orderNumber
 * - user_id: userId
 * - order_status: OrderStatus.PENDING
 * - is_completed: false
 * - created_at: timestamp for creation
 * - updated_at: timestamp for last update
 * <p>
 * Data:
 * - menuItems: Map<String, Integer> where key is MenuItem ID and value is the quantity
 * - total: 0.0
 * - special_requests: null
 * - payment_details: null
 * @see IDataManager
 * @see MetadataWrapper
 * @see MenuItem
 * @see Stock
 */
public class Order implements IDataManager {

    /*============================================================================================================================================================
    Code Description:
    - Enumerations for OrderStatus
    - MetadataWrapper object for metadata
    - Map object for data
    ============================================================================================================================================================*/

    /**
     * Enumerations for the status of an order.
     */
    public enum OrderStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELED
    }

    private MetadataWrapper metadata;
    private final Map<String, Object> data;

    /*============================================================================================================================================================
    Code Description:
    - Constructor for Order object.

    Metadata:
        - order_id: UUID
        - order_number: orderNumber
        - user_id: userId
        - order_status: OrderStatus.PENDING
        - is_completed: false
        - created_at: timestamp for creation
        - updated_at: timestamp for last update

    Data:
        - menuItems: Map<String, Integer> where key is MenuItem ID and value is the quantity
        - total: 0.0
        - special_requests: null
        - payment_details: null
    ============================================================================================================================================================*/

    /**
     * Constructor for an order.
     * @param orderNumber The order number.
     * @param userId The user ID.
     */
    public Order(int orderNumber, String userId) {
        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("order_id", UUID.randomUUID().toString());
        metadataMap.put("order_number", orderNumber);
        metadataMap.put("user_id", userId);
        metadataMap.put("order_status", OrderStatus.PENDING);
        metadataMap.put("is_completed", false);
        metadataMap.put("created_at", System.currentTimeMillis());
        metadataMap.put("updated_at", System.currentTimeMillis());

        this.metadata = new MetadataWrapper(metadataMap);

        this.data = new HashMap<>();
        this.data.put("menuItems", new HashMap<String, Integer>());
        this.data.put("total", 0.0);
        this.data.put("special_requests", null);
        this.data.put("payment_details", null);
    }

    /*============================================================================================================================================================
    Code Description:
    - Handles internal logic for CRUD operations on Order object.

    Methods:
        - addMenuItem(MenuItem item, int quantity): Adds a MenuItem to the order and deducts the corresponding ingredients from stock.
        - removeMenuItem(MenuItem item, int quantity): Removes a MenuItem from the order and restores the corresponding ingredients to stock.
        - updateTotal(MenuItem item, int quantity): Updates the total cost of the order.
        - updateOrderStatus(OrderStatus newStatus): Updates the status of the order.
        - completeOrder(): Marks the order as completed and updates the status.
        - updateTimestamp(): Updates the timestamp in the metadata.
        - deductIngredientsFromStock(MenuItem menuItem, int quantity): Deducts the ingredients from stock.
        - restoreIngredientsToStock(MenuItem menuItem, int quantity): Restores the ingredients to stock.
    ============================================================================================================================================================*/

    /**
     * Adds a menu item to the order.
     * @param item The menu item to add.
     * @param quantity The quantity of the menu item to add.
     */
    public void addMenuItem(MenuItem item, int quantity) {
        Object menuItemsObj = data.get("menuItems");

        if (menuItemsObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Integer> menuItems = (Map<String, Integer>) menuItemsObj;
            String itemId = (String) item.getMetadata().metadata().get("id");

            if (menuItems.containsKey(itemId)) {
                menuItems.put(itemId, menuItems.get(itemId) + quantity);
            } else {
                menuItems.put(itemId, quantity);
            }

            updateTotal(item, quantity);
            deductIngredientsFromStock(item, quantity);
            updateTimestamp();
        } else {
            throw new IllegalStateException("Expected menuItems to be a Map<String, Integer>");
        }
    }

    /**
     * Removes a menu item from the order.
     * @param item The menu item to remove.
     * @param quantity The quantity of the menu item to remove.
     */
    public void removeMenuItem(MenuItem item, int quantity) {
        Object menuItemsObj = data.get("menuItems");

        if (menuItemsObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Integer> menuItems = (Map<String, Integer>) menuItemsObj;
            String itemId = (String) item.getMetadata().metadata().get("id");

            if (menuItems.containsKey(itemId)) {
                int currentQuantity = menuItems.get(itemId);
                if (currentQuantity - quantity <= 0) {
                    menuItems.remove(itemId);
                } else {
                    menuItems.put(itemId, currentQuantity - quantity);
                }

                updateTotal(item, -quantity);
                restoreIngredientsToStock(item, quantity);
                updateTimestamp();
            }
        } else {
            throw new IllegalStateException("Expected menuItems to be a Map<String, Integer>");
        }
    }

    /**
     * Updates the quantity of a menu item in the order.
     * @param menuItem The menu item to update.
     * @param newQuantity The new quantity of the menu item.
     */
    @SuppressWarnings("unchecked")
    public void updateMenuItem(MenuItem menuItem, int newQuantity) {
        Map<String, Integer> menuItems = (Map<String, Integer>) data.get("menuItems");
        String itemId = (String) menuItem.getMetadata().metadata().get("id");

        if (menuItems.containsKey(itemId)) {
            menuItems.put(itemId, newQuantity);
            updateTotal(menuItem, newQuantity - menuItems.get(itemId));
            updateTimestamp();
        } else {
            throw new IllegalArgumentException("Menu item not found in the order.");
        }
    }

    private void updateTotal(MenuItem item, int quantity) {
        double currentTotal = (double) data.get("total");
        double itemPrice = (double) item.getMetadata().metadata().get("price");
        data.put("total", currentTotal + (itemPrice * quantity));
    }

    /**
     * Updates the status of the order.
     * @param newStatus The new status of the order.
     */
    public void updateOrderStatus(OrderStatus newStatus) {
        updateMetadata("order_status", newStatus);
        if (newStatus == OrderStatus.COMPLETED) {
            updateMetadata("is_completed", true);
        }
    }

    /**
     * Marks the order as completed.
     */
    public void completeOrder() {
        updateOrderStatus(OrderStatus.COMPLETED);
    }

    private void updateTimestamp() {
        updateMetadata("updated_at", System.currentTimeMillis());
    }

    private void deductIngredientsFromStock(MenuItem menuItem, int quantity) {
        DataStore dataStore = DataStore.getInstance();

        for (Map.Entry<String, MenuItem.IngredientAmount> entry : menuItem.getIngredients().entrySet()) {
            String ingredientId = entry.getKey();
            MenuItem.IngredientAmount ingredientAmount = entry.getValue();

            Stock stockItem = dataStore.getStockItemByIngredientId(ingredientId);

            if (stockItem != null) {
                Object requiredAmount = ingredientAmount.numeral();
                Object currentStockAmount = stockItem.getData().get("numeral");

                if (requiredAmount instanceof Integer && currentStockAmount instanceof Integer) {
                    int totalDeduction = (Integer) requiredAmount * quantity;
                    int updatedStockAmount = (Integer) currentStockAmount - totalDeduction;
                    stockItem.setDataValue("numeral", updatedStockAmount);
                } else if (requiredAmount instanceof Double && currentStockAmount instanceof Double) {
                    double totalDeduction = (Double) requiredAmount * quantity;
                    double updatedStockAmount = (Double) currentStockAmount - totalDeduction;
                    stockItem.setDataValue("numeral", updatedStockAmount);
                }

                dataStore.updateStock(stockItem);
            } else {
                throw new IllegalStateException("Stock item not found for ingredient ID: " + ingredientId);
            }
        }
    }

    private void restoreIngredientsToStock(MenuItem menuItem, int quantity) {
        DataStore dataStore = DataStore.getInstance();

        for (Map.Entry<String, MenuItem.IngredientAmount> entry : menuItem.getIngredients().entrySet()) {
            String ingredientId = entry.getKey();
            MenuItem.IngredientAmount ingredientAmount = entry.getValue();

            Stock stockItem = dataStore.getStockItemByIngredientId(ingredientId);

            if (stockItem != null) {
                Object requiredAmount = ingredientAmount.numeral();
                Object currentStockAmount = stockItem.getData().get("numeral");

                // Restore based on the type of unit
                if (requiredAmount instanceof Integer && currentStockAmount instanceof Integer) {
                    int totalRestoration = (Integer) requiredAmount * quantity;
                    int updatedStockAmount = (Integer) currentStockAmount + totalRestoration;
                    stockItem.setDataValue("numeral", updatedStockAmount);
                } else if (requiredAmount instanceof Double && currentStockAmount instanceof Double) {
                    double totalRestoration = (Double) requiredAmount * quantity;
                    double updatedStockAmount = (Double) currentStockAmount + totalRestoration;
                    stockItem.setDataValue("numeral", updatedStockAmount);
                }

                dataStore.updateStock(stockItem);
            } else {
                throw new IllegalStateException("Stock item not found for ingredient ID: " + ingredientId);
            }
        }
    }

    /*============================================================================================================================================================
    Code Description:
    - Method to get metadata, update metadata, and provide a string representation of the Order object.

    Methods:
        - getMetadata(): returns the metadata
        - getData(): returns the data map
        - updateMetadata(String key, Object value): updates the metadata map
        - setDataValue(String key, Object value): sets data value in the data map
    ============================================================================================================================================================*/

    public void updateMetadata(String key, Object value) {
        Map<String, Object> modifiableMetadata = new HashMap<>(metadata.metadata());
        if (value != null) {
            modifiableMetadata.put(key, value);
        } else {
            modifiableMetadata.remove(key);
        }
        this.metadata = new MetadataWrapper(modifiableMetadata);
    }

    public void setDataValue(String key, Object value) {
        data.put(key, value);
    }

    public MetadataWrapper getMetadata() {
        return metadata;
    }

    public Map<String, Object> getData() {
        return data;
    }
}






