package teampixl.com.pixlpos.database.interfaces;

import javafx.collections.ObservableList;
import teampixl.com.pixlpos.models.Stock;

public interface IStockStore {

    /**
     * Read all stock items from the database
     * @return ObservableList of Stock
     */
    ObservableList<Stock> readStock();

    /**
     * Get a stock item by its name
     * @param itemName the name of the stock item
     * @return Stock
     */
    Stock getStockItem(String itemName);

    /**
     * Create a new stock item
     * @param stock the stock item to be created
     */
    void createStock(Stock stock);

    /**
     * Update a stock item
     * @param stock the stock item to be updated
     */
    void updateStock(Stock stock);

    /**
     * Delete a stock item
     * @param stock the stock item to be deleted
     */
    void deleteStock(Stock stock);
}
