package teampixl.com.pixlpos.database.api;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.models.MenuItem;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class MenuAPITest {

    private static MenuAPI menuAPI;
    private static String testMenuItemName;
    private static MenuItem.ItemType testMenuItemType;
    private static String testMenuItemDescription;
    private static double testMenuItemPrice;
    private static String testMenuItemNotes;
    private static boolean testMenuItemActive;
    private static MenuItem.DietaryRequirement testMenuItemDiet;


    @BeforeAll
    static void setUp() {
        menuAPI = MenuAPI.getInstance();
        testMenuItemName = "Dog Food";
        testMenuItemPrice = 23.00;
        testMenuItemType = MenuItem.ItemType.MAIN;
        testMenuItemDescription = "Cool Description";
        testMenuItemNotes = "Notes";
        testMenuItemActive = false;
        testMenuItemDiet = MenuItem.DietaryRequirement.VEGAN;

        List<StatusCode> postedStatusCodes = menuAPI.postMenuItem(testMenuItemName,testMenuItemPrice,testMenuItemActive,
                testMenuItemType,testMenuItemDescription,testMenuItemNotes,testMenuItemDiet);
    }

    @Test
    void testValidateMenuItemByName(){
        assertEquals(StatusCode.INVALID_MENU_ITEM_NAME, menuAPI.validateMenuItemByName(""));
        assertEquals(StatusCode.INVALID_MENU_ITEM_NAME, menuAPI.validateMenuItemByName(" "));
        assertEquals(StatusCode.MENU_ITEM_NAME_TOO_SHORT, menuAPI.validateMenuItemByName("Fi"));
        String LongName = "A".repeat(51);
        assertEquals(StatusCode.MENU_ITEM_NAME_TOO_LONG, menuAPI.validateMenuItemByName(LongName));
        assertEquals(StatusCode.MENU_ITEM_NAME_CONTAINS_DIGITS, menuAPI.validateMenuItemByName("Soup12"));
        assertEquals(StatusCode.MENU_ITEM_NAME_CONTAINS_SPECIAL_CHARACTERS, menuAPI.validateMenuItemByName("Soup?"));
        assertEquals(StatusCode.SUCCESS, menuAPI.validateMenuItemByName("Soup"));
    }

    @Test
    void testValidateMenuItemByPrice(){
        assertEquals(StatusCode.MENU_ITEM_PRICE_NEGATIVE, menuAPI.validateMenuItemByPrice(-1.00));
        assertEquals(StatusCode.SUCCESS, menuAPI.validateMenuItemByPrice(1.00));
    }

    @Test
    void testValidateMenuItemByDescription(){
        String LongerName = "A".repeat(501);
        assertEquals(StatusCode.MENU_ITEM_DESCRIPTION_TOO_LONG, menuAPI.validateMenuItemByDescription(LongerName));
        assertEquals(StatusCode.SUCCESS, menuAPI.validateMenuItemByDescription("It's really tasty."));
    }

    @Test
    void testValidateMenuItemByNotes(){
        String LongerName = "A".repeat(501);
        assertEquals(StatusCode.MENU_ITEM_DESCRIPTION_TOO_LONG, menuAPI.validateMenuItemByNotes(LongerName));
        assertEquals(StatusCode.SUCCESS, menuAPI.validateMenuItemByNotes("Super Cool Notes."));
    }

    @Test
    void testGetMenuItem(){
        assertNotNull(menuAPI.getMenuItem(testMenuItemName));
    }

    @Test
    void testPutMenuItemName(){
        assertTrue(Exceptions.isSuccessful(
                menuAPI.putMenuItemName(testMenuItemName, "Super Dog Food")));
    }


}
