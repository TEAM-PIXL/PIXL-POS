package teampixl.com.pixlpos.database.interfaces;

import javafx.collections.ObservableList;
import teampixl.com.pixlpos.models.Ingredients;

/**
 * Interface for IngredientsStore
 */
public interface IIngredientsStore {

    /**
     * Read all ingredients from the database
     * @return ObservableList of Ingredients
     */
    ObservableList<Ingredients> readIngredients();

    /**
     * Get an ingredient by its name
     * @param itemName Name of the ingredient
     * @return Ingredient
     */
    Ingredients getIngredient(String itemName);

    /**
     * Create a new ingredient
     * @param ingredient Ingredient to be created
     */
    void createIngredient(Ingredients ingredient);

    /**
     * Update an existing ingredient
     * @param ingredient Ingredient to be updated
     */
    void updateIngredient(Ingredients ingredient);

    /**
     * Delete an existing ingredient
     * @param ingredient Ingredient to be deleted
     */
    void deleteIngredient(Ingredients ingredient);
}
