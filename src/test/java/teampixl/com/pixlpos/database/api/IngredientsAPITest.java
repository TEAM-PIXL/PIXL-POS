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

    @Test
    void testValidateIngredientByName(){
        assertEquals(StatusCode.INGREDIENT_NAME_EMPTY, ingredientsAPI.validateIngredientByName(""));
        String LongName = "A".repeat(51);
        assertEquals(StatusCode.INGREDIENT_NAME_TOO_LONG, ingredientsAPI.validateIngredientByName(LongName));
        assertEquals(StatusCode.INGREDIENT_NAME_TOO_SHORT, ingredientsAPI.validateIngredientByName("A"));
        assertEquals(StatusCode.INGREDIENT_NAME_CONTAINS_DIGITS, ingredientsAPI.validateIngredientByName("Salt1"));
        assertEquals(StatusCode.INGREDIENT_NAME_CONTAINS_SPECIAL_CHARACTERS, ingredientsAPI.validateIngredientByName("Soup?"));
        assertEquals(StatusCode.INGREDIENT_NAME_TAKEN, ingredientsAPI.validateIngredientByName("Salt"));
        assertEquals(StatusCode.SUCCESS, ingredientsAPI.validateIngredientByName("Pepper"));
    }

    @Test
    void testValidateIngredientByNotes(){
        String LongerName = "A".repeat(501);
        assertEquals(StatusCode.INGREDIENT_NOTES_TOO_LONG, ingredientsAPI.validateIngredientByNotes(LongerName));
        assertEquals(StatusCode.SUCCESS, ingredientsAPI.validateIngredientByNotes("Super Cool Notes."));
    }
}
