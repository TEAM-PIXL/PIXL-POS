package teampixl.com.pixlpos.constructs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsersTest {

    private Users user;

    @BeforeEach
    void setUp() {
        user = new Users("testUser", "password123", "test@example.com", Users.UserRole.ADMIN);
    }

    @Test
    void testUserCreation() {
        assertNotNull(user.getMetadata().metadata().get("id"));
        assertEquals("testUser", user.getMetadata().metadata().get("username"));
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
    void testRoleAssignment() {
        assertEquals(Users.UserRole.ADMIN, user.getMetadata().metadata().get("role"));
        user.updateMetadata("role", Users.UserRole.WAITER);
        assertEquals(Users.UserRole.WAITER, user.getMetadata().metadata().get("role"));
    }
}
