package teampixl.com.pixlpos.authentication;

/**
 * Utility class for hashing and verifying passwords using the BCrypt library.
 * This class provides methods to hash a plain password and verify a plain password against a hashed password.
 */
public class PasswordUtils {

    /**
     * Hashes a plain password using the BCrypt library.
     *
     * @param plainPassword the plain password to be hashed
     * @return the hashed password
     */
    public static String hashPassword(String plainPassword) {
        return org.mindrot.jbcrypt.BCrypt.hashpw(plainPassword, org.mindrot.jbcrypt.BCrypt.gensalt());
    }

    /**
     * Verifies a plain password against a hashed password using the BCrypt library.
     *
     * @param plainPassword the plain password to be verified
     * @param hashedPassword the hashed password to verify against
     * @return boolean indicating whether the plain password matches the hashed password
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return org.mindrot.jbcrypt.BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
