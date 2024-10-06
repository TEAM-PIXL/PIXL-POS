package teampixl.com.pixlpos.models.logs;

import teampixl.com.pixlpos.models.logs.UserLogs.LogType;


public class UserLogTask implements Runnable {
    private final LogType LOG_TYPE;

    public UserLogTask(LogType logType) {
        this.LOG_TYPE = logType;
    }

    @Override
    public void run() {
        try {
            UserLogs userLogs = new UserLogs(LOG_TYPE);
            System.out.println("The log has the contents: " + userLogs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
