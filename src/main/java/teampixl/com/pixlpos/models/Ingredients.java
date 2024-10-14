package teampixl.com.pixlpos.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import teampixl.com.pixlpos.models.tools.DataManager;
import teampixl.com.pixlpos.models.tools.MetadataWrapper;

/**
 * Represents an ingredient with associated metadata and data. Extends DataManager.
 * <p>
 * Metadata:
 * - ingredient_id: UUID
 * - itemName: itemName
 * <p>
 * Data:
 * - notes: notes
 * @see DataManager
 * @see MetadataWrapper
 */
public class Ingredients extends DataManager {

    /**
     * Constructor for Ingredients object.
     * @param itemName Name of the ingredient.
     * @param notes Notes about the ingredient.
     */
    public Ingredients(String itemName, String notes) {
        super(initializeMetadata(itemName));

        this.data = new HashMap<>();
        data.put("notes", notes);
    }

    private static MetadataWrapper initializeMetadata(String itemName) {
        if (itemName == null || itemName.isEmpty()) {
            throw new IllegalArgumentException("itemName cannot be null or empty");
        }

        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("ingredient_id", UUID.randomUUID().toString());
        metadataMap.put("itemName", itemName);

        return new MetadataWrapper(metadataMap);
    }
}

