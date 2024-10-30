package teampixl.com.pixlpos.database.api;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.models.Ingredients;
import org.junit.jupiter.api.*;
import teampixl.com.pixlpos.database.DataStore;
import static org.junit.jupiter.api.Assertions.*;
import teampixl.com.pixlpos.database.api.util.StatusCode;

import java.util.List;

public class IngredientsAPITest {
    private IngredientsAPI ingredientsAPI;
    private String testIngredientName;

    private void awaitResponse(List<StatusCode> response, Runnable assertion) {
        while (response == null) {
            try {
                Thread.sleep(100);
                assertion.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @BeforeEach
    void setUp() {
        while (DataStore.getInstance() == null || IngredientsAPI.getInstance() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ingredientsAPI = IngredientsAPI.getInstance();
        testIngredientName = "Salt";
        String testIngredientNotes = "Notes";
        List<StatusCode> result = ingredientsAPI.postIngredient(testIngredientName, testIngredientNotes);
        awaitResponse(result, () -> assertTrue(Exceptions.isSuccessful(result)));
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        List<StatusCode> result = ingredientsAPI.deleteIngredient(testIngredientName);
        awaitResponse(result, () -> assertTrue(Exceptions.isSuccessful(result)));
        Thread.sleep(100);
    }

    @Test
    void testValidateIngredientByName(){
        assertEquals(StatusCode.SUCCESS, ingredientsAPI.validateIngredientByName("Sauce"));
    }

    @Test
    void testValidateIngredientByNameTooLong(){
        String LongName = "A".repeat(51);
        assertEquals(StatusCode.INGREDIENT_NAME_TOO_LONG, ingredientsAPI.validateIngredientByName(LongName));
    }

    @Test
    void testValidateIngredientByNameTooShort(){
        assertEquals(StatusCode.INGREDIENT_NAME_TOO_SHORT, ingredientsAPI.validateIngredientByName("A"));
    }

    @Test
    void testValidateIngredientByNameContainsDigits(){
        assertEquals(StatusCode.INGREDIENT_NAME_CONTAINS_DIGITS, ingredientsAPI.validateIngredientByName("Salt1"));
    }

    @Test
    void testValidateIngredientByNameContainsSpecialCharacters(){
        assertEquals(StatusCode.INGREDIENT_NAME_CONTAINS_SPECIAL_CHARACTERS, ingredientsAPI.validateIngredientByName("Soup?"));
    }

    @Test
    void testValidateIngredientByNameTaken(){
        assertEquals(StatusCode.INGREDIENT_NAME_TAKEN, ingredientsAPI.validateIngredientByName("Salt"));
    }

    @Test
    void testValidateIngredientByNotes(){
        assertEquals(StatusCode.SUCCESS, ingredientsAPI.validateIngredientByNotes("Super Cool Notes."));
    }

    @Test
    void testValidateIngredientByNotesTooLong(){
        String LongerName = "A".repeat(501);
        assertEquals(StatusCode.INGREDIENT_NOTES_TOO_LONG, ingredientsAPI.validateIngredientByNotes(LongerName));
    }

    @Test
    void testGetIngredientByName(){
        assertNotNull(ingredientsAPI.getIngredient(testIngredientName));
    }

    @Test
    void testPostIngredient(){
        List<StatusCode> result = ingredientsAPI.postIngredient("Pepper", "Notes");
        awaitResponse(result, () -> assertTrue(Exceptions.isSuccessful(result)));
    }

    @Test
    void testPutIngredientName(){
        List<StatusCode> result = ingredientsAPI.putIngredientName(testIngredientName, "Pepper");
        awaitResponse(result, () -> assertTrue(Exceptions.isSuccessful(result)));
    }

    @Test
    void testPutIngredientNotes(){
        List<StatusCode> result = ingredientsAPI.putIngredientNotes(testIngredientName, "Notes");
        awaitResponse(result, () -> assertTrue(Exceptions.isSuccessful(result)));
    }

    @Test
    void testDeleteIngredient(){
        List<StatusCode> result = ingredientsAPI.deleteIngredient(testIngredientName);
        awaitResponse(result, () -> assertTrue(Exceptions.isSuccessful(result)));
    }

    @Test
    void testSearchIngredient(){
        List<Ingredients> ingredients = ingredientsAPI.searchIngredients(testIngredientName);
        assertNotNull(ingredients);
    }

}
