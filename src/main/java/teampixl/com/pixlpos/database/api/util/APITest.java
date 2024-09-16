package teampixl.com.pixlpos.database.api.util;

import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.database.api.UserStack;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.database.DataStore;

import java.util.List;
import java.util.Scanner;

public class APITest {
    public static void main(String[] args) {
        UserStack userStack = UserStack.getInstance();
        DataStore dataStore = DataStore.getInstance();
        userStack.setCurrentUser("admin");

        System.out.println("Current user: " + userStack.getCurrentUser());

        userStack.clearCurrentUser();

        System.out.println("Current user: " + userStack.getCurrentUser());

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your search query: ");
        String query = scanner.nextLine();

        List<Order> results = OrderAPI.searchOrders(query);

        results.forEach(order -> System.out.println("Order Number: " + order.getMetadata().metadata().get("order_number")));
    }
}
