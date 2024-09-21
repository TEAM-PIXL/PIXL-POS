package teampixl.com.pixlpos.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.authentication.PasswordUtils;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ALL")
class UserTest {

    private DataStore dataStore;
    private PasswordUtils passwordUtils;

    @BeforeEach
    public void setUp() {
        dataStore = DataStore.getInstance();
        dataStore.clearData();
        passwordUtils = new PasswordUtils();
    }

    @Test
    public void testRegisterUser() {
        boolean result = AuthenticationManager.register("testUser", "firstName", "lastName", "username", "email@example.com", Users.UserRole.WAITER);
        assertTrue(result, "User registration failed");
    }

    @Test
    public void testLogin() {
        AuthenticationManager.register("testUser", "firstName", "lastName", "username", "email@example.com", Users.UserRole.WAITER);
        Users user = dataStore.getUser("testUser");
        assertNotNull(user, "User retrieval failed");
        assertTrue(AuthenticationManager.login("username", user.getData().get("password").toString()), "Password verification failed");
    }

    @Test
    public void testHashPassword() {
        String hashedPassword = ("password");
        assertNotNull(passwordUtils.hashPassword(hashedPassword), "Password hashing failed");
    }

    @Test
    public void testGetData() {
        AuthenticationManager.register("testUser", "firstName", "lastName", "username", "email@example.com", Users.UserRole.WAITER);
        Users user = dataStore.getUser("testUser");
        assertNotNull(user, "User retrieval failed");
        assertEquals("firstName", user.getData().get("firstName"), "User data retrieval failed");
        assertEquals("lastName", user.getData().get("lastName"), "User data retrieval failed");
        assertEquals("username", user.getData().get("username"), "User data retrieval failed");
        assertEquals("email@example.com", user.getData().get("email"), "User data retrieval failed");
    }
}