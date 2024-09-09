package teampixl.com.pixlpos.authentication;

import teampixl.com.pixlpos.constructs.Users;
import teampixl.com.pixlpos.database.DataStore;

public class RegistrationService {

    /*=============================================================================================================================================================================================================================================================
    Code Description:
    This class is responsible for registering a user. It checks if the username already exists in the database and if it does, it returns false. If the username does not exist, it hashes the password and creates a
    new user object. The new user object is then added to the database.

    Methods:
    - registerUser(String username, String plainPassword, String email, Users.UserRole role): This method takes in the username, plainPassword, email, and role of the user. It checks if the username already exists in
      the database. If it does, it returns false. If the username does not exist, it hashes the password and creates a new user object. The new user object is then added to the database.
    =============================================================================================================================================================================================================================================================*/


    private DataStore dataStore = DataStore.getInstance();

    public boolean registerUser(String username, String plainPassword, String email, Users.UserRole role) {
        if (dataStore.usernameExists(username)) {
            return false;
        }
        String hashedPassword = PasswordUtils.hashPassword(plainPassword);
        Users newUser = new Users(username, hashedPassword, email, role);
        dataStore.addUser(newUser);
        return true;
    }
}
