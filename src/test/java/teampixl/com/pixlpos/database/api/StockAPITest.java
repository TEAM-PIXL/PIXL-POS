package teampixl.com.pixlpos.database.api;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teampixl.com.pixlpos.database.api.StockAPI;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.database.api.IngredientsAPI;
import teampixl.com.pixlpos.models.Stock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class StockAPITest {

    private static StockAPI stockAPI;
    private String testIngredientID;
    private String testIngredientName;
    private Stock.StockStatus testStockStatus;
    private Stock.UnitType testUnitType;
    private double testStockQuantity;
    private boolean testOnOrderStatus;
    private static int testCounter;
    private static IngredientsAPI ingredientsAPI;

    private String postIngredient(String IngredientName) {
        String IngredientID;
        List<StatusCode> StatusCodes = ingredientsAPI.postIngredient(IngredientName);
        assertTrue(StatusCodes.contains(StatusCode.SUCCESS));

        ingredientsAPI = IngredientsAPI.getInstance();

        IngredientID = ingredientsAPI.keySearch(IngredientName);
        System.out.println(IngredientID);
        assertNotEquals(null, IngredientID);

        return IngredientID;
    }

    @BeforeAll
    static void setUpAll() {
        testCounter = 0;
    }

    @BeforeEach
    void setUp() {
        ingredientsAPI = IngredientsAPI.getInstance();
        stockAPI = StockAPI.getInstance();
        testCounter++;

        testIngredientName = "testIngredientID" + testCounter;
        testStockStatus = Stock.StockStatus.INSTOCK;
        testUnitType = Stock.UnitType.KG;
        testStockQuantity = 100.0;
        testOnOrderStatus = false;

        testIngredientID = postIngredient(testIngredientName);

        List<StatusCode> StatusCodes = stockAPI.postStock(testIngredientID, testStockStatus, testUnitType, testStockQuantity, testOnOrderStatus);
        System.out.println(StatusCodes);
        assertTrue(Exceptions.isSuccessful(StatusCodes));
    }

    @AfterAll
    static void tearDown() {
        for (int i = 1; i <= testCounter; i++) {
            String ingredientName = "testIngredientID" + i;
            String ingredientID = ingredientsAPI.keySearch(ingredientName);
            assertNotEquals(null, ingredientID);
            List<StatusCode> StatusCodes = stockAPI.deleteStock(ingredientID);
            assertTrue(Exceptions.isSuccessful(StatusCodes));
            StatusCodes = ingredientsAPI.deleteIngredient(ingredientID);
            assertTrue(Exceptions.isSuccessful(StatusCodes));
        }
    }

    @Test
    void testValidStockByIngredientID() {
        StatusCode statusCode = stockAPI.validateStockByIngredientID(testIngredientID);
        assertEquals(StatusCode.SUCCESS, statusCode);
    }

    @Test
    void testValidStockByQuantity() {
        StatusCode statusCode = stockAPI.validateStockByQuantity(testStockQuantity);
        assertEquals(StatusCode.SUCCESS, statusCode);
    }

    @Test
    void testValidStockByUnitType() {
        StatusCode statusCode = stockAPI.validateStockByUnitType(testUnitType);
        assertEquals(StatusCode.SUCCESS, statusCode);
    }

    @Test
    void testValidateStockByStockStatus() {
        StatusCode statusCode = stockAPI.validateStockByStockStatus(testStockStatus);
        assertEquals(StatusCode.SUCCESS, statusCode);
    }

}
