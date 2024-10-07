package teampixl.com.pixlpos.database.api;

import javafx.collections.ObservableList;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.database.api.util.Util;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.models.Users;

import java.util.*;
import java.time.LocalDate;
import java.time.ZoneId;
import javafx.util.Pair;

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
     * Validates the order ID.
     *
     * @param ORDER_ID the order ID to validate
     * @return the appropriate StatusCode
     */
    public StatusCode validateOrderById(Object ORDER_ID) {
        return Util.validateNotNullOrEmpty((String) ORDER_ID, StatusCode.INVALID_ORDER_ID, StatusCode.INVALID_ORDER_ID);
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
     * Validates the order status.
     *
     * @param ORDER_STATUS the order status to validate
     * @return the appropriate StatusCode
     */
    public StatusCode validateOrderByStatus(Object ORDER_STATUS) {
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
     * @return a list of StatusCodes indicating the validation results
     */
    public List<StatusCode> validateOrderByItems(Map<MenuItem, Integer> MENU_ITEMS) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        if (MENU_ITEMS == null || MENU_ITEMS.isEmpty()) {
            VALIDATIONS.add(StatusCode.NO_ORDER_ITEMS);
            return VALIDATIONS;
        }
        for (Map.Entry<MenuItem, Integer> entry : MENU_ITEMS.entrySet()) {
            MenuItem MENU_ITEM = entry.getKey();
            Integer QUANTITY = entry.getValue();

            if (MENU_ITEM == null) {
                VALIDATIONS.add(StatusCode.UNKNOWN_MENU_ITEM);
                continue;
            }
            if (QUANTITY == null || QUANTITY <= 0) {
                VALIDATIONS.add(StatusCode.INVALID_QUANTITY);
            }
            StatusCode MENU_ITEM_VALIDATION = MENUAPI.validateMenuItemForOrder(MENU_ITEM);
            if (MENU_ITEM_VALIDATION != StatusCode.SUCCESS) {
                VALIDATIONS.add(MENU_ITEM_VALIDATION);
            }
        }
        return VALIDATIONS;
    }

    /**
     * Validates the entire Order object.
     *
     * @param ORDER the Order object to validate
     * @return a list of StatusCodes indicating the validation results
     */
    public List<StatusCode> validateOrder(Order ORDER) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        if (ORDER == null) {
            return List.of(StatusCode.INVALID_ORDER);
        }

        String orderId = (String) ORDER.getMetadata().metadata().get("order_id");
        Integer orderNumber = (Integer) ORDER.getMetadata().metadata().get("order_number");
        String userId = (String) ORDER.getMetadata().metadata().get("user_id");
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(ORDER.getMetadata().metadata().get("order_status").toString());
        Integer tableNumber = (Integer) ORDER.getMetadata().metadata().get("table_number");
        Integer customers = (Integer) ORDER.getMetadata().metadata().get("customers");
        String specialRequests = (String) ORDER.getMetadata().metadata().get("special_requests");

        VALIDATIONS.add(validateOrderById(orderId));
        VALIDATIONS.add(validateOrderByNumber(orderNumber));
        VALIDATIONS.add(validateOrderByUserId(userId));
        VALIDATIONS.add(validateOrderByStatus(orderStatus));
        VALIDATIONS.add(validateOrderByTableNumber(tableNumber));
        VALIDATIONS.add(validateOrderByCustomers(customers));
        VALIDATIONS.add(validateOrderBySpecialRequests(specialRequests));
        VALIDATIONS.addAll(validateOrderByItems(getOrderItemsById(orderId)));
        System.out.println("Order validation results: " + VALIDATIONS);
        return VALIDATIONS;
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
     * Updates memory with the latest orders from the database. This also prevents memory leaks.
     * This method should be called when restoring state after large order loads.
     */
    public void reloadOrders() {
        DATASTORE.reloadOrdersFromDatabase();
    }

    /**
     * Updates memory with the latest orders from the database within a timeframe.
     * @param start the start time of the timeframe
     * @param end the end time of the timeframe
     */
    public void loadOrderTimeframe(long start, long end) {
        DATASTORE.loadOrdersFromDatabase(start, end);
    }

    /**
     * Updates memory with the latest orders from the database. This takes all orders. DANGEROUS! Be careful with this method.
     */
    public void loadAllOrders() {
        DATASTORE.loadAllOrdersFromDatabase();
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

        Pair<List<StatusCode>, Order> orderResult = Util.validateAndGetObject(
                this::validateOrderById,
                this::keyTransform,
                ORDER_ID,
                ORDER_ID,
                StatusCode.ORDER_NOT_FOUND
        );
        List<StatusCode> VALIDATIONS = new ArrayList<>(orderResult.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }
        Order ORDER = orderResult.getValue();

        Pair<List<StatusCode>, MenuItem> menuItemResult = Util.validateAndGetObject(
                MENUAPI::validateMenuItemById,
                MENUAPI::keyTransform,
                MENU_ITEM_ID,
                MENU_ITEM_ID,
                StatusCode.MENU_ITEM_NOT_FOUND
        );
        VALIDATIONS.addAll(menuItemResult.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }
        MenuItem MENU_ITEM = menuItemResult.getValue();

        if (QUANTITY == null || QUANTITY <= 0) {
            VALIDATIONS.add(StatusCode.INVALID_QUANTITY);
            return VALIDATIONS;
        }

        try {
            ORDER.addMenuItem(MENU_ITEM, QUANTITY);
            DATASTORE.updateOrder(ORDER);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.ORDER_UPDATE_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Updates the order with new menu items.
     *
     * @param ORDER_ID the order ID to update
     * @param MENU_ITEMS the new menu items to set for the order
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> putOrderItems(String ORDER_ID, Map<MenuItem, Integer> MENU_ITEMS) {
        Pair<List<StatusCode>, Order> orderResult = Util.validateAndGetObject(
                this::validateOrderById,
                this::keyTransform,
                ORDER_ID,
                ORDER_ID,
                StatusCode.ORDER_NOT_FOUND
        );
        List<StatusCode> VALIDATIONS = new ArrayList<>(orderResult.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }
        Order ORDER = orderResult.getValue();

        VALIDATIONS.addAll(validateOrderByItems(MENU_ITEMS));
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }

        try {
            ORDER.setDataValue("menuItems", new HashMap<>());
            for (Map.Entry<MenuItem, Integer> entry : MENU_ITEMS.entrySet()) {
                ORDER.addMenuItem(entry.getKey(), entry.getValue());
            }
            DATASTORE.updateOrder(ORDER);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.ORDER_UPDATE_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Updates the status of an existing order.
     *
     * @param ORDER_ID the order ID
     * @param NEW_ORDER_STATUS the new status to set
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> putOrderStatus(String ORDER_ID, Order.OrderStatus NEW_ORDER_STATUS) {

        Pair<List<StatusCode>, Order> orderResult = Util.validateAndGetObject(
                status -> validateOrderByStatus(NEW_ORDER_STATUS),
                this::keyTransform,
                NEW_ORDER_STATUS,
                ORDER_ID,
                StatusCode.ORDER_NOT_FOUND
        );
        List<StatusCode> VALIDATIONS = new ArrayList<>(orderResult.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }
        Order ORDER = orderResult.getValue();

        try {
            ORDER.updateOrderStatus(NEW_ORDER_STATUS);
            DATASTORE.updateOrder(ORDER);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.ORDER_UPDATE_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Updates the table number of an existing order.
     *
     * @param ORDER_ID the order ID
     * @param TABLE_NUMBER the new table number to set
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> putOrderTableNumber(String ORDER_ID, Integer TABLE_NUMBER) {
        Pair<List<StatusCode>, Order> orderResult = Util.validateAndGetObject(
                this::validateOrderById,
                this::keyTransform,
                ORDER_ID,
                ORDER_ID,
                StatusCode.ORDER_NOT_FOUND
        );
        List<StatusCode> VALIDATIONS = new ArrayList<>(orderResult.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }
        Order ORDER = orderResult.getValue();

        VALIDATIONS.add(validateOrderByTableNumber(TABLE_NUMBER));
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }

        try {
            ORDER.updateMetadata("table_number", TABLE_NUMBER);
            DATASTORE.updateOrder(ORDER);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.ORDER_UPDATE_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Updates the number of customers associated with an existing order.
     *
     * @param ORDER_ID the order ID
     * @param CUSTOMERS the new number of customers to set
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> putOrderCustomers(String ORDER_ID, Integer CUSTOMERS) {
        Pair<List<StatusCode>, Order> orderResult = Util.validateAndGetObject(
                this::validateOrderById,
                this::keyTransform,
                ORDER_ID,
                ORDER_ID,
                StatusCode.ORDER_NOT_FOUND
        );
        List<StatusCode> VALIDATIONS = new ArrayList<>(orderResult.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }
        Order ORDER = orderResult.getValue();

        VALIDATIONS.add(validateOrderByCustomers(CUSTOMERS));
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }

        try {
            ORDER.updateMetadata("customers", CUSTOMERS);
            DATASTORE.updateOrder(ORDER);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.ORDER_UPDATE_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Updates the special requests associated with an existing order.
     *
     * @param ORDER_ID the order ID
     * @param SPECIAL_REQUESTS the new special requests to set
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> putOrderSpecialRequests(String ORDER_ID, String SPECIAL_REQUESTS) {
        Pair<List<StatusCode>, Order> orderResult = Util.validateAndGetObject(
                this::validateOrderById,
                this::keyTransform,
                ORDER_ID,
                ORDER_ID,
                StatusCode.ORDER_NOT_FOUND
        );
        List<StatusCode> VALIDATIONS = new ArrayList<>(orderResult.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }
        Order ORDER = orderResult.getValue();

        VALIDATIONS.add(validateOrderBySpecialRequests(SPECIAL_REQUESTS));
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }

        try {
            ORDER.updateMetadata("special_requests", SPECIAL_REQUESTS);
            DATASTORE.updateOrder(ORDER);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.ORDER_UPDATE_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Updates the order type of existing order.
     *
     * @param ORDER_ID the order ID
     * @param ORDER_TYPE the new order type to set
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> putOrderType(String ORDER_ID, Order.OrderType ORDER_TYPE) {
        Pair<List<StatusCode>, Order> orderResult = Util.validateAndGetObject(
                status -> validateOrderByStatus(ORDER_TYPE),
                this::keyTransform,
                ORDER_TYPE,
                ORDER_ID,
                StatusCode.ORDER_NOT_FOUND
        );
        List<StatusCode> VALIDATIONS = new ArrayList<>(orderResult.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }
        Order ORDER = orderResult.getValue();

        try {
            ORDER.updateMetadata("order_type", ORDER_TYPE);
            DATASTORE.updateOrder(ORDER);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.ORDER_UPDATE_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Updates the payment method of an existing order.
     *
     * @param ORDER_ID the order ID
     * @param PAYMENT_METHOD the new payment method to set
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> putOrderPaymentMethod(String ORDER_ID, Order.PaymentMethod PAYMENT_METHOD) {
        Pair<List<StatusCode>, Order> orderResult = Util.validateAndGetObject(
                status -> validateOrderByStatus(PAYMENT_METHOD),
                this::keyTransform,
                PAYMENT_METHOD,
                ORDER_ID,
                StatusCode.ORDER_NOT_FOUND
        );
        List<StatusCode> VALIDATIONS = new ArrayList<>(orderResult.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }
        Order ORDER = orderResult.getValue();

        try {
            ORDER.updateMetadata("payment_method", PAYMENT_METHOD);
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

        Pair<List<StatusCode>, Order> orderResult = Util.validateAndGetObject(
                this::validateOrderById,
                this::keyTransform,
                ORDER_ID,
                ORDER_ID,
                StatusCode.ORDER_NOT_FOUND
        );
        List<StatusCode> VALIDATIONS = new ArrayList<>(orderResult.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }
        Order ORDER = orderResult.getValue();

        Pair<List<StatusCode>, MenuItem> menuItemResult = Util.validateAndGetObject(
                MENUAPI::validateMenuItemById,
                MENUAPI::keyTransform,
                MENU_ITEM_ID,
                MENU_ITEM_ID,
                StatusCode.MENU_ITEM_NOT_FOUND
        );
        VALIDATIONS.addAll(menuItemResult.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }
        MenuItem MENU_ITEM = menuItemResult.getValue();

        if (QUANTITY == null || QUANTITY <= 0) {
            VALIDATIONS.add(StatusCode.INVALID_QUANTITY);
            return VALIDATIONS;
        }

        try {
            ORDER.removeMenuItem(MENU_ITEM, QUANTITY);
            DATASTORE.updateOrder(ORDER);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
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

        Pair<List<StatusCode>, Order> orderResult = Util.validateAndGetObject(
                this::validateOrderById,
                this::keyTransform,
                ORDER_ID,
                ORDER_ID,
                StatusCode.ORDER_NOT_FOUND
        );
        List<StatusCode> VALIDATIONS = new ArrayList<>(orderResult.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }
        Order ORDER = orderResult.getValue();

        try {
            DATASTORE.deleteOrder(ORDER);
            VALIDATIONS.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.ORDER_DELETION_FAILED);
        }
        return VALIDATIONS;
    }

    /**
     * Clears all menu items from an existing order.
     *
     * @param ORDER_ID the order ID
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> clearOrderItems(String ORDER_ID) {

        Pair<List<StatusCode>, Order> orderResult = Util.validateAndGetObject(
                this::validateOrderById,
                this::keyTransform,
                ORDER_ID,
                ORDER_ID,
                StatusCode.ORDER_NOT_FOUND
        );
        List<StatusCode> VALIDATIONS = new ArrayList<>(orderResult.getKey());
        if (!Exceptions.isSuccessful(VALIDATIONS)) {
            return VALIDATIONS;
        }
        Order ORDER = orderResult.getValue();

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
