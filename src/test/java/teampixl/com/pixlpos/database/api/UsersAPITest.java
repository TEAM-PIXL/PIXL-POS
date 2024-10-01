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
}