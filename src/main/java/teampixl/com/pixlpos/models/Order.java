package teampixl.com.pixlpos.models;

import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.models.tools.MetadataWrapper;
import teampixl.com.pixlpos.database.api.MenuAPI;
import teampixl.com.pixlpos.models.tools.DataManager;

import java.util.*;

/**
 * The Order class represents an order in the system.
 * It contains metadata, data, and methods to manage the order.
 */
public class Order extends DataManager {

    /*============================================================================================================================================================
    Code Description:
    - Enumerations for OrderStatus
    - Enumerations for OrderType
    ============================================================================================================================================================*/

    /**
     * Enumerations for the status of an order.
     */
    public enum OrderStatus {
        PENDING,
        SENT,
        RECEIVED,
        IN_PROGRESS,
        COMPLETED,
        CANCELED,
        PAID
    }

    /**
     * Enumerations for the type of order.
     */
    public enum OrderType {
        DINE_IN,
        TAKE_OUT,
        DELIVERY
    }

    /**
     * Enumerations for the payment method of an order.
     */
    public enum PaymentMethod {
        CASH,
        CARD,
        NOT_PAID, MOBILE
    }

    /*============================================================================================================================================================
    Code Description:
    - Constructor for Order object.

    Metadata:
        - order_id: UUID
        - order_number: orderNumber
        - user_id: userId
        - order_status: OrderStatus.PENDING
        - is_completed: false
        - order_type: OrderType.DINE_IN
        - table_number: 0
        - customers: 1
        - created_at: timestamp for creation
        - updated_at: timestamp for last update

    Data:
        - menuItems: Map<String, Integer> where key is MenuItem ID and value is the quantity
        - total: 0.0
        - special_requests: null
        - payment_method: null
    ============================================================================================================================================================*/

    /**
     * Constructor for an order.
     *
     * @param orderNumber The order number.
     * @param userId The user ID.
     */
    public Order(int orderNumber, String userId) {
        super(initializeMetadata(orderNumber, userId));

        this.data = new HashMap<>();
        this.data.put("menuItems", new HashMap<String, Integer>());
        this.data.put("total", 0.0);
        this.data.put("special_requests", null);
        this.data.put("payment_method", PaymentMethod.CARD.name());
    }

    private static MetadataWrapper initializeMetadata(int orderNumber, String userId) {
        if (orderNumber <= 0) {
            throw new IllegalArgumentException("Order number must be positive.");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID must not be null or empty.");
        }

        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("order_id", UUID.randomUUID().toString());
        metadataMap.put("order_number", orderNumber);
        metadataMap.put("user_id", userId);
        metadataMap.put("order_status", OrderStatus.PENDING.name());
        metadataMap.put("is_completed", false);
        metadataMap.put("order_type", OrderType.DINE_IN.name());
        metadataMap.put("table_number", 0);
        metadataMap.put("customers", 1);
        metadataMap.put("created_at", System.currentTimeMillis());
        metadataMap.put("updated_at", System.currentTimeMillis());
        return new MetadataWrapper(metadataMap);
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
        - getOrderNumber(): Returns the order number.
        - getTotal(): Returns the total cost of the order.
    ============================================================================================================================================================*/

    /**
     * Adds a menu item to the order.
     *
     * @param item The menu item to add.
     * @param quantity The quantity of the menu item to add.
     */
    public void addMenuItem(MenuItem item, int quantity) {
        if (item == null) {
            throw new IllegalArgumentException("MenuItem cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Integer> menuItems = (Map<String, Integer>) data.get("menuItems");
        String itemId = item.getMetadata().metadata().get("id").toString();

        int currentQuantity = menuItems.getOrDefault(itemId, 0);
        int newQuantity = currentQuantity + quantity;

        menuItems.put(itemId, newQuantity);

        updateTotal(item.getPrice() * quantity);
        deductIngredientsFromStock(item, quantity);
        updateTimestamp();

        MenuItem instanceItem = MenuAPI.getInstance().keyTransform(itemId);
        int currentOrderTotal = (int) instanceItem.getData().get("amountOrdered");
        int newOrderTotal = currentOrderTotal + quantity;
        instanceItem.setDataValue("amountOrdered", newOrderTotal);
        DataStore.getInstance().updateMenuItem(instanceItem);

        DataStore.getInstance().updateOrderItem(this, itemId, newQuantity);
    }

    /**
     * Removes a menu item from the order.
     *
     * @param item The menu item to remove.
     * @param quantity The quantity of the menu item to remove.
     */
    public void removeMenuItem(MenuItem item, int quantity) {
        if (item == null) {
            throw new IllegalArgumentException("MenuItem cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Integer> menuItems = (Map<String, Integer>) data.get("menuItems");
        String itemId = item.getMetadata().metadata().get("id").toString();

        if (menuItems.containsKey(itemId)) {
            int currentQuantity = menuItems.get(itemId);
            if (quantity > currentQuantity) {
                throw new IllegalArgumentException("Cannot remove more items than are in the order.");
            }
            int newQuantity = currentQuantity - quantity;
            if (newQuantity == 0) {
                menuItems.remove(itemId);
            } else {
                menuItems.put(itemId, newQuantity);
            }

            updateTotal(-item.getPrice() * quantity);
            restoreIngredientsToStock(item, quantity);
            updateTimestamp();

            MenuItem instanceItem = MenuAPI.getInstance().keyTransform(itemId);
            int currentOrderTotal = (int) instanceItem.getData().get("amountOrdered");
            int newOrderTotal = currentOrderTotal - quantity;
            instanceItem.setDataValue("amountOrdered", newOrderTotal);
            DataStore.getInstance().updateMenuItem(instanceItem);
        } else {
            throw new IllegalArgumentException("Menu item not found in the order.");
        }
    }

    /**
     * Updates the quantity of a menu item in the order.
     *
     * @param menuItem The menu item to update.
     * @param newQuantity The new quantity of the menu item.
     */
    public void updateMenuItem(MenuItem menuItem, int newQuantity) {
        if (menuItem == null) {
            throw new IllegalArgumentException("MenuItem cannot be null.");
        }
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Integer> menuItems = (Map<String, Integer>) data.get("menuItems");
        String itemId = menuItem.getMetadata().metadata().get("id").toString();

        if (menuItems.containsKey(itemId)) {
            int currentQuantity = menuItems.get(itemId);
            int quantityDifference = newQuantity - currentQuantity;

            if (quantityDifference > 0) {
                deductIngredientsFromStock(menuItem, quantityDifference);
                updateTotal(menuItem.getPrice() * quantityDifference);
            } else if (quantityDifference < 0) {
                restoreIngredientsToStock(menuItem, -quantityDifference);
                updateTotal(menuItem.getPrice() * quantityDifference);
            }

            if (newQuantity == 0) {
                menuItems.remove(itemId);
            } else {
                menuItems.put(itemId, newQuantity);
            }

            updateTimestamp();
        } else {
            throw new IllegalArgumentException("Menu item not found in the order.");
        }
    }

    private void updateTotal(double amount) {
        double currentTotal = (double) data.get("total");
        double newTotal = currentTotal + amount;
        if (newTotal < 0) {
            throw new IllegalStateException("Total amount cannot be negative.");
        }
        data.put("total", newTotal);
        DataStore.getInstance().updateOrder(this);
    }

    /**
     * Updates the status of the order.
     *
     * @param newStatus The new status of the order.
     */
    public void updateOrderStatus(OrderStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("OrderStatus cannot be null.");
        }
        updateMetadata("order_status", newStatus.name());
        if (newStatus == OrderStatus.COMPLETED) {
            updateMetadata("is_completed", true);
        }
        updateTimestamp();
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
                    if (updatedStockAmount < 0) {
                        throw new IllegalStateException("Insufficient stock for ingredient ID: " + ingredientId);
                    }
                    stockItem.setDataValue("numeral", updatedStockAmount);
                } else if (requiredAmount instanceof Double && currentStockAmount instanceof Double) {
                    double totalDeduction = (Double) requiredAmount * quantity;
                    double updatedStockAmount = (Double) currentStockAmount - totalDeduction;
                    if (updatedStockAmount < 0) {
                        throw new IllegalStateException("Insufficient stock for ingredient ID: " + ingredientId);
                    }
                    stockItem.setDataValue("numeral", updatedStockAmount);
                } else {
                    throw new IllegalStateException("Incompatible types for stock amount.");
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

                if (requiredAmount instanceof Integer && currentStockAmount instanceof Integer) {
                    int totalRestoration = (Integer) requiredAmount * quantity;
                    int updatedStockAmount = (Integer) currentStockAmount + totalRestoration;
                    stockItem.setDataValue("numeral", updatedStockAmount);
                } else if (requiredAmount instanceof Double && currentStockAmount instanceof Double) {
                    double totalRestoration = (Double) requiredAmount * quantity;
                    double updatedStockAmount = (Double) currentStockAmount + totalRestoration;
                    stockItem.setDataValue("numeral", updatedStockAmount);
                } else {
                    throw new IllegalStateException("Incompatible types for stock amount.");
                }

                dataStore.updateStock(stockItem);
            } else {
                throw new IllegalStateException("Stock item not found for ingredient ID: " + ingredientId);
            }
        }
    }

    public int getOrderNumber() {
        return (int) metadata.metadata().get("order_number");
    }

    public double getTotal() {
        return (double) data.get("total");
    }

    /*============================================================================================================================================================
    Code Description:
    - Overridden methods for Order object.

    Methods:
        - equals(Object obj): checks if two Order objects are equal
        - hashCode(): returns the hash code of the Order object
    ============================================================================================================================================================*/

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Order other)) return false;
        return Objects.equals(metadata.metadata().get("order_id"), other.getMetadata().metadata().get("order_id"));
    }

    @Override
    public int hashCode() {
        return Objects.hash(metadata.metadata().get("order_id"));
    }
}







