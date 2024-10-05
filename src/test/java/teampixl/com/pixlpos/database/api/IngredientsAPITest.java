package teampixl.com.pixlpos.database.api;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.models.Ingredients;
import teampixl.com.pixlpos.models.Stock;
import org.junit.jupiter.api.*;
import teampixl.com.pixlpos.database.DataStore;
import static org.junit.jupiter.api.Assertions.*;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import javafx.util.Pair;

import java.util.List;

public class IngredientsAPITest {
    private IngredientsAPI ingredientsAPI;
    private String testIngredientName;
    private String testIngredientNotes;

    @BeforeEach
    void setUp() {
        ingredientsAPI = IngredientsAPI.getInstance();
        testIngredientName = "Salt";
        testIngredientNotes = "Notes";

        List<StatusCode> postedStatusCodes = ingredientsAPI.postIngredient(testIngredientName, testIngredientNotes);
    }

    @AfterEach
    void tearDown() {
        DataStore.getInstance().deleteIngredient(ingredientsAPI.getIngredient(testIngredientName));
    }



}
