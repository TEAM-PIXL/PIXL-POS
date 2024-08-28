package teampixl.com.pixlpos.database.interfaces;

import javafx.collections.ObservableList;
import teampixl.com.pixlpos.constructs.Ingredients;

public interface IIngredientsStore {
    ObservableList<Ingredients> getIngredients();
    void addIngredient(Ingredients ingredient);
    void updateIngredient(Ingredients ingredient);
    void removeIngredient(Ingredients ingredient);
}
