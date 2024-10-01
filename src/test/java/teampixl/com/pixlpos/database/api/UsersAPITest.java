package teampixl.com.pixlpos.database.api;

import org.junit.jupiter.api.*;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.database.api.util.StatusCode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsersAPITest {

    private static UsersAPI usersAPI;
    private String testUsername;
    private static int postCounter = 1;

    @BeforeEach
    void setUp() {
        usersAPI = UsersAPI.getInstance();
        testUsername = "johndoe" + postCounter;
    }

    @AfterEach
    void tearDown() {
        postCounter++;
        System.out.println("Post Counter: " + postCounter);
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
        List<StatusCode> statusCodes = usersAPI.postUsers("John" + postCounter, "Doe" + postCounter, testUsername, "Password1!" + postCounter, "johndoe@example.com" + postCounter, Users.UserRole.ADMIN, "Some info" + postCounter);
        assertTrue(statusCodes.contains(StatusCode.SUCCESS));
    }

    @Test
    void testPostUsersAdditionalInfo() {
        List<StatusCode> statusCodes = usersAPI.postUsers("John" + postCounter, "Doe" + postCounter, testUsername, "Password1!" + postCounter, "johndoe@example.com" + postCounter, Users.UserRole.ADMIN, "Some info" + postCounter);
        assertTrue(statusCodes.contains(StatusCode.SUCCESS));
    }

    @Test
    void testGetUsersByUsername() {
        usersAPI.postUsers("John" + postCounter, "Doe" + postCounter, testUsername, "Password1!" + postCounter, "johndoe@example.com" + postCounter, Users.UserRole.ADMIN, "Some info" + postCounter);
        assertNotNull(usersAPI.getUsersByUsername(testUsername));
    }

    @Test
    void testGetUsersByEmailAddress() {
        usersAPI.postUsers("John" + postCounter, "Doe" + postCounter, testUsername, "Password1!" + postCounter, "johndoe@example.com" + postCounter, Users.UserRole.ADMIN, "Some info" + postCounter);
        assertNotNull(usersAPI.getUsersByEmailAddress("johndoe@example.com" + postCounter));
    }

    @Test
    void testGetUsersFirstName() {
        usersAPI.postUsers("John" + postCounter, "Doe" + postCounter, testUsername, "Password1!" + postCounter, "johndoe@example.com" + postCounter, Users.UserRole.ADMIN, "Some info" + postCounter);
        assertEquals("John" + postCounter, usersAPI.getUsersFirstName(testUsername));
    }

    @Test
    void testGetUsersLastName() {
        usersAPI.postUsers("John" + postCounter, "Doe" + postCounter, testUsername, "Password1!" + postCounter, "johndoe@example.com" + postCounter, Users.UserRole.ADMIN, "Some info" + postCounter);
        assertEquals("Doe" + postCounter, usersAPI.getUsersLastName(testUsername));
    }

    @Test
    void testGetUserById() {
        usersAPI.postUsers("John" + postCounter, "Doe" + postCounter, testUsername, "Password1!" + postCounter, "johndoe@example.com" + postCounter, Users.UserRole.ADMIN, "Some info" + postCounter);
        String userId = usersAPI.getUsersByUsername(testUsername);
        assertNotNull(usersAPI.getUserById(userId));
    }

    @Test
    void testPutUsersUsername() {
        usersAPI.postUsers("John" + postCounter, "Doe" + postCounter, testUsername, "Password1!" + postCounter, "johndoe@example.com" + postCounter, Users.UserRole.ADMIN, "Some info" + postCounter);
        List<StatusCode> result = usersAPI.putUsersUsername(testUsername, "newusername");
        assertTrue(result.contains(StatusCode.SUCCESS));
    }

    @Test
    void testPutUsersEmailAddress() {
        usersAPI.postUsers("John" + postCounter, "Doe" + postCounter, testUsername, "Password1!" + postCounter, "johndoe@example.com" + postCounter, Users.UserRole.ADMIN, "Some info" + postCounter);
        List<StatusCode> result = usersAPI.putUsersEmailAddress(testUsername, "newemail@example.com");
        assertTrue(result.contains(StatusCode.SUCCESS));
    }

    @Test
    void testPutUsersFirstName() {
        usersAPI.postUsers("John" + postCounter, "Doe" + postCounter, testUsername, "Password1!" + postCounter, "johndoe@example.com" + postCounter, Users.UserRole.ADMIN, "Some info" + postCounter);
        List<StatusCode> result = usersAPI.putUsersFirstName(testUsername, "Jane");
        assertTrue(result.contains(StatusCode.SUCCESS));
    }

    @Test
    void testPutUsersLastName() {
        usersAPI.postUsers("John" + postCounter, "Doe" + postCounter, testUsername, "Password1!" + postCounter, "johndoe@example.com" + postCounter, Users.UserRole.ADMIN, "Some info" + postCounter);
        List<StatusCode> result = usersAPI.putUsersLastName(testUsername, "Smith");
        assertTrue(result.contains(StatusCode.SUCCESS));
    }

    @Test
    void testPutUsersPassword() {
        usersAPI.postUsers("John" + postCounter, "Doe" + postCounter, testUsername, "Password1!" + postCounter, "johndoe@example.com" + postCounter, Users.UserRole.ADMIN, "Some info" + postCounter);
        List<StatusCode> result = usersAPI.putUsersPassword(testUsername, "NewPassword1!");
        assertTrue(result.contains(StatusCode.SUCCESS));
    }

    @Test
    void testPutUsersRole() {
        usersAPI.postUsers("John" + postCounter, "Doe" + postCounter, testUsername, "Password1!" + postCounter, "johndoe@example.com" + postCounter, Users.UserRole.ADMIN, "Some info" + postCounter);
        List<StatusCode> result = usersAPI.putUsersRole(testUsername, Users.UserRole.WAITER);
        assertTrue(result.contains(StatusCode.SUCCESS));
    }

    @Test
    void testPutUsersStatus() {
        usersAPI.postUsers("John" + postCounter, "Doe" + postCounter, testUsername, "Password1!" + postCounter, "johndoe@example.com" + postCounter, Users.UserRole.ADMIN, "Some info" + postCounter);
        List<StatusCode> result = usersAPI.putUsersStatus(testUsername, false);
        assertTrue(result.contains(StatusCode.SUCCESS));
    }

    @Test
    void testPutUsersAdditionalInfo() {
        usersAPI.postUsers("John" + postCounter, "Doe" + postCounter, testUsername, "Password1!" + postCounter, "johndoe@example.com" + postCounter, Users.UserRole.ADMIN, "Some info" + postCounter);
        List<StatusCode> result = usersAPI.putUsersAdditionalInfo(testUsername, "Updated info");
        assertTrue(result.contains(StatusCode.SUCCESS));
    }

    @Test
    void testDeleteUser() {
        usersAPI.postUsers("John" + postCounter, "Doe" + postCounter, testUsername, "Password1!" + postCounter, "johndoe@example.com" + postCounter, Users.UserRole.ADMIN, "Some info" + postCounter);
        List<StatusCode> result = usersAPI.deleteUser(testUsername);
        assertTrue(result.contains(StatusCode.SUCCESS));
    }

    @AfterAll
    static void deleteAllUsers() {
        int currentCounter = postCounter;
        for (int i = 1; i <= currentCounter; i++) {
            usersAPI.deleteUser("johndoe" + i);
        }
        usersAPI.deleteUser("newusername");
    }
}