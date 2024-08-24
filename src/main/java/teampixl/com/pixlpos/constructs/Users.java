package teampixl.com.pixlpos.constructs;

import teampixl.com.pixlpos.database.MetadataWrapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.TreeMap;

public class Users {

    // Enum for Role
    public enum Role {
        DEFAULT,
        ADMIN,
        COOK,
        WAITER
    }

    private MetadataWrapper metadata;  // MetadataWrapper encapsulates the metadata map
    private final Map<String, Object> data;  // Data section

    // Constructor
    public Users(String username, String password, String email, Role role) {
        Map<String, Object> metadataMap = new HashMap<>();

        // Initialize metadata
        metadataMap.put("id", UUID.randomUUID());
        metadataMap.put("creationTime", LocalTime.now());
        metadataMap.put("creationDate", LocalDate.now());
        metadataMap.put("role", role != null ? role : Role.DEFAULT);  // Default role if none provided
        metadataMap.put("isActive", false);
        metadataMap.put("username", username);

        this.metadata = new MetadataWrapper(metadataMap);  // Wrap the metadata in MetadataWrapper

        // Initialize data
        this.data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);
        data.put("email", email);
    }

    // Getter for Metadata
    public MetadataWrapper getMetadata() {
        return metadata;
    }

    // Getter for Data
    public Map<String, Object> getData() {
        return data;
    }

    // Methods to safely update Metadata
    public void updateMetadata(String key, Object value) {
        Map<String, Object> modifiableMetadata = new HashMap<>(metadata.metadata());
        if (value != null) {
            modifiableMetadata.put(key, value);
        } else {
            modifiableMetadata.remove(key);
        }
        this.metadata = new MetadataWrapper(modifiableMetadata);
    }

    // Setters for specific fields in data
    public void setDataValue(String key, Object value) {
        data.put(key, value);
    }

    @Override
    public String toString() {
        // Use TreeMap to ensure keys are sorted in the output
        Map<String, Object> sortedMetadata = new TreeMap<>(metadata.metadata());
        Map<String, Object> sortedData = new TreeMap<>(data);

        return String.format("Users{Metadata: %s, Data: %s}", sortedMetadata, sortedData);
    }
}
