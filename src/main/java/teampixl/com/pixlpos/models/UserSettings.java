package teampixl.com.pixlpos.models;

import teampixl.com.pixlpos.models.tools.MetadataWrapper;
import java.util.Map;
import java.util.HashMap;

import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.models.tools.IDataManager;

/**
 * UserSettings class is a construct for creating UserSettings object. Implements IDataManager.
 * <p>
 * Metadata:
 * - theme: Theme. LIGHT
 * - resolution: Resolution. SD
 * - currency: Currency. AUD
 * - timezone: Timezone. AEST
 * <p>
 * Data:
 * - language: Language. ENGLISH
 * - access_level: AccessLevel. BASIC
 * @see IDataManager
 * @see MetadataWrapper
 */

public class UserSettings implements IDataManager {

    /*============================================================================================================================================================
    Code Description:
    - Enumerations for Theme, Resolution, Currency, Timezone, Language, and AccessLevel.
    - MetadataWrapper object for metadata
    - Map object for data
    ============================================================================================================================================================*/

    /**
     * Enumerations for Theme - New feature which could be added in the future. I.e. multi-theme support.
     * Currently only supports LIGHT and DARK.
     */
    public enum Theme {
        LIGHT,
        DARK
    }

    /**
     * Enumerations for Resolution - New feature which could be added in the future. I.e. multi-resolution support.
     * Currently only supports SD and HD.
     */
    public enum Resolution {
        SD,
        HD
    }

    /**
     * Enumerations for Currency - New feature which could be added in the future. I.e. multi-currency support.
     * Currently only supports AUD.
     */
    public enum Currency {
        USD,
        EUR,
        GBP,
        AUD
    }

    /**
     * Enumerations for Timezone - New feature which could be added in the future. I.e. multi-timezone support.
     * Currently only supports AEST / System Timezone.
     */
    public enum Timezone {
        PST,
        EST,
        CST,
        MST,
        GMT,
        AEST
    }

    /**
     * Enumerations for Language - New feature which could be added in the future. I.e. multi-language support.
     * Currently only supports ENGLISH.
     */
    public enum Language {
        ENGLISH,
        SPANISH,
        FRENCH,
        GERMAN,
        ITALIAN,
        GREEK
    }

    /**
     * Enumerations for AccessLevel - New feature which could be added in the future. I.e. subscription based access levels.
     * Currently not linked to any subscription service.
     */
    public enum AccessLevel {
        BASIC,
        STANDARD,
        PREMIUM
    }

    private MetadataWrapper metadata;
    private final Map<String, Object> data;

    /*============================================================================================================================================================
    Code Description:
    - Constructor for UserSettings object.

    Metadata:
        - user_id: USER_ID
        - theme: Theme.LIGHT
        - resolution: Resolution.SD
        - currency: Currency.AUD
        - timezone: Timezone.AEST

    Data:
        - language: Language.ENGLISH
        - access_level: AccessLevel.BASIC
     ============================================================================================================================================================*/

    /**
     * Constructor for UserSettings object. Stores metadata and data.
     */
    public UserSettings() {
        Map<String, Object> metadataMap = new HashMap<>();
        String USER_ID = UserStack.getInstance().getCurrentUserId();
        metadataMap.put("user_id", USER_ID);
        metadataMap.put("theme", Theme.LIGHT);
        metadataMap.put("resolution", Resolution.SD);
        metadataMap.put("currency", Currency.AUD);
        metadataMap.put("timezone", Timezone.AEST);

        this.metadata = new MetadataWrapper(metadataMap);

        this.data = new HashMap<>();
        this.data.put("language", Language.ENGLISH);
        this.data.put("access_level", AccessLevel.BASIC);
    }

    /*============================================================================================================================================================
    Code Description:
    - Method to get metadata, data, update metadata, and set data value.

    Methods:
        - getMetadata(): MetadataWrapper
        - getData(): Map<String, Object>
        - updateMetadata(String key, Object value): void
        - setDataValue(String key, Object value): void
        - toString(): String
    ============================================================================================================================================================*/

    public Map<String, Object> getData() { return data; }

    public MetadataWrapper getMetadata() { return metadata; }

    public void updateMetadata(String key, Object value) {
        Map<String, Object> modifiableMetadata = new HashMap<>(metadata.metadata());
        if (value != null) {
            modifiableMetadata.put(key, value);
        } else {
            modifiableMetadata.remove(key);
        }
        this.metadata = new MetadataWrapper(modifiableMetadata);
    }

    public void setDataValue(String key, Object value) {
        data.put(key, value);
    }

    @Override
    public String toString() {
        return String.format("UserSettings{Metadata: %s, Data: %s}", new HashMap<>(metadata.metadata()), new HashMap<>(data));
    }
}
