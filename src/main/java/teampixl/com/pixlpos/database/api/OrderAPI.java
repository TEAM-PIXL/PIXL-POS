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
     * @param ORDER_NUMBER the order number
     * @return the order ID
     */
    public String keySearch(Integer ORDER_NUMBER) {
        if (ORDER_NUMBER == null) {
            return null;
        }
        return DATASTORE.readOrders().stream()
                .filter(order -> Objects.equals(order.getMetadata().metadata().get("order_number"), ORDER_NUMBER))
                .findFirst()
                .map(order -> (String) order.getMetadata().metadata().get("order_id"))
                .orElse(null);
    }

    /**
     * Retrieves the order number based on the order ID.
     *
     * @param ORDER_ID the order ID
     * @return the order number
     */
    public Integer reverseKeySearch(String ORDER_ID) {
        if (ORDER_ID == null) {
            return null;
        }
        return DATASTORE.readOrders().stream()
                .filter(order -> ORDER_ID.equals(order.getMetadata().metadata().get("order_id")))
                .findFirst()
                .map(order -> (Integer) order.getMetadata().metadata().get("order_number"))
                .orElse(null);
    }

    /**
     * Transforms an order ID into an Order object.
     *
     * @param ORDER_ID the order ID
     * @return the Order object
     */
    public Order keyTransform(String ORDER_ID) {
        if (ORDER_ID == null) {
            return null;
        }
        return DATASTORE.readOrders().stream()
                .filter(order -> ORDER_ID.equals(order.getMetadata().metadata().get("order_id")))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves an Order object based on its order number.
     *
     * @param ORDER_NUMBER the order number
     * @return the Order object
     */
    public Order getOrderByNumber(Integer ORDER_NUMBER) {
        String orderId = keySearch(ORDER_NUMBER);
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
     * @param ORDER_ID the order ID
     * @return a map of MenuItem objects and their quantities
     */
    public Map<MenuItem, Integer> getOrderItemsById(String ORDER_ID) {
        Order ORDER = keyTransform(ORDER_ID);
        if (ORDER == null) {
            return Collections.emptyMap();
        }

        DATASTORE.loadOrderItems(ORDER_ID, ORDER);

        @SuppressWarnings("unchecked")
        Map<String, Integer> MENU_ITEMS_MAP = (Map<String, Integer>) ORDER.getData().get("menuItems");
        Map<MenuItem, Integer> MENU_ITEMS_WITH_QUANTITY = new HashMap<>();

        for (Map.Entry<String, Integer> entry : MENU_ITEMS_MAP.entrySet()) {
            MenuItem MENU_ITEM = MENUAPI.keyTransform(entry.getKey());
            if (MENU_ITEM != null) {
                MENU_ITEMS_WITH_QUANTITY.put(MENU_ITEM, entry.getValue());
            }
        }

        return MENU_ITEMS_WITH_QUANTITY;
    }

    /**
     * Initializes a new order for a user. If the user already has a pending order for today, it returns that order.
     *
     * @return the initialized Order object
     */
    public Order initializeOrder() {
        String USER_ID = UserStack.getInstance().getCurrentUserId();
        if (USER_ID == null) {
            System.out.println("No current user found.");
            return null;
        }

        List<Order> ORDERS = DATASTORE.readOrders();
        long startOfToday = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        Optional<Order> PENDING_ORDER = ORDERS.stream()
                .filter(order -> USER_ID.equals(order.getMetadata().metadata().get("user_id")))
                .filter(order -> Order.OrderStatus.PENDING.name().equals(order.getMetadata().metadata().get("order_status")))
                .filter(order -> {
                    Object CREATE_AT_OBJ = order.getMetadata().metadata().get("created_at");
                    if (CREATE_AT_OBJ instanceof Number) {
                        long createdAt = ((Number) CREATE_AT_OBJ).longValue();
                        System.out.println("Order created at: " + createdAt);
                        return createdAt >= startOfToday;
                    } else {
                        System.out.println("Invalid created_at for order: " + order.getMetadata().metadata().get("order_id"));
                        return false;
                    }
                })
                .max(Comparator.comparing(order -> {
                    Object CREATE_AT_OBJ = order.getMetadata().metadata().get("created_at");
                    if (CREATE_AT_OBJ instanceof Number) {
                        System.out.println("Order created at: " + CREATE_AT_OBJ);
                        return ((Number) CREATE_AT_OBJ).longValue();
                    } else {
                        return 0L;
                    }
                }));

        if (PENDING_ORDER.isPresent()) {
            System.out.println("Pending order found for user: " + USER_ID);
            return PENDING_ORDER.get();
        } else {
            int ORDER_NUMBER = 1;
            if (!ORDERS.isEmpty()) {
                ORDER_NUMBER = ORDERS.stream()
                        .mapToInt(Order::getOrderNumber)
                        .max()
                        .orElse(0) + 1;
            }

            try {
                Order ORDER = new Order(ORDER_NUMBER, USER_ID);
                DATASTORE.createOrder(ORDER);
                System.out.println("New order created for user: " + USER_ID + " with order number: " + ORDER_NUMBER);
                return ORDER;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }



    /**
     * Posts an order to the database.
     *
     * @param ORDER the Order object to post
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> postOrder(Order ORDER) {
        List<StatusCode> VALIDATIONS = validateOrder(ORDER);
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            System.out.println("Order post validation failed.");
            return VALIDATIONS;
        }
        System.out.println("Order post validation successful.");

        try {
            ORDER.updateMetadata("order_status", Order.OrderStatus.SENT.name());
            DATASTORE.updateOrder(ORDER);
            System.out.println("Order posted successfully.");
            return VALIDATIONS;
        } catch (Exception e) {
            return List.of(StatusCode.POST_ORDER_FAILED);
        }
    }

    /**
     * Adds a menu item to an existing order.
     *
     * @param ORDER_ID the order ID
     * @param MENU_ITEM_ID the menu item ID
     * @param QUANTITY the quantity to add
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> putOrderItem(String ORDER_ID, String MENU_ITEM_ID, Integer QUANTITY) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();

        StatusCode ORDER_VALIDATION = validateOrderById(ORDER_ID);
        VALIDATIONS.add(ORDER_VALIDATION);
        if (ORDER_VALIDATION != StatusCode.SUCCESS) {
            return VALIDATIONS;
        }

        MenuItem menuItem = MENUAPI.keyTransform(MENU_ITEM_ID);
        if (menuItem == null) {
            VALIDATIONS.add(StatusCode.MENU_ITEM_NOT_FOUND);
            return VALIDATIONS;
        }

        if (QUANTITY == null || QUANTITY <= 0) {
            VALIDATIONS.add(StatusCode.INVALID_QUANTITY);
            return VALIDATIONS;
        }

        Order order = keyTransform(ORDER_ID);
        if (order == null) {
            VALIDATIONS.add(StatusCode.ORDER_NOT_FOUND);
            return VALIDATIONS;
        }

        try {
            order.addMenuItem(menuItem, QUANTITY);
            DATASTORE.updateOrder(order);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (IllegalArgumentException | IllegalStateException e) {
            VALIDATIONS.add(StatusCode.ORDER_UPDATE_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Updates the status of an existing order.
     *
     * @param ORDER_ID the order ID
     * @param ORDER_STATUS the new order status
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> putOrderStatus(String ORDER_ID, Order.OrderStatus ORDER_STATUS) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();

        VALIDATIONS.add(validateOrderById(ORDER_ID));
        VALIDATIONS.add(validateOrderByStatus(ORDER_STATUS));

        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }

        Order ORDER = keyTransform(ORDER_ID);
        if (ORDER == null) {
            VALIDATIONS.add(StatusCode.ORDER_NOT_FOUND);
            return VALIDATIONS;
        }

        try {
            ORDER.updateOrderStatus(ORDER_STATUS);
            DATASTORE.updateOrder(ORDER);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.ORDER_UPDATE_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Removes a menu item from an existing order.
     *
     * @param ORDER_ID the order ID
     * @param MENU_ITEM_ID the menu item ID
     * @param QUANTITY the quantity to remove
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> deleteOrderItem(String ORDER_ID, String MENU_ITEM_ID, Integer QUANTITY) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();

        StatusCode ORDER_VALIDATION = validateOrderById(ORDER_ID);
        VALIDATIONS.add(ORDER_VALIDATION);
        if (ORDER_VALIDATION != StatusCode.SUCCESS) {
            return VALIDATIONS;
        }
        MenuItem menuItem = MENUAPI.keyTransform(MENU_ITEM_ID);
        if (menuItem == null) {
            VALIDATIONS.add(StatusCode.MENU_ITEM_NOT_FOUND);
            return VALIDATIONS;
        }

        if (QUANTITY == null || QUANTITY <= 0) {
            VALIDATIONS.add(StatusCode.INVALID_QUANTITY);
            return VALIDATIONS;
        }

        Order ORDER = keyTransform(ORDER_ID);
        if (ORDER == null) {
            VALIDATIONS.add(StatusCode.ORDER_NOT_FOUND);
            return VALIDATIONS;
        }

        try {
            ORDER.removeMenuItem(menuItem, QUANTITY);
            DATASTORE.updateOrder(ORDER);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (IllegalArgumentException | IllegalStateException e) {
            VALIDATIONS.add(StatusCode.ORDER_UPDATE_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Deletes an order from the database.
     *
     * @param ORDER_ID the order ID
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> deleteOrder(String ORDER_ID) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();

        StatusCode ORDER_VALIDATION = validateOrderById(ORDER_ID);
        VALIDATIONS.add(ORDER_VALIDATION);
        if (ORDER_VALIDATION != StatusCode.SUCCESS) {
            return VALIDATIONS;
        }

        Order order = keyTransform(ORDER_ID);
        if (order == null) {
            VALIDATIONS.add(StatusCode.ORDER_NOT_FOUND);
            return VALIDATIONS;
        }

        try {
            DATASTORE.deleteOrder(order);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.ORDER_DELETION_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Clears all items from an existing order.
     *
     * @param ORDER_ID the order ID
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> clearOrderItems(String ORDER_ID) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();

        StatusCode ORDER_VALIDATION = validateOrderById(ORDER_ID);
        VALIDATIONS.add(ORDER_VALIDATION);
        if (ORDER_VALIDATION != StatusCode.SUCCESS) {
            return VALIDATIONS;
        }

        Order ORDER = keyTransform(ORDER_ID);
        if (ORDER == null) {
            VALIDATIONS.add(StatusCode.ORDER_NOT_FOUND);
            return VALIDATIONS;
        }

        try {
            ORDER.setDataValue("menuItems", new HashMap<>());
            DATASTORE.updateOrder(ORDER);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.ORDER_UPDATE_FAILED);
        }
        return VALIDATIONS;
    }
}