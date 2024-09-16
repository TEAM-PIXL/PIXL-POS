package teampixl.com.pixlpos.models.interfaces;

import teampixl.com.pixlpos.database.MetadataWrapper;

import java.util.Map;

/**
 * Interface for data managers.
 */
public interface IDataManager {
    /**
     * Get the data map.
     * @return Map of data.
     */
    Map<String, Object> getData();
    /**
     * Get the metadata wrapper.
     * @return MetadataWrapper object.
     */
    MetadataWrapper getMetadata();
    /**
     * Update the value of a metadata key.
     * @param key Key to update value of.
     * @param value Value to update.
     */
    void updateMetadata(String key, Object value);
    /**
     * Set the value of a data key.
     * @param key Key to set value of.
     * @param value Value to set.
     */
    void setDataValue(String key, Object value);
}
