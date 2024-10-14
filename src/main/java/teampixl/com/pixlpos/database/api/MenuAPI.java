package teampixl.com.pixlpos.database.api;

import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.database.api.util.Util;
import teampixl.com.pixlpos.models.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.util.Pair;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * The MenuAPI class is a singleton responsible for managing menu items in memory.
 * It provides methods to create, read, update, and delete menu items, with robust validation and error handling.
 */
public class MenuAPI {
    private static MenuAPI INSTANCE;
    private static DataStore DATA_STORE;

    private MenuAPI() { initializeDependencies(); }

    /**
     * Gets the singleton instance of the MenuAPI.
     *
     * @return the instance of the MenuAPI
     */
    public static synchronized MenuAPI getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MenuAPI();
        }
        return INSTANCE;
    }

    private void initializeDependencies() {
        while ((DATA_STORE = DataStore.getInstance()) == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Validates the menu item ID.
     *
     * @param MENU_ITEM_ID the ID to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateMenuItemById(Object MENU_ITEM_ID) {
        return Util.validateNotNullOrEmpty((String) MENU_ITEM_ID, StatusCode.INVALID_MENU_ITEM_ID, StatusCode.INVALID_MENU_ITEM_ID);
    }

    /**
     * Validates the name of a menu item.
     *
     * @param MENU_ITEM_NAME the name to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateMenuItemByName(String MENU_ITEM_NAME) {
        if (MENU_ITEM_NAME == null || MENU_ITEM_NAME.trim().isEmpty()) {
            return StatusCode.INVALID_MENU_ITEM_NAME;
        }
        if (MENU_ITEM_NAME.length() < 3) {
            return StatusCode.MENU_ITEM_NAME_TOO_SHORT;
        }
        if (MENU_ITEM_NAME.length() > 50) {
            return StatusCode.MENU_ITEM_NAME_TOO_LONG;
        }
        if (MENU_ITEM_NAME.matches(".*\\d.*")) {
            return StatusCode.MENU_ITEM_NAME_CONTAINS_DIGITS;
        }
        if (MENU_ITEM_NAME.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            return StatusCode.MENU_ITEM_NAME_CONTAINS_SPECIAL_CHARACTERS;
        }
        boolean MENU_ITEM_EXISTS = DATA_STORE.readMenuItems().stream()
                .anyMatch(ITEM -> ITEM.getMetadata().metadata().get("itemName").toString().equalsIgnoreCase(MENU_ITEM_NAME));
        return MENU_ITEM_EXISTS ? StatusCode.MENU_ITEM_NAME_TAKEN : StatusCode.SUCCESS;
    }

    /**
     * Validates the price of a menu item.
     *
     * @param MENU_ITEM_PRICE the price to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateMenuItemByPrice(Double MENU_ITEM_PRICE) {
        if (MENU_ITEM_PRICE == null) {
            return StatusCode.INVALID_MENU_ITEM_PRICE;
        }
        if (MENU_ITEM_PRICE < 0) {
            return StatusCode.MENU_ITEM_PRICE_NEGATIVE;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the item type of menu item.
     *
     * @param MENU_ITEM_TYPE the item type to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateMenuItemByItemType(MenuItem.ItemType MENU_ITEM_TYPE) {
        if (MENU_ITEM_TYPE == null) {
            return StatusCode.INVALID_MENU_ITEM_CATEGORY;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the description of a menu item.
     *
     * @param MENU_ITEM_DESCRIPTION the description to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateMenuItemByDescription(String MENU_ITEM_DESCRIPTION) {
        if (MENU_ITEM_DESCRIPTION == null || MENU_ITEM_DESCRIPTION.trim().isEmpty()) {
            return StatusCode.INVALID_MENU_ITEM_DESCRIPTION;
        }
        if (MENU_ITEM_DESCRIPTION.length() > 500) {
            return StatusCode.MENU_ITEM_DESCRIPTION_TOO_LONG;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the notes of a menu item.
     *
     * @param MENU_ITEM_NOTES the notes to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateMenuItemByNotes(String MENU_ITEM_NOTES) {
        if (MENU_ITEM_NOTES != null && MENU_ITEM_NOTES.length() > 500) {
            return StatusCode.MENU_ITEM_NOTES_TOO_LONG;
        }
        return StatusCode.SUCCESS;
    }

    public StatusCode validateMenuItemByStatus(Boolean ACTIVE_ITEM) {
        if (ACTIVE_ITEM == null) {
            return StatusCode.INVALID_MENU_ITEM_STATUS;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates a menu item object.
     *
     * @param MENUITEM the menu item to validate
     * @return a list of status codes indicating the result of the validation
     */
    public List<StatusCode> validateMenuItem(MenuItem MENUITEM) {
        ArrayList<StatusCode> VALIDATIONS = new ArrayList<>();
        if (MENUITEM == null) {
            return List.of(StatusCode.INVALID_MENU_ITEM);
        }

        String MENU_ITEM_NAME = MENUITEM.getMetadata().metadata().get("itemName").toString();
        Double MENU_ITEM_PRICE = (Double) MENUITEM.getMetadata().metadata().get("price");
        MenuItem.ItemType MENU_ITEM_TYPE = (MenuItem.ItemType) MENUITEM.getMetadata().metadata().get("itemType");
        String MENU_ITEM_DESCRIPTION = (String) MENUITEM.getData().get("description");
        String MENU_ITEM_NOTES = (String) MENUITEM.getData().get("notes");

        VALIDATIONS.add(validateMenuItemByName(MENU_ITEM_NAME));
        VALIDATIONS.add(validateMenuItemByPrice(MENU_ITEM_PRICE));
        VALIDATIONS.add(validateMenuItemByItemType(MENU_ITEM_TYPE));
        VALIDATIONS.add(validateMenuItemByDescription(MENU_ITEM_DESCRIPTION));
        VALIDATIONS.add(validateMenuItemByNotes(MENU_ITEM_NOTES));

        return VALIDATIONS;
    }

    /**
     * Validates a menu item object for order.
     *
     * @param menuItem the menu item to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateMenuItemForOrder(MenuItem menuItem) {
        if (menuItem == null) {
            return StatusCode.INVALID_MENU_ITEM;
        }
        String menuItemId = (String) menuItem.getMetadata().metadata().get("id");
        MenuItem storedMenuItem = keyTransform(menuItemId);
        if (storedMenuItem == null) {
            return StatusCode.MENU_ITEM_NOT_FOUND;
        }
        Boolean isActive = (Boolean) storedMenuItem.getMetadata().metadata().get("active");
        if (isActive != null && !isActive) {
            return StatusCode.MENU_ITEM_INACTIVE;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates and retrieves a MenuItem object.
     *
     * @param FIELD the field to validate
     * @param VALUE the value to validate
     * @param MENU_ITEM_NAME the name of the menu item
     * @return a Pair containing the list of StatusCodes and the MenuItem object
     */
    public Pair<List<StatusCode>, MenuItem> validateAndGetMenuItem(String FIELD, Object VALUE, String MENU_ITEM_NAME) {
        Function<Object, StatusCode> validationFunction = val -> {
            try {
                Class<?> VALUE_TYPE = VALUE.getClass();
                if (VALUE_TYPE == Boolean.class) {
                    VALUE_TYPE = boolean.class;
                }
                Method VALIDATION_METHOD = this.getClass().getMethod("validateMenuItemBy" + FIELD, VALUE_TYPE);
                return (StatusCode) VALIDATION_METHOD.invoke(this, val);
            } catch (NoSuchMethodException e) {
                return StatusCode.INTERNAL_METHOD_NOT_FOUND;
            } catch (Exception e) {
                return StatusCode.INTERNAL_FAILURE;
            }
        };

        Function<String, MenuItem> retrievalFunction = this::getMenuItem;

        return Util.validateAndGetObject(
                validationFunction,
                retrievalFunction,
                VALUE,
                MENU_ITEM_NAME,
                StatusCode.MENU_ITEM_NOT_FOUND
        );
    }

    /**
     * Retrieves the menu item ID based on the item name.
     *
     * @param MENU_ITEM_NAME the name of the menu item
     * @return the menu item ID
     */
    public String keySearch(String MENU_ITEM_NAME) {
        if (MENU_ITEM_NAME == null) {
            return null;
        }
        return DATA_STORE.readMenuItems().stream()
                .filter(MENU_ITEM -> MENU_ITEM.getMetadata().metadata().get("itemName").toString().equalsIgnoreCase(MENU_ITEM_NAME))
                .findFirst()
                .map(MENU_ITEM -> MENU_ITEM.getMetadata().metadata().get("id").toString())
                .orElse(null);
    }

    /**
     * Retrieves the menu item name based on the item ID.
     *
     * @param MENU_ITEM_ID the ID of the menu item
     * @return the menu item name
     */
    public String reverseKeySearch(String MENU_ITEM_ID) {
        if (MENU_ITEM_ID == null) {
            return null;
        }
        return DATA_STORE.readMenuItems().stream()
                .filter(MENU_ITEM -> MENU_ITEM.getMetadata().metadata().get("id").toString().equals(MENU_ITEM_ID))
                .findFirst()
                .map(MENU_ITEM -> MENU_ITEM.getMetadata().metadata().get("itemName").toString())
                .orElse(null);
    }

    /**
     * Transforms a menu item ID into a MenuItem object.
     *
     * @param MENU_ITEM_ID the ID of the menu item
     * @return the MenuItem object
     */
    public MenuItem keyTransform(String MENU_ITEM_ID) {
        if (MENU_ITEM_ID == null) {
            return null;
        }
        return DATA_STORE.readMenuItems().stream()
                .filter(MENU_ITEM -> MENU_ITEM.getMetadata().metadata().get("id").toString().equals(MENU_ITEM_ID))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all menu items from the database.
     *
     * @return a list of menu items
     */
    public List<MenuItem> getMenuItems() {
        return DATA_STORE.readMenuItems();
    }

    /**
     * Gets a MenuItem object based on its name.
     *
     * @param MENU_ITEM_NAME the name of the menu item
     * @return the MenuItem object
     */
    public MenuItem getMenuItem(String MENU_ITEM_NAME) {
        String ID = keySearch(MENU_ITEM_NAME);
        return keyTransform(ID);
    }

    /**
     * Creates a new menu item and adds it to the database.
     *
     * @param MENU_ITEM_NAME the name of the menu item
     * @param MENU_ITEM_PRICE the price of the menu item
     * @param ACTIVE_ITEM the active status of the menu item
     * @param MENU_ITEM_TYPE the type of the menu item (ItemType)
     * @param MENU_ITEM_DESCRIPTION the description of the menu item
     * @param MENU_ITEM_NOTES the notes for the menu item
     * @param DIETARY_REQUIREMENT the dietary requirement of the menu item
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> postMenuItem(String MENU_ITEM_NAME, Double MENU_ITEM_PRICE, boolean ACTIVE_ITEM,
                                         MenuItem.ItemType MENU_ITEM_TYPE, String MENU_ITEM_DESCRIPTION,
                                         String MENU_ITEM_NOTES, MenuItem.DietaryRequirement DIETARY_REQUIREMENT) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        VALIDATIONS.add(validateMenuItemByName(MENU_ITEM_NAME));
        VALIDATIONS.add(validateMenuItemByPrice(MENU_ITEM_PRICE));
        VALIDATIONS.add(validateMenuItemByItemType(MENU_ITEM_TYPE));
        VALIDATIONS.add(validateMenuItemByDescription(MENU_ITEM_DESCRIPTION));
        VALIDATIONS.add(validateMenuItemByNotes(MENU_ITEM_NOTES));

        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }

        MenuItem EXISTING_MENU_ITEM = getMenuItem(MENU_ITEM_NAME);
        if (EXISTING_MENU_ITEM != null) {
            VALIDATIONS.add(StatusCode.MENU_ITEM_ALREADY_EXISTS);
            return VALIDATIONS;
        }

        try {
            MenuItem MENU_ITEM = new MenuItem(MENU_ITEM_NAME, MENU_ITEM_PRICE, MENU_ITEM_TYPE, ACTIVE_ITEM,
                    MENU_ITEM_DESCRIPTION, DIETARY_REQUIREMENT);

            if (MENU_ITEM_NOTES != null) {
                MENU_ITEM.setDataValue("notes", MENU_ITEM_NOTES);
            }

            DATA_STORE.createMenuItem(MENU_ITEM);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.MENU_ITEM_CREATION_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Overloaded method to create a new menu item with minimal parameters.
     *
     * @param MENU_ITEM_NAME the name of the menu item
     * @param MENU_ITEM_PRICE the price of the menu item
     * @param MENU_ITEM_TYPE the type of the menu item
     * @param MENU_ITEM_DESCRIPTION the description of the menu item
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> postMenuItem(String MENU_ITEM_NAME, Double MENU_ITEM_PRICE, MenuItem.ItemType MENU_ITEM_TYPE,
                                         String MENU_ITEM_DESCRIPTION) {
        return postMenuItem(MENU_ITEM_NAME, MENU_ITEM_PRICE, true, MENU_ITEM_TYPE, MENU_ITEM_DESCRIPTION, null, MenuItem.DietaryRequirement.NONE);
    }

    /**
     * Updates the name of an existing menu item.
     *
     * @param MENU_ITEM_NAME the current name of the menu item
     * @param NEW_MENU_ITEM_NAME the new name for the menu item
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> putMenuItemName(String MENU_ITEM_NAME, String NEW_MENU_ITEM_NAME) {

        Pair<List<StatusCode>, MenuItem> RESULT = validateAndGetMenuItem("Name", NEW_MENU_ITEM_NAME, MENU_ITEM_NAME);
        List<StatusCode> VALIDATIONS = new ArrayList<>(RESULT.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }

        MenuItem MENU_ITEM = RESULT.getValue();

        try {
            MENU_ITEM.updateMetadata("itemName", NEW_MENU_ITEM_NAME);
            DATA_STORE.updateMenuItem(MENU_ITEM);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.MENU_ITEM_UPDATE_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Updates the price of an existing menu item.
     *
     * @param MENU_ITEM_NAME the name of the menu item
     * @param NEW_MENU_ITEM_PRICE the new price for the menu item
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> putMenuItemPrice(String MENU_ITEM_NAME, Double NEW_MENU_ITEM_PRICE) {

        Pair<List<StatusCode>, MenuItem> RESULT = validateAndGetMenuItem("Price", NEW_MENU_ITEM_PRICE, MENU_ITEM_NAME);
        List<StatusCode> VALIDATIONS = new ArrayList<>(RESULT.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }

        MenuItem MENU_ITEM = RESULT.getValue();

        try {
            MENU_ITEM.updateMetadata("price", NEW_MENU_ITEM_PRICE);
            DATA_STORE.updateMenuItem(MENU_ITEM);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.MENU_ITEM_UPDATE_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Updates the item type of existing menu item.
     *
     * @param MENU_ITEM_NAME the name of the menu item
     * @param NEW_MENU_ITEM_TYPE the new item type for the menu item
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> putMenuItemItemType(String MENU_ITEM_NAME, MenuItem.ItemType NEW_MENU_ITEM_TYPE) {

        Pair<List<StatusCode>, MenuItem> RESULT = validateAndGetMenuItem("ItemType", NEW_MENU_ITEM_TYPE, MENU_ITEM_NAME);
        List<StatusCode> VALIDATIONS = new ArrayList<>(RESULT.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }

        MenuItem MENU_ITEM = RESULT.getValue();

        try {
            MENU_ITEM.updateMetadata("itemType", NEW_MENU_ITEM_TYPE);
            DATA_STORE.updateMenuItem(MENU_ITEM);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.MENU_ITEM_UPDATE_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Updates the description of an existing menu item.
     *
     * @param MENU_ITEM_NAME the name of the menu item
     * @param NEW_MENU_ITEM_DESCRIPTION the new description for the menu item
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> putMenuItemDescription(String MENU_ITEM_NAME, String NEW_MENU_ITEM_DESCRIPTION) {

        Pair<List<StatusCode>, MenuItem> RESULT = validateAndGetMenuItem("Description", NEW_MENU_ITEM_DESCRIPTION, MENU_ITEM_NAME);
        List<StatusCode> VALIDATIONS = new ArrayList<>(RESULT.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }

        MenuItem MENU_ITEM = RESULT.getValue();

        try {
            MENU_ITEM.setDataValue("description", NEW_MENU_ITEM_DESCRIPTION);
            DATA_STORE.updateMenuItem(MENU_ITEM);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.MENU_ITEM_UPDATE_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Updates the notes of an existing menu item.
     *
     * @param MENU_ITEM_NAME the name of the menu item
     * @param NEW_MENU_ITEM_NOTES the new notes for the menu item
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> putMenuItemNotes(String MENU_ITEM_NAME, String NEW_MENU_ITEM_NOTES) {

        Pair<List<StatusCode>, MenuItem> RESULT = validateAndGetMenuItem("Notes", NEW_MENU_ITEM_NOTES, MENU_ITEM_NAME);
        List<StatusCode> VALIDATIONS = new ArrayList<>(RESULT.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }

        MenuItem MENU_ITEM = RESULT.getValue();

        try {
            MENU_ITEM.setDataValue("notes", NEW_MENU_ITEM_NOTES);
            DATA_STORE.updateMenuItem(MENU_ITEM);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.MENU_ITEM_UPDATE_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Updates the status of an existing menu item.
     *
     * @param MENU_ITEM_NAME the name of the menu item
     * @param ACTIVE_ITEM the new status for the menu item
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> putMenuItemStatus(String MENU_ITEM_NAME, boolean ACTIVE_ITEM) {
        Pair<List<StatusCode>, MenuItem> RESULT = validateAndGetMenuItem("Status", ACTIVE_ITEM, MENU_ITEM_NAME);
        List<StatusCode> VALIDATIONS = new ArrayList<>(RESULT.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }

        MenuItem MENU_ITEM = RESULT.getValue();

        try {
            MENU_ITEM.updateMetadata("activeItem", ACTIVE_ITEM);
            DATA_STORE.updateMenuItem(MENU_ITEM);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.MENU_ITEM_UPDATE_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Deletes a menu item from the database.
     *
     * @param MENU_ITEM_NAME the name of the menu item to delete
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> deleteMenuItem(String MENU_ITEM_NAME) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        MenuItem MENU_ITEM = getMenuItem(MENU_ITEM_NAME);
        if (MENU_ITEM == null) {
            VALIDATIONS.add(StatusCode.MENU_ITEM_NOT_FOUND);
            return VALIDATIONS;
        }

        try {
            DATA_STORE.deleteMenuItem(MENU_ITEM);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.MENU_ITEM_DELETION_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Searches for menu items in the database based on a query string.
     *
     * @param QUERY the search query
     * @return a list of menu items matching the query
     */
    public List<MenuItem> searchMenuItem(String QUERY) {
        if (QUERY == null || QUERY.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String[] TOKENS = QUERY.trim().toLowerCase().split("\\s+");

        return DATA_STORE.readMenuItems().parallelStream()
                .filter(MENU_ITEM -> {
                    String ITEM_NAME = MENU_ITEM.getMetadata().metadata().get("itemName").toString().toLowerCase();
                    return TOKENS.length == 1
                            ? ITEM_NAME.contains(TOKENS[0])
                            : ITEM_NAME.contains(TOKENS[0]) && ITEM_NAME.contains(TOKENS[1]);
                })
                .collect(Collectors.toList());
    }
}
