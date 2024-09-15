package teampixl.com.pixlpos.database.api.userapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsersTest {

    private Users user;

    @BeforeEach
    void setUp() {
        user = new Users("John", "Doe", "johndoe", "password", "johndoe@example.com", Users.UserRole.ADMIN);
    }

    @Test
    void testUserCreation() {
        assertNotNull(user.getMetadata().metadata().get("id"));
        assertEquals("johndoe", user.getMetadata().metadata().get("username"));
        assertEquals(Users.UserRole.ADMIN, user.getMetadata().metadata().get("role"));
    }

    @Test
    void testUpdateUserMetadata() {
        user.updateMetadata("username", "updatedUser");
        assertEquals("updatedUser", user.getMetadata().metadata().get("username"));
    }

    @Test
    void testUpdateUserEmail() {
        user.setDataValue("email", "newemail@example.com");
        assertEquals("newemail@example.com", user.getData().get("email"));
    }

    @Test
    void testUserIsActive() {
        assertTrue((Boolean) user.getMetadata().metadata().get("is_active"));
        user.updateMetadata("is_active", false);
        assertFalse((Boolean) user.getMetadata().metadata().get("is_active"));
    }

    @Test
    void testUserCreationWithNullUsername() {
        assertThrows(IllegalArgumentException.class, () -> new Users("John", "Doe", null, "password", "", Users.UserRole.ADMIN));
    }

    @Test
    void testUserCreationWithNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> new Users("John", "Doe", "johndoe", null, "", Users.UserRole.ADMIN));
    }

    @Test
    void testUserCreationWithNullRole() {
        assertThrows(IllegalArgumentException.class, () -> new Users("John", "Doe", "johndoe", "password", "", null));
    }

    @Test
    void testRoleAssignment() {
        assertEquals(Users.UserRole.ADMIN, user.getMetadata().metadata().get("role"));
        user.updateMetadata("role", Users.UserRole.WAITER);
        assertEquals(Users.UserRole.WAITER, user.getMetadata().metadata().get("role"));
    }
}
