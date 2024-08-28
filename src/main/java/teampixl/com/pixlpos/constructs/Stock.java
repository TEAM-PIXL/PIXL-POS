package teampixl.com.pixlpos.constructs;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import teampixl.com.pixlpos.constructs.interfaces.IDataManager;
import teampixl.com.pixlpos.database.MetadataWrapper;

public class Stock implements IDataManager {

    /*======================================================================================================================================================================================================================================================
    Code Description:
    - Enumerations for StockStatus and UnitType
    - MetadataWrapper object for metadata
    - Map object for data
    ========================================================================================================================================================================================================================================================*/

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

    /*======================================================================================================================================================================================================================================================
    Code Description:
    - The Stock class is a data structure that holds the stock information of an ingredient.

    Metadata:
        - stock_id: UUID
        - ingredient_id: ingredient.getMetadata().metadata().get("ingredient_id")
        - stockStatus: stockStatus
        - onOrder: onOrder
        - created_at: timestamp for creation
        - lastUpdated: timestamp for last update

    Data:
        - unit: unitType
        - numeral: numeral
    ========================================================================================================================================================================================================================================================*/


    public Stock(Ingredients ingredient, StockStatus stockStatus, UnitType unitType, Object numeral, boolean onOrder) {
        if (unitType == UnitType.QTY && !(numeral instanceof Integer)) {
            throw new IllegalArgumentException("Numeral must be an Integer for QTY unit type");
        }
        if ((unitType == UnitType.KG || unitType == UnitType.L) && !(numeral instanceof Double)) {
            throw new IllegalArgumentException("Numeral must be a Double for KG or L unit types");
        }

        // Metadata
        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("stock_id", UUID.randomUUID().toString());
        metadataMap.put("ingredient_id", ingredient.getMetadata().metadata().get("ingredient_id"));
        metadataMap.put("stockStatus", stockStatus);
        metadataMap.put("onOrder", onOrder);
        metadataMap.put("created_at", LocalDateTime.now());
        metadataMap.put("lastUpdated", LocalDateTime.now());

        this.metadata = new MetadataWrapper(metadataMap);

        // Data
        this.data = new HashMap<>();
        data.put("unit", unitType);
        data.put("numeral", numeral);
    }

    /*======================================================================================================================================================================================================================================================
    Code Description:
    - Getters for metadata and data.

    Methods:
        - getMetadata(): MetadataWrapper
        - getData(): Map<String, Object>
        - updateMetadata(String key, Object value): void
        - setDataValue(String key, Object value): void
        - toString(): String
    ========================================================================================================================================================================================================================================================*/


    public MetadataWrapper getMetadata() {
        return metadata;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void updateMetadata(String key, Object value) {
        Map<String, Object> updatedMetadata = new HashMap<>(metadata.metadata());
        updatedMetadata.put(key, value);
        updatedMetadata.put("lastUpdated", LocalDateTime.now());
        this.metadata = new MetadataWrapper(updatedMetadata);
    }

    public void setDataValue(String key, Object value) {
        data.put(key, value);
    }

    @Override
    public String toString() {
        return String.format("Stock{Metadata: %s, Data: %s}", metadata.metadata(), new HashMap<>(data));
    }
}

