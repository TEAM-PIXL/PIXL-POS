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
        AuthenticationManager.register("firstName", "lastName", "testUser", "password", "email@example.com", Users.UserRole.WAITER);
        Users user = dataStore.getUser("testUser");
        assertNotNull(user, "User retrieval failed");
        assertTrue(passwordUtils.verifyPassword("password", user.getData().get("password").toString()), "Password verification failed");
    }

    @Test
    public void testHashPassword() {
        String hashedPassword = ("password");
        assertNotNull(passwordUtils.hashPassword(hashedPassword), "Password hashing failed");
    }

    @Test
    public void testGetData() {
        AuthenticationManager.register("firstName", "lastName", "testUser", "password", "email@example.com", Users.UserRole.WAITER);
        Users user = dataStore.getUser("testUser");
        assertNotNull(user, "User retrieval failed");
        assertEquals("firstName", user.getMetadata().metadata().get("first_name"), "User data retrieval failed");
        assertEquals("lastName", user.getMetadata().metadata().get("last_name"), "User data retrieval failed");
        assertEquals("testUser", user.getMetadata().metadata().get("username"), "User data retrieval failed");
        assertEquals("email@example.com", user.getData().get("email"), "User data retrieval failed");
    }

    @Test
    public void testGetMetadata() {
        AuthenticationManager.register("firstName", "lastName", "testUser", "password", "email@example.com", Users.UserRole.WAITER);
        Users user = dataStore.getUser("testUser");
        assertNotNull(user, "User retrieval failed");
        assertEquals(Users.UserRole.WAITER, user.getMetadata().metadata().get("role"), "User metadata retrieval failed");
    }

    @Test
    public void testGetUsers() {
        AuthenticationManager.register("firstName1", "lastName1", "testUser1", "password1", "email1@example.com", Users.UserRole.WAITER);
        AuthenticationManager.register("firstName2", "lastName2", "testUser2", "password2", "email2@example.com", Users.UserRole.COOK);
        assertEquals(2, dataStore.getUsers().size(), "User list retrieval failed");
    }

    @Test
    public void testGetUser() {
        AuthenticationManager.register("firstName", "lastName", "testUser", "password", "email@example.com", Users.UserRole.WAITER);
        Users user = dataStore.getUser("testUser");
        assertNotNull(user, "User retrieval failed");
    }

    @Test
    public void testSetDataValue() {
        AuthenticationManager.register("firstName", "lastName", "testUser", "password", "email@example.com", Users.UserRole.WAITER);
        Users user = dataStore.getUser("testUser");
        assertNotNull(user, "User retrieval failed");
        user.setDataValue("additional_info", "Test Info");
        dataStore.updateUser(user);
        assertEquals("Test Info", dataStore.getUser("testUser").getData().get("additional_info"), "User data update failed");
    }

    @Test
    public void testUpdateMetadata() {
        AuthenticationManager.register("firstName", "lastName", "testUser", "password", "email@example.com", Users.UserRole.WAITER);
        Users user = dataStore.getUser("testUser");
        assertNotNull(user, "User retrieval failed");
        user.updateMetadata("role", Users.UserRole.ADMIN);
        dataStore.updateUser(user);
        assertEquals(Users.UserRole.ADMIN, dataStore.getUser("testUser").getMetadata().metadata().get("role"), "User metadata update failed");
    }

    @Test
    public void testUpdateUser() {
        AuthenticationManager.register("firstName", "lastName", "testUser", "password", "email@example.com", Users.UserRole.WAITER);
        Users user = dataStore.getUser("testUser");
        assertNotNull(user, "User retrieval failed");
        user.setDataValue("email", "newemail@example.com");
        dataStore.updateUser(user);
        assertEquals("newemail@example.com", dataStore.getUser("testUser").getData().get("email"), "User update failed");
    }

    @Test
    public void testUpdateUserPassword() {
        AuthenticationManager.register("firstName", "lastName", "testUser", "password", "email@example.com", Users.UserRole.WAITER);
        Users user = dataStore.getUser("testUser");
        assertNotNull(user, "User retrieval failed");
        dataStore.updateUserPassword(user, "newPassword");
        assertTrue(passwordUtils.verifyPassword("newPassword", user.getData().get("password").toString()), "User password update failed");
    }

    @Test
    public void testRemoveUser() {
        AuthenticationManager.register("firstName", "lastName", "testUser", "password", "email@example.com", Users.UserRole.WAITER);
        Users user = dataStore.getUser("testUser");
        assertNotNull(user, "User retrieval failed");
        dataStore.removeUser(user);
        assertNull(dataStore.getUser("testUser"), "User removal failed");
    }
}