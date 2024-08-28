package teampixl.com.pixlpos.constructs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IngredientsTest {

    private Ingredients flour;
    private Ingredients milk;
    private Ingredients eggs;

    @BeforeEach
    void setUp() {
        // Create ingredients
        flour = new Ingredients("Flour", Ingredients.StockStatus.INSTOCK, false, Ingredients.UnitType.KG, 5.0, "Organic white flour");
        milk = new Ingredients("Milk", Ingredients.StockStatus.LOWSTOCK, true, Ingredients.UnitType.L, 2.5, "Fresh cow milk");
        eggs = new Ingredients("Eggs", Ingredients.StockStatus.NOSTOCK, false, Ingredients.UnitType.QTY, 12, "Free-range eggs");
    }

    @Test
    void testIngredientCreation() {
        // Test metadata values
        assertEquals("Flour", flour.getMetadata().metadata().get("itemName"));
        assertEquals(Ingredients.StockStatus.INSTOCK, flour.getMetadata().metadata().get("stockStatus"));
        assertFalse((Boolean) flour.getMetadata().metadata().get("onOrder"));

        // Test data values
        assertEquals(Ingredients.UnitType.KG, flour.getData().get("unit"));
        assertEquals(5.0, flour.getData().get("numeral"));
        assertEquals("Organic white flour", flour.getData().get("notes"));
    }

    @Test
    void testInvalidNumeralForQTY() {
        // This should throw an exception because QTY requires an Integer numeral
        assertThrows(IllegalArgumentException.class, () -> new Ingredients("Invalid Eggs", Ingredients.StockStatus.INSTOCK, false, Ingredients.UnitType.QTY, 5.5, "Invalid numeral type"));
    }

    @Test
    void testInvalidNumeralForKG() {
        // This should throw an exception because KG requires a Double numeral
        assertThrows(IllegalArgumentException.class, () -> new Ingredients("Invalid Flour", Ingredients.StockStatus.INSTOCK, false, Ingredients.UnitType.KG, 5, "Invalid numeral type"));
    }

    @Test
    void testUpdateMetadata() {
        // Update the stock status for milk
        milk.updateMetadata("stockStatus", Ingredients.StockStatus.INSTOCK);
        assertEquals(Ingredients.StockStatus.INSTOCK, milk.getMetadata().metadata().get("stockStatus"));

        // Update the onOrder status for eggs
        eggs.updateMetadata("onOrder", true);
        assertTrue((Boolean) eggs.getMetadata().metadata().get("onOrder"));
    }

    @Test
    void testUpdateInvalidMetadata() {
        // This should throw an exception because the value type is incorrect
        assertThrows(IllegalArgumentException.class, () -> flour.updateMetadata("stockStatus", "INVALID_STATUS"));
    }

    @Test
    void testUpdateData() {
        // Update the numeral value for flour
        flour.setDataValue("numeral", 10.0);
        assertEquals(10.0, flour.getData().get("numeral"));

        // Update the unit type for eggs
        eggs.setDataValue("unit", Ingredients.UnitType.KG);  // This should pass since we are just changing the unit type
        assertEquals(Ingredients.UnitType.KG, eggs.getData().get("unit"));
    }

    @Test
    void testUpdateInvalidData() {
        // This should throw an exception because the numeral type is incorrect
        assertThrows(IllegalArgumentException.class, () -> flour.setDataValue("numeral", "InvalidType"));

        // This should throw an exception because the unit type is incorrect
        assertThrows(IllegalArgumentException.class, () -> flour.setDataValue("unit", "InvalidType"));
    }

    @Test
    void testToString() {
        // Check the string representation of the ingredient
        String flourString = flour.toString();
        assertTrue(flourString.contains("Flour"));
        assertTrue(flourString.contains("Organic white flour"));
    }
}