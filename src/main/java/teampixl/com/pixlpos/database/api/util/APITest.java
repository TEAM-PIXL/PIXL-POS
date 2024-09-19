package teampixl.com.pixlpos.database.api.util;

import teampixl.com.pixlpos.database.api.*;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.models.Users;

import java.util.List;
import java.util.Scanner;

import static teampixl.com.pixlpos.database.api.util.Exceptions.isSuccessful;
import static teampixl.com.pixlpos.database.api.util.Exceptions.returnStatus;

public class APITest {
    public static void main(String[] args) {
        UserStack userStack = UserStack.getInstance();
        UsersAPI usersAPI = UsersAPI.getInstance();
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
//
        /* ---> INIT WORKS <---- */
        Order order = orderAPI.initializeOrder();
        //print contents of order
        System.out.println("Order Contents: " + order.getMetadata().metadata());
        int ORDER_NUM = (int) order.getMetadata().metadata().get("order_number");
        System.out.println("Order Number: " + ORDER_NUM);

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
        String[] roles = {"COOK", "WAITER"};

        for (int i = 1; i <= 10; i++) {
            String FIRST_NAME = "First" + i;
            String LAST_NAME = "Last" + i;
            String USERNAME = "NewUser" + i;
            String PASSWORD = "GobldyG00p!" + i;
            String EMAIL = "examplar" + i + "@example.ezy.com";
            Users.UserRole ROLE = Users.UserRole.valueOf(roles[i % 2]);

            List<StatusCode> STATUS = usersAPI.deleteUser(USERNAME);
            if (isSuccessful(STATUS)) {
                System.out.println("User " + USERNAME + " created successfully.");
            } else {
                System.out.println(returnStatus("User " + USERNAME + " could not be created with the following errors:", STATUS));
            }
        }


    }
}
