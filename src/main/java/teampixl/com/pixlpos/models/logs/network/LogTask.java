package teampixl.com.pixlpos.models.logs.network;

import teampixl.com.pixlpos.models.logs.definitions.*;
import teampixl.com.pixlpos.models.logs.Logs;

public class LogTask implements Runnable {
    private final Action ACTION;
    private final Status LOG_STATUS;
    private final Type LOG_TYPE;
    private final Category LOG_CATEGORY;
    private final Priority LOG_PRIORITY;

    public LogTask(Action action, Status logStatus, Type logType, Category logCategory, Priority logPriority) {
        this.ACTION = action;
        this.LOG_STATUS = logStatus;
        this.LOG_TYPE = logType;
        this.LOG_CATEGORY = logCategory;
        this.LOG_PRIORITY = logPriority;
    }

    @Override
    public void run() {
        try {
            Logs logSet = new Logs(ACTION, LOG_STATUS, LOG_TYPE, LOG_CATEGORY, LOG_PRIORITY);
            System.out.println("The log has the contents: " + logSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
