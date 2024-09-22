package teampixl.com.pixlpos.authentication;

import teampixl.com.pixlpos.database.api.UsersAPI;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.models.Users;

import java.io.ObjectInputFilter;

/**
 * This class is responsible for authenticating the user's login credentials.
 * It uses the DataStore class to retrieve user information and the PasswordUtils class to verify passwords.
 */
public class LoginService {

    private final UsersAPI usersAPI = UsersAPI.getInstance();

    /**
     * This class is responsible for managing the authentication of users. It is responsible for registering and logging in users.
     * It uses the RegistrationService and LoginService classes to perform these operations.
     *
     * @param USERNAME the username of the user
     * @param PLAIN_PASSWORD the plain password of the user
     * @return boolean indicating whether the user is authenticated
     */
    public boolean authenticate(String USERNAME, String PLAIN_PASSWORD) {
        Users CHECK_USER = usersAPI.getUsersByUsername(USERNAME);
        StatusCode STATUS = usersAPI.validateUsersByStatus(USERNAME);
        return CHECK_USER != null && PasswordUtils.verifyPassword(PLAIN_PASSWORD, (String) CHECK_USER.getData().get("password")) && STATUS == StatusCode.SUCCESS;
    }
}
