package teampixl.com.pixlpos.models.logs;

import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.models.logs.network.Util;
import teampixl.com.pixlpos.models.tools.MetadataWrapper;
import teampixl.com.pixlpos.models.tools.DataManager;
import teampixl.com.pixlpos.models.logs.definitions.*;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class UserLogs extends DataManager {

    public UserLogs(Action action,
                    Status logStatus,
                    Type logType,
                    Category logCategory,
                    Priority logPriority
    ) throws Exception {
        super(initializeMetadata(action));

        this.data = new HashMap<>();
        this.data.put("log_description", generateLogDescription());
        this.data.put("log_status", logStatus);
        this.data.put("log_type", logType);
        this.data.put("log_category", logCategory);
        this.data.put("log_priority", logPriority);
        String IP = Util.getIp();
        this.data.put("log_location", Util.getLocation(IP));
        this.data.put("log_device", Util.getDeviceInfo());
        this.data.put("log_ip", IP);
        this.data.put("log_mac", Util.getMacAddress());
        this.data.put("log_os", Util.checkOS());
    }

    private static MetadataWrapper initializeMetadata(Action action) {
        Map<String, Object> metadataMap = new HashMap<>();
        String ID = null;
        try {
            ID = UserStack.getInstance().getCurrentUserId();
        } catch (Exception e) {
            ID = "Unknown";
        }
        metadataMap.put("user_id", ID != null ? ID : "Unknown");
        metadataMap.put("log_id", UUID.randomUUID().toString());
        metadataMap.put("log_timestamp", System.currentTimeMillis());
        metadataMap.put("log_action", action != null ? action : "Unknown");
        return new MetadataWrapper(metadataMap);
    }

    public void setLogDescription(String logDescription) {
        this.data.put("log_description", logDescription);
    }

    public String generateLogDescription() {
        return "User " + UserStack.getInstance().getCurrentUserId() + " performed the action " + this.getMetadataValue("log_action") + " at the time " + this.getMetadataValue("log_timestamp");
    }
}
