package teampixl.com.pixlpos.database.api;

import teampixl.com.pixlpos.database.api.userapi.UserStack;

public class APITest {
    public static void main(String[] args) {

        UserStack userStack = UserStack.getInstance();
        userStack.setCurrentUser("admin");

        System.out.println("Current user: " + userStack.getCurrentUser());

        userStack.clearCurrentUser();

        System.out.println("Current user: " + userStack.getCurrentUser());

    }
}
