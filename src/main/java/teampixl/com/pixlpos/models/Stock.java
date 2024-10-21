package teampixl.com.pixlpos.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import teampixl.com.pixlpos.models.tools.DataManager;
import teampixl.com.pixlpos.models.tools.MetadataWrapper;

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
 * @see DataManager
 * @see MetadataWrapper
 * @see Ingredients
 */
public class Stock extends DataManager {

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

    /**
     * Constructor for Stock
     * @param ingredient: Ingredients
     * @param stockStatus: StockStatus
     * @param unitType: UnitType
     * @param numeral: Object
     * @param onOrder: boolean
     */
    public Stock(Ingredients ingredient, StockStatus stockStatus, UnitType unitType, Object numeral, boolean onOrder) {
        super(initializeMetadata(ingredient, stockStatus, onOrder));
        if ((numeral instanceof Integer && (Integer) numeral < 0) || (numeral instanceof Double && (Double) numeral < 0)) {
            numeral = 0;
            throw new IllegalArgumentException("Numeral cannot be negative");
        }
        if ((numeral instanceof Integer && unitType == UnitType.KG) || (numeral instanceof Double && unitType == UnitType.QTY)) {
            throw new IllegalArgumentException("Invalid numeral for unit type");
        }

        this.data = new HashMap<>();
        data.put("unit", unitType);
        data.put("numeral", numeral);
        data.put("desired_quantity", 0);
        data.put("price_per_unit", 0.00);
        data.put("low_stock_threshold", 0.00);

        adjustStockStatus(numeral);
    }

    private static MetadataWrapper initializeMetadata(Ingredients ingredient, StockStatus stockStatus, boolean onOrder) {
        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("stock_id", UUID.randomUUID().toString());
        metadataMap.put("ingredient_id", ingredient.getMetadata().metadata().get("ingredient_id"));
        metadataMap.put("stockStatus", stockStatus);
        metadataMap.put("onOrder", onOrder);
        metadataMap.put("created_at", LocalDateTime.now());
        metadataMap.put("lastUpdated", LocalDateTime.now());
        return new MetadataWrapper(metadataMap);
    }

    /*======================================================================================================================================================================================================================================================
    ----------------------------------------------------------------------> STOCK HELPER METHODS <---------------------------------------------------------------------------------------------
    ========================================================================================================================================================================================================================================================*/

    private void adjustStockStatus(Object numeral) {
        if (numeral instanceof Integer) {
            if ((Integer) numeral == 0) {
                this.updateMetadata("stockStatus", StockStatus.NOSTOCK);
            } else if ((Integer) numeral < this.getLowStockThreshold()) {
                this.updateMetadata("stockStatus", StockStatus.LOWSTOCK);
            } else {
                this.updateMetadata("stockStatus", StockStatus.INSTOCK);
            }
        }
    }

    protected double getLowStockThreshold() {
        return (double) this.getDataValue("low_stock_threshold");
    }
}


