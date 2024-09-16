package teampixl.com.pixlpos.database.api;

import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.Ingredients;
import teampixl.com.pixlpos.models.MenuItem;

import java.util.List;

public class MenuAPI {
    private static MenuAPI instance;
    private static final DataStore dataStore = DataStore.getInstance();

    private MenuAPI() { }

    public static synchronized MenuAPI getInstance() {
        if (instance == null) {
            instance = new MenuAPI();
        }
        return instance;
    }

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
            putMenuItemNotes(menuItem.getMetadata().metadata().get("id").toString(), menuItemNotes);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.MENU_ITEM_CREATION_FAILED;
        }
    }

    public StatusCode putMenuItemName(String menuItemId, String menuItemName) {
        try {
            MenuItem menuItem = getMenuItemById(menuItemId);
            if (menuItem == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            StatusCode statusCode = validateMenuItemByName(menuItemName);
            if (statusCode != StatusCode.SUCCESS) {
                return statusCode;
            }

            menuItem.getMetadata().metadata().put("itemName", menuItemName);
            dataStore.updateMenuItem(menuItem);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.MENU_ITEM_UPDATE_FAILED;
        }
    }

    public StatusCode putMenuItemPrice(String menuItemId, double menuItemPrice) {
        try {
            MenuItem menuItem = getMenuItemById(menuItemId);
            if (menuItem == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            StatusCode statusCode = validateMenuItemByPrice(menuItemPrice);
            if (statusCode != StatusCode.SUCCESS) {
                return statusCode;
            }

            menuItem.getMetadata().metadata().put("itemPrice", menuItemPrice);
            dataStore.updateMenuItem(menuItem);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.MENU_ITEM_UPDATE_FAILED;
        }
    }

    public StatusCode putMenuItemCategory(String menuItemId, MenuItem.ItemType menuItemCategory) {
        try {
            MenuItem menuItem = getMenuItemById(menuItemId);
            if (menuItem == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            StatusCode statusCode = validateMenuItemByCategory(menuItemCategory);
            if (statusCode != StatusCode.SUCCESS) {
                return statusCode;
            }

            menuItem.getMetadata().metadata().put("itemCategory", menuItemCategory);
            dataStore.updateMenuItem(menuItem);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.MENU_ITEM_UPDATE_FAILED;
        }
    }

    public StatusCode putMenuItemIngredients(String menuItemName, List<String> menuItemIngredients, double numeral) {
        try {
            String id = getMenuItemByName(menuItemName);
            if (id == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            MenuItem menuItem = getMenuItemById(id);

            StatusCode statusCode = validateMenuItemByIngredients(menuItemIngredients);
            if (statusCode != StatusCode.SUCCESS) {
                return statusCode;
            }

            for (String ingredientId : menuItemIngredients) {
                Ingredients ingredient = IngredientsAPI.getInstance().getIngredientById(ingredientId);
                if (ingredient == null) {
                    return StatusCode.INVALID_INGREDIENT;
                }
                menuItem.addIngredient(ingredient, numeral);
            }

            dataStore.updateMenuItem(menuItem);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.MENU_ITEM_UPDATE_FAILED;
        }
    }

    public StatusCode putMenuItemStockStatus(String menuItemId, String menuItemStockStatus) {
        try {
            MenuItem menuItem = getMenuItemById(menuItemId);
            if (menuItem == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            StatusCode statusCode = validateMenuItemByStockStatus(menuItemStockStatus);
            if (statusCode != StatusCode.SUCCESS) {
                return statusCode;
            }

            menuItem.getMetadata().metadata().put("itemStockStatus", menuItemStockStatus);
            dataStore.updateMenuItem(menuItem);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.MENU_ITEM_UPDATE_FAILED;
        }
    }

    public StatusCode putMenuItemDescription(String menuItemId, String menuItemDescription) {
        try {
            MenuItem menuItem = getMenuItemById(menuItemId);
            if (menuItem == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            StatusCode statusCode = validateMenuItemByDescription(menuItemDescription);
            if (statusCode != StatusCode.SUCCESS) {
                return statusCode;
            }

            menuItem.getMetadata().metadata().put("itemDescription", menuItemDescription);
            dataStore.updateMenuItem(menuItem);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.MENU_ITEM_UPDATE_FAILED;
        }
    }

    public StatusCode putMenuItemNotes(String menuItemId, String menuItemNotes) {
        try {
            MenuItem menuItem = getMenuItemById(menuItemId);
            if (menuItem == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            StatusCode statusCode = validateMenuItemByNotes(menuItemNotes);
            if (statusCode != StatusCode.SUCCESS) {
                return statusCode;
            }

            menuItem.getMetadata().metadata().put("itemNotes", menuItemNotes);
            dataStore.updateMenuItem(menuItem);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.MENU_ITEM_UPDATE_FAILED;
        }
    }

    public StatusCode putMenuItemDietaryRequirement(String menuItemId, MenuItem.DietaryRequirement dietaryRequirement) {
        try {
            MenuItem menuItem = getMenuItemById(menuItemId);
            if (menuItem == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            menuItem.getMetadata().metadata().put("dietaryRequirement", dietaryRequirement);
            dataStore.updateMenuItem(menuItem);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.MENU_ITEM_UPDATE_FAILED;
        }
    }

    public StatusCode putMenuItemActiveStatus(String menuItemId, boolean activeItem) {
        try {
            MenuItem menuItem = getMenuItemById(menuItemId);
            if (menuItem == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            menuItem.getMetadata().metadata().put("activeItem", activeItem);
            dataStore.updateMenuItem(menuItem);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.MENU_ITEM_UPDATE_FAILED;
        }
    }


    public StatusCode deleteMenuItem(String menuItemId) {
        try {
            MenuItem menuItem = getMenuItemById(menuItemId);
            if (menuItem == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            dataStore.removeMenuItem(menuItem);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.MENU_ITEM_DELETION_FAILED;
        }
    }

}
