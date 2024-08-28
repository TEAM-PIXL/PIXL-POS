package teampixl.com.pixlpos.constructs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import teampixl.com.pixlpos.constructs.interfaces.IDataManager;
import teampixl.com.pixlpos.database.MetadataWrapper;

public class MenuItem implements IDataManager {

    /*============================================================================================================================================================
    Code Description:
    - Enumerations for ItemType and DietaryRequirement
    - MetadataWrapper object for metadata
    - Map object for data
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
        - ingredients: map to store ingredients using ingredient_id as the key
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

        if (dietaryRequirement != null) {
            metadataMap.put("dietaryRequirement", dietaryRequirement);
        }

        metadataMap.put("created_at", System.currentTimeMillis());
        metadataMap.put("updated_at", System.currentTimeMillis());

        this.metadata = new MetadataWrapper(metadataMap);

        // Data
        this.data = new HashMap<>();
        this.data.put("description", description);
        this.data.put("notes", null);
        this.data.put("amountOrdered", 0);
        this.data.put("ingredients", new HashMap<String, Ingredients>());
    }

    /*=========================================================================================================================================================================================================
    Code Description:
    This section of the code contains methods to add, update, remove, clear and check for ingredients in a MenuItem object.

    Methods:
    - getIngredients(): returns ingredients
    - addIngredient(Ingredients ingredient): adds ingredient
    - updateIngredient(String oldIngredientUUID, Ingredients newIngredient): updates ingredient
    - removeIngredient(Ingredients ingredient): removes ingredient
    - clearIngredients(): clears ingredients
    - hasIngredient(String ingredientUUID): checks if ingredient exists
    - updateTimestamp(): updates timestamp
    =========================================================================================================================================================================================================*/

    public Map<String, Ingredients> getIngredients() {
        Object ingredientsObj = data.get("ingredients");

        if (ingredientsObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Ingredients> ingredients = (Map<String, Ingredients>) ingredientsObj;
            return ingredients;
        } else {
            throw new IllegalStateException("Expected ingredients to be a Map<String, Ingredients>");
        }
    }

    public void addIngredient(Ingredients ingredient) {
        Map<String, Ingredients> ingredients = getIngredients();
        String ingredientId = (String) ingredient.getMetadata().metadata().get("ingredient_id");  // Ensure correct key

        if (ingredientId == null || ingredientId.isEmpty()) {
            throw new IllegalArgumentException("Ingredient must have a valid ID.");
        }

        if (ingredients.containsKey(ingredientId)) {
            throw new IllegalArgumentException("Ingredient already exists.");
        }

        ingredients.put(ingredientId, ingredient);
        updateTimestamp();
    }

    public void updateIngredient(String oldIngredientId, Ingredients newIngredient) {
        Map<String, Ingredients> ingredients = getIngredients();
        if (ingredients.containsKey(oldIngredientId)) {
            ingredients.remove(oldIngredientId);
        } else {
            throw new IllegalArgumentException("Ingredient not found.");
        }
        String newIngredientId = (String) newIngredient.getMetadata().metadata().get("ingredient_id");
        ingredients.put(newIngredientId, newIngredient);
        updateTimestamp();
    }


    public void removeIngredient(Ingredients ingredient) {
        Map<String, Ingredients> ingredients = getIngredients();
        String ingredientId = (String) ingredient.getMetadata().metadata().get("ingredient_id");  // Ensure correct key

        if (ingredients.containsKey(ingredientId)) {
            ingredients.remove(ingredientId);
            updateTimestamp();
        } else {
            throw new IllegalArgumentException("Ingredient not found.");
        }
    }

    public boolean hasIngredient(String ingredientUUID) {
        return getIngredients().containsKey(ingredientUUID);
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






