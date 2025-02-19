package teampixl.com.pixlpos.database.api;

import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.Ingredients;
import teampixl.com.pixlpos.models.Stock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.util.Pair;
import java.lang.reflect.Method;

/**
 * The StockAPI class is a singleton class responsible for managing stock items in memory.
 * It provides methods to create, read, update, and delete stock items, as well as validation and search functionalities.
 */
public class StockAPI {
    private static StockAPI INSTANCE;
    private static DataStore DATA_STORE;
    private static IngredientsAPI INGREDIENTS_API;

    private StockAPI() { initializeDependencies(); }

    /**
     * Gets the singleton instance of the StockAPI.
     *
     * @return the instance of the StockAPI
     */
    public static synchronized StockAPI getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StockAPI();
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

        while ((INGREDIENTS_API = IngredientsAPI.getInstance()) == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Validates the ingredient ID associated with the stock.
     *
     * @param INGREDIENT_ID the ingredient ID to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateStockByIngredientID(String INGREDIENT_ID) {
        if (INGREDIENT_ID == null || INGREDIENT_ID.trim().isEmpty()) {
            return StatusCode.INVALID_INGREDIENT_ID;
        }
        Ingredients INGREDIENT = IngredientsAPI.getInstance().keyTransform(INGREDIENT_ID);
        if (INGREDIENT == null) {
            return StatusCode.INGREDIENT_NOT_FOUND;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the quantity of the stock.
     *
     * @param QUANTITY the quantity to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateStockByQuantity(Object QUANTITY) {
        if (QUANTITY == null) {
            return StatusCode.INVALID_STOCK_QUANTITY;
        }
        if (QUANTITY instanceof Integer) {
            if ((Integer) QUANTITY < 0) {
                return StatusCode.INVALID_STOCK_QUANTITY;
            }
        } else if (QUANTITY instanceof Double) {
            if ((Double) QUANTITY < 0) {
                return StatusCode.INVALID_STOCK_QUANTITY;
            }
        } else {
            return StatusCode.INVALID_STOCK_QUANTITY_TYPE;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the unit type of the stock.
     *
     * @param UNIT_TYPE the unit type to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateStockByUnitType(Stock.UnitType UNIT_TYPE) {
        if (UNIT_TYPE == null) {
            return StatusCode.INVALID_STOCK_UNIT_TYPE;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the onOrder status of the stock.
     *
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateStockByOnOrder() {
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the stock status.
     *
     * @param STOCK_STATUS the stock status to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateStockByStockStatus(Stock.StockStatus STOCK_STATUS) {
        if (STOCK_STATUS == null) {
            return StatusCode.INVALID_STOCK_STATUS;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the low stock threshold.
     *
     * @param LOW_STOCK_THRESHOLD the low stock threshold to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateStockByLowStockThreshold(Object LOW_STOCK_THRESHOLD) {
        if (LOW_STOCK_THRESHOLD == null) {
            return StatusCode.INVALID_LOW_STOCK_THRESHOLD;
        }
        if (LOW_STOCK_THRESHOLD instanceof Integer) {
            if ((Integer) LOW_STOCK_THRESHOLD < 0) {
                return StatusCode.INVALID_LOW_STOCK_THRESHOLD;
            }
        } else if (LOW_STOCK_THRESHOLD instanceof Double) {
            if ((Double) LOW_STOCK_THRESHOLD < 0) {
                return StatusCode.INVALID_LOW_STOCK_THRESHOLD;
            }
        } else {
            return StatusCode.INVALID_LOW_STOCK_THRESHOLD_TYPE;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the desired quantity.
     *
     * @param DESIRED_QUANTITY the desired quantity to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateStockByDesiredQuantity(Object DESIRED_QUANTITY) {
        if (DESIRED_QUANTITY == null) {
            return StatusCode.INVALID_DESIRED_QUANTITY;
        }
        if (DESIRED_QUANTITY instanceof Integer) {
            if ((Integer) DESIRED_QUANTITY < 0) {
                return StatusCode.INVALID_DESIRED_QUANTITY;
            }
        } else if (DESIRED_QUANTITY instanceof Double) {
            if ((Double) DESIRED_QUANTITY < 0) {
                return StatusCode.INVALID_DESIRED_QUANTITY;
            }
        } else {
            return StatusCode.INVALID_DESIRED_QUANTITY_TYPE;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the price per unit.
     *
     * @param PRICE_PER_UNIT the price per unit to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateStockByPricePerUnit(Object PRICE_PER_UNIT) {
        if (PRICE_PER_UNIT == null) {
            return StatusCode.INVALID_PRICE_PER_UNIT;
        }
        if (PRICE_PER_UNIT instanceof Integer) {
            if ((Integer) PRICE_PER_UNIT < 0) {
                return StatusCode.INVALID_PRICE_PER_UNIT;
            }
        } else if (PRICE_PER_UNIT instanceof Double) {
            if ((Double) PRICE_PER_UNIT < 0) {
                return StatusCode.INVALID_PRICE_PER_UNIT;
            }
        } else {
            return StatusCode.INVALID_PRICE_PER_UNIT_TYPE;
        }
        return StatusCode.SUCCESS;
    }

    /* ---> IMPORTANT INTERNAL FUNCTION FOR SAFELY MANAGING AND VALIDATING A STOCK UPDATE <---- */
    private Pair<List<StatusCode>, Stock> validateAndGetStock(String FIELD, Object VALUE, String INGREDIENT_ID) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        try {
            // Use Object.class as parameter type to match validation methods
            Method VALIDATION_METHOD = this.getClass().getMethod("validateStockBy" + FIELD, Object.class);
            StatusCode VALIDATION_RESULT = (StatusCode) VALIDATION_METHOD.invoke(this, VALUE);
            VALIDATIONS.add(VALIDATION_RESULT);
            if (!Exceptions.isSuccessful(VALIDATIONS)) {
                return new Pair<>(VALIDATIONS, null);
            }
        } catch (NoSuchMethodException E) {
            VALIDATIONS.add(StatusCode.INTERNAL_METHOD_NOT_FOUND);
            return new Pair<>(VALIDATIONS, null);
        } catch (Exception E) {
            VALIDATIONS.add(StatusCode.INTERNAL_FAILURE);
            return new Pair<>(VALIDATIONS, null);
        }

        Stock STOCK = getStockByIngredientID(INGREDIENT_ID);

        if (STOCK == null) {
            VALIDATIONS.add(StatusCode.STOCK_NOT_FOUND);
            return new Pair<>(VALIDATIONS, null);
        }

        return new Pair<>(VALIDATIONS, STOCK);
    }

    /**
     * Retrieves the stock ID based on the ingredient ID.
     *
     * @param INGREDIENT_ID the ingredient ID
     * @return the stock ID
     */
    public String keySearch(String INGREDIENT_ID) {
        if (INGREDIENT_ID == null) {
            return null;
        }
        return DATA_STORE.readStock().stream()
                .filter(STOCK -> STOCK.getMetadata().metadata().get("ingredient_id").toString().equals(INGREDIENT_ID))
                .findFirst()
                .map(STOCK -> STOCK.getMetadata().metadata().get("stock_id").toString())
                .orElse(null);
    }

    /**
     * Retrieves the ingredient ID based on the stock ID.
     *
     * @param STOCK_ID the stock ID
     * @return the ingredient ID
     */
    public String reverseKeySearch(String STOCK_ID) {
        if (STOCK_ID == null) {
            return null;
        }
        return DATA_STORE.readStock().stream()
                .filter(STOCK -> STOCK.getMetadata().metadata().get("stock_id").toString().equals(STOCK_ID))
                .findFirst()
                .map(STOCK -> STOCK.getMetadata().metadata().get("ingredient_id").toString())
                .orElse(null);
    }

    /**
     * Transforms a stock ID into a Stock object.
     *
     * @param STOCK_ID the stock ID
     * @return the Stock object
     */
    public Stock keyTransform(String STOCK_ID) {
        if (STOCK_ID == null) {
            return null;
        }
        return DATA_STORE.readStock().stream()
                .filter(STOCK -> STOCK.getMetadata().metadata().get("stock_id").toString().equals(STOCK_ID))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets all stock items from the database.
     *
     * @return a list of stock items
     */
    public List<Stock> getStock() {
        return DATA_STORE.readStock();
    }

    /**
     * Gets the Stock object associated with the given ingredient ID.
     *
     * @param INGREDIENT_ID the ingredient ID
     * @return the Stock object
     */
    public Stock getStockByIngredientID(String INGREDIENT_ID) {
        String STOCK_ID = keySearch(INGREDIENT_ID);
        return keyTransform(STOCK_ID);
    }

    /**
     * Creates a new stock entry and adds it to the database.
     *
     * @param INGREDIENT_ID the ingredient ID
     * @param STOCK_STATUS the stock status
     * @param UNIT_TYPE the unit type
     * @param NUMERAL the quantity
     * @param ON_ORDER the onOrder status
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> postStock(String INGREDIENT_ID, Stock.StockStatus STOCK_STATUS, Stock.UnitType UNIT_TYPE, Object NUMERAL, boolean ON_ORDER) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        try {
            VALIDATIONS.add(validateStockByIngredientID(INGREDIENT_ID));
            VALIDATIONS.add(validateStockByStockStatus(STOCK_STATUS));
            VALIDATIONS.add(validateStockByUnitType(UNIT_TYPE));
            VALIDATIONS.add(validateStockByQuantity(NUMERAL));
            VALIDATIONS.add(validateStockByOnOrder());
            if (UNIT_TYPE == Stock.UnitType.QTY && !(NUMERAL instanceof Integer)) {
                VALIDATIONS.add(StatusCode.STOCK_QUANTITY_MUST_BE_INTEGER);
            }
            if ((UNIT_TYPE == Stock.UnitType.KG || UNIT_TYPE == Stock.UnitType.L) && !(NUMERAL instanceof Double)) {
                VALIDATIONS.add(StatusCode.STOCK_QUANTITY_MUST_BE_DOUBLE);
            }

            if (!Exceptions.isSuccessful(VALIDATIONS)) {
                return VALIDATIONS;
            }

            Ingredients INGREDIENT = INGREDIENTS_API.keyTransform(INGREDIENT_ID);
            if (INGREDIENT == null) {
                VALIDATIONS.add(StatusCode.INGREDIENT_NOT_FOUND);
                return VALIDATIONS;
            }

            Stock EXISTING_STOCK = getStockByIngredientID(INGREDIENT_ID);
            if (EXISTING_STOCK != null) {
                VALIDATIONS.add(StatusCode.STOCK_ALREADY_EXISTS);
                return VALIDATIONS;
            }
            if (!Exceptions.isSuccessful(VALIDATIONS)) {
                return VALIDATIONS;
            }
            Stock STOCK = new Stock(INGREDIENT, STOCK_STATUS, UNIT_TYPE, NUMERAL, ON_ORDER);
            DATA_STORE.createStock(STOCK);
            return VALIDATIONS;
        } catch (Exception E) {
            VALIDATIONS.add(StatusCode.STOCK_CREATION_FAILED);
            return VALIDATIONS;
        }
    }

    /**
     * Updates the quantity of an existing stock.
     *
     * @param INGREDIENT_ID the ingredient ID
     * @param NEW_NUMERAL the new quantity
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> putStockNumeral(String INGREDIENT_ID, Object NEW_NUMERAL) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        try {
            Pair<List<StatusCode>, Stock> RESULT = validateAndGetStock("Quantity", NEW_NUMERAL, INGREDIENT_ID);
            VALIDATIONS.addAll(RESULT.getKey());
            if (!Exceptions.isSuccessful(VALIDATIONS)) {
                return VALIDATIONS;
            }

            Stock STOCK = RESULT.getValue();

            STOCK.setDataValue("numeral", NEW_NUMERAL);
            DATA_STORE.updateStock(STOCK);
            VALIDATIONS.add(StatusCode.SUCCESS);
            return VALIDATIONS;
        } catch (Exception E) {
            VALIDATIONS.add(StatusCode.STOCK_UPDATE_FAILED);
            return VALIDATIONS;
        }
    }

    /**
     * Updates the unit type of existing stock.
     *
     * @param INGREDIENT_ID the ingredient ID
     * @param NEW_UNIT_TYPE the new unit type
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> putStockUnitType(String INGREDIENT_ID, Stock.UnitType NEW_UNIT_TYPE) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        try {
            Pair<List<StatusCode>, Stock> RESULT = validateAndGetStock("UnitType", NEW_UNIT_TYPE, INGREDIENT_ID);
            VALIDATIONS.addAll(RESULT.getKey());
            if (!Exceptions.isSuccessful(VALIDATIONS)) {
                return VALIDATIONS;
            }

            Stock STOCK = RESULT.getValue();

            STOCK.setDataValue("unit", NEW_UNIT_TYPE);
            DATA_STORE.updateStock(STOCK);
            VALIDATIONS.add(StatusCode.SUCCESS);
            return VALIDATIONS;
        } catch (Exception E) {
            VALIDATIONS.add(StatusCode.STOCK_UPDATE_FAILED);
            return VALIDATIONS;
        }
    }

    /**
     * Updates the onOrder status of an existing stock.
     *
     * @param INGREDIENT_ID the ingredient ID
     * @param NEW_ON_ORDER the new onOrder status
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> putStockOnOrder(String INGREDIENT_ID, boolean NEW_ON_ORDER) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        try {
            Pair<List<StatusCode>, Stock> RESULT = validateAndGetStock("OnOrder", NEW_ON_ORDER, INGREDIENT_ID);
            VALIDATIONS.addAll(RESULT.getKey());
            if (!Exceptions.isSuccessful(VALIDATIONS)) {
                return VALIDATIONS;
            }

            Stock STOCK = RESULT.getValue();

            STOCK.updateMetadata("onOrder", NEW_ON_ORDER);
            DATA_STORE.updateStock(STOCK);
            VALIDATIONS.add(StatusCode.SUCCESS);
            return VALIDATIONS;
        } catch (Exception E) {
            VALIDATIONS.add(StatusCode.STOCK_UPDATE_FAILED);
            return VALIDATIONS;
        }
    }

    /**
     * Updates the stock status of an existing stock.
     *
     * @param INGREDIENT_ID the ingredient ID
     * @param NEW_STOCK_STATUS the new stock status
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> putStockStatus(String INGREDIENT_ID, Stock.StockStatus NEW_STOCK_STATUS) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        try {
            Pair<List<StatusCode>, Stock> RESULT = validateAndGetStock("StockStatus", NEW_STOCK_STATUS, INGREDIENT_ID);
            VALIDATIONS.addAll(RESULT.getKey());
            if (!Exceptions.isSuccessful(VALIDATIONS)) {
                return VALIDATIONS;
            }

            Stock STOCK = RESULT.getValue();

            STOCK.updateMetadata("stockStatus", NEW_STOCK_STATUS);
            DATA_STORE.updateStock(STOCK);
            VALIDATIONS.add(StatusCode.SUCCESS);
            return VALIDATIONS;
        } catch (Exception E) {
            VALIDATIONS.add(StatusCode.STOCK_UPDATE_FAILED);
            return VALIDATIONS;
        }
    }

    /**
     * Updates the low_stock_threshold of an existing stock.
     *
     * @param INGREDIENT_ID the ingredient ID
     * @param NEW_LOW_STOCK_THRESHOLD the new low stock threshold
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> putStockLowStockThreshold(String INGREDIENT_ID, Object NEW_LOW_STOCK_THRESHOLD) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        try {
            Pair<List<StatusCode>, Stock> RESULT = validateAndGetStock("LowStockThreshold", NEW_LOW_STOCK_THRESHOLD, INGREDIENT_ID);
            VALIDATIONS.addAll(RESULT.getKey());
            if (!Exceptions.isSuccessful(VALIDATIONS)) {
                return VALIDATIONS;
            }

            Stock STOCK = RESULT.getValue();

            STOCK.setDataValue("low_stock_threshold", NEW_LOW_STOCK_THRESHOLD);
            DATA_STORE.updateStock(STOCK);
            VALIDATIONS.add(StatusCode.SUCCESS);
            return VALIDATIONS;
        } catch (Exception E) {
            VALIDATIONS.add(StatusCode.STOCK_UPDATE_FAILED);
            return VALIDATIONS;
        }
    }

    /**
     * Updates the desired_quantity of an existing stock.
     *
     * @param INGREDIENT_ID the ingredient ID
     * @param NEW_DESIRED_QUANTITY the new desired quantity
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> putStockDesiredQuantity(String INGREDIENT_ID, Object NEW_DESIRED_QUANTITY) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        try {
            Pair<List<StatusCode>, Stock> RESULT = validateAndGetStock("DesiredQuantity", NEW_DESIRED_QUANTITY, INGREDIENT_ID);
            VALIDATIONS.addAll(RESULT.getKey());
            if (!Exceptions.isSuccessful(VALIDATIONS)) {
                return VALIDATIONS;
            }

            Stock STOCK = RESULT.getValue();

            STOCK.setDataValue("desired_quantity", NEW_DESIRED_QUANTITY);
            DATA_STORE.updateStock(STOCK);
            VALIDATIONS.add(StatusCode.SUCCESS);
            return VALIDATIONS;
        } catch (Exception E) {
            VALIDATIONS.add(StatusCode.STOCK_UPDATE_FAILED);
            return VALIDATIONS;
        }
    }

    /**
     * Updates the price_per_unit of an existing stock.
     *
     * @param INGREDIENT_ID the ingredient ID
     * @param NEW_PRICE_PER_UNIT the new price per unit
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> putStockPricePerUnit(String INGREDIENT_ID, Object NEW_PRICE_PER_UNIT) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        try {
            Pair<List<StatusCode>, Stock> RESULT = validateAndGetStock("PricePerUnit", NEW_PRICE_PER_UNIT, INGREDIENT_ID);
            VALIDATIONS.addAll(RESULT.getKey());
            if (!Exceptions.isSuccessful(VALIDATIONS)) {
                return VALIDATIONS;
            }

            Stock STOCK = RESULT.getValue();

            STOCK.setDataValue("price_per_unit", NEW_PRICE_PER_UNIT);
            DATA_STORE.updateStock(STOCK);
            VALIDATIONS.add(StatusCode.SUCCESS);
            return VALIDATIONS;
        } catch (Exception E) {
            VALIDATIONS.add(StatusCode.STOCK_UPDATE_FAILED);
            return VALIDATIONS;
        }
    }

    /**
     * Deletes a stock entry from the database.
     *
     * @param INGREDIENT_ID the ingredient ID
     * @return a list of status codes indicating the result of the operation
     */
    public List<StatusCode> deleteStock(String INGREDIENT_ID) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        try {
            Stock STOCK = getStockByIngredientID(INGREDIENT_ID);
            if (STOCK == null) {
                VALIDATIONS.add(StatusCode.STOCK_NOT_FOUND);
                return VALIDATIONS;
            }

            DATA_STORE.deleteStock(STOCK);
            VALIDATIONS.add(StatusCode.SUCCESS);
            return VALIDATIONS;
        } catch (Exception E) {
            VALIDATIONS.add(StatusCode.STOCK_DELETION_FAILED);
            return VALIDATIONS;
        }
    }

    /**
     * Searches for stock items in the database based on a query string.
     *
     * @param QUERY the search query
     * @return a list of stock items matching the query
     */
    public List<Stock> searchStock(String QUERY) {
        if (QUERY == null || QUERY.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<Ingredients> MATCHING_INGREDIENTS = INGREDIENTS_API.searchIngredients(QUERY);

        List<String> MATCHING_INGREDIENT_IDS = MATCHING_INGREDIENTS.stream()
                .map(INGREDIENT -> INGREDIENT.getMetadata().metadata().get("ingredient_id").toString())
                .toList();

        return DATA_STORE.readStock().parallelStream()
                .filter(STOCK -> MATCHING_INGREDIENT_IDS.contains(STOCK.getMetadata().metadata().get("ingredient_id").toString()))
                .collect(Collectors.toList());
    }
}

