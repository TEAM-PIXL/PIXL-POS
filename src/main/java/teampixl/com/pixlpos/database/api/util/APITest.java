package teampixl.com.pixlpos.database.api.util;

import javafx.collections.ObservableList;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.database.api.*;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.models.Users;


import java.io.ObjectInputFilter;
import java.util.List;

import static teampixl.com.pixlpos.database.api.util.Exceptions.isSuccessful;
import static teampixl.com.pixlpos.database.api.util.Exceptions.returnStatus;

public class APITest {
    public static void main(String[] args) {
        UsersAPI usersAPI = UsersAPI.getInstance();

        List<StatusCode> result = usersAPI.putUsersStatus("admin", false);
        if (isSuccessful(result)) {
            System.out.println("User status updated successfully. With result " + result);
        } else {
            System.out.println(returnStatus("User status could not be updated with the following errors:", result));
        }
//    public static void main(String[] args) {
//        DataStore dataStore = DataStore.getInstance();
//        UserStack userStack = UserStack.getInstance();
//        UsersAPI usersAPI = UsersAPI.getInstance();
//        OrderAPI orderAPI = OrderAPI.getInstance();
//
//// Example query
//        String query = "What was today's vs yesterday's revenue";
//
//        OrderAPI.QueryResult result = orderAPI.searchOrders(query);
//
//        if (result.getRevenueComparison() != null) {
//            OrderAPI.RevenueComparison comparison = result.getRevenueComparison();
//            System.out.println("Revenue for " + comparison.getRange1().getStartDate() + ": $" + comparison.getRevenue1());
//            System.out.println("Revenue for " + comparison.getRange2().getStartDate() + ": $" + comparison.getRevenue2());
//            System.out.println("Difference: $" + comparison.getDifference());
//        } else if (result.getTotalRevenue() != null) {
//            System.out.println("Total Revenue: $" + result.getTotalRevenue());
//        } else if (result.getOrders() != null) {
//            System.out.println("Orders:");
//            for (Order order : result.getOrders()) {
//                System.out.println("Order #" + order.getOrderNumber() + ", Total: $" + order.getTotal());
//            }
//        }


//        List<StatusCode> STATUS = usersAPI.postUsers("john", "doe", "johnnyboy", "Goo7yLu%%y", "example@example.com", Users.UserRole.WAITER);
//        if (isSuccessful(STATUS)) {
//            System.out.println("User created successfully.");
//        } else {
//            System.out.println(returnStatus("User could not be created with the following errors:", STATUS));
//        }
//
//        String ID = usersAPI.getUsersByUsername("johnnyboy");
//        System.out.println("The user with the username johnnyboy has the ID: " + ID);
//
//        List<StatusCode> STATUS2 = usersAPI.putUsersPassword("johnnyboy", "Goo7yLu%%y2");
//        if (isSuccessful(STATUS2)) {
//            System.out.println("User password updated successfully.");
//        } else {
//            System.out.println(returnStatus("User password could not be updated with the following errors:", STATUS2));
//        }


//        ORDERS = dataStore.readOrders();


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
