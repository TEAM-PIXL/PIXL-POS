package teampixl.com.pixlpos.database.api.util;

import teampixl.com.pixlpos.database.api.MenuAPI;
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.models.Order;

import java.util.List;
import java.util.Scanner;

import static teampixl.com.pixlpos.database.api.util.Exceptions.isSuccessful;
import static teampixl.com.pixlpos.database.api.util.Exceptions.returnStatus;

public class APITest {
    public static void main(String[] args) {
        UserStack userStack = UserStack.getInstance();
        userStack.setCurrentUser("admin");
//
//        System.out.println("Current user: " + userStack.getCurrentUser());
//
//        Scanner scanner = new Scanner(System.in);
//        System.out.print("Enter your search query: ");
//        String query = scanner.nextLine();
//
//        List<Order> results = OrderAPI.searchOrders(query);
//
//        results.forEach(order -> System.out.println("Order Number: " + order.getMetadata().metadata().get("order_number")));
        /* ---> CONSTRUCTOR WORKS <---- */
        OrderAPI orderAPI = OrderAPI.getInstance();

        /* ---> INIT WORKS <---- */
        Order order = orderAPI.initializeOrder();
        //print contents of order
        System.out.println("Order Contents: " + order.getMetadata().metadata());
        int ORDER_NUM = (int) order.getMetadata().metadata().get("order_number");
        System.out.println("Order Number: " + ORDER_NUM);

        List<StatusCode> RESULT = orderAPI.putOrderByItem(8, "Pizza", 2);
        if (isSuccessful(RESULT)) {
            System.out.println("Order placed successfully.");
        } else {
            System.out.println(returnStatus("Order items could not be added with the following errors:", RESULT));
        }

        List<StatusCode> RESULT2 = orderAPI.postOrder(order);
        if (isSuccessful(RESULT2)) {
            System.out.println("Order placed successfully.");
        } else {
            System.out.println(returnStatus("Order could not be placed with the following errors:", RESULT2));
        }

    }
}
