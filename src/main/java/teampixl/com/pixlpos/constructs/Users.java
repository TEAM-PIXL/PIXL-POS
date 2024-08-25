package teampixl.com.pixlpos.constructs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import teampixl.com.pixlpos.database.MetadataWrapper;

public class Users {

    public enum UserRole {
        WAITER,
        COOK,
        ADMIN
    }

    private MetadataWrapper metadata;
    private final Map<String, Object> data;

    public Users(String username, String password, String email, UserRole role) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("username cannot be null or empty");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("password cannot be null or empty");
        }
        if (role == null) {
            throw new IllegalArgumentException("role cannot be null");
        }

        // Metadata
        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("id", UUID.randomUUID().toString());
        metadataMap.put("username", username);
        metadataMap.put("role", role);
        metadataMap.put("created_at", System.currentTimeMillis()); // Timestamp for creation
        metadataMap.put("updated_at", System.currentTimeMillis()); // Timestamp for last update

        this.metadata = new MetadataWrapper(metadataMap);

        // Data
        this.data = new HashMap<>();
        data.put("password", password);  // Store encrypted password in practice
        data.put("email", email);
        data.put("additional_info", null);  // Optional field for additional info
    }

    public MetadataWrapper getMetadata() {
        return metadata;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void updateMetadata(String key, Object value) {
        Map<String, Object> modifiableMetadata = new HashMap<>(metadata.metadata());
        if (value != null) {
            modifiableMetadata.put(key, value);
        } else {
            modifiableMetadata.remove(key);
        }
        this.metadata = new MetadataWrapper(modifiableMetadata);
    }

    public void setDataValue(String key, Object value) {
        data.put(key, value);
    }

    @Override
    public String toString() {
        return String.format("Users{Metadata: %s, Data: %s}", new HashMap<>(metadata.metadata()), new HashMap<>(data));
    }
}

