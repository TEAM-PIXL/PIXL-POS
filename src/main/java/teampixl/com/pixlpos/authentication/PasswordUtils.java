package teampixl.com.pixlpos.authentication;

public class PasswordUtils {

    /*========================================================================================================================
    Code Description:
    This class is used to hash and verify passwords using the BCrypt library.

    Methods:
    - hashPassword(String plainPassword): This method takes a plain password as input and returns a hashed
      password.
    - verifyPassword(String plainPassword, String hashedPassword): This method takes a plain password and a
      hashed password as input and returns a boolean value indicating whether the plain password matches the
      hashed password.
    =========================================================================================================================*/


    public static String hashPassword(String plainPassword) {
        return org.mindrot.jbcrypt.BCrypt.hashpw(plainPassword, org.mindrot.jbcrypt.BCrypt.gensalt());
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return org.mindrot.jbcrypt.BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
