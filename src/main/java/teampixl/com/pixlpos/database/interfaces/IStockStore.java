package teampixl.com.pixlpos.database.interfaces;

import javafx.collections.ObservableList;
import teampixl.com.pixlpos.models.Stock;

public interface IStockStore {
    ObservableList<Stock> readStock();
    Stock getStockItem(String itemName);
    void createStock(Stock stock);
    void updateStock(Stock stock);
    void deleteStock(Stock stock);
}
