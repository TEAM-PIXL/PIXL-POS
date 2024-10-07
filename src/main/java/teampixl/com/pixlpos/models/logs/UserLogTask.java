package teampixl.com.pixlpos.models.logs;

import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.models.logs.UserLogs.LogType;

import java.util.concurrent.CompletableFuture;


public class UserLogTask implements Runnable {
    private final LogType LOG_TYPE;

    public UserLogTask(LogType logType) {
        this.LOG_TYPE = logType;
    }

    @Override
    public void run() {
        try {
            UserLogs userLogs = new UserLogs(LOG_TYPE);
            DataStore.getInstance().createUserLogs(userLogs);
            System.out.println("The log has the contents: " + userLogs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void login() {
        new Thread(new UserLogTask(LogType.LOGIN)).start();
    }

    public static CompletableFuture<Void> logout() {
        return CompletableFuture.runAsync(() -> {
            new UserLogTask(LogType.LOGOUT).run();
        });
    }
}
