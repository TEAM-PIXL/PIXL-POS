package teampixl.com.pixlpos.constructs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import teampixl.com.pixlpos.database.MetadataWrapper;

public class Order {

    public enum OrderStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELED
    }

    private MetadataWrapper metadata;
    private final Map<String, Object> data;

    // Constructor
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

    // Method to add MenuItem to the Order
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
            updateTimestamp();
        } else {
            throw new IllegalStateException("Expected menuItems to be a Map<String, Integer>");
        }
    }

    // Method to update the total cost of the Order
    private void updateTotal(MenuItem item, int quantity) {
        double currentTotal = (double) data.get("total");
        double itemPrice = (double) item.getMetadata().metadata().get("price");
        data.put("total", currentTotal + (itemPrice * quantity));
    }

    // Method to update the order status
    public void updateOrderStatus(OrderStatus newStatus) {
        updateMetadata("order_status", newStatus);
        if (newStatus == OrderStatus.COMPLETED) {
            updateMetadata("is_completed", true);
        }
    }

    // Method to complete the order
    public void completeOrder() {
        updateOrderStatus(OrderStatus.COMPLETED);
    }

    // Method to update the timestamp
    private void updateTimestamp() {
        updateMetadata("updated_at", System.currentTimeMillis());
    }

    // Method to update metadata
    public void updateMetadata(String key, Object value) {
        Map<String, Object> modifiableMetadata = new HashMap<>(metadata.metadata());
        if (value != null) {
            modifiableMetadata.put(key, value);
        } else {
            modifiableMetadata.remove(key);
        }
        this.metadata = new MetadataWrapper(modifiableMetadata);
    }

    // Method to update data value
    public void setDataValue(String key, Object value) {
        data.put(key, value);
    }

    // Getters
    public MetadataWrapper getMetadata() {
        return metadata;
    }

    public Map<String, Object> getData() {
        return data;
    }
}





