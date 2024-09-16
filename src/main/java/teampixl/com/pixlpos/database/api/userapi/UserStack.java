package teampixl.com.pixlpos.database.api.userapi;

import teampixl.com.pixlpos.database.DataStore;

public class UserStack extends UsersAPI {
    private static final DataStore dataStore = DataStore.getInstance();
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
     * @param username the username of the user
     */
    public void setCurrentUser(String username) {
        currentUser = dataStore.getUsers().stream()
                .filter(user -> user.getMetadata().metadata().get("username").toString().equals(username))
                .findFirst()
                .orElse(null);
        currentUserId = UsersAPI.getUsersByUsername(username);
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
