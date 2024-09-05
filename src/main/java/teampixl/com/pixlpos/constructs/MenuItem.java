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
    - Map to store data
    - Map to store ingredients
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
    private final Map<String, IngredientAmount> ingredients;

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
        - ingredients: map to store ingredients using ingredient_id as the key and IngredientAmount as the value
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
        this.ingredients = new HashMap<>();
    }

    /*=========================================================================================================================================================================================================
    Code Description:
    This section of the code contains methods to add, update, remove, clear and check for ingredients in a MenuItem object.

    Methods:
    - getIngredients(): returns ingredients
    - addIngredient(Ingredients ingredient, Object numeral): adds ingredient with a numeral
    - updateIngredient(String oldIngredientId, Ingredients newIngredient, Object numeral): updates ingredient with a numeral
    - removeIngredient(Ingredients ingredient): removes ingredient
    - clearIngredients(): clears ingredients
    - hasIngredient(String ingredientId): checks if ingredient exists
    - updateTimestamp(): updates timestamp
    =========================================================================================================================================================================================================*/

    public Map<String, IngredientAmount> getIngredients() {
        return ingredients;
    }

    public void addIngredient(Ingredients ingredient, Object numeral) {
        if ((numeral instanceof Integer && (Integer) numeral < 0) || (numeral instanceof Double && (Double) numeral < 0)) {
            throw new IllegalArgumentException("Numeral must be a non-negative value.");
        }

        String ingredientId = (String) ingredient.getMetadata().metadata().get("ingredient_id");

        if (ingredientId == null || ingredientId.isEmpty()) {
            throw new IllegalArgumentException("Ingredient must have a valid ID.");
        }

        if (ingredients.containsKey(ingredientId)) {
            throw new IllegalArgumentException("Ingredient already exists.");
        }

        ingredients.put(ingredientId, new IngredientAmount(ingredient, numeral));
        updateTimestamp();
    }

    public void updateIngredient(String oldIngredientId, Ingredients newIngredient, Object numeral) {
        if ((numeral instanceof Integer && (Integer) numeral < 0) || (numeral instanceof Double && (Double) numeral < 0)) {
            throw new IllegalArgumentException("Numeral must be a non-negative value.");
        }

        if (ingredients.containsKey(oldIngredientId)) {
            ingredients.remove(oldIngredientId);
        } else {
            throw new IllegalArgumentException("Ingredient not found.");
        }
        String newIngredientId = (String) newIngredient.getMetadata().metadata().get("ingredient_id");
        ingredients.put(newIngredientId, new IngredientAmount(newIngredient, numeral));
        updateTimestamp();
    }

    public void removeIngredient(Ingredients ingredient) {
        String ingredientId = (String) ingredient.getMetadata().metadata().get("ingredient_id");

        if (ingredients.containsKey(ingredientId)) {
            ingredients.remove(ingredientId);
            updateTimestamp();
        } else {
            throw new IllegalArgumentException("Ingredient not found.");
        }
    }

    public boolean hasIngredient(String ingredientId) {
        return ingredients.containsKey(ingredientId);
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
        return String.format("MenuItem{Metadata: %s, Data: %s, Ingredients: %s}", new HashMap<>(metadata.metadata()), new HashMap<>(data), ingredients);
    }

    /*============================================================================================================================================================
    Code Description:
    - IngredientAmount class to hold an ingredient and its corresponding numeral.
    ============================================================================================================================================================*/

    public record IngredientAmount(Ingredients ingredient, Object numeral) {

        @Override
            public String toString() {
                return String.format("IngredientAmount{Ingredient: %s, Numeral: %s}", ingredient, numeral);
            }
        }
}





