package teampixl.com.pixlpos.constructs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

    public class MenuItem implements Item {

        // Enum for item type

        public enum ItemType {
            ENTRE,
            MAIN,
            DESSERT,
            DRINKS,
            EXTRAS;
        }

        private final Map<String, Object> metadata;
        private final Map<String, Object> data;

        // Constructor for MenuItem
        public MenuItem(String itemName, double price, ItemType itemType, boolean activeItem, String description) {
            this.metadata = new HashMap<>();
            this.data = new HashMap<>();

            // Populate metadata
            metadata.put("id", UUID.randomUUID().toString());
            metadata.put("itemName", itemName);
            metadata.put("price", Math.round(price * 100.0) / 100.0);
            metadata.put("itemType", itemType);
            metadata.put("activeItem", activeItem);

            // Populate data
            data.put("description", description);
            data.put("notes", null); // Initially null
            data.put("amountOrdered", 0); // Default value
        }

        // Getters for metadata and data
        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public Map<String, Object> getData() {
            return data;
        }

        // Setters for metadata and data
        public void setMetadataValue(String key, Object value) {
            metadata.put(key, value);
        }

        public void setDataValue(String key, Object value) {
            data.put(key, value);
        }

        @Override
        public String toString() {
            return String.format("MenuItem{Metadata: %s, Data: %s}", metadata, data);
        }
    }
