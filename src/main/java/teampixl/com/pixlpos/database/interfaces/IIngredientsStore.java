package teampixl.com.pixlpos.database.interfaces;

import javafx.collections.ObservableList;
import teampixl.com.pixlpos.models.Ingredients;

public interface IIngredientsStore {
    ObservableList<Ingredients> getIngredients();
    Ingredients getIngredient(String itemName);
    void addIngredient(Ingredients ingredient);
    void updateIngredient(Ingredients ingredient);
    void removeIngredient(Ingredients ingredient);
}
