package teampixl.com.pixlpos.constructs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import teampixl.com.pixlpos.constructs.interfaces.IDataManager;
import teampixl.com.pixlpos.database.MetadataWrapper;
import teampixl.com.pixlpos.authentication.PasswordUtils;

public class Users implements IDataManager {

    /*============================================================================================================================================================
    Code Description:
    - Enumerations for UserRole
    - MetadataWrapper object for metadata
    - Map object for data
    ============================================================================================================================================================*/

    public enum UserRole {
        WAITER,
        COOK,
        ADMIN
    }

    private MetadataWrapper metadata;
    private final Map<String, Object> data;

    /*============================================================================================================================================================
    Code Description:
    - Constructor for Users object.

    Metadata:
        - id: UUID
        - username: username
        - role: role
        - created_at: timestamp for creation
        - updated_at: timestamp for last update

    Data:
        - password: password
        - email: email
        - additional_info: null
    ============================================================================================================================================================*/

    public Users(String username, String plainPassword, String email, UserRole role) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("username cannot be null or empty");
        }
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("password cannot be null or empty");
        }
        if (role == null) {
            throw new IllegalArgumentException("role cannot be null");
        }

        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("id", UUID.randomUUID().toString());
        metadataMap.put("username", username);
        metadataMap.put("role", role);
        metadataMap.put("created_at", System.currentTimeMillis());
        metadataMap.put("updated_at", System.currentTimeMillis());

        this.metadata = new MetadataWrapper(metadataMap);

        this.data = new HashMap<>();
        this.data.put("password", plainPassword);
        this.data.put("email", email);
        this.data.put("additional_info", null);
    }

    /*============================================================================================================================================================
    Code Description:
    - Method to get metadata, data, update metadata, and set data value.

    Methods:
        - getMetadata(): MetadataWrapper
        - getData(): Map<String, Object>
        - updateMetadata(String key, Object value): void
        - setDataValue(String key, Object value): void
        - toString(): String
    ============================================================================================================================================================*/

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

