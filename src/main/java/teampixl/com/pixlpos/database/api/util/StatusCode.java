package teampixl.com.pixlpos.database.api.util;

/**
 * Enum containing status codes for the API.
 */
public enum StatusCode {
    /* ---> GENERAL STATUS CODES <---- */
    SUCCESS,
    FAILURE,
    INTERNAL_FAILURE,
    INTERNAL_METHOD_NOT_FOUND,

    /* ---> USER CHECK STATUS CODES <---- */
    USERNAME_NULL,
    USER_ALREADY_EXISTS,
    USER_NOT_FOUND,

    /* ---> USERNAME STATUS CODES <---- */
    USERNAME_TAKEN,
    USERNAME_TOO_SHORT,
    USERNAME_TOO_LONG,
    USERNAME_INVALID_CHARACTERS,
    USERNAME_ONLY_DIGITS,
    USERNAME_CONTAINS_SPACES,

    /* ---> EMAIL STATUS CODES <---- */
    EMAIL_TAKEN,
    EMAIL_INVALID_FORMAT,
    EMAIL_NULL,
    EMAIL_CONTAINS_SPACES,

    /* ---> PASSWORD STATUS CODES <---- */
    PASSWORD_TOO_SHORT,
    PASSWORD_TOO_LONG,
    PASSWORD_INVALID_CHARACTERS,
    PASSWORD_CONTAINS_SPACES,
    PASSWORD_NULL,
    PASSWORD_NO_DIGITS,
    PASSWORD_NO_UPPERCASE,
    PASSWORD_NO_LOWERCASE,
    PASSWORD_NO_SPECIAL_CHAR,

    /* ---> USER APPLICATION STATUS CODES <---- */
    USER_PUT_FAILED,
    USER_REGISTRATION_FAILED,
    USER_POST_FAILED,
    USER_DELETION_FAILED,
    MULTIPLE_USERS_FOUND,
    USER_INACTIVE,

    /* ---> VALIDATION STATUS CODES <---- */
    INVALID_USER_ID,
    INVALID_USERNAME,
    INVALID_EMAIL,
    INVALID_FIRST_NAME,
    INVALID_LAST_NAME,
    INVALID_NAME,
    INVALID_PASSWORD,
    INVALID_USER_ROLE,
    INVALID_USER_STATUS,
    INVALID_USER_ADDITIONAL_INFO,

    /* ---> INGREDIENT CHECK STATUS CODES <---- */
    INGREDIENT_NOT_FOUND,

    /* ---> INGREDIENT NAME STATUS CODES <---- */
    INGREDIENT_NAME_EMPTY,
    INGREDIENT_NAME_TOO_LONG,
    INGREDIENT_NAME_TOO_SHORT,
    INGREDIENT_NAME_CONTAINS_DIGITS,
    INGREDIENT_NAME_CONTAINS_SPECIAL_CHARACTERS,
    INGREDIENT_NAME_TAKEN,

    /* ---> INGREDIENT APPLICATION STATUS CODES <---- */
    INGREDIENT_PUT_FAILED,
    INGREDIENT_POST_FAILED,
    INGREDIENT_DELETION_FAILED,
    INGREDIENT_NAME_UPDATE_FAILED,
    INGREDIENT_NOTES_UPDATE_FAILED,

    /* ---> INGREDIENT NOTES STATUS CODES <---- */
    INGREDIENT_NOTES_TOO_LONG,

    /* ---> INGREDIENT VALIDATION STATUS CODES <---- */
    INVALID_INGREDIENT_NAME,
    INVALID_INGREDIENT_NOTES,
    INVALID_INGREDIENT,
    INVALID_INGREDIENT_QUANTITY,
    INVALID_INGREDIENT_UNIT,
    INVALID_INGREDIENT_STATUS,

    /* ---> STOCK CHECK STATUS CODES <---- */
    STOCK_NOT_FOUND,

    /* ---> STOCK QUANTITY & UNIT STATUS CODES <---- */
    INVALID_STOCK_QUANTITY,
    INVALID_STOCK_UNIT,
    INVALID_STOCK_ONORDER,
    STOCK_QUANTITY_MUST_BE_INTEGER,
    STOCK_QUANTITY_MUST_BE_DOUBLE,

    /* ---> STOCK APPLICATION STATUS CODES <---- */
    STOCK_UPDATE_FAILED,
    STOCK_CREATION_FAILED,
    STOCK_DELETION_FAILED,
    STOCK_ALREADY_EXISTS,

    /* ---> STOCK VALIDATION STATUS CODES <---- */
    INVALID_STOCK_STATUS,
    INVALID_INGREDIENT_ID,
    INVALID_STOCK_QUANTITY_TYPE,
    INVALID_STOCK_UNIT_TYPE,

    /* ---> MENU ITEM NAME STATUS CODES <---- */
    MENU_ITEM_NAME_TOO_SHORT,
    MENU_ITEM_NAME_TOO_LONG,
    MENU_ITEM_NAME_CONTAINS_DIGITS,
    MENU_ITEM_NAME_CONTAINS_SPECIAL_CHARACTERS,
    MENU_ITEM_NAME_TAKEN,

    /* ---> MENU ITEM PRICE STATUS CODES <---- */
    MENU_ITEM_PRICE_TOO_LOW,
    MENU_ITEM_PRICE_TOO_HIGH,
    MENU_ITEM_PRICE_NEGATIVE,

    /* ---> MENU ITEM DESCRIPTION STATUS CODES <---- */
    MENU_ITEM_DESCRIPTION_TOO_LONG,

    /* ---> MENU ITEM NOTES STATUS CODES <---- */
    MENU_ITEM_NOTES_TOO_LONG,

    /* ---> MENU ITEM APPLICATION STATUS CODES <---- */
    MENU_ITEM_NOT_FOUND,
    MENU_ITEM_UPDATE_FAILED,
    MENU_ITEM_CREATION_FAILED,
    MENU_ITEM_DELETION_FAILED,
    MENU_ITEM_ALREADY_EXISTS,
    MENU_ITEM_INACTIVE,

    /* ---> MENU ITEM VALIDATION STATUS CODES <---- */
    INVALID_MENU_ITEM_NAME,
    INVALID_MENU_ITEM_PRICE,
    INVALID_MENU_ITEM_CATEGORY,
    INVALID_MENU_ITEM_INGREDIENTS,
    INVALID_MENU_ITEM_STOCK_STATUS,
    INVALID_MENU_ITEM_DESCRIPTION,
    INVALID_MENU_ITEM_NOTES,
    INVALID_MENU_ITEM,
    INVALID_MENU_ITEM_STATUS,

    /* ---> ORDER APPLICATION STATUS CODES <---- */
    ORDER_NOT_FOUND,
    ORDER_UPDATE_FAILED,
    ORDER_CREATION_FAILED,
    ORDER_DELETION_FAILED,
    ORDER_ALREADY_EXISTS,
    POST_ORDER_FAILED,

    /* ---> ORDER VALIDATION STATUS CODES <---- */
    INVALID_ORDER,
    INVALID_ORDER_ID,
    INVALID_ORDER_STATUS,
    INVALID_ORDER_DATE,
    INVALID_ORDER_TIME,
    INVALID_ORDER_TOTAL,
    INVALID_ORDER_ITEMS,

    /* ---> ORDER ITEM VALIDATION STATUS CODES <---- */
    INVALID_ORDER_ITEM,
    INVALID_ORDER_ITEM_QUANTITY,
    INVALID_ORDER_NUMBER,
    INVALID_ORDER_ITEM_STATUS,
    INVALID_ORDER_ITEM_ID,
    INVALID_ORDER_ITEM_NOTES,
    INVALID_ORDER_ITEM_PRICE,
    ORDER_ITEM_NOT_FOUND,
    QUANTITY_EXCEEDS_ORDER,
    INVALID_QUANTITY,

    /* ---> ORDER ITEM APPLICATION STATUS CODES <---- */
    ORDER_ITEM_UPDATE_FAILED,
    ORDER_ITEM_CREATION_FAILED,
    ORDER_ITEM_DELETION_FAILED,

    /* ---> ORDER SPECIAL REQUEST STATUS CODES <---- */
    INVALID_SPECIAL_REQUESTS,
    SPECIAL_REQUESTS_TOO_LONG,

    /* ---> ORDER CUSTOMER STATUS CODES <---- */
    INVALID_CUSTOMERS,
    INVALID_TABLE_NUMBER,

    /* ---> MENU ITEM CHECK STATUS CODES <---- */
    UNKNOWN_MENU_ITEM,
    INVALID_MENU_ITEM_ID,
    NO_ORDER_ITEMS
}
