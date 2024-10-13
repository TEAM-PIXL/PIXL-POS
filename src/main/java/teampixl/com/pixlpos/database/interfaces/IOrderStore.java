package teampixl.com.pixlpos.database.interfaces;

import javafx.collections.ObservableList;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Order;

import java.util.Map;

public interface IOrderStore {
    ObservableList<Order> readOrders();
    Order getOrder(int orderNumber);
    void createOrder(Order order);
    void deleteOrder(Order order);
    void updateOrder(Order order);
    Map<String, Integer> readOrderItems(Order order);
    Map<String, Integer> getOrderItem(Order order, String itemName);
    void createOrderItem(Order order, MenuItem item, int quantity);
    void deleteOrderItem(Order order, MenuItem item, int quantity);
    void updateOrderItem(Order order, MenuItem item, int newQuantity);
}
