package teampixl.com.pixlpos.constructs;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import teampixl.com.pixlpos.database.MetadataWrapper;

public class Ingredients {

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
        metadataMap.put("uuid", UUID.randomUUID().toString());
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

    public MetadataWrapper getMetadata() {
        return metadata;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void updateStockStatus(StockStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("stockStatus cannot be null");
        }
        this.metadata.updateMetadata("stockStatus", newStatus);
        this.metadata.updateMetadata("lastUpdated", LocalDateTime.now());
    }

    public void updateNumeral(Object newNumeral) {
        UnitType unitType = (UnitType) data.get("unit");
        if (unitType == UnitType.QTY && !(newNumeral instanceof Integer)) {
            throw new IllegalArgumentException("Numeral must be an Integer for QTY unit type");
        }
        if ((unitType == UnitType.KG || unitType == UnitType.L) && !(newNumeral instanceof Double)) {
            throw new IllegalArgumentException("Numeral must be a Double for KG or L unit types");
        }
        this.data.put("numeral", newNumeral);
        this.metadata.updateMetadata("lastUpdated", LocalDateTime.now());
    }

    public void updateNotes(String newNotes) {
        this.data.put("notes", newNotes);
        this.metadata.updateMetadata("lastUpdated", LocalDateTime.now());
    }

    @Override
    public String toString() {
        return String.format("Ingredients{Metadata: %s, Data: %s}", metadata.metadata(), new HashMap<>(data));
    }


}
