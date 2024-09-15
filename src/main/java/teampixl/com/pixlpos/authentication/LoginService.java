package teampixl.com.pixlpos.authentication;

import teampixl.com.pixlpos.database.api.userapi.Users;
import teampixl.com.pixlpos.database.DataStore;

/**
 * This class is responsible for authenticating the user's login credentials.
 * It uses the DataStore class to retrieve user information and the PasswordUtils class to verify passwords.
 */
public class LoginService {

    private final DataStore dataStore = DataStore.getInstance();

    /**
     * This class is responsible for managing the authentication of users. It is responsible for registering and logging in users.
     * It uses the RegistrationService and LoginService classes to perform these operations.
     */
    public boolean authenticate(String username, String plainPassword) {
        Users usercheck = dataStore.getUser(username);
        return usercheck != null && PasswordUtils.verifyPassword(plainPassword, (String) usercheck.getData().get("password"));
    }
}
