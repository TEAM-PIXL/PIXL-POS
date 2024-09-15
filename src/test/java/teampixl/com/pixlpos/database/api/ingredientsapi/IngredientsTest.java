package teampixl.com.pixlpos.database.api.ingredientsapi;

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

    @Test
    void testToString() {
        String flourString = flour.toString();
        assertTrue(flourString.contains("Flour"));
        assertTrue(flourString.contains("Organic white flour"));
    }
}
