package teampixl.com.pixlpos.database.interfaces;

import javafx.collections.ObservableList;
import teampixl.com.pixlpos.constructs.Ingredients;
import teampixl.com.pixlpos.constructs.MenuItem;

public interface IIngredientsStore {
    ObservableList<Ingredients> getIngredients();
    void addIngredient(Ingredients ingredient);
    void updateIngredient(Ingredients ingredient);
    void removeIngredient(Ingredients ingredient);
    void updateMenuItemIngredient(MenuItem menuItem);
    void removeMenuItemIngredient(MenuItem menuItem, Ingredients ingredient);
    void addMenuItemIngredient(MenuItem menuItem, Ingredients ingredient);
}
