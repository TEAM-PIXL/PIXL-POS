package teampixl.com.pixlpos.authentication;

import teampixl.com.pixlpos.models.Users;

/**
 * This class is responsible for managing the authentication of users. It is responsible for registering and logging in users.
 * It uses the RegistrationService and LoginService classes to perform these operations.
 */
public class AuthenticationManager {

    private static final RegistrationService registrationService = new RegistrationService();
    private static final LoginService loginService = new LoginService();

    /**
     * Registers a user.
     *
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @param username the username of the user
     * @param password the password of the user
     * @param email the email of the user
     * @param role the role of the user
     * @return boolean indicating whether the registration was successful
     */
    public static boolean register(String firstName, String lastName, String username, String password, String email, Users.UserRole role) {
        return registrationService.registerUser(firstName, lastName, username, password, email, role);
    }

    /**
     * Logs in a user.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return boolean indicating whether the login was successful
     */
    public static boolean login(String username, String password) {
        return loginService.authenticate(username, password);
    }
}
