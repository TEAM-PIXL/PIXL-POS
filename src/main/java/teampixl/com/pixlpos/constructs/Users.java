package teampixl.com.pixlpos.constructs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import teampixl.com.pixlpos.constructs.interfaces.IDataManager;
import teampixl.com.pixlpos.database.MetadataWrapper;

/**
 * Users class is a construct for creating Users object. Implements IDataManager.
 * <p>
 * Metadata:
 * - id: UUID
 * - first_name: first name
 * - last_name: last name
 * - username: username
 * - role: role
 * - created_at: timestamp for creation
 * - updated_at: timestamp for last update
 * - is_active: boolean
 * <p>
 * Data:
 * - password: password
 * - email: email
 * - additional_info: null
 * @see IDataManager
 * @see MetadataWrapper
 */
public class Users implements IDataManager {

    /*============================================================================================================================================================
    Code Description:
    - Enumerations for UserRole
    - MetadataWrapper object for metadata
    - Map object for data
    ============================================================================================================================================================*/

    /**
     * Enumerations for UserRole
     */
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
        - first_name: first name
        - last_name: last name
        - username: username
        - role: role
        - created_at: timestamp for creation
        - updated_at: timestamp for last update
        - is_active: boolean

    Data:
        - password: password
        - email: email
        - additional_info: null
    ============================================================================================================================================================*/

    /**
     * Constructor for Users object.
     * @param firstName first name
     * @param lastName last name
     * @param username username
     * @param plainPassword password
     * @param email email
     * @param role role
     */
    public Users(String firstName, String lastName, String username, String plainPassword, String email, UserRole role) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("username cannot be null or empty");
        }
        if (firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("first name cannot be null or empty");
        }
        if (lastName == null || lastName.isEmpty()) {
            throw new IllegalArgumentException("last name cannot be null or empty");
        }
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("password cannot be null or empty");
        }
        if (role == null) {
            throw new IllegalArgumentException("role cannot be null");
        }

        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("id", UUID.randomUUID().toString());
        metadataMap.put("first_name", firstName);
        metadataMap.put("last_name", lastName);
        metadataMap.put("username", username);
        metadataMap.put("role", role);
        metadataMap.put("created_at", System.currentTimeMillis());
        metadataMap.put("updated_at", System.currentTimeMillis());
        metadataMap.put("is_active", true);

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

