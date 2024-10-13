package teampixl.com.pixlpos.models;

import teampixl.com.pixlpos.models.tools.DataManager;
import teampixl.com.pixlpos.models.tools.MetadataWrapper;
import java.util.Map;
import java.util.HashMap;

import teampixl.com.pixlpos.database.api.UserStack;

/**
 * UserSettings class is a construct for creating UserSettings object. Extends DataManager.
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
 * @see DataManager
 * @see MetadataWrapper
 */

public class UserSettings extends DataManager {

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

    /**
     * Constructor for UserSettings object. Stores metadata and data.
     * Takes no parameters. These values are set to default values.
     */
    public UserSettings(String USER_ID) {
        super(initializeMetadata(USER_ID));

        this.data = new HashMap<>();
        this.data.put("language", Language.ENGLISH);
        this.data.put("access_level", AccessLevel.BASIC);
    }

    private static MetadataWrapper initializeMetadata(String USER_ID) {
        Map<String, Object> metadataMap = new HashMap<>();
        metadataMap.put("user_id", USER_ID);
        metadataMap.put("theme", Theme.LIGHT);
        metadataMap.put("resolution", Resolution.SD);
        metadataMap.put("currency", Currency.AUD);
        metadataMap.put("timezone", Timezone.AEST);
        return new MetadataWrapper(metadataMap);
    }
}
