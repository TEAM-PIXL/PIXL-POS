package teampixl.com.pixlpos.common;

import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.database.api.MenuAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for serializing and deserializing order notes
 */
public class OrderUtil {

    /**
     * Serialize the item notes to a string
     * @param itemNotes the map of menu items to their notes
     * @return the serialized string
     */
    public static String serializeItemNotes(Map<MenuItem, List<String>> itemNotes) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<MenuItem, List<String>> entry : itemNotes.entrySet()) {
            MenuItem menuItem = entry.getKey();
            List<String> notes = entry.getValue();
            if (notes != null && !notes.isEmpty()) {
                String itemId = (String) menuItem.getMetadataValue("id");
                if (itemId == null) {
                    continue;
                }
                sb.append(itemId).append(":");
                for (String note : notes) {
                    sb.append(escape(note)).append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append("|");
            }
        }
        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Deserialize the item notes from a string
     * @param specialRequests the serialized string
     * @param menuAPI the menu API to use for key transformation
     * @return the map of menu items to their notes
     */
    public static Map<MenuItem, List<String>> deserializeItemNotes(String specialRequests, MenuAPI menuAPI) {
        Map<MenuItem, List<String>> itemNotes = new HashMap<>();
        if (specialRequests == null || specialRequests.isEmpty()) {
            return itemNotes;
        }
        String[] items = specialRequests.split("\\|");
        for (String item : items) {
            String[] parts = item.split(":", 2);
            if (parts.length == 2) {
                String itemId = parts[0];
                String[] noteArray = parts[1].split(",");
                MenuItem menuItem = menuAPI.keyTransform(itemId);
                if (menuItem != null) {
                    List<String> notes = new java.util.ArrayList<>();
                    for (String note : noteArray) {
                        notes.add(unescape(note));
                    }
                    itemNotes.put(menuItem, notes);
                }
            }
        }
        return itemNotes;
    }

    private static String escape(String s) {
        return s.replace(",", "\\,").replace("|", "\\|").replace(":", "\\:");
    }

    private static String unescape(String s) {
        return s.replace("\\,", ",").replace("\\|", "|").replace("\\:", ":");
    }
}

