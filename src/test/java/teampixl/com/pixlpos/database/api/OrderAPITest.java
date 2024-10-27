package teampixl.com.pixlpos.database.api;

import org.junit.jupiter.api.BeforeEach;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.util.StatusCode;

import java.util.List;

public class OrderAPITest {

    public static final OrderAPI orderAPI = OrderAPI.getInstance();
    public static final MenuAPI menuAPI = MenuAPI.getInstance();
    public static final UsersAPI usersAPI = UsersAPI.getInstance();
    public static final UserStack userStack = UserStack.getInstance();

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
    public void setUp() {
        while (
                DataStore.getInstance() == null ||
                OrderAPI.getInstance() == null ||
                MenuAPI.getInstance() == null ||
                UsersAPI.getInstance() == null ||
                UserStack.getInstance() == null
        ) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
