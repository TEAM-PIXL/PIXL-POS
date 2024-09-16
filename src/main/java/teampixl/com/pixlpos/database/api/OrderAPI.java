package teampixl.com.pixlpos.database.api;

import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.Users;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

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
     * @param orderNumber the order number to validate
     * @return the status code
     */
    public StatusCode validateOrderByNumber(int orderNumber) {
        if (orderNumber < 0) {
            return StatusCode.INVALID_ORDER_NUMBER;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the order by user.
     * @param username the username to validate
     * @return the status code
     */
    public StatusCode validateOrderByUser(String username) {
        if (username == null) {
            return StatusCode.INVALID_USER_ID;
        }
        else if (UsersAPI.getUsersByUsername(username) == null) {
            return StatusCode.USER_NOT_FOUND;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the order.
     * @param order the order to validate
     * @return the status code
     */
    public StatusCode validateOrder(Order order) {
        if (order == null) {
            return StatusCode.INVALID_ORDER;
        }
        if (order.getMetadata().metadata().get("order_id") == null) {
            return StatusCode.INVALID_ORDER_ID;
        }
        if (order.getMetadata().metadata().get("order_status") == null) {
            return StatusCode.INVALID_ORDER_STATUS;
        }
        if (order.getMetadata().metadata().get("order_date") == null) {
            return StatusCode.INVALID_ORDER_DATE;
        }
        if (order.getMetadata().metadata().get("order_time") == null) {
            return StatusCode.INVALID_ORDER_TIME;
        }
        if (order.getMetadata().metadata().get("order_total") == null) {
            return StatusCode.INVALID_ORDER_TOTAL;
        }
        if (order.getMetadata().metadata().get("order_items") == null) {
            return StatusCode.INVALID_ORDER_ITEMS;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Gets the order by order number.
     *
     * @param orderNumber the order number
     * @return the order id
     */
    public static String getOrderByNumber(String orderNumber) {
        return dataStore.getOrders().stream()
                .filter(order -> order.getMetadata().metadata().get("order_number").toString().equals(orderNumber))
                .findFirst()
                .map(order -> order.getMetadata().metadata().get("order_id").toString())
                .orElse(null);
    }

    /**
     * Gets the order by order id.
     *
     * @param orderId the order id
     * @return the order
     */
    public static Order getOrderById(String orderId) {
        return dataStore.getOrders().stream()
                .filter(order -> order.getMetadata().metadata().get("order_id").toString().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    public List<MenuItem> getOrderItemsById(String orderId) {
        Order order = getOrderById(orderId);
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
            return new Order(ORDER_NUMBER, USER_ID);
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
    public StatusCode postOrder(Order order) {
        StatusCode statusCode = validateOrder(order);
        if (statusCode != StatusCode.SUCCESS) {
            return statusCode;
        }
        dataStore.addOrder(order);
        return StatusCode.SUCCESS;
    }

    /**
     * Puts the order by item.
     *
     * @param ORDER_NUMBER the order number
     * @param itemName the item name
     * @param quantity the quantity
     * @return the status code
     */
    public StatusCode putOrderByItem(int ORDER_NUMBER, String itemName, int quantity) {
        Order order = dataStore.getOrders().stream()
                .filter(o -> (int) o.getMetadata().metadata().get("order_number") == ORDER_NUMBER)
                .findFirst()
                .orElse(null);
        if (order == null) {
            return StatusCode.ORDER_NOT_FOUND;
        }
        String id = MenuAPI.getInstance().getMenuItemByName(itemName);
        if (id == null) {
            return StatusCode.MENU_ITEM_NOT_FOUND;
        }
        MenuItem menuItem = MenuAPI.getInstance().getMenuItemById(id);
        if (menuItem == null) {
            return StatusCode.MENU_ITEM_NOT_FOUND;
        }
        order.addMenuItem(menuItem, quantity);
        return StatusCode.SUCCESS;
    }

    public static StatusCode deleteOrderByItem(int ORDER_NUMBER, String itemName, int quantity) {
        String orderId = getOrderByNumber(String.valueOf(ORDER_NUMBER));
        if (orderId == null) {
            return StatusCode.ORDER_NOT_FOUND;
        }
        Order order = getOrderById(orderId);
        if (order == null) {
            return StatusCode.ORDER_NOT_FOUND;
        }

        String id = MenuAPI.getInstance().getMenuItemByName(itemName);
        if (id == null) {
            return StatusCode.MENU_ITEM_NOT_FOUND;
        }
        MenuItem menuItem = MenuAPI.getInstance().getMenuItemById(id);
        if (menuItem == null) {
            return StatusCode.MENU_ITEM_NOT_FOUND;
        }

        Map<String, Object> menuItems = (Map<String, Object>) order.getMetadata().metadata().get("menuItems");
        if (menuItems == null) {
            return StatusCode.MENU_ITEM_NOT_FOUND;
        }

        Map<String, Object> orderItem = menuItems.values().stream()
                .map(item -> (Map<String, Object>) item)
                .filter(item -> item.get("id").toString().equals(menuItem.getMetadata().metadata().get("id").toString()))
                .findFirst()
                .orElse(null);
        if (orderItem == null) {
            return StatusCode.MENU_ITEM_NOT_FOUND;
        }

        int currentQuantity = (int) orderItem.get("quantity");
        if (quantity > currentQuantity) {
            return StatusCode.QUANTITY_EXCEEDS_ORDER;
        }
        if (quantity == currentQuantity) {
            menuItems.remove(orderItem.get("id").toString());
        } else {
            orderItem.put("quantity", currentQuantity - quantity);
        }
        order.updateMetadata("menuItems", menuItems);
        return StatusCode.SUCCESS;
    }

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

