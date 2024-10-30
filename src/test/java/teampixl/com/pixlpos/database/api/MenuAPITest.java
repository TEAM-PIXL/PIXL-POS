package teampixl.com.pixlpos.database.api;
import teampixl.com.pixlpos.models.MenuItem;
import org.junit.jupiter.api.*;
import teampixl.com.pixlpos.database.DataStore;
import static org.junit.jupiter.api.Assertions.*;
import static teampixl.com.pixlpos.database.api.util.Exceptions.isSuccessful;

import teampixl.com.pixlpos.database.api.util.StatusCode;

import java.util.List;

public class MenuAPITest {

    private static final MenuAPI menuAPI = MenuAPI.getInstance();
    private static String testMenuItemName;

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
        while (DataStore.getInstance() == null || MenuAPI.getInstance() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        testMenuItemName = "Dog Food";
        double testMenuItemPrice = 23.00;
        MenuItem.ItemType testMenuItemType = MenuItem.ItemType.MAIN;
        String testMenuItemDescription = "Cool Description";
        String testMenuItemNotes = "Notes";
        boolean testMenuItemActive = false;
        MenuItem.DietaryRequirement testMenuItemDiet = MenuItem.DietaryRequirement.VEGAN;

        List<StatusCode> result = menuAPI.postMenuItem(testMenuItemName, testMenuItemPrice, testMenuItemActive,
                testMenuItemType, testMenuItemDescription, testMenuItemNotes, testMenuItemDiet);
        awaitResponse(result, () -> assertTrue(isSuccessful(result)));
    }
    @AfterEach
    void tearDown() throws InterruptedException {
        Thread.sleep(100);
        DataStore.getInstance().deleteMenuItem(menuAPI.getMenuItem(testMenuItemName));
    }

    @Test
    void testValidateMenuItemByName(){
        assertEquals(StatusCode.SUCCESS, menuAPI.validateMenuItemByName("Soup"));
    }

    @Test
    void testValidateMenuItemByNameEmpty(){
        assertEquals(StatusCode.INVALID_MENU_ITEM_NAME, menuAPI.validateMenuItemByName(""));
    }


    @Test
    void testValidateMenuItemNameAllSpaces(){
        assertEquals(StatusCode.INVALID_MENU_ITEM_NAME, menuAPI.validateMenuItemByName(" "));
    }

    @Test
    void testValidateMenuItemNameTooShort(){
        assertEquals(StatusCode.MENU_ITEM_NAME_TOO_SHORT, menuAPI.validateMenuItemByName("Fi"));
    }

    @Test
    void testValidateMenuItemNameTooLong(){
        String LongName = "A".repeat(51);
        assertEquals(StatusCode.MENU_ITEM_NAME_TOO_LONG, menuAPI.validateMenuItemByName(LongName));
    }

    @Test
    void testValidateMenuItemNameContainsDigits(){
        assertEquals(StatusCode.MENU_ITEM_NAME_CONTAINS_DIGITS, menuAPI.validateMenuItemByName("Soup12"));
    }

    @Test
    void testValidateMenuItemNameContainsSpecialCharacters(){
        assertEquals(StatusCode.MENU_ITEM_NAME_CONTAINS_SPECIAL_CHARACTERS, menuAPI.validateMenuItemByName("Soup?"));
    }

    @Test
    void testValidateMenuItemByPrice(){
        assertEquals(StatusCode.SUCCESS, menuAPI.validateMenuItemByPrice(1.00));
    }

    @Test
    void testValidateMenuItemPriceIsNegative(){
        assertEquals(StatusCode.MENU_ITEM_PRICE_NEGATIVE, menuAPI.validateMenuItemByPrice(-1.00));
    }

    @Test
    void testValidateMenuItemByDescription(){
        assertEquals(StatusCode.SUCCESS, menuAPI.validateMenuItemByDescription("It's really tasty."));
    }

    @Test
    void testValidateMenuItemByDescriptionEmpty(){
        assertEquals(StatusCode.INVALID_MENU_ITEM_DESCRIPTION, menuAPI.validateMenuItemByDescription(""));
    }

    @Test
    void testValidateMenuItemByDescriptionAllSpaces(){
        assertEquals(StatusCode.INVALID_MENU_ITEM_DESCRIPTION, menuAPI.validateMenuItemByDescription(" "));
    }

    @Test
    void testValidateMenuItemByDescriptionTooLong(){
        String LongName = "A".repeat(501);
        assertEquals(StatusCode.MENU_ITEM_DESCRIPTION_TOO_LONG, menuAPI.validateMenuItemByDescription(LongName));
    }

    @Test
    void testValidateMenuItemByNotes(){
        assertEquals(StatusCode.SUCCESS, menuAPI.validateMenuItemByNotes("Super Cool Notes."));
    }

    @Test
    void testValidateMenuItemByNotesEmpty(){
        assertEquals(StatusCode.INVALID_MENU_ITEM_NOTES, menuAPI.validateMenuItemByNotes(""));
    }

    @Test
    void testValidateMenuItemByNotesAllSpaces(){
        assertEquals(StatusCode.INVALID_MENU_ITEM_NOTES, menuAPI.validateMenuItemByNotes(" "));
    }

    @Test
    void testValidateMenuItemByNotesTooLong(){
        String LongName = "A".repeat(501);
        assertEquals(StatusCode.MENU_ITEM_NOTES_TOO_LONG, menuAPI.validateMenuItemByNotes(LongName));
    }

    @Test
    void testGetMenuItemByName(){
        assertNotNull(menuAPI.getMenuItem(testMenuItemName));
    }

    @Test
    void testGetMenuItemIDByName(){
        assertNotNull(menuAPI.keySearch(testMenuItemName));
    }

    @Test
    void testPutMenuItemName(){
        List<StatusCode> result = menuAPI.putMenuItemName(testMenuItemName, "Lithium Batteries");
        awaitResponse(result, () -> assertTrue(isSuccessful(result)));
    }

    @Test
    void testPutMenuItemPrice(){
        List<StatusCode> result = menuAPI.putMenuItemPrice(testMenuItemName, 12.00);
        awaitResponse(result, () -> assertTrue(isSuccessful(result)));
    }

    @Test
    void testPutMenuItemType(){
        List<StatusCode> result = menuAPI.putMenuItemItemType(testMenuItemName, MenuItem.ItemType.DESSERT);
        awaitResponse(result, () -> assertTrue(isSuccessful(result)));
    }

    @Test
    void testPutMenuItemDescription(){
        List<StatusCode> result = menuAPI.putMenuItemDescription(testMenuItemName, "New Description");
        awaitResponse(result, () -> assertTrue(isSuccessful(result)));
    }

    @Test
    void testPutMenuItemNotes(){
        List<StatusCode> result = menuAPI.putMenuItemNotes(testMenuItemName, "New Notes");
        awaitResponse(result, () -> assertTrue(isSuccessful(result)));
    }

    @Test
    void testSearchMenuItem(){
        List<MenuItem> menuItems = menuAPI.searchMenuItem(testMenuItemName);
        assertNotNull(menuItems);
    }
}
