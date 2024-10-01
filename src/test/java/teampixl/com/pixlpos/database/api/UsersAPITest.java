package teampixl.com.pixlpos.database.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.database.api.util.StatusCode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsersAPITest {

    private UsersAPI usersAPI;
    private DataStore dataStore;

    @BeforeEach
    void setUp() {
        dataStore = DataStore.getInstance();
        usersAPI = UsersAPI.getInstance();
    }

    @Test
    void testValidateUsersByUsername() {
        assertEquals(StatusCode.SUCCESS, usersAPI.validateUsersByUsername("validUsername"));
        assertEquals(StatusCode.USERNAME_TOO_SHORT, usersAPI.validateUsersByUsername("abc"));
        assertEquals(StatusCode.USERNAME_TOO_LONG, usersAPI.validateUsersByUsername("a".repeat(21)));
        assertEquals(StatusCode.USERNAME_CONTAINS_SPACES, usersAPI.validateUsersByUsername("user name"));
        assertEquals(StatusCode.USERNAME_INVALID_CHARACTERS, usersAPI.validateUsersByUsername("user@name"));
        assertEquals(StatusCode.USERNAME_ONLY_DIGITS, usersAPI.validateUsersByUsername("123456"));
    }

    @Test
    void testValidateUsersByEmail() {
        assertEquals(StatusCode.SUCCESS, usersAPI.validateUsersByEmailAddress("email@example.com"));
        assertEquals(StatusCode.EMAIL_INVALID_FORMAT, usersAPI.validateUsersByEmailAddress("emailexample.com"));
        assertEquals(StatusCode.EMAIL_CONTAINS_SPACES, usersAPI.validateUsersByEmailAddress("email @example"));
    }

    @Test
    void testValidateUsersByFirstName() {
        assertEquals(StatusCode.SUCCESS, usersAPI.validateUsersByFirstName("John"));
        assertEquals(StatusCode.INVALID_FIRST_NAME, usersAPI.validateUsersByFirstName(null));
        assertEquals(StatusCode.INVALID_FIRST_NAME, usersAPI.validateUsersByFirstName(""));
    }

    @Test
    void testValidateUsersByLastName() {
        assertEquals(StatusCode.SUCCESS, usersAPI.validateUsersByLastName("Doe"));
        assertEquals(StatusCode.INVALID_LAST_NAME, usersAPI.validateUsersByLastName(null));
        assertEquals(StatusCode.INVALID_LAST_NAME, usersAPI.validateUsersByLastName(""));
    }

    @Test
    void testValidateUsersByPassword() {
        assertEquals(StatusCode.SUCCESS, usersAPI.validateUsersByPassword("Password1!"));
        assertEquals(StatusCode.PASSWORD_TOO_SHORT, usersAPI.validateUsersByPassword("Pass1!"));
        assertEquals(StatusCode.PASSWORD_TOO_LONG, usersAPI.validateUsersByPassword("P".repeat(21)));
        assertEquals(StatusCode.PASSWORD_NO_DIGITS, usersAPI.validateUsersByPassword("Password!"));
        assertEquals(StatusCode.PASSWORD_NO_UPPERCASE, usersAPI.validateUsersByPassword("password1!"));
        assertEquals(StatusCode.PASSWORD_NO_LOWERCASE, usersAPI.validateUsersByPassword("PASSWORD1!"));
        assertEquals(StatusCode.PASSWORD_NO_SPECIAL_CHAR, usersAPI.validateUsersByPassword("Password1"));
    }

    @Test
    void testValidateUsersByRole() {
        assertEquals(StatusCode.SUCCESS, usersAPI.validateUsersByRole(Users.UserRole.ADMIN));
        assertEquals(StatusCode.INVALID_USER_ROLE, usersAPI.validateUsersByRole(null));
    }

    @Test
    void testValidateUsersByAdditionalInfo() {
        assertEquals(StatusCode.SUCCESS, usersAPI.validateUsersByAdditionalInfo("additionalInfo"));
        assertEquals(StatusCode.INVALID_USER_ADDITIONAL_INFO, usersAPI.validateUsersByAdditionalInfo(null));
    }

    @Test
    void testPostUsers() {
        List<StatusCode> statusCodes = usersAPI.postUsers("John", "Doe", "johndoe", "Password1!", "johndoe@example.com", Users.UserRole.ADMIN);
        assertTrue(statusCodes.contains(StatusCode.SUCCESS));
    }

    @Test
    void testPostUsersAdditionalInfo() {
        List<StatusCode> statusCodes = usersAPI.postUsers("John", "Doe", "johndoe", "Password1!", "johndoe@example.com", Users.UserRole.ADMIN, "additionalInfo");
        assertTrue(statusCodes.contains(StatusCode.SUCCESS));
    }

    @Test
    public void testGetUsersByUsername() {
        usersAPI.postUsers("John", "Doe", "johndoe", "Password1!", "johndoe@example.com", Users.UserRole.ADMIN, "Some info");
        assertNotNull(usersAPI.getUsersByUsername("johndoe"));
    }

    @Test
    public void testGetUsersByEmailAddress() {
        usersAPI.postUsers("John", "Doe", "johndoe", "Password1!", "johndoe@example.com", Users.UserRole.ADMIN, "Some info");
        assertNotNull(usersAPI.getUsersByEmailAddress("johndoe@example.com"));
    }

    @Test
    public void testGetUsersFirstName() {
        usersAPI.postUsers("John", "Doe", "johndoe", "Password1!", "johndoe@example.com", Users.UserRole.ADMIN, "Some info");
        assertEquals("John", usersAPI.getUsersFirstName("johndoe"));
    }

    @Test
    public void testGetUsersLastName() {
        usersAPI.postUsers("John", "Doe", "johndoe", "Password1!", "johndoe@example.com", Users.UserRole.ADMIN, "Some info");
        assertEquals("Doe", usersAPI.getUsersLastName("johndoe"));
    }

    @Test
    public void testGetUserById() {
        usersAPI.postUsers("John", "Doe", "johndoe", "Password1!", "johndoe@example.com", Users.UserRole.ADMIN, "Some info");
        String userId = usersAPI.getUsersByUsername("johndoe");
        assertNotNull(usersAPI.getUserById(userId));
    }

    @Test
    public void testPutUsersUsername() {
        usersAPI.postUsers("John", "Doe", "johndoe", "Password1!", "johndoe@example.com", Users.UserRole.ADMIN, "Some info");
        List<StatusCode> result = usersAPI.putUsersUsername("johndoe", "newusername");
        assertTrue(result.contains(StatusCode.SUCCESS));
    }

    @Test
    public void testPutUsersEmailAddress() {
        usersAPI.postUsers("John", "Doe", "johndoe", "Password1!", "johndoe@example.com", Users.UserRole.ADMIN, "Some info");
        List<StatusCode> result = usersAPI.putUsersEmailAddress("johndoe", "newemail@example.com");
        assertTrue(result.contains(StatusCode.SUCCESS));
    }

    @Test
    public void testPutUsersFirstName() {
        usersAPI.postUsers("John", "Doe", "johndoe", "Password1!", "johndoe@example.com", Users.UserRole.ADMIN, "Some info");
        List<StatusCode> result = usersAPI.putUsersFirstName("johndoe", "Jane");
        assertTrue(result.contains(StatusCode.SUCCESS));
    }

    @Test
    public void testPutUsersLastName() {
        usersAPI.postUsers("John", "Doe", "johndoe", "Password1!", "johndoe@example.com", Users.UserRole.ADMIN, "Some info");
        List<StatusCode> result = usersAPI.putUsersLastName("johndoe", "Smith");
        assertTrue(result.contains(StatusCode.SUCCESS));
    }

    @Test
    public void testPutUsersPassword() {
        usersAPI.postUsers("John", "Doe", "johndoe", "Password1!", "johndoe@example.com", Users.UserRole.ADMIN, "Some info");
        List<StatusCode> result = usersAPI.putUsersPassword("johndoe", "NewPassword1!");
        assertTrue(result.contains(StatusCode.SUCCESS));
    }

    @Test
    public void testPutUsersRole() {
        usersAPI.postUsers("John", "Doe", "johndoe", "Password1!", "johndoe@example.com", Users.UserRole.ADMIN, "Some info");
        List<StatusCode> result = usersAPI.putUsersRole("johndoe", Users.UserRole.WAITER);
        assertTrue(result.contains(StatusCode.SUCCESS));
    }

    @Test
    public void testPutUsersStatus() {
        usersAPI.postUsers("John", "Doe", "johndoe", "Password1!", "johndoe@example.com", Users.UserRole.ADMIN, "Some info");
        List<StatusCode> result = usersAPI.putUsersStatus("johndoe", false);
        assertTrue(result.contains(StatusCode.SUCCESS));
    }

}