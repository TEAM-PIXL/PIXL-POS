package teampixl.com.pixlpos.database.api;

import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.models.Users;

public class UserStack extends UsersAPI {
    private static final DataStore dataStore = DataStore.getInstance();
    private static final UsersAPI usersAPI = UsersAPI.getInstance();
    private static UserStack instance;

    private UserStack() {
        super(dataStore);
    }

    /**
     * Gets the instance of the UserStack.
     *
     * @return the instance of the UserStack
     */
    public static synchronized UserStack getInstance() {
        if (instance == null) {
            instance = new UserStack();
        }
        return instance;
    }

    /**
     * Stack for the current user.
     */
    private Users currentUser;

    /**
     * Stack for the current user id.
     */
    private String currentUserId;

    /**
     * Sets the current user.
     *
     * @param USERNAME the username of the user
     */
    public void setCurrentUser(String USERNAME) {
        currentUser = usersAPI.getUsersByUsername(USERNAME);
        currentUserId = usersAPI.keySearch(USERNAME);
    }

    /**
     * Gets the current user id.
     *
     * @return the current user id
     */
    public String getCurrentUserId() {
        return currentUserId;
    }

    /**
     * Gets the current user.
     *
     * @return the current user
     */
    public Users getCurrentUser() {
        return currentUser;
    }

    /**
     * Clears the current user.
     */
    public void clearCurrentUser() {
        currentUser = null;
        currentUserId = null;
    }
}
