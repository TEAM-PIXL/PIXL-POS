package teampixl.com.pixlpos.database.interfaces;

import javafx.collections.ObservableList;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Order;

import java.util.Map;

/**
 * Interface for the OrderStore class
 */
public interface IOrderStore {

    /**
     * Read all orders from the database
     * @return ObservableList of Order objects
     */
    ObservableList<Order> readOrders();

    /**
     * Read a specific order from the database
     * @param orderNumber the order number of the order to read
     * @return Order object
     */
    Order getOrder(int orderNumber);

    /**
     * Create a new order in the database
     * @param order the order to create
     */
    void createOrder(Order order);

    /**
     * Delete an order from the database
     * @param order the order to delete
     */
    void deleteOrder(Order order);

    /**
     * Update an order in the database
     * @param order the order to update
     */
    void updateOrder(Order order);

    /**
     * Read all items in an order
     * @param order the order to read items from
     * @return Map of item names and quantities
     */
    Map<String, Integer> readOrderItems(Order order);

    /**
     * Read a specific item in an order
     * @param order the order to read the item from
     * @param itemName the name of the item to read
     * @return Map of item name and quantity
     */
    Map<String, Integer> getOrderItem(Order order, String itemName);

    /**
     * Create a new item in an order
     * @param order the order to create the item in
     * @param item the item to create
     * @param quantity the quantity of the item to create
     */
    void createOrderItem(Order order, MenuItem item, int quantity);

    /**
     * Delete an item from an order
     * @param order the order to delete the item from
     * @param item the item to delete
     * @param quantity the quantity of the item to delete
     */
    void deleteOrderItem(Order order, MenuItem item, int quantity);

    /**
     * Update an item in an order
     * @param order the order to update the item in
     * @param item the item to update
     * @param newQuantity the new quantity of the item
     */
    void updateOrderItem(Order order, MenuItem item, int newQuantity);
}
