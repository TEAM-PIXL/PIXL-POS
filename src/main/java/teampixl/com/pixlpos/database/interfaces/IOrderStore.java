package teampixl.com.pixlpos.database.interfaces;

import javafx.collections.ObservableList;
import teampixl.com.pixlpos.constructs.MenuItem;
import teampixl.com.pixlpos.constructs.Order;

public interface IOrderStore {
    ObservableList<Order> getOrders();
    void addOrder(Order order);
    void removeOrder(Order order);
    void updateOrder(Order order);
    void addOrderItem(Order order, MenuItem item, int quantity);
    void removeOrderItem(Order order, MenuItem item, int quantity);
    void updateOrderItem(Order order, MenuItem item, int newQuantity);
}
