package teampixl.com.pixlpos.database.api;

import org.junit.jupiter.api.AfterAll;
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
    private int testCounter;
    private IngredientsAPI ingredientsAPI;

    private String postIngredient() {
        String IngredientID;
        List<StatusCode> StatusCodes = ingredientsAPI.postIngredient(testIngredientName);
        assertTrue(Exceptions.isSuccessful(StatusCodes));

        IngredientID = ingredientsAPI.keySearch(testIngredientName);
        assertNotEquals(null, IngredientID);

        return IngredientID;
    }

    @BeforeEach
    void setUp() {
        stockAPI = StockAPI.getInstance();
        testCounter++;

        testIngredientName = "testIngredientID" + testCounter;
        testStockStatus = Stock.StockStatus.INSTOCK;
        testUnitType = Stock.UnitType.KG;
        testStockQuantity = 100.0;
        testOnOrderStatus = false;

        testIngredientID = postIngredient();

        List<StatusCode> StatusCodes = stockAPI.postStock(testIngredientID, testStockStatus, testUnitType, testStockQuantity, testOnOrderStatus);
        assertTrue(Exceptions.isSuccessful(StatusCodes));
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
