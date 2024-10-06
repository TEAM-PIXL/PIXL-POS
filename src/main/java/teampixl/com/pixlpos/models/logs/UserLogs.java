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
                    String logDescription,
                    Status logStatus,
                    Type logType,
                    Category logCategory,
                    Priority logPriority,
                    String logLocation,
                    String logDevice,
                    String logIp,
                    String logMac,
                    String logOs
    ) throws Exception {
        super(initializeMetadata(action));

        this.data = new HashMap<>();
        this.data.put("log_description", "N/A");
        this.data.put("log_status", "");
        this.data.put("log_type", "");
        this.data.put("log_category", "");
        this.data.put("log_priority", "");
        this.data.put("log_location", "Unknown");
        this.data.put("log_device", "Unknown");
        this.data.put("log_ip", Util.getIp());
        this.data.put("log_mac", "Unknown");
        this.data.put("log_os", "Unknown");
    }

    private static MetadataWrapper initializeMetadata(Action action) {
        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("user_id", UserStack.getInstance().getCurrentUserId());
        metadataMap.put("log_id", UUID.randomUUID().toString());
        metadataMap.put("log_timestamp", System.currentTimeMillis());
        metadataMap.put("log_action", action);
        return new MetadataWrapper(metadataMap);
    }

    public void setLogDescription(String logDescription) {
        this.data.put("log_description", logDescription);
    }

    public String generateLogDescription() {
        String generalDescription = "User " + UserStack.getInstance().getCurrentUserId() + " performed action: " + this.data.get("log_action") + " at " + this.data.get("log_timestamp") + ".";
        return generalDescription + " Additional information: " + this.data.get("log_description");
    }

}
