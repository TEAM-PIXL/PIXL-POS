package teampixl.com.pixlpos.database.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teampixl.com.pixlpos.database.api.StockAPI;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.Stock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StockAPITest {

    private static StockAPI stockAPI;
    private String testStockID;
    private Stock.StockStatus testStockStatus;
    private Stock.UnitType testUnitType;
    private double testStockQuantity;
    private boolean testOnOrderStatus;
    private int testCounter;

    @BeforeEach
    void setUp() {
        stockAPI = StockAPI.getInstance();
        testCounter++;

        testStockID = "testID" + testCounter;
        testStockStatus = Stock.StockStatus.INSTOCK
        testUnitType = Stock.UnitType.KG;
        testStockQuantity = 100.0;
        testOnOrderStatus = false;

        List<StatusCode> StatusCodes = stockAPI.postStock(testStockID, testStockStatus, testUnitType, testStockQuantity, testOnOrderStatus);
        assertTrue(Exceptions.isSuccessful(StatusCodes));
    }




}
