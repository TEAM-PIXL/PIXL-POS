package teampixl.com.pixlpos.constructs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import teampixl.com.pixlpos.database.MetadataWrapper;

public class Order {

    /*============================================================================================================================================================
    Code Description:
    - Enumerations for OrderStatus
    - MetadataWrapper object for metadata
    - Map object for data
    ============================================================================================================================================================*/

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
        - menuItems: Map<String, Integer>
        - total: 0.0
        - special_requests: null
        - payment_details: null
    ============================================================================================================================================================*/

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
        - addMenuItem(MenuItem item, int quantity)
        - removeMenuItem(MenuItem item, int quantity)
        - updateTotal(MenuItem item, int quantity)
        - updateOrderStatus(OrderStatus newStatus)
        - completeOrder()
        - updateTimestamp()
    ============================================================================================================================================================*/

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
                updateTimestamp();
            }
        } else {
            throw new IllegalStateException("Expected menuItems to be a Map<String, Integer>");
        }
    }

    private void updateTotal(MenuItem item, int quantity) {
        double currentTotal = (double) data.get("total");
        double itemPrice = (double) item.getMetadata().metadata().get("price");
        data.put("total", currentTotal + (itemPrice * quantity));
    }

    public void updateOrderStatus(OrderStatus newStatus) {
        updateMetadata("order_status", newStatus);
        if (newStatus == OrderStatus.COMPLETED) {
            updateMetadata("is_completed", true);
        }
    }

    public void completeOrder() {
        updateOrderStatus(OrderStatus.COMPLETED);
    }

    private void updateTimestamp() {
        updateMetadata("updated_at", System.currentTimeMillis());
    }

    /*============================================================================================================================================================
    Code Description:
    - Method to get metadata, data, update metadata and set data value.

    Methods:
        - getMetadata(): returns metadata
        - getData(): returns data
        - updateMetadata(String key, Object value): updates metadata
        - setDataValue(String key, Object value): sets data value
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





