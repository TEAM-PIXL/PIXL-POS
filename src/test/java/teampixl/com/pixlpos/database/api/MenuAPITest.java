package teampixl.com.pixlpos.database.api;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MenuAPITest {

    private static MenuAPI menuAPI;

    @BeforeAll
    static void setUp() {
        menuAPI = MenuAPI.getInstance();
    }

}
