package teampixl.com.pixlpos.database.api.menuapi;

import teampixl.com.pixlpos.database.api.*;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.menuapi.*;
import teampixl.com.pixlpos.database.api.ingredientsapi.*;
import teampixl.com.pixlpos.database.api.stockapi.*;
import java.util.List;

public class MenuAPI {
    private static DataStore dataStore = DataStore.getInstance();

    public MenuAPI(DataStore dataStore) { MenuAPI.dataStore = dataStore; }

    public static StatusCode validateMenuItemByName(String menuItemName) {
        if (menuItemName == null || menuItemName.isEmpty()) {
            return StatusCode.INVALID_MENU_ITEM_NAME;
        }
        return StatusCode.SUCCESS;
    }

    public StatusCode validateMenuItemByPrice(double menuItemPrice) {
        if (menuItemPrice < 0) {
            return StatusCode.INVALID_MENU_ITEM_PRICE;
        }
        return StatusCode.SUCCESS;
    }

    public StatusCode validateMenuItemByCategory(MenuItem.ItemType menuItemCategory) {
        if (menuItemCategory == null || menuItemCategory.toString().isEmpty()) {
            return StatusCode.INVALID_MENU_ITEM_CATEGORY;
        }
        return StatusCode.SUCCESS;
    }

    public StatusCode validateMenuItemByIngredients(List<String> menuItemIngredients) {
        if (menuItemIngredients == null || menuItemIngredients.isEmpty()) {
            return StatusCode.INVALID_MENU_ITEM_INGREDIENTS;
        }
        return StatusCode.SUCCESS;
    }

    public StatusCode validateMenuItemByStockStatus(String menuItemStockStatus) {
        if (menuItemStockStatus == null || menuItemStockStatus.isEmpty()) {
            return StatusCode.INVALID_MENU_ITEM_STOCK_STATUS;
        }
        return StatusCode.SUCCESS;
    }

    public StatusCode validateMenuItemByDescription(String menuItemDescription) {
        if (menuItemDescription == null || menuItemDescription.isEmpty()) {
            return StatusCode.INVALID_MENU_ITEM_DESCRIPTION;
        }
        return StatusCode.SUCCESS;
    }

    public StatusCode validateMenuItemByNotes(String menuItemNotes) {
        if (menuItemNotes == null || menuItemNotes.isEmpty()) {
            return StatusCode.INVALID_MENU_ITEM_NOTES;
        }
        return StatusCode.SUCCESS;
    }

    public String getMenuItemByName(String menuItemName) {
        return dataStore.getMenuItems().stream()
                .filter(menuItem -> menuItem.getMetadata().metadata().get("itemName").equals(menuItemName))
                .findFirst()
                .map(menuItem -> menuItem.getMetadata().metadata().get("id").toString())
                .orElse(null);
    }

    public MenuItem getMenuItemById(String menuItemId) {
        return dataStore.getMenuItems().stream()
                .filter(menuItem -> menuItem.getMetadata().metadata().get("id").equals(menuItemId))
                .findFirst()
                .orElse(null);
    }

    public StatusCode postMenuItem(String menuItemName, double menuItemPrice, MenuItem.ItemType menuItemCategory, List<String> menuItemIngredients, String menuItemDescription, String menuItemNotes, MenuItem.DietaryRequirement dietaryRequirement, boolean activeItem) {
        try {
            List<StatusCode> statusCodes = List.of(
                    validateMenuItemByName(menuItemName),
                    validateMenuItemByPrice(menuItemPrice),
                    validateMenuItemByCategory(menuItemCategory),
                    validateMenuItemByIngredients(menuItemIngredients),
                    validateMenuItemByDescription(menuItemDescription),
                    validateMenuItemByNotes(menuItemNotes)
            );

            for (StatusCode statusCode : statusCodes) {
                if (statusCode != StatusCode.SUCCESS) {
                    return statusCode;
                }
            }

            MenuItem menuItem = new MenuItem(menuItemName, menuItemPrice, menuItemCategory, activeItem, menuItemDescription, dietaryRequirement);
            dataStore.addMenuItem(menuItem);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.MENU_ITEM_CREATION_FAILED;
        }
    }
}
