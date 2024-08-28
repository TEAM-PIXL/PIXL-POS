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
    - Enumerations for StockStatus and UnitType
    - MetadataWrapper object for metadata
    - Map object for data
    ===============================================================================================================================================================================================================*/

    public enum StockStatus {
        INSTOCK,
        LOWSTOCK,
        NOSTOCK
    }

    public enum UnitType {
        KG,
        L,
        QTY
    }

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

    public Ingredients(String itemName, StockStatus stockStatus, boolean onOrder, UnitType unitType, Object numeral, String notes) {
        if (itemName == null || itemName.isEmpty()) {
            throw new IllegalArgumentException("itemName cannot be null or empty");
        }
        if (stockStatus == null) {
            throw new IllegalArgumentException("stockStatus cannot be null");
        }
        if (unitType == UnitType.QTY && !(numeral instanceof Integer)) {
            throw new IllegalArgumentException("Numeral must be an Integer for QTY unit type");
        }
        if ((unitType == UnitType.KG || unitType == UnitType.L) && !(numeral instanceof Double)) {
            throw new IllegalArgumentException("Numeral must be a Double for KG or L unit types");
        }

        // Metadata
        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("ingredient_id", UUID.randomUUID().toString());
        metadataMap.put("itemName", itemName);
        metadataMap.put("stockStatus", stockStatus);
        metadataMap.put("onOrder", onOrder);
        metadataMap.put("lastUpdated", LocalDateTime.now());

        this.metadata = new MetadataWrapper(metadataMap);

        // Data
        this.data = new HashMap<>();
        data.put("unit", unitType);
        data.put("numeral", numeral);
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

    // Method to update metadata, similar to Order.java
    public void updateMetadata(String key, Object value) {
        if (key.equals("stockStatus") && !(value instanceof StockStatus)) {
            throw new IllegalArgumentException("Invalid value type for stockStatus");
        }
        if (key.equals("onOrder") && !(value instanceof Boolean)) {
            throw new IllegalArgumentException("Invalid value type for onOrder");
        }
        Map<String, Object> updatedMetadata = new HashMap<>(metadata.metadata());
        updatedMetadata.put(key, value);
        updatedMetadata.put("lastUpdated", LocalDateTime.now());
        this.metadata = new MetadataWrapper(updatedMetadata);
    }

    // Method to update data, similar to how metadata is updated
    public void setDataValue(String key, Object value) {
        if (key.equals("unit") && !(value instanceof UnitType)) {
            throw new IllegalArgumentException("Invalid value type for unit");
        }
        if (key.equals("numeral") && !(value instanceof Integer || value instanceof Double)) {
            throw new IllegalArgumentException("Invalid value type for numeral");
        }
        data.put(key, value);
    }

    @Override
    public String toString() {
        return String.format("Ingredients{Metadata: %s, Data: %s}", metadata.metadata(), new HashMap<>(data));
    }
}
