package teampixl.com.pixlpos.authentication;

import teampixl.com.pixlpos.constructs.Users;

public class AuthenticationManager {

    /*====================================================================================================================================================================================================
    Code Description:
    This class is responsible for managing the authentication of users. It is responsible for registering and logging in users. It uses the RegistrationService
    and LoginService classes to perform these operations.

    Methods:
    1. register(String username, String password, String email, Users.UserRole role) -> boolean:
        - This method is responsible for registering a user. It takes in the username, password, email, and role of the user as parameters.
        - It calls the registerUser method of the registrationService object to register the user.
        - It returns a boolean value indicating whether the registration was successful.
     2. login(String username, String password) -> boolean:
        - This method is responsible for logging in a user. It takes in the username and password of the user as parameters.
        - It calls the authenticate method of the loginService object to authenticate the user.
        - It returns a boolean value indicating whether the login was successful.
    ====================================================================================================================================================================================================*/



    private static final RegistrationService registrationService = new RegistrationService();
    private static final LoginService loginService = new LoginService();

    public static boolean register(String username, String password, String email, Users.UserRole role) {
        return registrationService.registerUser(username, password, email, role);
    }

    public static boolean login(String username, String password) {
        return loginService.authenticate(username, password);
    }
}
