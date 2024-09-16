package teampixl.com.pixlpos.database.interfaces;

import javafx.collections.ObservableList;
import teampixl.com.pixlpos.database.api.menuapi.MenuItem;
import teampixl.com.pixlpos.database.api.orderapi.Order;

import java.util.Map;

public interface IOrderStore {
    ObservableList<Order> getOrders();
    Order getOrder(int orderNumber);
    void addOrder(Order order);
    void removeOrder(Order order);
    void updateOrder(Order order);
    Map<String, Object> getOrderItems(Order order);
    Map<String, Object> getOrderItem(Order order, String itemName);
    void addOrderItem(Order order, MenuItem item, int quantity);
    void removeOrderItem(Order order, MenuItem item, int quantity);
    void updateOrderItem(Order order, MenuItem item, int newQuantity);
}
