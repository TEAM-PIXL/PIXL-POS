package teampixl.com.pixlpos.constructs;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import teampixl.com.pixlpos.database.api.interfaces.IDataManager;
import teampixl.com.pixlpos.database.MetadataWrapper;
import teampixl.com.pixlpos.database.api.ingredientsapi.Ingredients;

/**
 * The Stock class is a data structure that holds the stock information of an ingredient.
 * <p>
 * Metadata:
 * - stock_id: UUID
 * - ingredient_id: ingredient.getMetadata().metadata().get("ingredient_id")
 * - stockStatus: stockStatus
 * - onOrder: onOrder
 * - created_at: timestamp for creation
 * - lastUpdated: timestamp for last update
 * <p>
 * Data:
 * - unit: unitType
 * - numeral: numeral
 * @see IDataManager
 * @see MetadataWrapper
 * @see Ingredients
 */
public class Stock implements IDataManager {

    /*======================================================================================================================================================================================================================================================
    Code Description:
    - Enumerations for StockStatus and UnitType
    - MetadataWrapper object for metadata
    - Map object for data
    ========================================================================================================================================================================================================================================================*/

    /**
     * Enumerations for StockStatus
     */
    public enum StockStatus {
        INSTOCK,
        LOWSTOCK,
        NOSTOCK
    }

    /**
     * Enumerations for UnitType
     */
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

    /**
     * Constructor for Stock
     * @param ingredient: Ingredients
     * @param stockStatus: StockStatus
     * @param unitType: UnitType
     * @param numeral: Object
     * @param onOrder: boolean
     */
    public Stock(Ingredients ingredient, StockStatus stockStatus, UnitType unitType, Object numeral, boolean onOrder) {
        if (unitType == UnitType.QTY && !(numeral instanceof Integer)) {
            throw new IllegalArgumentException("Numeral must be an Integer for QTY unit type");
        }
        if ((unitType == UnitType.KG || unitType == UnitType.L) && !(numeral instanceof Double)) {
            throw new IllegalArgumentException("Numeral must be a Double for KG or L unit types");
        }
        if ((numeral instanceof Integer && (Integer) numeral < 0) || (numeral instanceof Double && (Double) numeral < 0)) {
            numeral = 0;
        }

        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("stock_id", UUID.randomUUID().toString());
        metadataMap.put("ingredient_id", ingredient.getMetadata().metadata().get("ingredient_id"));
        metadataMap.put("stockStatus", stockStatus);
        metadataMap.put("onOrder", onOrder);
        metadataMap.put("created_at", LocalDateTime.now());
        metadataMap.put("lastUpdated", LocalDateTime.now());

        this.metadata = new MetadataWrapper(metadataMap);

        this.data = new HashMap<>();
        data.put("unit", unitType);
        data.put("numeral", numeral);

        adjustStockStatus(numeral);
    }

    /*======================================================================================================================================================================================================================================================
    Code Description:
    - Getters for metadata and data.

    Methods:
        - getMetadata(): MetadataWrapper
        - getData(): Map<String, Object>
        - updateMetadata(String key, Object value): void
        - setDataValue(String key, Object value): void
        - adjustStockStatus(Object numeral): void
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
        if ("numeral".equals(key)) {
            if (value instanceof Integer && (Integer) value < 0) {
                value = 0;
            } else if (value instanceof Double && (Double) value < 0) {
                value = 0.0;
            }
            adjustStockStatus(value);
        }
        data.put(key, value);
    }

    private void adjustStockStatus(Object numeral) {
        double numeralValue = numeral instanceof Integer ? (Integer) numeral : (Double) numeral;

        if (numeralValue == 0) {
            updateMetadata("stockStatus", StockStatus.NOSTOCK);
        } else if (!Double.isNaN(getLowStockThreshold()) && numeralValue <= getLowStockThreshold()) {
            updateMetadata("stockStatus", StockStatus.LOWSTOCK);
        } else {
            updateMetadata("stockStatus", StockStatus.INSTOCK);
        }
    }

    private double getLowStockThreshold() {
        return Double.NaN;
    }

    @Override
    public String toString() {
        return String.format("Stock{Metadata: %s, Data: %s}", metadata.metadata(), new HashMap<>(data));
    }
}


