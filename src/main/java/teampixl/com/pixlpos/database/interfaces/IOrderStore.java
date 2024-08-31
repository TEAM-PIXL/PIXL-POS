package teampixl.com.pixlpos.database.interfaces;

import javafx.collections.ObservableList;
import teampixl.com.pixlpos.constructs.MenuItem;
import teampixl.com.pixlpos.constructs.Order;

import java.util.Map;

public interface IOrderStore {
    ObservableList<Order> getOrders();
    Order getOrder(int orderNumber);
    void addOrder(Order order);
    void removeOrder(Order order);
    void updateOrder(Order order);
    Map<String, Object> getOrderItems(Order order);
    void addOrderItem(Order order, MenuItem item, int quantity);
    void removeOrderItem(Order order, MenuItem item, int quantity);
    void updateOrderItem(Order order, MenuItem item, int newQuantity);
}
