package teampixl.com.pixlpos.database.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teampixl.com.pixlpos.database.api.StockAPI;
import teampixl.com.pixlpos.models.Stock;

class StockAPITest {

    private static StockAPI stockAPI;
    private String testStockID;
    private Stock.StockStatus testStockStatus;
    private Stock.UnitType testUnitType;
    private double testStockQuantity;
    private boolean testOnOrderStatus;

    @BeforeEach
    void setUp() {
        stockAPI = StockAPI.getInstance();

    }

}
