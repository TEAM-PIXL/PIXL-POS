package teampixl.com.pixlpos.database.api;

import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.Ingredients;
import teampixl.com.pixlpos.models.Stock;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * API for managing stock in the database.
 */
public class StockAPI {
    private static StockAPI instance;
    private static final DataStore dataStore = DataStore.getInstance();

    private StockAPI() { }

    public static synchronized StockAPI getInstance() {
        if (instance == null) {
            instance = new StockAPI();
        }
        return instance;
    }

    /**
     * Validates the stock ID.
     * @param ingredientID ID of the ingredient.
     * @return StatusCode indicating the validation status.
     */
    public StatusCode validateStockByIngredientID(UUID ingredientID) {
        if (ingredientID == null) { return StatusCode.INVALID_STOCK_ID; }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the stock quantity.
     * @param quantity Quantity of the stock.
     * @return StatusCode indicating the validation status.
     */
    public StatusCode validateStockByQuantity(double quantity) {
        if (quantity < 0) { return StatusCode.INVALID_STOCK_QUANTITY; }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the stock unit.
     * @param unit Unit of the stock.
     * @return StatusCode indicating the validation status.
     */
    public StatusCode validateStockByUnit(Stock.UnitType unit) {
        if (unit == null) { return StatusCode.INVALID_STOCK_UNIT; }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the stock on order status.
     * @param onOrder On order status of the stock.
     * @return StatusCode indicating the validation status.
     */
    public StatusCode validateStockByOnOrder(int onOrder) {
        if (onOrder != 0 && onOrder != 1) { return StatusCode.INVALID_STOCK_ONORDER; }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the stock status.
     * @param stockStatus Status of the stock.
     * @return StatusCode indicating the validation status.
     */
    public StatusCode validateStockStatus(Stock.StockStatus stockStatus) {
        if (stockStatus == null) { return StatusCode.INVALID_STOCK_STATUS; }
        return StatusCode.SUCCESS;
    }

    /**
     * Gets stock by ingredient name.
     * @param ingredientName name of the ingredient.
     * @return Stock object.
     */
    public String getStockByIngredientName(String ingredientName) {
        IngredientsAPI ingredientsAPI = IngredientsAPI.getInstance();
        try {
            String id = ingredientsAPI.getIngredientsByName(ingredientName);
            if (id == null) {
                return null;
            }
            return dataStore.getStockItems().stream().filter(stock -> stock.getMetadata().metadata().get("ingredient_id").equals(id)).findFirst().get().getMetadata().metadata().get("stock_id").toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Posts stock to the database.
     * @param ingredientName Name of the ingredient.
     * @param stockStatus Status of the stock.
     * @param unitType Unit of the stock.
     * @param numeral Quantity of the stock.
     * @param onOrder On order status of the stock.
     * @return StatusCode indicating the post status.
     */
    public StatusCode postStock(String ingredientName, Stock.StockStatus stockStatus, Stock.UnitType unitType, double numeral, int onOrder) {
        try {
            StatusCode quantityCheck = validateStockByQuantity(numeral);
            StatusCode unitCheck = validateStockByUnit(unitType);
            StatusCode onOrderCheck = validateStockByOnOrder(onOrder);
            StatusCode statusCheck = validateStockStatus(stockStatus);
            for (StatusCode status : new StatusCode[]{quantityCheck, unitCheck, onOrderCheck, statusCheck}) {
                if (status != StatusCode.SUCCESS) {
                    return status;
                }
            }

            IngredientsAPI ingredientsAPI = IngredientsAPI.getInstance();
            String id = ingredientsAPI.getIngredientsByName(ingredientName);
            if (id == null) {
                return StatusCode.INGREDIENT_NOT_FOUND;
            }

            Ingredients ingredient = ingredientsAPI.getIngredientById(id);
            if (ingredient == null) {
                return StatusCode.INGREDIENT_NOT_FOUND;
            }

            boolean onOrderStatus = onOrder == 1;
            Stock stock = new Stock(ingredient, stockStatus, unitType, numeral, onOrderStatus);
            dataStore.addStock(stock);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.STOCK_CREATION_FAILED;
        }
    }

    /**
     * Puts stock quantity.
     * @param ingredientName Name of the ingredient.
     * @param quantity Quantity of the stock.
     * @return StatusCode indicating the put status.
     */
    public StatusCode putStockQuantity(String ingredientName, double quantity) {
        try {
            IngredientsAPI ingredientsAPI = IngredientsAPI.getInstance();
            String id = ingredientsAPI.getIngredientsByName(ingredientName);
            if (id == null) {
                return StatusCode.INGREDIENT_NOT_FOUND;
            }

            Ingredients ingredient = ingredientsAPI.getIngredientById(id);
            if (ingredient == null) {
                return StatusCode.INGREDIENT_NOT_FOUND;
            }

            Stock stock = dataStore.getStockItems().stream().filter(s -> s.getMetadata().metadata().get("ingredient_id").equals(id)).findFirst().get();
            stock.setDataValue("numeral", quantity);
            stock.updateMetadata("lastUpdated", stock.getMetadata().metadata().get("lastUpdated"));
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.STOCK_UPDATE_FAILED;
        }
    }

    /**
     * Puts stock unit.
     * @param ingredientName Name of the ingredient.
     * @param unit Unit of the stock.
     * @return StatusCode indicating the put status.
     */
    public StatusCode putStockUnit(String ingredientName, Stock.UnitType unit) {
        try {
            IngredientsAPI ingredientsAPI = IngredientsAPI.getInstance();
            String id = ingredientsAPI.getIngredientsByName(ingredientName);
            if (id == null) {
                return StatusCode.INGREDIENT_NOT_FOUND;
            }

            Ingredients ingredient = ingredientsAPI.getIngredientById(id);
            if (ingredient == null) {
                return StatusCode.INGREDIENT_NOT_FOUND;
            }

            Stock stock = dataStore.getStockItems().stream().filter(s -> s.getMetadata().metadata().get("ingredient_id").equals(id)).findFirst().get();
            stock.setDataValue("unit", unit);
            stock.updateMetadata("lastUpdated", stock.getMetadata().metadata().get("lastUpdated"));
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.STOCK_UPDATE_FAILED;
        }
    }

    /**
     * Puts stock on order status.
     * @param ingredientName Name of the ingredient.
     * @param onOrder On order status of the stock.
     * @return StatusCode indicating the put status.
     */
    public StatusCode putStockOnOrder(String ingredientName, int onOrder) {
        try {
            IngredientsAPI ingredientsAPI = IngredientsAPI.getInstance();
            String id = ingredientsAPI.getIngredientsByName(ingredientName);
            if (id == null) {
                return StatusCode.INGREDIENT_NOT_FOUND;
            }

            Ingredients ingredient = ingredientsAPI.getIngredientById(id);
            if (ingredient == null) {
                return StatusCode.INGREDIENT_NOT_FOUND;
            }

            Stock stock = dataStore.getStockItems().stream().filter(s -> s.getMetadata().metadata().get("ingredient_id").equals(id)).findFirst().get();
            stock.updateMetadata("onOrder", onOrder);
            stock.updateMetadata("lastUpdated", stock.getMetadata().metadata().get("lastUpdated"));
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.STOCK_UPDATE_FAILED;
        }
    }

    /**
     * Puts stock status.
     * @param ingredientName Name of the ingredient.
     * @param stockStatus Status of the stock.
     * @return StatusCode indicating the put status.
     */
    public StatusCode putStockStatus(String ingredientName, Stock.StockStatus stockStatus) {
        try {
            IngredientsAPI ingredientsAPI = IngredientsAPI.getInstance();
            String id = ingredientsAPI.getIngredientsByName(ingredientName);
            if (id == null) {
                return StatusCode.INGREDIENT_NOT_FOUND;
            }

            Ingredients ingredient = ingredientsAPI.getIngredientById(id);
            if (ingredient == null) {
                return StatusCode.INGREDIENT_NOT_FOUND;
            }

            Stock stock = dataStore.getStockItems().stream().filter(s -> s.getMetadata().metadata().get("ingredient_id").equals(id)).findFirst().get();
            stock.updateMetadata("stockStatus", stockStatus);
            stock.updateMetadata("lastUpdated", stock.getMetadata().metadata().get("lastUpdated"));
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.STOCK_UPDATE_FAILED;
        }
    }

    /**
     * Deletes stock from the database.
     * @param ingredientName Name of the ingredient.
     * @return StatusCode indicating the deletion status.
     */
    public StatusCode deleteStock(String ingredientName) {
        try {
            IngredientsAPI ingredientsAPI = IngredientsAPI.getInstance();
            String id = ingredientsAPI.getIngredientsByName(ingredientName);
            if (id == null) {
                return StatusCode.INGREDIENT_NOT_FOUND;
            }

            Ingredients ingredient = ingredientsAPI.getIngredientById(id);
            if (ingredient == null) {
                return StatusCode.INGREDIENT_NOT_FOUND;
            }

            Stock stock = dataStore.getStockItems().stream().filter(s -> s.getMetadata().metadata().get("ingredient_id").equals(id)).findFirst().get();
            dataStore.removeStock(stock);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.STOCK_DELETION_FAILED;
        }
    }

    /**
     * Searches for stock items based on a query.
     * @param query the query to search for stock items.
     * @return list of stock items matching the query.
     */
    public List<Stock> searchStock(String query) {
        String[] parts = query.trim().split("\\s+");

        if (parts.length > 2) {
            return List.of();
        }

        IngredientsAPI ingredientsAPI = IngredientsAPI.getInstance();
        List<String> ingredientIds = IngredientsAPI.searchIngredients(query).stream()
                .map(ingredient -> (String) ingredient.getMetadata().metadata().get("ingredient_id"))
                .toList();

        return dataStore.getStockItems().parallelStream()
                .filter(stock -> ingredientIds.contains(stock.getMetadata().metadata().get("ingredient_id")))
                .collect(Collectors.toList());
    }

}
