package teampixl.com.pixlpos.constructs;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import teampixl.com.pixlpos.constructs.interfaces.IDataManager;
import teampixl.com.pixlpos.database.MetadataWrapper;

public class Ingredients implements IDataManager {

    /*===============================================================================================================================================================================================================
    Code Description:
    - MetadataWrapper object for metadata
    - Map object for data
    ===============================================================================================================================================================================================================*/

    private MetadataWrapper metadata;
    private final Map<String, Object> data;

    /*===============================================================================================================================================================================================================
    Code Description:
    - Constructor for Ingredients object.

    Metadata:
        - uuid: UUID
        - itemName: itemName
        - stockStatus: stockStatus
        - onOrder: onOrder
        - lastUpdated: timestamp for last update

    Data:
        - unit: unitType
        - numeral: numeral
        - notes: notes
    ===============================================================================================================================================================================================================*/

    public Ingredients(String itemName, String notes) {
        if (itemName == null || itemName.isEmpty()) {
            throw new IllegalArgumentException("itemName cannot be null or empty");
        }

        // Metadata
        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("ingredient_id", UUID.randomUUID().toString());
        metadataMap.put("itemName", itemName);

        this.metadata = new MetadataWrapper(metadataMap);

        // Data
        this.data = new HashMap<>();
        data.put("notes", notes);
    }

    /*===============================================================================================================================================================================================================
    Code Description:
    - Getters for metadata and data

    Methods:
    - getMetadata(): MetadataWrapper
    - getData(): Map<String, Object>
    - updateMetadata(String key, Object value): void
    - updateData(String key, Object value): void
    - toString(): String
    ===============================================================================================================================================================================================================*/

    public MetadataWrapper getMetadata() {
        return metadata;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void updateMetadata(String key, Object value) {
        Map<String, Object> updatedMetadata = new HashMap<>(metadata.metadata());
        updatedMetadata.put(key, value);
        this.metadata = new MetadataWrapper(updatedMetadata);
    }

    public void setDataValue(String key, Object value) {
        data.put(key, value);
    }

    @Override
    public String toString() {
        return String.format("Ingredients{Metadata: %s, Data: %s}", metadata.metadata(), new HashMap<>(data));
    }
}

