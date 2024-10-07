package teampixl.com.pixlpos.database.api.util;

import teampixl.com.pixlpos.database.api.*;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.models.logs.Logs;
import teampixl.com.pixlpos.models.logs.definitions.Priority;
import teampixl.com.pixlpos.models.logs.definitions.Category;
import teampixl.com.pixlpos.models.logs.definitions.Type;
import teampixl.com.pixlpos.models.logs.definitions.Status;
import teampixl.com.pixlpos.models.logs.definitions.Action;

import java.util.List;

import static teampixl.com.pixlpos.database.api.util.Exceptions.isSuccessful;
import static teampixl.com.pixlpos.database.api.util.Exceptions.returnStatus;

public class APITest {
    public static void main(String[] args) throws Exception {
        UserStack.getInstance().setCurrentUser("admin");
        Logs logs = new Logs(Action.CREATE, Status.SUCCESS, Type.DATABASE, Category.INFO, Priority.LOW);
        System.out.println("User Logs: " + logs.getMetadata().metadata());
        System.out.println("User Logs: " + logs.getData());

        OrderAPI orderAPI = OrderAPI.getInstance();

        Order order = orderAPI.initializeOrder();

        System.out.println("Order Contents: " + order.getMetadata().metadata());

        List<StatusCode> Stat = orderAPI.putOrderItem(order.getMetadataValue("order_id").toString(), MenuAPI.getInstance().keySearch("Pizza"), 4);
        if (isSuccessful(Stat)) {
            System.out.println("Order placed successfully.");
        } else {
            System.out.println(returnStatus("Order items could not be added with the following errors:", Stat));
        }

        order.setDataValue("special_requests", "No cheese");

        List<StatusCode> Status = orderAPI.postOrder(order);
        if (isSuccessful(Status)) {
            System.out.println("Order placed successfully.");
        } else {
            System.out.println(returnStatus("Order could not be placed with the following errors:", Status));
        }

        /* KEYS */
//        String[] MetadataKeys = {"id", "itemName", "price", "itemType", "activeItem", "dietaryRequirement", "created_at", "updated_at"};
//        String[] DataKeys = {"description", "notes", "amountOrdered", "ingredients"};
//
//        MenuItem menuItem = new MenuItem("Pizza", 10.99, MenuItem.ItemType.MAIN, true, "This is a Pizza", MenuItem.DietaryRequirement.NONE);
//        Ingredients ingredients = new Ingredients("Cheese", "This is cheese");
//        Ingredients ingredients1 = new Ingredients("Tomato", "This is tomato");
//        System.out.println("Ingredients: " + ingredients.getMetadata().metadata());
//        System.out.println("Ingredients: " + ingredients.getData());
//
//
//        menuItem.addIngredient(ingredients, 2);
//        menuItem.addIngredient(ingredients1, 3);
//        System.out.println("Menu Item: " + menuItem.getMetadata().metadata());
//        System.out.println("Menu Item: " + menuItem.getData());
//        System.out.println("Menu Item: " + menuItem.getIngredients());
//
//        /* KEYS */
//        System.out.println("Metadata Keys: ");
//        for (String key : MetadataKeys) {
//            System.out.println(key);
//        }
//
//        System.out.println("Data Keys: ");
//        for (String key : DataKeys) {
//            System.out.println(key);
//        }
//
//        System.out.println("Item Name: " + menuItem.getMetadataValue(MetadataKeys[1]));
//        System.out.println("Price: " + menuItem.getMetadataValue(MetadataKeys[2]));
//        System.out.println("Item Type: " + menuItem.getMetadataValue(MetadataKeys[3]));
//        System.out.println("Active Item: " + menuItem.getMetadataValue(MetadataKeys[4]));
//        System.out.println("Dietary Requirement: " + menuItem.getMetadataValue(MetadataKeys[5]));
//        System.out.println("Created At: " + menuItem.getMetadataValue(MetadataKeys[6]));
//        System.out.println("Updated At: " + menuItem.getMetadataValue(MetadataKeys[7]));
//
//        System.out.println("Description: " + menuItem.getDataValue(DataKeys[0]));
//        System.out.println("Notes: " + menuItem.getDataValue(DataKeys[1]));
//        System.out.println("Amount Ordered: " + menuItem.getDataValue(DataKeys[2]));
//        System.out.println("Ingredients: " + menuItem.getDataValue(DataKeys[3]));

        /* Network */


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
