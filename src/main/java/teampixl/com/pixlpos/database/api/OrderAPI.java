package teampixl.com.pixlpos.database.api;

import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.models.Users;

import java.util.*;
import java.time.LocalDate;
import java.time.ZoneId;


/**
 * The OrderAPI class is responsible for managing orders.
 * It provides methods to create, read, update, and delete orders,
 * with robust validation and error handling.
 */
public class OrderAPI {
    private static OrderAPI instance;
    private static final DataStore DATASTORE = DataStore.getInstance();
    private static final UsersAPI USERSAPI = UsersAPI.getInstance();
    private static final MenuAPI MENUAPI = MenuAPI.getInstance();

    private OrderAPI() {
    }

    /**
     * Gets the singleton instance of the OrderAPI.
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
     *
     * @param ORDER_NUMBER the order number to validate
     * @return the appropriate StatusCode
     */
    public StatusCode validateOrderByNumber(Integer ORDER_NUMBER) {
        if (ORDER_NUMBER == null || ORDER_NUMBER <= 0) {
            return StatusCode.INVALID_ORDER_NUMBER;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the user ID associated with the order.
     *
     * @param USER_ID the user ID to validate
     * @return the appropriate StatusCode
     */
    public StatusCode validateOrderByUserId(String USER_ID) {
        if (USER_ID == null || USER_ID.trim().isEmpty()) {
            return StatusCode.INVALID_USER_ID;
        }
        Users USER = USERSAPI.keyTransform(USER_ID);
        return USER != null ? StatusCode.SUCCESS : StatusCode.USER_NOT_FOUND;
    }

    /**
     * Validates the order ID.
     *
     * @param ORDER_ID the order ID to validate
     * @return the appropriate StatusCode
     */
    public StatusCode validateOrderById(String ORDER_ID) {
        if (ORDER_ID == null || ORDER_ID.trim().isEmpty()) {
            return StatusCode.INVALID_ORDER_ID;
        }
        Order ORDER = keyTransform(ORDER_ID);
        return ORDER != null ? StatusCode.SUCCESS : StatusCode.ORDER_NOT_FOUND;
    }

    /**
     * Validates the order status.
     *
     * @param ORDER_STATUS the order status to validate
     * @return the appropriate StatusCode
     */
    public StatusCode validateOrderByStatus(Order.OrderStatus ORDER_STATUS) {
        if (ORDER_STATUS == null) {
            return StatusCode.INVALID_ORDER_STATUS;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the table number associated with the order.
     *
     * @param TABLE_NUMBER the table number to validate
     * @return the appropriate StatusCode
     */
    public StatusCode validateOrderByTableNumber(Integer TABLE_NUMBER) {
        if (TABLE_NUMBER == null || TABLE_NUMBER < 0) {
            return StatusCode.INVALID_TABLE_NUMBER;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the number of customers associated with the order.
     *
     * @param CUSTOMERS the number of customers to validate
     * @return the appropriate StatusCode
     */
    public StatusCode validateOrderByCustomers(Integer CUSTOMERS) {
        if (CUSTOMERS == null || CUSTOMERS <= 0) {
            return StatusCode.INVALID_CUSTOMERS;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the special requests associated with the order.
     *
     * @param SPECIAL_REQUESTS the special requests to validate
     * @return the appropriate StatusCode
     */
    public StatusCode validateOrderBySpecialRequests(String SPECIAL_REQUESTS) {
        if (SPECIAL_REQUESTS != null && SPECIAL_REQUESTS.length() > 255) {
            return StatusCode.SPECIAL_REQUESTS_TOO_LONG;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the menu items associated with the order.
     *
     * @param MENU_ITEMS the menu items to validate
     * @return the appropriate StatusCode
     */
    public List<StatusCode> validateOrderByItems(Map<MenuItem, Integer> MENU_ITEMS) {
        ArrayList<StatusCode> VALIDATIONS = new ArrayList<>();
        if (MENU_ITEMS == null || MENU_ITEMS.isEmpty()) {
            return List.of(StatusCode.NO_ORDER_ITEMS);
        }
        if (MENU_ITEMS.keySet().stream().anyMatch(Objects::isNull)) {
            VALIDATIONS.add(StatusCode.UNKNOWN_MENU_ITEM);
        }
        if (MENU_ITEMS.values().stream().anyMatch(quantity -> quantity <= 0)) {
            VALIDATIONS.add(StatusCode.INVALID_QUANTITY);
        }
        if (MENU_ITEMS.keySet().stream().anyMatch(menuItem -> MENUAPI.keyTransform(menuItem.getMetadata().metadata().get("id").toString()) == null)) {
            VALIDATIONS.add(StatusCode.MENU_ITEM_NOT_FOUND);
        }
        for (Map.Entry<MenuItem, Integer> entry : MENU_ITEMS.entrySet()) {
            List<StatusCode> MENU_ITEM_CHECK = MENUAPI.validateMenuItem(entry.getKey());
            if (!Exceptions.isSuccessful(MENU_ITEM_CHECK)) {
                VALIDATIONS.addAll(MENU_ITEM_CHECK);
            }
        }
        return VALIDATIONS;
    }


    /**
     * Validates the entire Order object.
     *
     * @param order the Order object to validate
     * @return a list of StatusCodes indicating the validation results
     */
    public List<StatusCode> validateOrder(Order order) {
        List<StatusCode> statusCodes = new ArrayList<>();
        if (order == null) {
            statusCodes.add(StatusCode.INVALID_ORDER);
            return statusCodes;
        }

        String orderId = (String) order.getMetadata().metadata().get("order_id");
        Integer orderNumber = (Integer) order.getMetadata().metadata().get("order_number");
        String userId = (String) order.getMetadata().metadata().get("user_id");
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(order.getMetadata().metadata().get("order_status").toString());
        Integer tableNumber = (Integer) order.getMetadata().metadata().get("table_number");
        Integer customers = (Integer) order.getMetadata().metadata().get("customers");
        String specialRequests = (String) order.getMetadata().metadata().get("special_requests");

        statusCodes.add(validateOrderById(orderId));
        statusCodes.add(validateOrderByNumber(orderNumber));
        statusCodes.add(validateOrderByUserId(userId));
        statusCodes.add(validateOrderByStatus(orderStatus));
        statusCodes.add(validateOrderByTableNumber(tableNumber));
        statusCodes.add(validateOrderByCustomers(customers));
        statusCodes.add(validateOrderBySpecialRequests(specialRequests));
        statusCodes.addAll(validateOrderByItems(getOrderItemsById(orderId)));

        return statusCodes;
    }

    /**
     * Retrieves the order ID based on the order number.
     *
     * @param orderNumber the order number
     * @return the order ID
     */
    public String keySearch(Integer orderNumber) {
        if (orderNumber == null) {
            return null;
        }
        return DATASTORE.readOrders().stream()
                .filter(order -> Objects.equals(order.getMetadata().metadata().get("order_number"), orderNumber))
                .findFirst()
                .map(order -> (String) order.getMetadata().metadata().get("order_id"))
                .orElse(null);
    }

    /**
     * Retrieves the order number based on the order ID.
     *
     * @param orderId the order ID
     * @return the order number
     */
    public Integer reverseKeySearch(String orderId) {
        if (orderId == null) {
            return null;
        }
        return DATASTORE.readOrders().stream()
                .filter(order -> orderId.equals(order.getMetadata().metadata().get("order_id")))
                .findFirst()
                .map(order -> (Integer) order.getMetadata().metadata().get("order_number"))
                .orElse(null);
    }

    /**
     * Transforms an order ID into an Order object.
     *
     * @param orderId the order ID
     * @return the Order object
     */
    public Order keyTransform(String orderId) {
        if (orderId == null) {
            return null;
        }
        return DATASTORE.readOrders().stream()
                .filter(order -> orderId.equals(order.getMetadata().metadata().get("order_id")))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves an Order object based on its order number.
     *
     * @param orderNumber the order number
     * @return the Order object
     */
    public Order getOrderByNumber(Integer orderNumber) {
        String orderId = keySearch(orderNumber);
        return keyTransform(orderId);
    }

    /**
     * Retrieves all orders from the data store.
     *
     * @return a list of all orders
     */
    public List<Order> getOrders() {
        return DATASTORE.readOrders();
    }

    /**
     * Retrieves menu items associated with an order ID.
     *
     * @param orderId the order ID
     * @return a map of MenuItem objects and their quantities
     */
    public Map<MenuItem, Integer> getOrderItemsById(String orderId) {
        Order order = keyTransform(orderId);
        if (order == null) {
            return Collections.emptyMap();
        }

        DATASTORE.loadOrderItems(orderId, order);

        @SuppressWarnings("unchecked")
        Map<String, Integer> menuItemsMap = (Map<String, Integer>) order.getData().get("menuItems");
        Map<MenuItem, Integer> menuItemsWithQuantity = new HashMap<>();

        for (Map.Entry<String, Integer> entry : menuItemsMap.entrySet()) {
            MenuItem menuItem = MENUAPI.keyTransform(entry.getKey());
            if (menuItem != null) {
                menuItemsWithQuantity.put(menuItem, entry.getValue());
            }
        }

        return menuItemsWithQuantity;
    }

    /**
     * Initializes a new order for a user. If the user already has a pending order for today, it returns that order.
     *
     * @return the initialized Order object
     */
    public Order initializeOrder() {
        String userId = UserStack.getInstance().getCurrentUserId();
        if (userId == null) {
            System.out.println("No current user found.");
            return null;
        }

        List<Order> orders = DATASTORE.readOrders();
        long startOfToday = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        Optional<Order> pendingOrder = orders.stream()
                .filter(order -> userId.equals(order.getMetadata().metadata().get("user_id")))
                .filter(order -> Order.OrderStatus.PENDING.name().equals(order.getMetadata().metadata().get("order_status")))
                .filter(order -> {
                    Object createdAtObj = order.getMetadata().metadata().get("created_at");
                    if (createdAtObj instanceof Number) {
                        long createdAt = ((Number) createdAtObj).longValue();
                        System.out.println("Order created at: " + createdAt);
                        return createdAt >= startOfToday;
                    } else {
                        System.out.println("Invalid created_at for order: " + order.getMetadata().metadata().get("order_id"));
                        return false;
                    }
                })
                .max(Comparator.comparing(order -> {
                    Object createdAtObj = order.getMetadata().metadata().get("created_at");
                    if (createdAtObj instanceof Number) {
                        System.out.println("Order created at: " + createdAtObj);
                        return ((Number) createdAtObj).longValue();
                    } else {
                        return 0L;
                    }
                }));

        if (pendingOrder.isPresent()) {
            System.out.println("Pending order found for user: " + userId);
            return pendingOrder.get();
        } else {
            int orderNumber = 1;
            if (!orders.isEmpty()) {
                orderNumber = orders.stream()
                        .mapToInt(Order::getOrderNumber)
                        .max()
                        .orElse(0) + 1;
            }

            try {
                Order order = new Order(orderNumber, userId);
                DATASTORE.createOrder(order);
                System.out.println("New order created for user: " + userId + " with order number: " + orderNumber);
                return order;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }




    public List<StatusCode> postOrder(Order order) {
        List<StatusCode> validations = validateOrder(order);
        if (!Exceptions.isSuccessful(validations)) {
            System.out.println("Order post validation failed.");
            return validations;
        }
        System.out.println("Order post validation successful.");

        try {
            order.updateMetadata("order_status", Order.OrderStatus.SENT.name());
            DATASTORE.updateOrder(order);
            System.out.println("Order posted successfully.");
            return validations;
        } catch (Exception e) {
            return List.of(StatusCode.POST_ORDER_FAILED);
        }
    }

    /**
     * Adds a menu item to an existing order.
     *
     * @param orderId the order ID
     * @param menuItemId the menu item ID
     * @param quantity the quantity to add
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> putOrderItem(String orderId, String menuItemId, Integer quantity) {
        List<StatusCode> validations = new ArrayList<>();

        StatusCode orderValidation = validateOrderById(orderId);
        validations.add(orderValidation);
        if (orderValidation != StatusCode.SUCCESS) {
            return validations;
        }

        MenuItem menuItem = MENUAPI.keyTransform(menuItemId);
        if (menuItem == null) {
            validations.add(StatusCode.MENU_ITEM_NOT_FOUND);
            return validations;
        }

        if (quantity == null || quantity <= 0) {
            validations.add(StatusCode.INVALID_QUANTITY);
            return validations;
        }

        Order order = keyTransform(orderId);
        if (order == null) {
            validations.add(StatusCode.ORDER_NOT_FOUND);
            return validations;
        }

        try {
            order.addMenuItem(menuItem, quantity);
            DATASTORE.updateOrder(order);
            validations.add(StatusCode.SUCCESS);
        } catch (IllegalArgumentException | IllegalStateException e) {
            validations.add(StatusCode.ORDER_UPDATE_FAILED);
        }
        return validations;
    }

    /**
     * Updates the status of an existing order.
     *
     * @param orderId the order ID
     * @param orderStatus the new order status
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> putOrderStatus(String orderId, Order.OrderStatus orderStatus) {
        List<StatusCode> validations = new ArrayList<>();

        validations.add(validateOrderById(orderId));
        validations.add(validateOrderByStatus(orderStatus));

        if (!Exceptions.isSuccessful(validations)) {
            return validations;
        }

        Order order = keyTransform(orderId);
        if (order == null) {
            validations.add(StatusCode.ORDER_NOT_FOUND);
            return validations;
        }

        try {
            order.updateOrderStatus(orderStatus);
            DATASTORE.updateOrder(order);
            validations.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            validations.add(StatusCode.ORDER_UPDATE_FAILED);
        }
        return validations;
    }

    /**
     * Removes a menu item from an existing order.
     *
     * @param orderId the order ID
     * @param menuItemId the menu item ID
     * @param quantity the quantity to remove
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> deleteOrderItem(String orderId, String menuItemId, Integer quantity) {
        List<StatusCode> validations = new ArrayList<>();

        StatusCode orderValidation = validateOrderById(orderId);
        validations.add(orderValidation);
        if (orderValidation != StatusCode.SUCCESS) {
            return validations;
        }
        MenuItem menuItem = MENUAPI.keyTransform(menuItemId);
        if (menuItem == null) {
            validations.add(StatusCode.MENU_ITEM_NOT_FOUND);
            return validations;
        }

        if (quantity == null || quantity <= 0) {
            validations.add(StatusCode.INVALID_QUANTITY);
            return validations;
        }

        Order order = keyTransform(orderId);
        if (order == null) {
            validations.add(StatusCode.ORDER_NOT_FOUND);
            return validations;
        }

        try {
            order.removeMenuItem(menuItem, quantity);
            DATASTORE.updateOrder(order);
            validations.add(StatusCode.SUCCESS);
        } catch (IllegalArgumentException | IllegalStateException e) {
            validations.add(StatusCode.ORDER_UPDATE_FAILED);
        }
        return validations;
    }

    /**
     * Deletes an order from the database.
     *
     * @param orderId the order ID
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> deleteOrder(String orderId) {
        List<StatusCode> validations = new ArrayList<>();

        StatusCode validation = validateOrderById(orderId);
        validations.add(validation);
        if (validation != StatusCode.SUCCESS) {
            return validations;
        }

        Order order = keyTransform(orderId);
        if (order == null) {
            validations.add(StatusCode.ORDER_NOT_FOUND);
            return validations;
        }

        try {
            DATASTORE.deleteOrder(order);
            validations.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            validations.add(StatusCode.ORDER_DELETION_FAILED);
        }
        return validations;
    }

    /**
     * Clears all items from an existing order.
     *
     * @param orderId the order ID
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> clearOrderItems(String orderId) {
        List<StatusCode> validations = new ArrayList<>();

        StatusCode orderValidation = validateOrderById(orderId);
        validations.add(orderValidation);
        if (orderValidation != StatusCode.SUCCESS) {
            return validations;
        }

        Order order = keyTransform(orderId);
        if (order == null) {
            validations.add(StatusCode.ORDER_NOT_FOUND);
            return validations;
        }

        try {
            order.setDataValue("menuItems", new HashMap<>());
            DATASTORE.updateOrder(order);
            validations.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            validations.add(StatusCode.ORDER_UPDATE_FAILED);
        }
        return validations;
    }
}