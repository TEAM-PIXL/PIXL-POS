package teampixl.com.pixlpos.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IngredientsTest {

    private Ingredients flour;

    @BeforeEach
    void setUp() {
        flour = new Ingredients("Flour", "Organic white flour");
    }

    @Test
    void testIngredientCreation() {
        assertEquals("Flour", flour.getMetadata().metadata().get("itemName"));
        assertEquals("Organic white flour", flour.getData().get("notes"));
    }

//    @Test
//    void testToString() {
//        String flourString = flour.toString();
//        assertTrue(flourString.contains("Flour"));
//        assertTrue(flourString.contains("Organic white flour"));
//    }

    @Test
    void testGetMetadata() {
        assertEquals("Flour", flour.getMetadata().metadata().get("itemName"));
    }

    @Test
    void testGetData() {
        assertEquals("Organic white flour", flour.getData().get("notes"));
    }
    @Test
    void testSetDataValue() {
        flour.setDataValue("notes", "Organic black flour");
        assertEquals("Organic black flour", flour.getData().get("notes"));
        flour.setDataValue("notes", "Organic white flour");
    }
}
