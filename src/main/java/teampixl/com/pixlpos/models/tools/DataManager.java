package teampixl.com.pixlpos.models.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * DataManager class is a construct for creating DataManager object.
 * <p>
 * Metadata:
 * - metadata: Map object
 * <p>
 * Data:
 * - data: Map object
 * @see MetadataWrapper
 * @see IDataManager
 */
public abstract class DataManager {

    protected MetadataWrapper metadata;
    protected Map<String, Object> data;

    /**
     * Constructor for DataManager object.
     * @param metadata MetadataWrapper object
     */
    public DataManager(MetadataWrapper metadata) {
        this.metadata = metadata;
        this.data = new HashMap<>();
    }

    /**
     * Metadata Getter for safe access to metadata.
     * @param key String key
     * @return Object value
     */
    public Object getMetadataValue(String key) {
        return metadata.metadata().get(key);
    }

    /**
     * Metadata Setter for safe access to metadata.
     * @param key String key
     * @param value Object value
     */
    public void updateMetadata(String key, Object value) {
        Map<String, Object> modifiableMetadata = new HashMap<>(metadata.metadata());
        if (value != null) {
            modifiableMetadata.put(key, value);
        } else {
            modifiableMetadata.remove(key);
        }
        this.metadata = new MetadataWrapper(modifiableMetadata);
    }

    /**
     * Data Getter for safe access to data.
     * @param key String key
     * @return Object value
     */
    public Object getDataValue(String key) {
        return data.get(key);
    }

    /**
     * Data Setter for safe access to data.
     * @param key String key
     * @param value Object value
     */
    public void setDataValue(String key, Object value) {
        data.put(key, value);
    }

    /**
     * Metadata Getter for safe direct access to metadata.
     * @return MetadataWrapper object
     */
    public MetadataWrapper getMetadata() {
        return metadata;
    }

    /**
     * Data Getter for safe direct access to data.
     * @return Map object
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * Intermediary method to get metadata as a map.
     * @return Map object
     */
    public Map<String, Object> getMetadataMap() {
        return metadata.metadata();
    }

    /**
     * Converts DataManager object to string.
     * @return String representation of DataManager object
     */
    @Override
    public String toString() {
        return String.format("%s{Metadata: %s, Data: %s}",
                this.getClass().getSimpleName(),
                metadata != null ? metadata.metadata() : "null",
                data != null ? new HashMap<>(data) : "null");
    }
}