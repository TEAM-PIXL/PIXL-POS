package teampixl.com.pixlpos.authentication;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilsTest {

    @Test
    public void testHashPassword() {
        String plainPassword = "securePassword123";
        String hashedPassword = PasswordUtils.hashPassword(plainPassword);

        assertNotNull(hashedPassword);
        assertNotEquals(plainPassword, hashedPassword);
    }

    @Test
    public void testVerifyPassword() {
        String plainPassword = "securePassword123";
        String hashedPassword = PasswordUtils.hashPassword(plainPassword);

        assertTrue(PasswordUtils.verifyPassword(plainPassword, hashedPassword));
        assertFalse(PasswordUtils.verifyPassword("wrongPassword", hashedPassword));
    }

    @Test
    public void testHashPasswordConsistency() {
        String plainPassword = "consistentPassword123";
        String hashedPassword1 = PasswordUtils.hashPassword(plainPassword);
        String hashedPassword2 = PasswordUtils.hashPassword(plainPassword);

        assertNotEquals(hashedPassword1, hashedPassword2);
    }
}