package teampixl.com.pixlpos.constructs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.TreeMap;
import teampixl.com.pixlpos.database.MetadataWrapper;

public class MenuItem {

    public enum ItemType {
        ENTREE,
        MAIN,
        DESSERT,
        DRINK
    }

    public enum DietaryRequirement {
        VEGAN,
        VEGETARIAN,
        GLUTEN_FREE,
        SPICY,
        ALLERGEN_FREE
    }

    private MetadataWrapper metadata;
    private final Map<String, Object> data;

    // Constructor with DietaryRequirement
    public MenuItem(String itemName, double price, ItemType itemType, boolean activeItem, String description, DietaryRequirement dietaryRequirement) {
        if (itemName == null || itemName.isEmpty()) {
            throw new IllegalArgumentException("itemName cannot be null or empty");
        }
        if (itemType == null) {
            throw new IllegalArgumentException("itemType cannot be null");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("description cannot be null or empty");
        }

        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("id", UUID.randomUUID().toString());
        metadataMap.put("itemName", itemName);
        metadataMap.put("price", Math.round(price * 100.0) / 100.0);
        metadataMap.put("itemType", itemType);
        metadataMap.put("activeItem", activeItem);

        // Only add dietaryRequirement if it's not null
        if (dietaryRequirement != null) {
            metadataMap.put("dietaryRequirement", dietaryRequirement);
        }

        // Debugging: Log the map contents before wrapping
        System.out.println("Metadata map before wrapping: " + metadataMap);

        this.metadata = new MetadataWrapper(metadataMap);

        this.data = new HashMap<>();
        data.put("description", description);
        data.put("notes", null);  // Default to null
        data.put("amountOrdered", 0);  // Default value is 0
    }

    // Overloaded constructor without DietaryRequirement
    public MenuItem(String itemName, double price, ItemType itemType, boolean activeItem, String description) {
        this(itemName, price, itemType, activeItem, description, null);
    }

    // Getters for Metadata and Data
    public MetadataWrapper getMetadata() {
        return metadata;
    }

    public Map<String, Object> getData() {
        return data;
    }

    // Methods to safely update Metadata
    public void updateMetadata(String key, Object value) {
        Map<String, Object> modifiableMetadata = new HashMap<>(metadata.metadata());
        if (value != null) {
            modifiableMetadata.put(key, value);
        } else {
            modifiableMetadata.remove(key); // Or keep the key with null value depending on your design
        }
        this.metadata = new MetadataWrapper(modifiableMetadata);
    }

    // Setters for specific fields in data
    public void setDataValue(String key, Object value) {
        data.put(key, value);
    }

    @Override
    public String toString() {
        // Use TreeMap to ensure keys are sorted in the output
        Map<String, Object> sortedMetadata = new TreeMap<>(metadata.metadata());
        Map<String, Object> sortedData = new TreeMap<>(data);

        return String.format("MenuItem{Metadata: %s, Data: %s}", sortedMetadata, sortedData);
    }
}



