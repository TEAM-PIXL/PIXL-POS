package teampixl.com.pixlpos.database.api;

import javafx.collections.ObservableList;
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
     * @return the appropriate StatusCode from the enumeration class StatusCode
     */
    public static StatusCode validateOrderByNumber(int ORDER_NUMBER) {
        if (ORDER_NUMBER < 0) { return StatusCode.INVALID_ORDER_NUMBER; }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the order by user.
     * @param USERNAME the username to validate
     * @return the appropriate StatusCode from the enumeration class StatusCode
     */
    public static StatusCode validateOrderByUser(String USERNAME) {
        if (USERNAME == null) { return StatusCode.INVALID_USERNAME; }
        else if (UsersAPI.getUsersByUsername(USERNAME) == null) { return StatusCode.USER_NOT_FOUND; }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the order by id.
     * @param ORDER_ID the order id to validate
     * @return the appropriate StatusCode from the enumeration class StatusCode
     */
    public static StatusCode validateOrderById(String ORDER_ID) {
        if (ORDER_ID == null) { return StatusCode.INVALID_ORDER_ID; }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the order at the time of creation in the database.
     * @param ORDER the order to validate
     * @return the appropriate StatusCode from the enumeration class StatusCode
     */
    public static List<StatusCode> validateOrder(Order ORDER) {
        List<StatusCode> STATUS_CODES = new ArrayList<>();
        if (ORDER == null) {
            STATUS_CODES.add(StatusCode.INVALID_ORDER);
        } else {
            if (ORDER.getMetadata().metadata().get("order_id") == null) {
                STATUS_CODES.add(StatusCode.INVALID_ORDER_ID);
            }
            if (ORDER.getMetadata().metadata().get("order_status") == null) {
                STATUS_CODES.add(StatusCode.INVALID_ORDER_STATUS);
            } else {
                try {
                    Order.OrderStatus.valueOf(ORDER.getMetadata().metadata().get("order_status").toString());
                } catch (IllegalArgumentException e) {
                    STATUS_CODES.add(StatusCode.INVALID_ORDER_STATUS);
                }
            }
            if (ORDER.getMetadata().metadata().get("created_at") == null) {
                STATUS_CODES.add(StatusCode.INVALID_ORDER_TIME);
            }
            if (ORDER.getData().get("total") == null || (double) ORDER.getData().get("total") == 0.0) {
                STATUS_CODES.add(StatusCode.INVALID_ORDER_TOTAL);
            }
            Object MENU_ITEMS = ORDER.getData().get("menuItems");
            if (!(MENU_ITEMS instanceof Map<?, ?> menuItemsMap)) {
                STATUS_CODES.add(StatusCode.INVALID_ORDER_ITEMS);
            } else {
                if (menuItemsMap.isEmpty()) {
                    STATUS_CODES.add(StatusCode.INVALID_ORDER_ITEMS);
                }
            }
        }
        return STATUS_CODES;
    }


    /**
     * Gets the order by order number.
     *
     * @param ORDER_NUMBER the order number
     * @return the order id
     */
    public static String getOrderByNumber(int ORDER_NUMBER) {
        StatusCode STATUS_CODE = OrderAPI.validateOrderByNumber(ORDER_NUMBER);
        if (STATUS_CODE != StatusCode.SUCCESS) {
            return null;
        }
        String ORDER_NUMBER_STRING = String.valueOf(ORDER_NUMBER);
        return dataStore.readOrders().stream()
                .filter(order -> order.getMetadata().metadata().get("order_number").toString().equals(ORDER_NUMBER_STRING))
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
        StatusCode STATUS_CODE = OrderAPI.validateOrderById(ORDER_ID);
        if (STATUS_CODE != StatusCode.SUCCESS) { return null; }
        return dataStore.readOrders().stream()
                .filter(order -> order.getMetadata().metadata().get("order_id").toString().equals(ORDER_ID))
                .findFirst()
                .orElse(null);
    }

    public static List<MenuItem> getOrderItemsById(String ORDER_ID) {
        Order ORDER = getOrderById(ORDER_ID);
        if (ORDER == null) { return null; }
        try {
            return dataStore.readOrderItems(ORDER).values().stream()
                    .map(item -> (MenuItem) item)
                    .collect(Collectors.toList());
        }
        catch (Exception e) {
            return List.of();
        }

    }

    public static ObservableList<Order> getOrders() {
        return dataStore.readOrders();
    }

    /**
     * Initializes the order.
     *
     * @return the order
     */
    public static Order initializeOrder() {
        int ORDER_NUMBER = 1;
        String USER_ID;
        List<Order> ORDERS = dataStore.readOrders();
        if (!ORDERS.isEmpty()) {
            ORDER_NUMBER = (int) ORDERS.getLast().getMetadata().metadata().get("order_number") + 1;
        }
        USER_ID = UserStack.getInstance().getCurrentUserId();
        try {
            Order ORDER = new Order(ORDER_NUMBER, USER_ID);
            dataStore.createOrder(ORDER);
            return ORDER;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Posts the order.
     *
     * @param ORDER the order to post
     * @return the status code
     */
    public static List<StatusCode> postOrder(Order ORDER) {
        List<StatusCode> STATUS_CODES = validateOrder(ORDER);
        if (STATUS_CODES.isEmpty()) {
            ORDER.updateMetadata("order_status", Order.OrderStatus.SENT);

            // Check and calculate the total using the data map
            if (ORDER.getData().get("total") == null || (double) ORDER.getData().get("total") == 0.0) {
                @SuppressWarnings("unchecked")
                Map<String, Integer> menuItems = (Map<String, Integer>) ORDER.getData().get("menuItems");
                double total = 0.0;
                for (Map.Entry<String, Integer> entry : menuItems.entrySet()) {
                    String itemId = entry.getKey();
                    int quantity = entry.getValue();
                    MenuItem menuItem = dataStore.getMenuItemById(itemId);
                    if (menuItem != null) {
                        double price = (double) menuItem.getMetadata().metadata().get("price");
                        total += price * quantity;
                    }
                }
                ORDER.setDataValue("total", total);
            }

            dataStore.updateOrder(ORDER);
            STATUS_CODES.add(StatusCode.SUCCESS);
        }
        return STATUS_CODES;
    }


    /**
     * Puts the order by item.
     *
     * @param ORDER_NUMBER the order number
     * @param ITEM_NAME the item name
     * @param QUANTITY the quantity
     * @return the status code
     */
    public static List<StatusCode> putOrderByItem(int ORDER_NUMBER, String ITEM_NAME, int QUANTITY) {
        List<StatusCode> statusCodes = new ArrayList<>();
        String orderId = getOrderByNumber(ORDER_NUMBER);
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
        if (id == null) {
            statusCodes.add(StatusCode.MENU_ITEM_NOT_FOUND);
            return statusCodes;
        }
        MenuItem menuItem = MenuAPI.getInstance().getMenuItemById(id);
        if (menuItem == null) {
            statusCodes.add(StatusCode.MENU_ITEM_NOT_FOUND);
            return statusCodes;
        }

        @SuppressWarnings("unchecked")
        Map<String, Integer> menuItems = (Map<String, Integer>) order.getData().get("menuItems");
        if (menuItems == null) {
            statusCodes.add(StatusCode.MENU_ITEM_NOT_FOUND);
            return statusCodes;
        }

        if (!menuItems.containsKey(id)) {
            order.addMenuItem(menuItem, QUANTITY);
            dataStore.updateOrder(order);
        } else {
            int currentQuantity = menuItems.get(id);
            int NEW_QUANTITY = currentQuantity + QUANTITY;
            order.updateMenuItem(menuItem, NEW_QUANTITY);
            dataStore.updateOrder(order);
        }

        statusCodes.add(StatusCode.SUCCESS);
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
        List<Order> orders = dataStore.readOrders();

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

