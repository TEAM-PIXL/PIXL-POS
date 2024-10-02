package teampixl.com.pixlpos.database.api;

import org.junit.jupiter.api.*;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.database.api.util.StatusCode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsersAPITest {

    private static UsersAPI usersAPI;
    private String testUsername;
    private static int postCounter = 1;
    private String testFirstName;
    private String testLastName;
    private String testPassword;
    private String testEmail;
    private Users.UserRole testRole;
    private String testAdditionalInfo;

    @BeforeEach
    void setUp() {
        usersAPI = UsersAPI.getInstance();
        testUsername = "johndoe" + postCounter;
        testFirstName = "John" + postCounter;
        testLastName = "Doe" + postCounter;
        testPassword = "Password!1" + postCounter;
        testEmail = "johndoe@example.com" + postCounter;
        testRole = Users.UserRole.ADMIN;
        testAdditionalInfo = "additional info" + postCounter;

        List<StatusCode> StatusCodes = usersAPI.postUsers(testFirstName, testLastName, testUsername, testPassword, testEmail, testRole, testAdditionalInfo);
        assertTrue(Exceptions.isSuccessful(StatusCodes));
    }

    @AfterEach
    void postCounterUpdate() {
        postCounter++;
    }

    @AfterAll
    static void deleteAllUsers() {
        int currentCounter = postCounter;
        for (int i = 1; i <= currentCounter; i++) {
            usersAPI.deleteUser("johndoe" + i);
        }
        usersAPI.deleteUser("newusername");
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
        assertEquals(StatusCode.SUCCESS, usersAPI.validateUsersByRole(testRole));
        assertEquals(StatusCode.INVALID_USER_ROLE, usersAPI.validateUsersByRole(null));
    }

    @Test
    void testValidateUsersByAdditionalInfo() {
        assertEquals(StatusCode.SUCCESS, usersAPI.validateUsersByAdditionalInfo("additionalInfo"));
        assertEquals(StatusCode.INVALID_USER_ADDITIONAL_INFO, usersAPI.validateUsersByAdditionalInfo(null));
    }

    @Test
    void testGetUsersByUsername() {
        assertNotNull(usersAPI.getUser(testUsername));
    }

    @Test
    void testGetUserIDByUsername() {
        assertNotNull(usersAPI.keySearch(testUsername));
    }

    @Test
    void testGetUsernameByID() {
        String userID = usersAPI.keySearch(testUsername);
        assertEquals(testUsername, usersAPI.reverseKeySearch(userID));
    }

    @Test
    void testGetUsersFirstNameByUsername() {
        assertEquals(testFirstName, usersAPI.getUsersFirstNameByUsername(testUsername));
    }

    @Test
    void testGetUsersLastNameByUsername() {
        assertEquals(testLastName, usersAPI.getUsersLastNameByUsername(testUsername));
    }

    @Test
    void testGetUsersRoleByUsername() {
        assertEquals(testRole, usersAPI.getUsersRoleByUsername(testUsername));
    }

    @Test
    void testGetUsersEmailByUsername() {
        assertEquals(testEmail, usersAPI.getUsersEmailByUsername(testUsername));
    }

    @Test
    void testGetUsersAdditionalInfoByUsername() {
        assertEquals(testAdditionalInfo, usersAPI.getUsersAdditionalInfoByUsername(testUsername));
    }

    @Test
    void testPutUsersUsername() {
        List<StatusCode> result = usersAPI.putUsersUsername(testUsername, "newusername");
        assertTrue(Exceptions.isSuccessful(result));
    }

    @Test
    void testPutUsersEmailAddress() {
        List<StatusCode> result = usersAPI.putUsersEmailAddress(testUsername, "newemail@example.com");
        assertTrue(Exceptions.isSuccessful(result));
    }

    @Test
    void testPutUsersFirstName() {
        
        List<StatusCode> result = usersAPI.putUsersFirstName(testUsername, "Jane");
        assertTrue(Exceptions.isSuccessful(result));
    }

    @Test
    void testPutUsersLastName() {
        List<StatusCode> result = usersAPI.putUsersLastName(testUsername, "Smith");
        assertTrue(Exceptions.isSuccessful(result));
    }

    @Test
    void testPutUsersPassword() {
        List<StatusCode> result = usersAPI.putUsersPassword(testUsername, "NewPassword1!");
        assertTrue(Exceptions.isSuccessful(result));
    }

    @Test
    void testPutUsersRole() {
        List<StatusCode> result = usersAPI.putUsersRole(testUsername, Users.UserRole.WAITER);
        assertTrue(Exceptions.isSuccessful(result));
    }

    @Test
    void testPutUsersStatus() {
        List<StatusCode> result = usersAPI.putUsersStatus(testUsername, false);
        assertTrue(Exceptions.isSuccessful(result));
    }

    @Test
    void testPutUsersAdditionalInfo() {
        List<StatusCode> result = usersAPI.putUsersAdditionalInfo(testUsername, "Updated info");
        assertTrue(Exceptions.isSuccessful(result));
    }

    @Test
    void testDeleteUser() {
        List<StatusCode> result = usersAPI.deleteUser(testUsername);
        assertTrue(Exceptions.isSuccessful(result));
    }

    @Test
    void testSearchUsers() {
        List<Users> result = usersAPI.searchUsers("John");
        assertTrue(result.contains(usersAPI.getUser(testUsername)));
    }
}