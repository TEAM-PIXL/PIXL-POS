package teampixl.com.pixlpos.database.interfaces;

import javafx.collections.ObservableList;
import teampixl.com.pixlpos.models.Ingredients;

public interface IIngredientsStore {
    ObservableList<Ingredients> readIngredients();
    Ingredients getIngredient(String itemName);
    void createIngredient(Ingredients ingredient);
    void updateIngredient(Ingredients ingredient);
    void deleteIngredient(Ingredients ingredient);
}
