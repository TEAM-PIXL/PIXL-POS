package teampixl.com.pixlpos.database.api.util;

import javafx.collections.ObservableList;
import teampixl.com.pixlpos.database.api.*;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.OrderAPI;

public class APITest {
    public static void main(String[] args) {
        DataStore dataStore = DataStore.getInstance();
        UserStack userStack = UserStack.getInstance();
        UsersAPI usersAPI = UsersAPI.getInstance();
        OrderAPI orderAPI = OrderAPI.getInstance();
        userStack.setCurrentUser("admin");

        ObservableList<Order> ORDERS;


        ORDERS = dataStore.readOrders();

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
//        OrderAPI orderAPI = OrderAPI.getInstance();
//
//        /* ---> INIT WORKS <---- */
//        Order order = orderAPI.initializeOrder();
//        //print contents of order
//        System.out.println("Order Contents: " + order.getMetadata().metadata());
//        int ORDER_NUM = (int) order.getMetadata().metadata().get("order_number");
//        System.out.println("Order Number: " + ORDER_NUM);

//        /* ---> PUT ORDER BY ITEM WORKS <---- */
//        List<StatusCode> RESULT = orderAPI.putOrderByItem(8, "Pizza", 4);
//        if (isSuccessful(RESULT)) {
//            System.out.println("Order placed successfully.");
//        } else {
//            System.out.println(returnStatus("Order items could not be added with the following errors:", RESULT));
//        }

//        List<StatusCode> RESULT2 = orderAPI.postOrder(order);
//        if (isSuccessful(RESULT2)) {
//            System.out.println("Order placed successfully.");
//        } else {
//            System.out.println(returnStatus("Order could not be placed with the following errors:", RESULT2));
//        }

//        int ORDER_NUMBER = 8;
//
//        Order ORDER = OrderAPI.getOrderById(OrderAPI.getOrderByNumber(ORDER_NUMBER));
//        System.out.println("The order ORDER has the metadata contents: " + ORDER.getMetadata().metadata());
//        System.out.println("The order ORDER has the data contents: " + ORDER.getData());
//
//        List<StatusCode> RESULT3 = orderAPI.validateOrder(ORDER);
//        if (isSuccessful(RESULT3)) {
//            System.out.println("Order validated successfully.");
//        } else {
//            System.out.println(returnStatus("Order could not be validated with the following errors:", RESULT3));
//        }

    }
}
