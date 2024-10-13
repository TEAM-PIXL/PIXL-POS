package teampixl.com.pixlpos.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import teampixl.com.pixlpos.models.tools.DataManager;
import teampixl.com.pixlpos.models.tools.MetadataWrapper;

/**
 * The MenuItem class represents a menu item in the system. It contains metadata, data, and ingredients.
 * <p>
 * Metadata:
 * - id: UUID
 * - itemName: itemName
 * - price: price
 * - itemType: itemType
 * - activeItem: activeItem
 * - dietaryRequirement: dietaryRequirement
 * - created_at: timestamp for creation
 * - updated_at: timestamp for last update
 * <p>
 * Data:
 * - description: description
 * - notes: null
 * - amountOrdered: 0
 * - ingredients: map to store ingredients using ingredient_id as the key and IngredientAmount as the value
 * Ingredients:
 * - IngredientAmount: a record to hold an ingredient and its corresponding numeral
 * @see MetadataWrapper
 * @see DataManager
 * @see Ingredients
 */
public class MenuItem extends DataManager {

    /**
     * Enumerations for ItemType
     */
    public enum ItemType {
        ENTREE,
        MAIN,
        DESSERT,
        DRINK
    }

    /**
     * Enumerations for DietaryRequirement
     */
    public enum DietaryRequirement {
        VEGAN,
        VEGETARIAN,
        GLUTEN_FREE,
        SPICY,
        ALLERGEN_FREE,
        NONE
    }

    private final Map<String, IngredientAmount> ingredients;

    /**
     * Constructor for MenuItem object.
     *
     * @param itemName name of the item
     * @param price price of the item
     * @param itemType type of the item
     * @param activeItem whether the item is active
     * @param description description of the item
     * @param dietaryRequirement dietary requirement of the item
     */
    public MenuItem(String itemName, double price, ItemType itemType, boolean activeItem, String description, DietaryRequirement dietaryRequirement) {
        super(initializeMetadata(itemName, price, itemType, activeItem, dietaryRequirement));
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("description cannot be null or empty");
        }

        this.data.put("description", description);
        this.data.put("notes", null);
        this.data.put("amountOrdered", 0);
        this.ingredients = new HashMap<>();
        this.data.put("ingredients", this.ingredients); // Ensure ingredients map is added to data map
    }

    private static MetadataWrapper initializeMetadata(String itemName, double price, ItemType itemType, boolean activeItem, DietaryRequirement dietaryRequirement) {
        if (itemName == null || itemName.isEmpty()) {
            throw new IllegalArgumentException("itemName cannot be null or empty");
        }
        if (itemType == null) {
            throw new IllegalArgumentException("itemType cannot be null");
        }

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

        return new MetadataWrapper(metadataMap);
    }

    /*=========================================================================================================================================================================================================
    ---------------------------------------------------------------------------------> HELPER METHODS <-------------------------------------------------------------------------------------------------------
    =========================================================================================================================================================================================================*/

    /**
     * Get the ingredients of the menu item.
     * @return ingredients
     */
    public Map<String, IngredientAmount> getIngredients() {
        return ingredients;
    }

    /**
     * Add an ingredient to the menu item.
     * @param ingredient ingredient to add
     * @param numeral amount of the ingredient
     */

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
        this.data.put("ingredients", this.ingredients); // Update data map with ingredients
        updateTimestamp();
    }

    /**
     * Update an ingredient in the menu item.
     * @param oldIngredientId ID of the ingredient to update
     * @param newIngredient new ingredient to replace the old ingredient
     * @param numeral new amount of the ingredient
     */
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
        this.data.put("ingredients", this.ingredients); // Update data map with ingredients
        updateTimestamp();
    }

    /**
     * Remove an ingredient from the menu item.
     * @param ingredient ingredient to remove
     */
    public void removeIngredient(Ingredients ingredient) {
        String ingredientId = (String) ingredient.getMetadata().metadata().get("ingredient_id");

        if (ingredients.containsKey(ingredientId)) {
            ingredients.remove(ingredientId);
            this.data.put("ingredients", this.ingredients); // Update data map with ingredients
            updateTimestamp();
        } else {
            throw new IllegalArgumentException("Ingredient not found.");
        }
    }

    /**
     * Check if the menu item has an ingredient.
     * @param ingredientId ID of the ingredient to check
     * @return true if the menu item has the ingredient, false otherwise
     */
    public boolean hasIngredient(String ingredientId) {
        return ingredients.containsKey(ingredientId);
    }

    private void updateTimestamp() {
        updateMetadata("updated_at", System.currentTimeMillis());
    }

    public double getPrice() {
        return (double) metadata.metadata().get("price");
    }

    /*=============================================================================================================================================================================
    ---------------------------------------------------------------------------------> RECORD FOR INGREDIENT MAP <-------------------------------------------------------------
    ===============================================================================================================================================================================*/

    public record IngredientAmount(Ingredients ingredient, Object numeral) {

        @Override
            public String toString() {
                return String.format("IngredientAmount{Ingredient: %s, Numeral: %s}", ingredient, numeral);
            }
        }
}





