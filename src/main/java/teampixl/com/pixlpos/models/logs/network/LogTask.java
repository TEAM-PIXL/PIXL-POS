package teampixl.com.pixlpos.models.logs.network;

import teampixl.com.pixlpos.models.logs.definitions.*;
import teampixl.com.pixlpos.models.logs.Logs;

public class LogTask implements Runnable {
    private Action action;
    private Status logStatus;
    private Type logType;
    private Category logCategory;
    private Priority logPriority;

    public LogTask(Action action, Status logStatus, Type logType, Category logCategory, Priority logPriority) {
        this.action = action;
        this.logStatus = logStatus;
        this.logType = logType;
        this.logCategory = logCategory;
        this.logPriority = logPriority;
    }

    @Override
    public void run() {
        try {
            Logs logSet = new Logs(action, logStatus, logType, logCategory, logPriority);
            System.out.println("The log has the contents: " + logSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
