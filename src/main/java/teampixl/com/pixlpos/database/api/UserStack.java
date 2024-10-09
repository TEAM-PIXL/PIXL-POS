package teampixl.com.pixlpos.database.api;

import teampixl.com.pixlpos.models.Users;

import java.util.concurrent.*;

public class UserStack {
    private static UserStack instance;
    private final UsersAPI usersAPI;
    private static Users currentUser;
    private static String currentUserId;

    private final ExecutorService executorService;

    private UserStack() {
        usersAPI = UsersAPI.getInstance();
        executorService = Executors.newSingleThreadExecutor();
    }

    public static synchronized UserStack getInstance() {
        if (instance == null) {
            instance = new UserStack();
        }
        return instance;
    }

    public Future<Users> setCurrentUser(String username) {
        return executorService.submit(() -> {
            currentUser = usersAPI.getUser(username);
            currentUserId = usersAPI.keySearch(username);
            return currentUser;
        });
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public Users getCurrentUser() {
        return currentUser;
    }

    public static void clearCurrentUser() {
        currentUser = null;
        currentUserId = null;
    }

    public void shutdown() {
        executorService.shutdown();
    }
}


