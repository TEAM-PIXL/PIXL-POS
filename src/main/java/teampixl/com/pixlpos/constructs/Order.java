package teampixl.com.pixlpos.constructs;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
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
        COMPLETED
    }

    private MetadataWrapper metadata;  // MetadataWrapper should encapsulate the metadata map
    private final Map<String, Object> data;

    public Order(int orderNumber) {
        Map<String, Object> metadataMap = new HashMap<>();

        // Initialize metadata
        metadataMap.put("orderId", UUID.randomUUID());
        metadataMap.put("orderTime", LocalTime.now());
        metadataMap.put("orderDate", LocalDate.now());
        metadataMap.put("orderNumber", orderNumber > 0 ? orderNumber : 1);
        metadataMap.put("orderStatus", OrderStatus.NOT_STARTED);
        metadataMap.put("isCompleted", false);

        this.metadata = new MetadataWrapper(metadataMap);  // Correctly wrapping the map in MetadataWrapper

        // Initialize data
        this.data = new HashMap<>();
        data.put("items", new HashMap<MetadataWrapper, Integer>());  // Ensure the items map is correctly initialized
        data.put("total", 0.0);
    }

    // Getter for Metadata
    public MetadataWrapper getMetadata() {
        return metadata;
    }

    // Getter for Data
    public Map<String, Object> getData() {
        return data;
    }

    // Update order status
    public void updateOrderStatus(OrderStatus orderStatus) {
        Map<String, Object> modifiableMetadata = new HashMap<>(metadata.metadata());
        modifiableMetadata.put("orderStatus", orderStatus);
        if (orderStatus == OrderStatus.COMPLETED) {
            modifiableMetadata.put("isCompleted", true);
        }
        this.metadata = new MetadataWrapper(modifiableMetadata);  // Properly wrap the updated map
    }

    // Add an item to the order
    public void addItem(MenuItem menuItem, int quantity) {
        if (quantity < 1) {
            quantity = 1;
        }

        MetadataWrapper itemMetadata = menuItem.getMetadata();
        Map<MetadataWrapper, Integer> items = (Map<MetadataWrapper, Integer>) data.get("items");
        items.put(itemMetadata, items.getOrDefault(itemMetadata, 0) + quantity);

        double price = (double) itemMetadata.metadata().get("price");
        double total = (double) data.get("total");
        total += price * quantity;
        data.put("total", Math.max(total, 0));  // Ensure total is non-negative
    }

    // Remove an item from the order
    public void removeItem(MenuItem menuItem, int quantity) {
        MetadataWrapper itemMetadata = menuItem.getMetadata();
        Map<MetadataWrapper, Integer> items = (Map<MetadataWrapper, Integer>) data.get("items");
        if (items.containsKey(itemMetadata)) {
            int currentQuantity = items.get(itemMetadata);
            if (currentQuantity > quantity) {
                items.put(itemMetadata, currentQuantity - quantity);
            } else {
                items.remove(itemMetadata);
            }

            double price = (double) itemMetadata.metadata().get("price");
            double total = (double) data.get("total");
            total -= price * quantity;
            data.put("total", Math.max(total, 0));  // Ensure total is non-negative
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order{Metadata: ").append(new TreeMap<>(metadata.metadata())).append(", Data: ");

        Map<MetadataWrapper, Integer> items = (Map<MetadataWrapper, Integer>) data.get("items");
        sb.append("Items:\n");
        for (Map.Entry<MetadataWrapper, Integer> entry : new TreeMap<>(items).entrySet()) {
            sb.append(entry.getKey()).append(" x").append(entry.getValue()).append("\n");
        }
        sb.append("Total: $").append(String.format("%.2f", data.get("total"))).append("}");

        return sb.toString();
    }
}