package teampixl.com.pixlpos.authentication;

import teampixl.com.pixlpos.database.api.UsersAPI;
import teampixl.com.pixlpos.models.Users;
import teampixl.com.pixlpos.database.DataStore;

/**
 * This class is responsible for registering a user. It checks if the username already exists in the database and if it does, it returns false.
 * If the username does not exist, it hashes the password and creates a new user object. The new user object is then added to the database.
 */
public class RegistrationService {

    private final DataStore dataStore = DataStore.getInstance();

    /**
     * Registers a user.
     *
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @param username the username of the user
     * @param plainPassword the plain password of the user
     * @param email the email of the user
     * @param role the role of the user
     * @return boolean indicating whether the registration was successful
     */
    public boolean registerUser(String firstName, String lastName, String username, String plainPassword, String email, Users.UserRole role) {
        try {
            String hashedPassword = PasswordUtils.hashPassword(plainPassword);
            Users newUser = new Users(firstName, lastName, username, hashedPassword, email, role);
            dataStore.createUser(newUser);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
