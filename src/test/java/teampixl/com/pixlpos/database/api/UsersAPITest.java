package teampixl.com.pixlpos.database.api;

import org.junit.jupiter.api.*;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.database.api.util.StatusCode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsersAPITest {

    private static final UsersAPI usersAPI = UsersAPI.getInstance();
    private String testUsername;
    private static int postCounter = 1;
    private String testFirstName;
    private String testLastName;
    private String testPassword;
    private String testEmail;
    private Users.UserRole testRole;
    private String testAdditionalInfo;

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
        while (DataStore.getInstance() == null || UsersAPI.getInstance() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        testUsername = "johndoe" + postCounter;
        testFirstName = "John" + postCounter;
        testLastName = "Doe" + postCounter;
        testPassword = "Password!1" + postCounter;
        testEmail = "johndoe@example.com" + postCounter;
        testRole = Users.UserRole.ADMIN;
        testAdditionalInfo = "additional info" + postCounter;

        List<StatusCode> StatusCodes = usersAPI.postUsers(testFirstName, testLastName, testUsername, testPassword, testEmail, testRole, testAdditionalInfo);
        while (StatusCodes == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            StatusCodes = usersAPI.postUsers(testFirstName, testLastName, testUsername, testPassword, testEmail, testRole, testAdditionalInfo);
        }
    }

    @AfterEach
    void postCounterUpdate() throws InterruptedException {
        postCounter++;
        Thread.sleep(100);
    }

    @AfterAll
    static void deleteAllUsers() {
        List<StatusCode> StatusCodes;
        int currentCounter = postCounter;
        for (int i = 1; i <= currentCounter; i++) {
            StatusCodes = usersAPI.deleteUser("johndoe" + i);
        }
        StatusCodes = usersAPI.deleteUser("newusername");
        while (StatusCodes == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    void testValidateUsersByUsername() {
        assertEquals(StatusCode.SUCCESS, usersAPI.validateUsersByUsername("validUsername"));
    }

    @Test
    void testValidateUsersByTooShortUsername() {
        assertEquals(StatusCode.USERNAME_TOO_SHORT, usersAPI.validateUsersByUsername("abc"));
    }

    @Test
    void testValidateUsersByTooLongUsername() {
        assertEquals(StatusCode.USERNAME_TOO_LONG, usersAPI.validateUsersByUsername("a".repeat(21)));
    }

    @Test
    void testValidateUsersByContainsSpacesUsername() {
        assertEquals(StatusCode.USERNAME_CONTAINS_SPACES, usersAPI.validateUsersByUsername("user name"));
    }

    @Test
    void testValidateUsersByInvalidCharactersUsername() {
        assertEquals(StatusCode.USERNAME_INVALID_CHARACTERS, usersAPI.validateUsersByUsername("user@name"));
    }

    @Test
    void testValidateUsersByOnlyDigitsUsername() {
        assertEquals(StatusCode.USERNAME_ONLY_DIGITS, usersAPI.validateUsersByUsername("123456"));
    }

    @Test
    void testValidateUsersByEmail() {
        assertEquals(StatusCode.SUCCESS, usersAPI.validateUsersByEmailAddress("email@example.com"));
    }

    @Test
    void testValidateUsersByInvalidEmailAddress() {
        assertEquals(StatusCode.EMAIL_INVALID_FORMAT, usersAPI.validateUsersByEmailAddress("emailexample.com"));
    }

    @Test
    void testValidateUsersByContainsSpacesEmailAddress() {
        assertEquals(StatusCode.EMAIL_CONTAINS_SPACES, usersAPI.validateUsersByEmailAddress("email @example"));
    }

    @Test
    void testValidateUsersByFirstName() {
        assertEquals(StatusCode.SUCCESS, usersAPI.validateUsersByFirstName("John"));
    }

    @Test
    void testValidateUsersByEmptyFirstName() {
        assertEquals(StatusCode.INVALID_FIRST_NAME, usersAPI.validateUsersByFirstName(""));
    }

    @Test
    void testValidateUsersByNullFirstName() {
        assertEquals(StatusCode.INVALID_FIRST_NAME, usersAPI.validateUsersByFirstName(null));
    }

    @Test
    void testValidateUsersByLastName() {
        assertEquals(StatusCode.SUCCESS, usersAPI.validateUsersByLastName("Doe"));
    }

    @Test
    void testValidateUsersByNullLastName() {
        assertEquals(StatusCode.INVALID_LAST_NAME, usersAPI.validateUsersByLastName(null));
    }

    @Test
    void testValidateUsersByEmptyLastName() {
        assertEquals(StatusCode.INVALID_LAST_NAME, usersAPI.validateUsersByLastName(""));
    }

    @Test
    void testValidateUsersByPassword() {
        assertEquals(StatusCode.SUCCESS, usersAPI.validateUsersByPassword("Password1!"));
    }

    @Test
    void testValidateUsersByTooShortPassword() {
        assertEquals(StatusCode.PASSWORD_TOO_SHORT, usersAPI.validateUsersByPassword("Pass1!"));
    }

    @Test
    void testValidateUsersByTooLongPassword() {
        assertEquals(StatusCode.PASSWORD_TOO_LONG, usersAPI.validateUsersByPassword("P".repeat(21)));
    }

    @Test
    void testValidateUsersByNoDigitsPassword() {
        assertEquals(StatusCode.PASSWORD_NO_DIGITS, usersAPI.validateUsersByPassword("Password!"));
    }

    @Test
    void testValidateUsersByNoUppercasePassword() {
        assertEquals(StatusCode.PASSWORD_NO_UPPERCASE, usersAPI.validateUsersByPassword("password1!"));
    }

    @Test
    void testValidateUsersByNoLowercasePassword() {
        assertEquals(StatusCode.PASSWORD_NO_LOWERCASE, usersAPI.validateUsersByPassword("PASSWORD1!"));
    }

    @Test
    void testValidateUsersByNoSpecialCharPassword() {
        assertEquals(StatusCode.PASSWORD_NO_SPECIAL_CHAR, usersAPI.validateUsersByPassword("Password1"));
    }

    @Test
    void testValidateUsersByRole() {
        assertEquals(StatusCode.SUCCESS, usersAPI.validateUsersByRole(testRole));
    }

    @Test
    void testValidateUsersByNullRole() {
        assertEquals(StatusCode.INVALID_USER_ROLE, usersAPI.validateUsersByRole(null));
    }

    @Test
    void testValidateUsersByAdditionalInfo() {
        assertEquals(StatusCode.SUCCESS, usersAPI.validateUsersByAdditionalInfo("additionalInfo"));
    }

    @Test
    void testValidateUsersByNullAdditionalInfo() {
        assertEquals(StatusCode.INVALID_USER_ADDITIONAL_INFO, usersAPI.validateUsersByAdditionalInfo(null));
    }

    @Test
    void testGetUsersByUsername() {
        assertNotNull(usersAPI.getUser(testUsername));
    }

    @Test
    void testGetUserIDByUsername() {
        String userID = usersAPI.keySearch(testUsername);
        assertNotNull(userID);
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
        awaitResponse(result, () ->assertTrue(Exceptions.isSuccessful(result)));
    }

    @Test
    void testPutUsersEmailAddress() {
        List<StatusCode> result = usersAPI.putUsersEmailAddress(testUsername, "newemail@example.com");
        awaitResponse(result, () -> assertTrue(Exceptions.isSuccessful(result)));
    }

    @Test
    void testPutUsersFirstName() {

        List<StatusCode> result = usersAPI.putUsersFirstName(testUsername, "Jane");
        awaitResponse(result, () -> assertTrue(Exceptions.isSuccessful(result)));
    }

    @Test
    void testPutUsersLastName() {
        List<StatusCode> result = usersAPI.putUsersLastName(testUsername, "Smith");
        awaitResponse(result, () -> assertTrue(Exceptions.isSuccessful(result)));
    }

    @Test
    void testPutUsersPassword() {
        List<StatusCode> result = usersAPI.putUsersPassword(testUsername, "NewPassword1!");
        awaitResponse(result, () -> assertTrue(Exceptions.isSuccessful(result)));
    }

    @Test
    void testPutUsersRole() {
        List<StatusCode> result = usersAPI.putUsersRole(testUsername, Users.UserRole.WAITER);
        awaitResponse(result, () -> assertTrue(Exceptions.isSuccessful(result)));
    }

    @Test
    void testPutUsersStatus() {
        List<StatusCode> result = usersAPI.putUsersStatus(testUsername, false);
        awaitResponse(result, () -> assertTrue(Exceptions.isSuccessful(result)));
    }

    @Test
    void testPutUsersAdditionalInfo() {
        List<StatusCode> result = usersAPI.putUsersAdditionalInfo(testUsername, "Updated info");
        awaitResponse(result, () -> assertTrue(Exceptions.isSuccessful(result)));
    }

    @Test
    void testDeleteUser() {
        List<StatusCode> result = usersAPI.deleteUser(testUsername);
        awaitResponse(result, () -> assertTrue(Exceptions.isSuccessful(result)));
    }

    @Test
    void testSearchUsers() {
        List<Users> result = usersAPI.searchUsers("John");
        while (result == null) {
            try {
                Thread.sleep(100);
                assertNotNull(result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}