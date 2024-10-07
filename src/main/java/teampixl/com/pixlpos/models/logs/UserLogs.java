package teampixl.com.pixlpos.models.logs;

import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.models.tools.DataManager;
import teampixl.com.pixlpos.models.tools.MetadataWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * UserLogs class is a construct for creating UserLogs object. Extends DataManager.
 * <p>
 * Metadata:
 * - id: UUID
 * - user_id: UUID
 * - log_type: LogType
 * - created_at: timestamp for creation
 * @see DataManager
 * @see MetadataWrapper
 */
public class UserLogs extends DataManager {

    /**
     * Enumerations for LogType
     */
    public enum LogType {
        LOGIN,
        LOGOUT,
        SYSTEM
    }

    /**
     * Constructor for UserLogs object.
     * <p>
     * Metadata:
     * - id: UUID
     * - user_id: UUID
     * - log_type: LogType
     * - created_at: timestamp for creation
     */
    public UserLogs(LogType logType) {
        super(initializeMetadata(logType));
    }

    private static MetadataWrapper initializeMetadata(LogType logType) {
        if (logType == null) {
            throw new IllegalArgumentException("LogType cannot be null");
        }
        String userId;
        try {
            userId = UserStack.getInstance().getCurrentUserId();
            if (userId == null) {
                userId = "System";
                logType = LogType.SYSTEM;
            }
        } catch (Exception e) {
            userId = "00000000-0000-0000-0000-000000000000";
        }
        System.out.println("LogType = " + logType);
        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("id", UUID.randomUUID());
        metadataMap.put("user_id", userId);
        metadataMap.put("log_type", logType);
        metadataMap.put("created_at", System.currentTimeMillis());
        return new MetadataWrapper(metadataMap);
    }
}
