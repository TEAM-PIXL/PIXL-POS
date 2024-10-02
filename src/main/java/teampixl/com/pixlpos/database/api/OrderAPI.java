package teampixl.com.pixlpos.database.api;

import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.models.Users;

import java.util.stream.Collectors;
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
    private static final DataStore dataStore = DataStore.getInstance();
    private static final UsersAPI usersAPI = UsersAPI.getInstance();
    private static final MenuAPI menuAPI = MenuAPI.getInstance();

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
     * @param orderNumber the order number to validate
     * @return the appropriate StatusCode
     */
    public StatusCode validateOrderByNumber(Integer orderNumber) {
        if (orderNumber == null || orderNumber <= 0) {
            return StatusCode.INVALID_ORDER_NUMBER;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the user ID associated with the order.
     *
     * @param userId the user ID to validate
     * @return the appropriate StatusCode
     */
    public StatusCode validateOrderByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return StatusCode.INVALID_USER_ID;
        }
        Users user = usersAPI.keyTransform(userId);
        return user != null ? StatusCode.SUCCESS : StatusCode.USER_NOT_FOUND;
    }

    /**
     * Validates the order ID.
     *
     * @param orderId the order ID to validate
     * @return the appropriate StatusCode
     */
    public StatusCode validateOrderById(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            return StatusCode.INVALID_ORDER_ID;
        }
        Order order = keyTransform(orderId);
        return order != null ? StatusCode.SUCCESS : StatusCode.ORDER_NOT_FOUND;
    }

    /**
     * Validates the order status.
     *
     * @param orderStatus the order status to validate
     * @return the appropriate StatusCode
     */
    public StatusCode validateOrderByStatus(Order.OrderStatus orderStatus) {
        if (orderStatus == null) {
            return StatusCode.INVALID_ORDER_STATUS;
        }
        return StatusCode.SUCCESS;
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
        String orderStatus = (String) order.getMetadata().metadata().get("order_status");

        statusCodes.add(validateOrderById(orderId));
        statusCodes.add(validateOrderByNumber(orderNumber));
        statusCodes.add(validateOrderByUserId(userId));

        try {
            Order.OrderStatus.valueOf(orderStatus);
        } catch (IllegalArgumentException e) {
            statusCodes.add(StatusCode.INVALID_ORDER_STATUS);
        }

        Object totalObj = order.getData().get("total");
        if (!(totalObj instanceof Double) || (Double) totalObj < 0) {
            statusCodes.add(StatusCode.INVALID_ORDER_TOTAL);
        }

        Object menuItemsObj = order.getData().get("menuItems");
        if (!(menuItemsObj instanceof Map<?, ?> menuItemsMap) || menuItemsMap.isEmpty()) {
            statusCodes.add(StatusCode.INVALID_ORDER_ITEMS);
        }

        return statusCodes.stream()
                .filter(statusCode -> statusCode != StatusCode.SUCCESS)
                .collect(Collectors.toList());
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
        return dataStore.readOrders().stream()
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
        return dataStore.readOrders().stream()
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
        return dataStore.readOrders().stream()
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
        return dataStore.readOrders();
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

        dataStore.loadOrderItems(orderId, order);

        @SuppressWarnings("unchecked")
        Map<String, Integer> menuItemsMap = (Map<String, Integer>) order.getData().get("menuItems");
        Map<MenuItem, Integer> menuItemsWithQuantity = new HashMap<>();

        for (Map.Entry<String, Integer> entry : menuItemsMap.entrySet()) {
            MenuItem menuItem = menuAPI.keyTransform(entry.getKey());
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

        List<Order> orders = dataStore.readOrders();
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
                dataStore.createOrder(order);
                System.out.println("New order created for user: " + userId + " with order number: " + orderNumber);
                return order;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }



    /**
     * Creates a new order and adds it to the database.
     *
     * @param orderNumber the order number
     * @param userId the user ID
     * @return a list of StatusCodes indicating the result of the operation
     */
    public List<StatusCode> postOrder(Integer orderNumber, String userId) {
        List<StatusCode> validations = new ArrayList<>();

        StatusCode numberValidation = validateOrderByNumber(orderNumber);
        StatusCode userValidation = validateOrderByUserId(userId);
        validations.add(numberValidation);
        validations.add(userValidation);

        if (!Exceptions.isSuccessful(validations)) {
            return validations;
        }

        if (getOrderByNumber(orderNumber) != null) {
            validations.add(StatusCode.ORDER_ALREADY_EXISTS);
            return validations;
        }

        try {
            Order order = new Order(orderNumber, userId);
            dataStore.createOrder(order);
            validations.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            validations.add(StatusCode.ORDER_CREATION_FAILED);
        }
        return validations;
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

        MenuItem menuItem = menuAPI.keyTransform(menuItemId);
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
            dataStore.updateOrder(order);
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
            dataStore.updateOrder(order);
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
        MenuItem menuItem = menuAPI.keyTransform(menuItemId);
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
            dataStore.updateOrder(order);
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
            dataStore.deleteOrder(order);
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
            dataStore.updateOrder(order);
            validations.add(StatusCode.SUCCESS);
        } catch (Exception e) {
            validations.add(StatusCode.ORDER_UPDATE_FAILED);
        }
        return validations;
    }
}