package teampixl.com.pixlpos.database.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.Order;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        userStack.setCurrentUser("admin");

    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        Thread.sleep(100);
    }

    @Test
    public void testValidateOrderById() {
        assertEquals(StatusCode.SUCCESS, orderAPI.validateOrderById("0000-0000-0000-0000"));
    }

    @Test
    public void testValidateOrderByIdNull() {
        assertEquals(StatusCode.INVALID_ORDER_ID, orderAPI.validateOrderById(null));
    }

    @Test
    public void testValidateOrderByIdEmpty() {
        assertEquals(StatusCode.INVALID_ORDER_ID, orderAPI.validateOrderById(""));
    }

    @Test
    public void testValidateOrderByIdInvalid() {
        assertEquals(StatusCode.INVALID_ORDER_ID, orderAPI.validateOrderById("   "));
    }

    @Test
    public void testValidateOrderByNumber() {
        assertEquals(StatusCode.SUCCESS, orderAPI.validateOrderByNumber(1));
    }

    @Test
    public void testValidateOrderByNumberNegative() {
        assertEquals(StatusCode.INVALID_ORDER_NUMBER, orderAPI.validateOrderByNumber(-1));
    }

    @Test
    public void testValidateOrderByNumberNull() {
        assertEquals(StatusCode.INVALID_ORDER_NUMBER, orderAPI.validateOrderByNumber(null));
    }

    @Test
    public void testValidateOrderByUserIdNull() {
        assertEquals(StatusCode.INVALID_USER_ID, orderAPI.validateOrderByUserId(null));
    }

    @Test
    public void testValidateOrderByUserIdEmpty() {
        assertEquals(StatusCode.INVALID_USER_ID, orderAPI.validateOrderByUserId(""));
    }

    @Test
    public void testValidateOrderByUserIdInvalid() {
        assertEquals(StatusCode.INVALID_USER_ID, orderAPI.validateOrderByUserId("   "));
    }

    @Test
    public void testValidateOrderByUserId() {
        assertEquals(StatusCode.SUCCESS, orderAPI.validateOrderByUserId(usersAPI.keySearch("admin")));
    }

    @Test
    public void testValidateOrderByStatus() {
        assertEquals(StatusCode.SUCCESS, orderAPI.validateOrderByStatus(Order.OrderStatus.PENDING));
    }

    @Test
    public void testValidateOrderByTableNumber() {
        assertEquals(StatusCode.SUCCESS, orderAPI.validateOrderByTableNumber(1));
    }

    @Test
    public void testValidateOrderByTableNumberNegative() {
        assertEquals(StatusCode.INVALID_TABLE_NUMBER, orderAPI.validateOrderByTableNumber(-1));
    }

    @Test
    public void testValidateOrderByTableNumberNull() {
        assertEquals(StatusCode.INVALID_TABLE_NUMBER, orderAPI.validateOrderByTableNumber(null));
    }

    @Test
    public void testValidateOrderByCustomers() {
        assertEquals(StatusCode.SUCCESS, orderAPI.validateOrderByCustomers(1));
    }

    @Test
    public void testValidateOrderByCustomersNegative() {
        assertEquals(StatusCode.INVALID_CUSTOMERS, orderAPI.validateOrderByCustomers(-1));
    }

    @Test
    public void testValidateOrderByCustomersNull() {
        assertEquals(StatusCode.INVALID_CUSTOMERS, orderAPI.validateOrderByCustomers(null));
    }

    @Test
    public void testValidateOrderByCustomersZero() {
        assertEquals(StatusCode.INVALID_CUSTOMERS, orderAPI.validateOrderByCustomers(0));
    }

    @Test
    public void testValidateOrderBySpecialRequests() {
        assertEquals(StatusCode.SUCCESS, orderAPI.validateOrderBySpecialRequests("No salt"));
    }
    
    @Test
    public void validateOrderByItems() {
    }

}
