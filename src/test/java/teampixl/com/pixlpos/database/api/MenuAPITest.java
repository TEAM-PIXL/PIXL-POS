package teampixl.com.pixlpos.database.api;
import teampixl.com.pixlpos.models.MenuItem;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import teampixl.com.pixlpos.database.api.util.StatusCode;
public class MenuAPITest {

    private static MenuAPI menuAPI;

    @BeforeAll
    static void setUp() {
        menuAPI = MenuAPI.getInstance();
    }

    @Test
    void testValidateMenuItemByName(){
        assertEquals(StatusCode.INVALID_MENU_ITEM_NAME, menuAPI.validateMenuItemByName(""));
        assertEquals(StatusCode.INVALID_MENU_ITEM_NAME, menuAPI.validateMenuItemByName(" "));
        assertEquals(StatusCode.MENU_ITEM_NAME_TOO_SHORT, menuAPI.validateMenuItemByName("Fi"));
        assertEquals(StatusCode.MENU_ITEM_NAME_TOO_LONG, menuAPI.validateMenuItemByName("abcdefghijklmnopqrstuvwxyznowyoulnowmyabcswontyoucomecandsingwithme"));
        assertEquals(StatusCode.MENU_ITEM_NAME_CONTAINS_DIGITS, menuAPI.validateMenuItemByName("Soup12"));
        assertEquals(StatusCode.MENU_ITEM_NAME_CONTAINS_SPECIAL_CHARACTERS, menuAPI.validateMenuItemByName("Soup?"));
        assertEquals(StatusCode.SUCCESS, menuAPI.validateMenuItemByName("Soup"));
    }

}
