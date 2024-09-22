package teampixl.com.pixlpos.database.api;

import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.Ingredients;

import java.util.List;
import java.util.stream.Collectors;

/**
 * API for managing ingredients in the database.
 */
public class IngredientsAPI {
    private static IngredientsAPI instance;
    private static final DataStore dataStore = DataStore.getInstance();

    private IngredientsAPI() { }

    /**
     * Constructor for IngredientsAPI object.
     */
    public static synchronized IngredientsAPI getInstance() {
        if (instance == null) {
            instance = new IngredientsAPI();
        }
        return instance;
    }

    /**
     * Validates the ingredient name.
     * @param ingredientName Name of the ingredient.
     * @return StatusCode indicating the validation status.
     */
    public static StatusCode validateIngredientByName(String ingredientName) {
        if (ingredientName == null || ingredientName.isEmpty()) {
            return StatusCode.INVALID_INGREDIENT_NAME;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Posts an ingredient to the database.
     * @param ingredientName Name of the ingredient.
     * @param notes Notes about the ingredient.
     * @return StatusCode indicating the post status.
     */
    public StatusCode postIngredient(String ingredientName, String notes) {
        try {
            StatusCode status = validateIngredientByName(ingredientName);
            if (status != StatusCode.SUCCESS) {
                return status;
            }
            Ingredients ingredient = new Ingredients(ingredientName, notes);
            dataStore.createIngredient(ingredient);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.INGREDIENT_CREATION_FAILED;
        }
    }

    /**
     * Posts an ingredient to the database.
     * @param ingredientName Name of the ingredient.
     * @return StatusCode indicating the post status.
     */
    public StatusCode postIngredient(String ingredientName) {
        return postIngredient(ingredientName, null);
    }

    /**
     * Gets ingredient Id by name.
     * @param ingredientName Name of the ingredient.
     * @return Id of the ingredient.
     */
    public String getIngredientsByName(String ingredientName) {
        return dataStore.readIngredients().stream()
                .filter(ingredient -> ingredient.getMetadata().metadata().get("itemName").equals(ingredientName))
                .findFirst()
                .map(Ingredients::toString)
                .orElse(null);
    }

    public Ingredients getIngredientById(String ingredientId) {
        return dataStore.readIngredients().stream()
                .filter(ingredient -> ingredient.getMetadata().metadata().get("ingredient_id").equals(ingredientId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets ingredient by name.
     * @param ingredientName Name of the ingredient.
     * @return Ingredients object.
     */
    public StatusCode putIngredientName(String ingredientName, String newIngredientName) {
        try {
            String id = getIngredientsByName(ingredientName);
            if (id == null) {
                return StatusCode.INGREDIENT_NOT_FOUND;
            }
            Ingredients ingredient = dataStore.readIngredients().stream()
                    .filter(ing -> ing.getMetadata().metadata().get("ingredient_id").equals(id))
                    .findFirst()
                    .orElse(null);
            if (ingredient == null) {
                return StatusCode.INGREDIENT_NOT_FOUND;
            }
            ingredient.getMetadata().metadata().put("itemName", newIngredientName);
            dataStore.updateIngredient(ingredient);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.INGREDIENT_UPDATE_FAILED;
        }
    }

    /**
     * Puts ingredient notes.
     * @param ingredientName Name of the ingredient.
     * @param notes Notes about the ingredient.
     * @return StatusCode indicating the put status.
     */
    public StatusCode putIngredientNotes(String ingredientName, String notes) {
        try {
            String id = getIngredientsByName(ingredientName);
            if (id == null) {
                return StatusCode.INGREDIENT_NOT_FOUND;
            }
            Ingredients ingredient = dataStore.readIngredients().stream()
                    .filter(ing -> ing.getMetadata().metadata().get("ingredient_id").equals(id))
                    .findFirst()
                    .orElse(null);
            if (ingredient == null) {
                return StatusCode.INGREDIENT_NOT_FOUND;
            }
            ingredient.setDataValue("notes", notes);
            dataStore.updateIngredient(ingredient);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.INGREDIENT_UPDATE_FAILED;
        }
    }

    /**
     * Deletes an ingredient from the database.
     * @param ingredientName Name of the ingredient.
     * @return StatusCode indicating the deletion status.
     */
    public StatusCode deleteIngredient(String ingredientName) {
        try {
            String id = getIngredientsByName(ingredientName);
            if (id == null) {
                return StatusCode.INGREDIENT_NOT_FOUND;
            }
            Ingredients ingredient = dataStore.readIngredients().stream()
                    .filter(ing -> ing.getMetadata().metadata().get("ingredient_id").equals(id))
                    .findFirst()
                    .orElse(null);
            if (ingredient == null) {
                return StatusCode.INGREDIENT_NOT_FOUND;
            }
            dataStore.deleteIngredient(ingredient);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.INGREDIENT_DELETION_FAILED;
        }
    }

    /**
     * Searches for ingredients in the database.
     * @param query Search query.
     * @return List of Ingredients objects.
     */
    public static List<Ingredients> searchIngredients(String query) {
        String[] parts = query.trim().split("\\s+");

        if (parts.length > 2) {
            return List.of();
        }
        return dataStore.readIngredients().stream()
                .filter(ingredient -> {
                    String name = (String) ingredient.getMetadata().metadata().get("itemName");
                    return name.contains(parts[0]) && (parts.length == 1 || name.contains(parts[1]));
                })
                .collect(Collectors.toList());
    }
}
