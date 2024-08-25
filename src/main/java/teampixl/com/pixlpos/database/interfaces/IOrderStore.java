package teampixl.com.pixlpos.database.interfaces;

import javafx.collections.ObservableList;
import teampixl.com.pixlpos.constructs.Order;

public interface IOrderStore {
    ObservableList<Order> getOrders();
    void addOrder(Order order);
    void removeOrder(Order order);
    void updateOrder(Order order);
}
