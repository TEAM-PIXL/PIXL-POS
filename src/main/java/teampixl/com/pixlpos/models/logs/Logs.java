package teampixl.com.pixlpos.models.logs;

import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.database.api.UsersAPI;
import teampixl.com.pixlpos.models.logs.network.Util;
import teampixl.com.pixlpos.models.tools.MetadataWrapper;
import teampixl.com.pixlpos.models.tools.DataManager;
import teampixl.com.pixlpos.models.logs.definitions.*;
import teampixl.com.pixlpos.common.TimeUtil;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

/**
 * UserLogs class is a construct for creating UserLogs object. Extends DataManager.
 * <p>
 * Metadata:
 * - user_id: User ID
 * - log_id: Log ID
 * - log_timestamp: timestamp
 * - log_action: Action
 * <p>
 * Data:
 * - log_description: Description
 * - log_status: Status
 * - log_type: Type
 * - log_category: Category
 * - log_priority: Priority
 * - log_location: Location
 * - log_device: Device
 * - log_ip: IP
 * - log_mac: MAC
 * - log_os: OS
 * @see DataManager
 * @see MetadataWrapper
 */
public class Logs extends DataManager {

    /**
     * Constructor for UserLogs object.
     *
     * @param action: Action
     * @param logStatus: Status
     * @param logType: Type
     * @param logCategory: Category
     * @param logPriority: Priority
     * @param description: String
     *
     *  @see DataManager
     *  @see MetadataWrapper
     */
    public Logs(Action action,
                Status logStatus,
                Type logType,
                Category logCategory,
                Priority logPriority,
                String description
    ) throws Exception {
        super(initializeMetadata(action));

        this.data = new HashMap<>();
        this.data.put("log_description", description);
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

    /**
     * Overloaded constructor for UserLogs object.
     *
     * @param action: Action
     * @param logStatus: Status
     * @param logType: Type
     * @param logCategory: Category
     * @param logPriority: Priority
     *
     *  @see DataManager
     *  @see MetadataWrapper
     */
    public Logs(Action action,
                Status logStatus,
                Type logType,
                Category logCategory,
                Priority logPriority
    ) throws Exception {
        super(initializeMetadata(action));
        this.data = new HashMap<>();
        this.data.put("log_status", logStatus);
        this.data.put("log_type", logType);
        this.data.put("log_category", logCategory);
        this.data.put("log_priority", logPriority);
        this.data.put("log_description", this.generateLogDescription());
        String IP = Util.getIp();
        this.data.put("log_location", Util.getLocation(IP));
        this.data.put("log_device", Util.getDeviceInfo());
        this.data.put("log_ip", IP);
        this.data.put("log_mac", Util.getMacAddress());
        this.data.put("log_os", Util.checkOS());
    }

    private static MetadataWrapper initializeMetadata(Action action) {
        Map<String, Object> metadataMap = new HashMap<>();
        String ID;
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

    /**
     * Generates a log description based on the metadata values.
     *
     * @return String
     */
    public String generateLogDescription() {
        long timestamp = (long) this.getMetadataValue("log_timestamp");
        String readableTimestamp = TimeUtil.convertUnixTimestampToReadable(timestamp);
        return "User " + UsersAPI.getInstance().reverseKeySearch(UserStack.getInstance().getCurrentUserId()) + " performed the action " + this.getMetadataValue("log_action") + " at the time " + readableTimestamp;
    }
}
