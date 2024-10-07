//package teampixl.com.pixlpos.models;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class StockTest {
//
//    private Ingredients tomatoSauce;
//    private Stock stock;
//
//    @BeforeEach
//    void setUp() {
//        tomatoSauce = new Ingredients("Tomato Sauce", "Organic tomato sauce");
//        stock = new Stock(tomatoSauce, Stock.StockStatus.INSTOCK, Stock.UnitType.KG, 5.0, false);
//    }
//
//    @Test
//    void testStockCreation() {
//        assertNotNull(stock.getMetadata().metadata().get("stock_id"));
//        assertEquals(tomatoSauce.getMetadata().metadata().get("ingredient_id"), stock.getMetadata().metadata().get("ingredient_id"));
//        assertEquals(Stock.StockStatus.INSTOCK, stock.getMetadata().metadata().get("stockStatus"));
//        assertFalse((Boolean) stock.getMetadata().metadata().get("onOrder"));
//        assertEquals(Stock.UnitType.KG, stock.getData().get("unit"));
//        assertEquals(5.0, stock.getData().get("numeral"));
//    }
//
//    @Test
//    void testInvalidNumeralForQTY() {
//        assertThrows(IllegalArgumentException.class, () -> new Stock(tomatoSauce, Stock.StockStatus.INSTOCK, Stock.UnitType.QTY, 5.5, false));
//    }
//
//    @Test
//    void testInvalidNumeralForKG() {
//        assertThrows(IllegalArgumentException.class, () -> new Stock(tomatoSauce, Stock.StockStatus.INSTOCK, Stock.UnitType.KG, 5, false));
//    }
//
//    @Test
//    void testUpdateMetadata() {
//        stock.updateMetadata("stockStatus", Stock.StockStatus.LOWSTOCK);
//        assertEquals(Stock.StockStatus.LOWSTOCK, stock.getMetadata().metadata().get("stockStatus"));
//        stock.updateMetadata("onOrder", true);
//        assertTrue((Boolean) stock.getMetadata().metadata().get("onOrder"));
//        assertNotNull(stock.getMetadata().metadata().get("lastUpdated"));
//    }
//
//    @Test
//    void testUpdateData() {
//        stock.setDataValue("numeral", 7.0);
//        assertEquals(7.0, stock.getData().get("numeral"));
//        stock.setDataValue("unit", Stock.UnitType.L);
//        assertEquals(Stock.UnitType.L, stock.getData().get("unit"));
//    }
//
//    @Test
//    void testToString() {
//        String stockString = stock.toString();
//        System.out.println("Stock toString output: " + stockString);
//        assertTrue(stockString.contains("stock_id"), "The stock_id should be present in the toString output.");
//        assertTrue(stockString.contains("INSTOCK"), "The stock status should be present in the toString output.");
//    }
//}