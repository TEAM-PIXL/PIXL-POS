package teampixl.com.pixlpos.database.api;

import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.Users;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class OrderAPI {
    private static final DataStore dataStore = DataStore.getInstance();
    private static OrderAPI instance;

    private OrderAPI() { }

    /**
     * Gets the instance of the OrderAPI.
     *
     * @return the instance of the OrderAPI
     */
    public static synchronized OrderAPI getInstance() {
        if (instance == null) {
            instance = new OrderAPI();
        }
        return instance;
    }

    /**
     * Validates the order number.
     * @param ORDER_NUMBER the order number to validate
     * @return the status code
     */
    public StatusCode validateOrderByNumber(int ORDER_NUMBER) {
        if (ORDER_NUMBER < 0) {
            return StatusCode.INVALID_ORDER_NUMBER;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the order by user.
     * @param USERNAME the username to validate
     * @return the status code
     */
    public StatusCode validateOrderByUser(String USERNAME) {
        if (USERNAME == null) {
            return StatusCode.INVALID_USER_ID;
        }
        else if (UsersAPI.getUsersByUsername(USERNAME) == null) {
            return StatusCode.USER_NOT_FOUND;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the order.
     * @param order the order to validate
     * @return the status code
     */
    public List<StatusCode> validateOrder(Order order) {
        List<StatusCode> statusCodes = new ArrayList<>();
        if (order == null) {
            statusCodes.add(StatusCode.INVALID_ORDER);
        }
        assert order != null;
        if (order.getMetadata().metadata().get("order_id") == null) {
            statusCodes.add(StatusCode.INVALID_ORDER_ID);
        }
        if (order.getMetadata().metadata().get("order_status") == null) {
            statusCodes.add(StatusCode.INVALID_ORDER_STATUS);
        }
        if (order.getMetadata().metadata().get("created_at") == null) {
            statusCodes.add(StatusCode.INVALID_ORDER_TIME);
        }
        if (order.getData().get("total") == null) {
            statusCodes.add(StatusCode.INVALID_ORDER_TOTAL);
        }
        if (order.getData().get("menuItems") == null) {
            statusCodes.add(StatusCode.INVALID_ORDER_ITEMS);
        }
        return statusCodes;
    }

    /**
     * Gets the order by order number.
     *
     * @param ORDER_NUMBER the order number
     * @return the order id
     */
    public static String getOrderByNumber(String ORDER_NUMBER) {
        return dataStore.getOrders().stream()
                .filter(order -> order.getMetadata().metadata().get("order_number").toString().equals(ORDER_NUMBER))
                .findFirst()
                .map(order -> order.getMetadata().metadata().get("order_id").toString())
                .orElse(null);
    }

    /**
     * Gets the order by order id.
     *
     * @param ORDER_ID the order id
     * @return the order
     */
    public static Order getOrderById(String ORDER_ID) {
        return dataStore.getOrders().stream()
                .filter(order -> order.getMetadata().metadata().get("order_id").toString().equals(ORDER_ID))
                .findFirst()
                .orElse(null);
    }

    public List<MenuItem> getOrderItemsById(String ORDER_ID) {
        Order order = getOrderById(ORDER_ID);
        if (order == null) {
            return null;
        }
        return dataStore.getMenuItems().stream()
                .filter(menuItem -> order.getMetadata().metadata().get("order_items").toString().contains(menuItem.getMetadata().metadata().get("id").toString())).toList();
    }

    /**
     * Initializes the order.
     *
     * @return the order
     */
    public Order initializeOrder() {
        int ORDER_NUMBER = 0;
        String USER_ID = null;
        List<Order> orders = dataStore.getOrders();
        if (!orders.isEmpty()) {
            ORDER_NUMBER = (int) orders.getLast().getMetadata().metadata().get("order_number") + 1;
            USER_ID = UserStack.getInstance().getCurrentUserId();
        }
        try {
            Order order = new Order(ORDER_NUMBER, USER_ID);
            dataStore.addOrder(order);
            return order;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Posts the order.
     *
     * @param order the order to post
     * @return the status code
     */
    public List<StatusCode> postOrder(Order order) {
        List<StatusCode> statusCodes = validateOrder(order);
        if (statusCodes.isEmpty()) {
            dataStore.updateOrder(order);
            statusCodes.add(StatusCode.SUCCESS);
        }
        return statusCodes;
    }

    /**
     * Puts the order by item.
     *
     * @param ORDER_NUMBER the order number
     * @param ITEM_NAME the item name
     * @param QUANTITY the quantity
     * @return the status code
     */
    public List<StatusCode> putOrderByItem(int ORDER_NUMBER, String ITEM_NAME, int QUANTITY) {
        List<StatusCode> statusCodes = new ArrayList<>();
        String orderId = getOrderByNumber(String.valueOf(ORDER_NUMBER));
        System.out.println("Order ID: " + orderId);
        if (orderId == null) {
            statusCodes.add(StatusCode.ORDER_NOT_FOUND);
            return statusCodes;
        }
        Order order = getOrderById(orderId);
        if (order == null) {
            statusCodes.add(StatusCode.ORDER_NOT_FOUND);
            return statusCodes;
        }

        String id = MenuAPI.getInstance().getMenuItemByName(ITEM_NAME);
        System.out.println("Item ID: " + id);
        if (id == null) {
            statusCodes.add(StatusCode.MENU_ITEM_NOT_FOUND);
            return statusCodes;
        }
        MenuItem menuItem = MenuAPI.getInstance().getMenuItemById(id);
        if (menuItem == null) {
            statusCodes.add(StatusCode.MENU_ITEM_NOT_FOUND);
            return statusCodes;
        }

        Map<String, Object> menuItems = DataStore.getInstance().getOrderItems(order);
        if (menuItems == null) {
            statusCodes.add(StatusCode.MENU_ITEM_NOT_FOUND);
            return statusCodes;
        }

        System.out.println("Menu Items: " + menuItems);

        Map<String, Object> orderItem = DataStore.getInstance().getOrderItem(order, ITEM_NAME);

        if (orderItem == null) {
            DataStore.getInstance().addOrderItem(order, menuItem, QUANTITY);
        } else {
            int currentQuantity = (int) orderItem.get("quantity");
            int newQuantity = currentQuantity + QUANTITY;
            dataStore.updateOrderItem(order, menuItem, newQuantity);
        }

        if (statusCodes.isEmpty()) {
            statusCodes.add(StatusCode.SUCCESS);
        }
        return statusCodes;
    }

//    public static StatusCode deleteOrderByItem(int ORDER_NUMBER, String itemName, int quantity) {
//        String orderId = getOrderByNumber(String.valueOf(ORDER_NUMBER));
//        if (orderId == null) {
//            return StatusCode.ORDER_NOT_FOUND;
//        }
//        Order order = getOrderById(orderId);
//        if (order == null) {
//            return StatusCode.ORDER_NOT_FOUND;
//        }
//
//        String id = MenuAPI.getInstance().getMenuItemByName(itemName);
//        if (id == null) {
//            return StatusCode.MENU_ITEM_NOT_FOUND;
//        }
//        MenuItem menuItem = MenuAPI.getInstance().getMenuItemById(id);
//        if (menuItem == null) {
//            return StatusCode.MENU_ITEM_NOT_FOUND;
//        }
//
//        Map<String, Object> menuItems = (Map<String, Object>) order.getMetadata().metadata().get("menuItems");
//        if (menuItems == null) {
//            return StatusCode.MENU_ITEM_NOT_FOUND;
//        }
//
//        Map<String, Object> orderItem = menuItems.values().stream()
//                .map(item -> (Map<String, Object>) item)
//                .filter(item -> item.get("id").toString().equals(menuItem.getMetadata().metadata().get("id").toString()))
//                .findFirst()
//                .orElse(null);
//        if (orderItem == null) {
//            return StatusCode.MENU_ITEM_NOT_FOUND;
//        }
//
//        int currentQuantity = (int) orderItem.get("quantity");
//        if (quantity > currentQuantity) {
//            return StatusCode.QUANTITY_EXCEEDS_ORDER;
//        }
//        if (quantity == currentQuantity) {
//            menuItems.remove(orderItem.get("id").toString());
//        } else {
//            orderItem.put("quantity", currentQuantity - quantity);
//        }
//        order.updateMetadata("menuItems", menuItems);
//        return StatusCode.SUCCESS;
//    }

    /**
     * Searches for orders based on the query. This is experimental and may not work as expected. It uses a medium complexity search algorithm in the form of NLQ (Natural Language Query).
     *
     * @param query the search query
     * @return the list of orders that match the query
     */
    public static List<Order> searchOrders(String query) {
        String[] tokens = query.trim().toLowerCase().split("\\s+");
        List<Order> orders = dataStore.getOrders();

        return orders.stream()
                .filter(order -> matchesOrder(order, tokens))
                .collect(Collectors.toList());
    }

    private static boolean matchesOrder(Order order, String[] tokens) {
        for (String token : tokens) {
            if (!matchesToken(order, token)) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchesToken(Order order, String token) {
        return matchesOrderStatus(order, token) ||
                matchesOrderNumber(order, token) ||
                matchesUserName(order, token) ||
                matchesTime(order, token);
    }

    private static boolean matchesOrderStatus(Order order, String token) {
        try {
            Order.OrderStatus status = Order.OrderStatus.valueOf(token.toUpperCase());
            return order.getMetadata().metadata().get("order_status") == status;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static boolean matchesOrderNumber(Order order, String token) {
        return order.getMetadata().metadata().get("order_number").toString().contains(token);
    }

    private static boolean matchesUserName(Order order, String token) {
        String userId = order.getMetadata().metadata().get("user_id").toString();
        List<Users> users = UsersAPI.searchUsers(token);
        return users.stream().anyMatch(user -> user.getMetadata().metadata().get("id").toString().equals(userId));
    }

    private static boolean matchesTime(Order order, String token) {
        try {
            long timestamp = Long.parseLong(token);
            return order.getMetadata().metadata().get("created_at").equals(timestamp);
        } catch (NumberFormatException e) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(token);
                long orderTime = (long) order.getMetadata().metadata().get("created_at");
                Date orderDate = new Date(orderTime);
                return sdf.format(orderDate).equals(sdf.format(date));
            } catch (ParseException pe) {
                return matchesOtherDateFormats(order, token);
            }
        }
    }

    private static boolean matchesOtherDateFormats(Order order, String token) {
        List<String> dateFormats = Arrays.asList(
                "dd/MM/yy", "dd/MM/yyyy", "dd/MMM", "dd/MMM/yyyy", "MMMM", "MMMM yyyy", "yyyy", "dd"
        );
        for (String format : dateFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                Date date = sdf.parse(token);
                long orderTime = (long) order.getMetadata().metadata().get("created_at");
                Date orderDate = new Date(orderTime);
                if (sdf.format(orderDate).equals(sdf.format(date))) {
                    return true;
                }
            } catch (ParseException ignored) {
            }
        }

        Calendar calendar = Calendar.getInstance();
        long orderTime = (long) order.getMetadata().metadata().get("created_at");
        Date orderDate = new Date(orderTime);

        return switch (token.toLowerCase()) {
            case "today" -> {
                calendar.setTime(new Date());
                yield isSameDay(calendar.getTime(), orderDate);
            }
            case "yesterday" -> {
                calendar.add(Calendar.DATE, -1);
                yield isSameDay(calendar.getTime(), orderDate);
            }
            case "this year" -> {
                calendar.setTime(new Date());
                yield isSameYear(calendar.getTime(), orderDate);
            }
            case "last year" -> {
                calendar.add(Calendar.YEAR, -1);
                yield isSameYear(calendar.getTime(), orderDate);
            }
            default -> false;
        };
    }

    private static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private static boolean isSameYear(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }
}

