package teampixl.com.pixlpos.authentication;

import teampixl.com.pixlpos.constructs.Users;
import teampixl.com.pixlpos.database.DataStore;

public class LoginService {

    /*==============================================================================================================================================================================
    Code Description:
    The LoginService class is responsible for authenticating the user's login credentials.

    Methods:
    authenticate(String username, String plainPassword) - This method takes in the username and password entered by the user and checks if the user exists in the database.
    If the user exists, the method verifies the password entered by the user with the password stored in the database. If the passwords match, the method returns true,
    indicating successful authentication. Otherwise, the method returns false, indicating failed authentication.
    ==============================================================================================================================================================================*/

    private DataStore dataStore = DataStore.getInstance();

    public boolean authenticate(String username, String plainPassword) {
        Users usercheck = dataStore.getUser(username);
        if (usercheck != null && PasswordUtils.verifyPassword(plainPassword, (String) usercheck.getData().get("password"))) {
            return true; // Successful authentication
        }
        return false; // Authentication failed
    }
}
