package teampixl.com.pixlpos.database.api;

import teampixl.com.pixlpos.database.api.util.*;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.models.Ingredients;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.util.Pair;
import java.lang.reflect.Method;

/**
 * The IngredientsAPI class is a singleton class responsible for managing ingredients in memory.
 * It provides methods to create, read, update, and delete ingredients, as well as validation and search functionalities.
 */
public class IngredientsAPI {
    private static IngredientsAPI INSTANCE;
    private static final DataStore DATA_STORE = DataStore.getInstance();

    private IngredientsAPI() { }

    /**
     * Gets the singleton instance of the IngredientsAPI.
     *
     * @return the instance of the IngredientsAPI
     */
    public static synchronized IngredientsAPI getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new IngredientsAPI();
        }
        return INSTANCE;
    }

    /**
     * Validates the name of an ingredient.
     *
     * @param INGREDIENT_NAME the name to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateIngredientByName(String INGREDIENT_NAME) {
        if (INGREDIENT_NAME == null || INGREDIENT_NAME.trim().isEmpty()) {
            return StatusCode.INGREDIENT_NAME_EMPTY;
        }
        if (INGREDIENT_NAME.length() > 50) {
            return StatusCode.INGREDIENT_NAME_TOO_LONG;
        }
        if (INGREDIENT_NAME.length() < 3) {
            return StatusCode.INGREDIENT_NAME_TOO_SHORT;
        }
        if (INGREDIENT_NAME.matches(".*\\d.*")) {
            return StatusCode.INGREDIENT_NAME_CONTAINS_DIGITS;
        }
        if (INGREDIENT_NAME.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            return StatusCode.INGREDIENT_NAME_CONTAINS_SPECIAL_CHARACTERS;
        }
        boolean INGREDIENT_EXISTS = DATA_STORE.readIngredients().stream()
                .anyMatch(INGREDIENT -> INGREDIENT.getMetadata().metadata().get("itemName").toString().equalsIgnoreCase(INGREDIENT_NAME));
        return INGREDIENT_EXISTS ? StatusCode.INGREDIENT_NAME_TAKEN : StatusCode.SUCCESS;
    }

    /**
     * Validates the notes of an ingredient.
     *
     * @param NOTES the notes to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateIngredientByNotes(String NOTES) {
        if (NOTES != null && NOTES.length() > 500) {
            return StatusCode.INGREDIENT_NOTES_TOO_LONG;
        }
        return StatusCode.SUCCESS;
    }

    /* ---> IMPORTANT INTERNAL FUNCTION FOR SAFELY MANAGING AND VALIDATING AN INGREDIENT UPDATE <---- */
    private Pair<List<StatusCode>, Ingredients> validateAndGetIngredient(String FIELD, Object VALUE, String INGREDIENT_NAME) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        try {
            Class<?> VALUE_TYPE = VALUE.getClass();
            if (VALUE_TYPE == Boolean.class) {
                VALUE_TYPE = boolean.class;
            }
            Method VALIDATION_METHOD = this.getClass().getMethod("validateIngredientBy" + FIELD, VALUE_TYPE);
            StatusCode VALIDATION_RESULT = (StatusCode) VALIDATION_METHOD.invoke(this, VALUE);
            VALIDATIONS.add(VALIDATION_RESULT);
            if (!Exceptions.isSuccessful(VALIDATIONS)) {
                return new Pair<>(VALIDATIONS, null);
            }
        } catch (NoSuchMethodException E) {
            VALIDATIONS.add(StatusCode.INTERNAL_METHOD_NOT_FOUND);
            return new Pair<>(VALIDATIONS, null);
        } catch (Exception E) {
            VALIDATIONS.add(StatusCode.INTERNAL_FAILURE);
            return new Pair<>(VALIDATIONS, null);
        }

        Ingredients INGREDIENT = getIngredient(INGREDIENT_NAME);

        if (INGREDIENT == null) {
            VALIDATIONS.add(StatusCode.INGREDIENT_NOT_FOUND);
            return new Pair<>(VALIDATIONS, null);
        }

        return new Pair<>(VALIDATIONS, INGREDIENT);
    }

    /**
     * Gets an ingredient ID from memory based on its name.
     *
     * @param INGREDIENT_NAME the name of the ingredient
     * @return the ID of the ingredient
     */
    public String keySearch(String INGREDIENT_NAME) {
        if (INGREDIENT_NAME == null) {
            return null;
        }
        return DATA_STORE.readIngredients().stream()
                .filter(INGREDIENT -> INGREDIENT.getMetadata().metadata().get("itemName").toString().equalsIgnoreCase(INGREDIENT_NAME))
                .findFirst()
                .map(INGREDIENT -> INGREDIENT.getMetadata().metadata().get("ingredient_id").toString())
                .orElse(null);
    }

    /**
     * Gets an ingredient name from memory based on its ID.
     *
     * @param ID the ID of the ingredient
     * @return the name of the ingredient
     */
    public String reverseKeySearch(String ID) {
        if (ID == null) {
            return null;
        }
        return DATA_STORE.readIngredients().stream()
                .filter(INGREDIENT -> INGREDIENT.getMetadata().metadata().get("ingredient_id").toString().equals(ID))
                .findFirst()
                .map(INGREDIENT -> INGREDIENT.getMetadata().metadata().get("itemName").toString())
                .orElse(null);
    }

    /**
     * Transforms an ingredient ID into an Ingredients object.
     *
     * @param ID the ID of the ingredient
     * @return the Ingredients object matching the ID
     */
    public Ingredients keyTransform(String ID) {
        if (ID == null) {
            return null;
        }
        return DATA_STORE.readIngredients().stream()
                .filter(INGREDIENT -> INGREDIENT.getMetadata().metadata().get("ingredient_id").toString().equals(ID))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets an ingredient from memory based on its name.
     *
     * @param INGREDIENT_NAME the name of the ingredient
     * @return the Ingredients object matching the name
     */
    public Ingredients getIngredient(String INGREDIENT_NAME) {
        String ID = keySearch(INGREDIENT_NAME);
        return keyTransform(ID);
    }

    /**
     * Creates a new ingredient and adds it to the database.
     *
     * @param INGREDIENT_NAME the name of the ingredient
     * @param NOTES           notes about the ingredient
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> postIngredient(String INGREDIENT_NAME, String NOTES) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        try {
            StatusCode NAME_VALIDATION = validateIngredientByName(INGREDIENT_NAME);
            VALIDATIONS.add(NAME_VALIDATION);

            StatusCode NOTES_VALIDATION = validateIngredientByNotes(NOTES);
            VALIDATIONS.add(NOTES_VALIDATION);

            if (!Exceptions.isSuccessful(VALIDATIONS)) {
                return VALIDATIONS;
            }

            Ingredients INGREDIENT = new Ingredients(INGREDIENT_NAME, NOTES);
            DATA_STORE.createIngredient(INGREDIENT);
            VALIDATIONS.add(StatusCode.SUCCESS);
            return VALIDATIONS;
        } catch (Exception E) {
            VALIDATIONS.add(StatusCode.INGREDIENT_POST_FAILED);
            return VALIDATIONS;
        }
    }

    /**
     * Overloaded method to create a new ingredient without notes.
     *
     * @param INGREDIENT_NAME the name of the ingredient
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> postIngredient(String INGREDIENT_NAME) {
        return postIngredient(INGREDIENT_NAME, "");
    }

    /**
     * Updates the name of an existing ingredient.
     *
     * @param INGREDIENT_NAME     the current name of the ingredient
     * @param NEW_INGREDIENT_NAME the new name for the ingredient
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> putIngredientName(String INGREDIENT_NAME, String NEW_INGREDIENT_NAME) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        try {
            Pair<List<StatusCode>, Ingredients> RESULT = validateAndGetIngredient("Name", NEW_INGREDIENT_NAME, INGREDIENT_NAME);
            VALIDATIONS.addAll(RESULT.getKey());
            if (!Exceptions.isSuccessful(VALIDATIONS)) {
                return VALIDATIONS;
            }

            Ingredients INGREDIENT = RESULT.getValue();

            INGREDIENT.updateMetadata("itemName", NEW_INGREDIENT_NAME);
            DATA_STORE.updateIngredient(INGREDIENT);
            VALIDATIONS.add(StatusCode.SUCCESS);
            return VALIDATIONS;
        } catch (Exception E) {
            VALIDATIONS.add(StatusCode.INGREDIENT_PUT_FAILED);
            return VALIDATIONS;
        }
    }

    /**
     * Updates the notes of an existing ingredient.
     *
     * @param INGREDIENT_NAME the name of the ingredient
     * @param NEW_NOTES       the new notes for the ingredient
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> putIngredientNotes(String INGREDIENT_NAME, String NEW_NOTES) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        try {
            Pair<List<StatusCode>, Ingredients> RESULT = validateAndGetIngredient("Notes", NEW_NOTES, INGREDIENT_NAME);
            VALIDATIONS.addAll(RESULT.getKey());
            if (!Exceptions.isSuccessful(VALIDATIONS)) {
                return VALIDATIONS;
            }

            Ingredients INGREDIENT = RESULT.getValue();

            INGREDIENT.setDataValue("notes", NEW_NOTES);
            DATA_STORE.updateIngredient(INGREDIENT);
            VALIDATIONS.add(StatusCode.SUCCESS);
            return VALIDATIONS;
        } catch (Exception E) {
            VALIDATIONS.add(StatusCode.INGREDIENT_PUT_FAILED);
            return VALIDATIONS;
        }
    }

    /**
     * Deletes an ingredient from the database.
     *
     * @param INGREDIENT_NAME the name of the ingredient to delete
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> deleteIngredient(String INGREDIENT_NAME) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        try {
            Ingredients INGREDIENT = getIngredient(INGREDIENT_NAME);
            if (INGREDIENT == null) {
                VALIDATIONS.add(StatusCode.INGREDIENT_NOT_FOUND);
                return VALIDATIONS;
            }

            DATA_STORE.deleteIngredient(INGREDIENT);
            VALIDATIONS.add(StatusCode.SUCCESS);
            return VALIDATIONS;
        } catch (Exception E) {
            VALIDATIONS.add(StatusCode.INGREDIENT_DELETION_FAILED);
            return VALIDATIONS;
        }
    }

    /**
     * Searches for ingredients in the database based on a query string.
     *
     * @param QUERY the search query
     * @return a list of ingredients matching the query
     */
    public List<Ingredients> searchIngredients(String QUERY) {
        if (QUERY == null || QUERY.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String[] TOKENS = QUERY.trim().toLowerCase().split("\\s+");

        return DATA_STORE.readIngredients().parallelStream()
                .filter(INGREDIENT -> {
                    String ITEM_NAME = INGREDIENT.getMetadata().metadata().get("itemName").toString().toLowerCase();
                    return TOKENS.length == 1
                            ? ITEM_NAME.contains(TOKENS[0])
                            : ITEM_NAME.contains(TOKENS[0]) && ITEM_NAME.contains(TOKENS[1]);
                })
                .collect(Collectors.toList());
    }
}
