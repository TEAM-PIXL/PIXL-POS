package teampixl.com.pixlpos.database.api;

import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.Ingredients;
import teampixl.com.pixlpos.models.MenuItem;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import java.util.List;

public class MenuAPI {
    private static MenuAPI instance;
    private static final DataStore dataStore = DataStore.getInstance();

    private MenuAPI() { }

    /**
     * Get the instance of the MenuAPI.
     * @return MenuAPI instance.
     */
    public static synchronized MenuAPI getInstance() {
        if (instance == null) {
            instance = new MenuAPI();
        }
        return instance;
    }

    /**
     * Validate the menu item name.
     * @param menuItemName Name of the menu item.
     * @return StatusCode.
     */
    public static StatusCode validateMenuItemByName(String menuItemName) {
        if (menuItemName == null || menuItemName.isEmpty()) {
            return StatusCode.INVALID_MENU_ITEM_NAME;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validate the menu item price.
     * @param menuItemPrice Price of the menu item.
     * @return StatusCode.
     */
    public StatusCode validateMenuItemByPrice(double menuItemPrice) {
        if (menuItemPrice < 0) {
            return StatusCode.INVALID_MENU_ITEM_PRICE;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validate the menu item category.
     * @param menuItemCategory Category of the menu item.
     * @return StatusCode.
     */
    public StatusCode validateMenuItemByCategory(MenuItem.ItemType menuItemCategory) {
        if (menuItemCategory == null || menuItemCategory.toString().isEmpty()) {
            return StatusCode.INVALID_MENU_ITEM_CATEGORY;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validate the menu item ingredients.
     * @param menuItemIngredients Ingredients of the menu item.
     * @return StatusCode.
     */
    public StatusCode validateMenuItemByIngredients(List<String> menuItemIngredients) {
        if (menuItemIngredients == null || menuItemIngredients.isEmpty()) {
            return StatusCode.INVALID_MENU_ITEM_INGREDIENTS;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validate the menu item stock status.
     * @param menuItemStockStatus Stock status of the menu item.
     * @return StatusCode.
     */
    public StatusCode validateMenuItemByStockStatus(String menuItemStockStatus) {
        if (menuItemStockStatus == null || menuItemStockStatus.isEmpty()) {
            return StatusCode.INVALID_MENU_ITEM_STOCK_STATUS;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validate the menu item description.
     * @param menuItemDescription Description of the menu item.
     * @return StatusCode.
     */
    public StatusCode validateMenuItemByDescription(String menuItemDescription) {
        if (menuItemDescription == null || menuItemDescription.isEmpty()) {
            return StatusCode.INVALID_MENU_ITEM_DESCRIPTION;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validate the menu item notes.
     * @param menuItemNotes Notes of the menu item.
     * @return StatusCode.
     */
    public StatusCode validateMenuItemByNotes(String menuItemNotes) {
        if (menuItemNotes == null || menuItemNotes.isEmpty()) {
            return StatusCode.INVALID_MENU_ITEM_NOTES;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Get the menu item by name.
     * @param menuItemName Name of the menu item.
     * @return Menu item id.
     */
    public String getMenuItemByName(String menuItemName) {
        return dataStore.readMenuItems().stream()
                .filter(menuItem -> menuItem.getMetadata().metadata().get("itemName").equals(menuItemName))
                .findFirst()
                .map(menuItem -> menuItem.getMetadata().metadata().get("id").toString())
                .orElse(null);
    }

    /**
     * Get a menu item by id.
     * @param menuItemId id of the menu item.
     * @return Menu item.
     */
    public MenuItem getMenuItemById(String menuItemId) {
        return dataStore.readMenuItems().stream()
                .filter(menuItem -> menuItem.getMetadata().metadata().get("id").equals(menuItemId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Post a menu item.
     * @param menuItemName Name of the menu item.
     * @param menuItemPrice Price of the menu item.
     * @param menuItemCategory Category of the menu item.
     * @param menuItemDescription Description of the menu item.
     * @param menuItemNotes Notes of the menu item.
     * @param dietaryRequirement Dietary requirement of the menu item.
     * @param activeItem Active status of the menu item.
     * @return StatusCode.
     */
    public StatusCode postMenuItem(String menuItemName, double menuItemPrice, boolean activeItem, MenuItem.ItemType menuItemCategory, String menuItemDescription, String menuItemNotes, MenuItem.DietaryRequirement dietaryRequirement) {
        try {
            List<StatusCode> statusCodes = List.of(
                    validateMenuItemByName(menuItemName),
                    validateMenuItemByPrice(menuItemPrice),
                    validateMenuItemByCategory(menuItemCategory),
                    validateMenuItemByDescription(menuItemDescription)
            );

            for (StatusCode statusCode : statusCodes) {
                if (statusCode != StatusCode.SUCCESS) {
                    return statusCode;
                }
            }

            MenuItem menuItem = new MenuItem(menuItemName, menuItemPrice, menuItemCategory, activeItem, menuItemDescription, dietaryRequirement);
            dataStore.createMenuItem(menuItem);
            return putMenuItemNotes(menuItemName, menuItemNotes);
        } catch (Exception e) {
            return StatusCode.MENU_ITEM_CREATION_FAILED;
        }
    }

    /**
     * Post a menu item.
     * @param menuItemName Name of the menu item.
     * @param menuItemPrice Price of the menu item.
     * @param menuItemCategory Category of the menu item.
     * @param menuItemDescription Description of the menu item.
     * @return StatusCode.
     */
    public StatusCode postMenuItem(String menuItemName, double menuItemPrice, MenuItem.ItemType menuItemCategory, String menuItemDescription) {
        return postMenuItem(menuItemName, menuItemPrice, true, menuItemCategory, menuItemDescription, null, MenuItem.DietaryRequirement.NONE);
    }

    /**
     * Put a menu item name.
     * @param menuItemName Name of the menu item.
     * @param newMenuItemName New name of the menu item.
     * @return StatusCode.
     */
    public StatusCode putMenuItemName(String menuItemName, String newMenuItemName) {
        try {
            String id = getMenuItemByName(menuItemName);
            if (id == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            MenuItem menuItem = getMenuItemById(id);
            StatusCode statusCode = validateMenuItemByName(newMenuItemName);
            if (statusCode != StatusCode.SUCCESS) {
                return statusCode;
            }

            menuItem.getMetadata().metadata().put("itemName", newMenuItemName);
            dataStore.updateMenuItem(menuItem);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.MENU_ITEM_UPDATE_FAILED;
        }
    }

    /**
     * Put a menu item price.
     * @param menuItemName Name of the menu item.
     * @param menuItemPrice Price of the menu item.
     * @return StatusCode.
     */
    public StatusCode putMenuItemPrice(String menuItemName, double menuItemPrice) {
        try {
            String id = getMenuItemByName(menuItemName);
            if (id == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            MenuItem menuItem = getMenuItemById(id);
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

    /**
     * Put a menu item category.
     * @param menuItemName Name of the menu item.
     * @param menuItemCategory Category of the menu item.
     * @return StatusCode.
     */
    public StatusCode putMenuItemCategory(String menuItemName, MenuItem.ItemType menuItemCategory) {
        try {
            String id = getMenuItemByName(menuItemName);
            if (id == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            MenuItem menuItem = getMenuItemById(id);
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

    /**
     * Put a menu item ingredients.
     * @param menuItemName Name of the menu item.
     * @param menuItemIngredients Ingredients of the menu item.
     * @param numeral Numeral of the menu item.
     * @return StatusCode.
     */
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

    /**
     * Put a menu item stock status.
     * @param menuItemName Name of the menu item.
     * @param menuItemStockStatus Stock status of the menu item.
     * @return StatusCode.
     */
    public StatusCode putMenuItemStockStatus(String menuItemName, String menuItemStockStatus) {
        try {
            String id = getMenuItemByName(menuItemName);
            if (id == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            MenuItem menuItem = getMenuItemById(id);
            StatusCode statusCode = validateMenuItemByStockStatus(menuItemStockStatus);
            if (statusCode != StatusCode.SUCCESS) {
                return statusCode;
            }

            menuItem.getMetadata().metadata().put("stockStatus", menuItemStockStatus);
            dataStore.updateMenuItem(menuItem);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.MENU_ITEM_UPDATE_FAILED;
        }
    }

    /**
     * Put a menu item description.
     * @param menuItemName Name of the menu item.
     * @param menuItemDescription Description of the menu item.
     * @return StatusCode.
     */
    public StatusCode putMenuItemDescription(String menuItemName, String menuItemDescription) {
        try {
            String id = getMenuItemByName(menuItemName);
            if (id == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            MenuItem menuItem = getMenuItemById(id);
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

    /**
     * Put a menu item notes.
     * @param menuItemName Name of the menu item.
     * @param menuItemNotes Notes of the menu item.
     * @return StatusCode.
     */
    public StatusCode putMenuItemNotes(String menuItemName, String menuItemNotes) {
        try {
            String id = getMenuItemByName(menuItemName);
            if (id == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            MenuItem menuItem = getMenuItemById(id);
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

    /**
     * Put a menu item dietary requirement.
     * @param menuItemName Name of the menu item.
     * @param dietaryRequirement Dietary requirement of the menu item.
     * @return StatusCode.
     */
    public StatusCode putMenuItemDietaryRequirement(String menuItemName, MenuItem.DietaryRequirement dietaryRequirement) {
        try {
            String id = getMenuItemByName(menuItemName);
            if (id == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            MenuItem menuItem = getMenuItemById(id);
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

    /**
     * Put a menu item active status.
     * @param menuItemName Name of the menu item.
     * @param activeItem Active status of the menu item.
     * @return StatusCode.
     */
    public StatusCode putMenuItemActiveStatus(String menuItemName, boolean activeItem) {
        try {
            String id = getMenuItemByName(menuItemName);
            if (id == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            MenuItem menuItem = getMenuItemById(id);
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

    /**
     * Delete a menu item.
     * @param menuItemName Name of the menu item.
     * @return StatusCode.
     */
    public StatusCode deleteMenuItem(String menuItemName) {
        try {
            String id = getMenuItemByName(menuItemName);
            if (id == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            MenuItem menuItem = getMenuItemById(id);
            if (menuItem == null) {
                return StatusCode.MENU_ITEM_NOT_FOUND;
            }

            dataStore.deleteMenuItem(menuItem);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.MENU_ITEM_DELETION_FAILED;
        }
    }

    /**
     * Search for menu items.
     * @param query Search query.
     * @return List of menu items.
     */
    public List<MenuItem> searchMenuItem(String query) {
        String[] parts = query.trim().split("\\s+");

        return dataStore.readMenuItems().parallelStream()
                .filter(menuItem -> Arrays.stream(parts).allMatch(part -> {
                    String lowerCasePart = part.toLowerCase();
                    return menuItem.getMetadata().metadata().values().stream()
                            .filter(Objects::nonNull)
                            .anyMatch(value -> value.toString().toLowerCase().contains(lowerCasePart)) ||
                            menuItem.getData().values().stream()
                                    .filter(Objects::nonNull)
                                    .anyMatch(value -> value.toString().toLowerCase().contains(lowerCasePart));
                }))
                .collect(Collectors.toList());
    }

}
