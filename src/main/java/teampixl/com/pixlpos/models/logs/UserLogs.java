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
        LOGOUT
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
        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("id", UUID.randomUUID());
        metadataMap.put("user_id", UserStack.getInstance().getCurrentUserId());
        metadataMap.put("log_type", logType);
        metadataMap.put("created_at", System.currentTimeMillis());
        return new MetadataWrapper(metadataMap);
    }
}
