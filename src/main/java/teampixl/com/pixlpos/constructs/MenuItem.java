package teampixl.com.pixlpos.constructs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import teampixl.com.pixlpos.database.MetadataWrapper;

public class MenuItem {

    public enum ItemType {
        ENTREE,
        MAIN,
        DESSERT,
        DRINK;
    }

    public enum DietaryRequirement {
        VEGAN,
        VEGETARIAN,
        GLUTEN_FREE,
        SPICY,
        ALLERGEN_FREE;
    }

    private final MetadataWrapper metadata;
    private final Map<String, Object> data;

    public MenuItem(String itemName, double price, ItemType itemType, boolean activeItem, String description) {
        Map<String, Object> metadataMap = new HashMap<>();

        // Initialize metadata
        metadataMap.put("id", UUID.randomUUID().toString());
        metadataMap.put("itemName", itemName);
        metadataMap.put("price", Math.round(price * 100.0) / 100.0);
        metadataMap.put("itemType", itemType);
        metadataMap.put("activeItem", activeItem);
        metadataMap.put("dietaryRequirement", null);  // Default to null

        this.metadata = new MetadataWrapper(metadataMap);

        // Initialize data
        this.data = new HashMap<>();
        data.put("description", description);
        data.put("notes", null);  // Default to null
        data.put("amountOrdered", 0);  // Default value is 0
    }

    // Getters for Metadata and Data
    public MetadataWrapper getMetadata() {
        return metadata;
    }

    public Map<String, Object> getData() {
        return data;
    }

    // Setters for specific fields
    public void setMetadataValue(String key, Object value) {
        metadata.metadata().put(key, value);
    }

    public void setDataValue(String key, Object value) {
        data.put(key, value);
    }

    @Override
    public String toString() {
        return String.format("MenuItem{Metadata: %s, Data: %s}", metadata, data);
    }
}
