package teampixl.com.pixlpos.constructs;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import teampixl.com.pixlpos.database.MetadataWrapper;

public class Order {

    // Enum for Order Status

    public enum OrderStatus {
        SENT,
        RECEIVED,
        NOT_STARTED,
        ON_THE_WAY,
        NEARLY_DONE,
        COMPLETED;
    }

    private final Map<String, Object> metadata;
    private final Map<String, Object> data;

    public Order(int orderNumber) {
        this.metadata = new HashMap<>();
        this.data = new HashMap<>();

        // Initialize metadata
        metadata.put("orderId", UUID.randomUUID());
        metadata.put("orderTime", LocalTime.now());
        metadata.put("orderDate", LocalDate.now());
        metadata.put("orderNumber", orderNumber > 0 ? orderNumber : 1);
        metadata.put("orderStatus", OrderStatus.NOT_STARTED);
        metadata.put("isCompleted", false);

        // Initialize data
        data.put("items", new HashMap<MetadataWrapper, Integer>());
        data.put("total", 0.0);
    }

    // Getters for Metadata and Data
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public Map<String, Object> getData() {
        return data;
    }

    // Update order status
    public void setOrderStatus(OrderStatus orderStatus) {
        metadata.put("orderStatus", orderStatus);
        if (orderStatus == OrderStatus.COMPLETED) {
            metadata.put("isCompleted", true);
        }
    }

    // Add an item to the order
    public void addItem(MenuItem menuItem, int quantity) {
        if (quantity < 1) {
            quantity = 1;
        }

        MetadataWrapper metadataWrapper = new MetadataWrapper(menuItem.getMetadata().metadata());
        Map<MetadataWrapper, Integer> items = (Map<MetadataWrapper, Integer>) data.get("items");
        items.put(metadataWrapper, items.getOrDefault(metadataWrapper, 0) + quantity);

        double price = (double) menuItem.getMetadata().metadata().get("price");
        double total = (double) data.get("total");
        total += price * quantity;
        data.put("total", Math.max(total, 0));  // Ensure total is non-negative
    }

    // Remove an item from the order
    public void removeItem(MenuItem menuItem, int quantity) {
        MetadataWrapper metadataWrapper = new MetadataWrapper(menuItem.getMetadata().metadata());
        Map<MetadataWrapper, Integer> items = (Map<MetadataWrapper, Integer>) data.get("items");
        if (items.containsKey(metadataWrapper)) {
            int currentQuantity = items.get(metadataWrapper);
            if (currentQuantity > quantity) {
                items.put(metadataWrapper, currentQuantity - quantity);
            } else {
                items.remove(metadataWrapper);
            }

            double price = (double) menuItem.getMetadata().metadata().get("price");
            double total = (double) data.get("total");
            total -= price * quantity;
            data.put("total", Math.max(total, 0));  // Ensure total is non-negative
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order{Metadata: ").append(metadata).append(", Data: ");

        Map<MetadataWrapper, Integer> items = (Map<MetadataWrapper, Integer>) data.get("items");
        sb.append("Items:\n");
        for (Map.Entry<MetadataWrapper, Integer> entry : items.entrySet()) {
            sb.append(entry.getKey()).append(" x").append(entry.getValue()).append("\n");
        }
        sb.append("Total: $").append(String.format("%.2f", data.get("total"))).append("}");

        return sb.toString();
    }
}

