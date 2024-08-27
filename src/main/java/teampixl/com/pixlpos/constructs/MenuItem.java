package teampixl.com.pixlpos.constructs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import teampixl.com.pixlpos.database.MetadataWrapper;

public class MenuItem {

    /*============================================================================================================================================================
    Code Description:
    - Enumerations for ItemType and DietaryRequirement
    - MetadataWrapper object for metadata
    - Map object for data
    - Map object for ingredients
    ============================================================================================================================================================*/

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
        ALLERGEN_FREE,
        NONE
    }

    private MetadataWrapper metadata;
    private final Map<String, Object> data;
    private final Map<String, Ingredients> ingredients;

    /*============================================================================================================================================================
    Code Description:
    - Constructor for MenuItem object.

    Metadata:
        - id: UUID
        - itemName: itemName
        - price: price
        - itemType: itemType
        - activeItem: activeItem
        - dietaryRequirement: dietaryRequirement
        - created_at: timestamp for creation
        - updated_at: timestamp for last update

    Data:
        - description: description
        - notes: null
        - amountOrdered: 0
        - ingredients: null
    ============================================================================================================================================================*/

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

        // Metadata
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

        metadataMap.put("created_at", System.currentTimeMillis()); // Timestamp for creation
        metadataMap.put("updated_at", System.currentTimeMillis()); // Timestamp for last update

        // Create immutable map for metadata
        this.metadata = new MetadataWrapper(Map.copyOf(metadataMap));

        // Data
        this.data = new HashMap<>();
        data.put("description", description);
        data.put("notes", null);  // Default to null
        data.put("amountOrdered", 0);  // Default value is 0

        // Ingredients - can be empty, using a Map to link ingredient UUIDs to the actual Ingredients
        this.ingredients = new HashMap<>();
    }

    public Map<String, Ingredients> getIngredients() {
        return ingredients;
    }

    public void addIngredient(Ingredients ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("Ingredient cannot be null");
        }
        ingredients.put((String) ingredient.getMetadata().metadata().get("uuid"), ingredient);
    }

    public void removeIngredient(Ingredients ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("Ingredient cannot be null");
        }
        ingredients.remove(ingredient.getMetadata().metadata().get("uuid"));
    }

    public boolean hasIngredient(String ingredientUUID) {
        return ingredients.containsKey(ingredientUUID);
    }

    public void updateIngredient(String ingredientUUID, Ingredients updatedIngredient) {
        if (ingredientUUID == null || !ingredients.containsKey(ingredientUUID)) {
            throw new IllegalArgumentException("Ingredient not found");
        }
        ingredients.put(ingredientUUID, updatedIngredient);
    }

    public void clearIngredients() {
        ingredients.clear();
    }

    /*============================================================================================================================================================
    Code Description:
    - Method to get metadata, data, update metadata and set data value.

    Methods:
    - getMetadata(): returns metadata
    - getData(): returns data
    - updateMetadata(String key, Object value): updates metadata
    - setDataValue(String key, Object value): sets data value
    - toString(): returns string representation of MenuItem object
    ============================================================================================================================================================*/

    public MetadataWrapper getMetadata() {
        return metadata;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void updateMetadata(String key, Object value) {
        Map<String, Object> modifiableMetadata = new HashMap<>(metadata.metadata());
        if (value != null) {
            modifiableMetadata.put(key, value);
        } else {
            modifiableMetadata.remove(key);
        }
        this.metadata = new MetadataWrapper(Map.copyOf(modifiableMetadata));
    }

    public void setDataValue(String key, Object value) {
        data.put(key, value);
    }

    @Override
    public String toString() {
        return String.format("MenuItem{Metadata: %s, Data: %s}", new HashMap<>(metadata.metadata()), new HashMap<>(data));
    }
}




