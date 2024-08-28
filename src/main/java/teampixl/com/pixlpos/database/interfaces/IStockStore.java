package teampixl.com.pixlpos.database.interfaces;

import javafx.collections.ObservableList;
import teampixl.com.pixlpos.constructs.Stock;

public interface IStockStore {
    ObservableList<Stock> getStockItems();
    void addStock(Stock stock);
    void updateStock(Stock stock);
    void removeStock(Stock stock);
}
