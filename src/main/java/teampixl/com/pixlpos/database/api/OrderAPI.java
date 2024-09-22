package teampixl.com.pixlpos.database.api;

//import edu.stanford.nlp.ling.CoreAnnotations;
//import edu.stanford.nlp.ling.CoreLabel;
//import edu.stanford.nlp.util.CoreMap;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.models.MenuItem;
import teampixl.com.pixlpos.models.Order;
import teampixl.com.pixlpos.models.Users;

//import edu.stanford.nlp.pipeline.*;
//import edu.stanford.nlp.time.TimeAnnotations;
//import edu.stanford.nlp.util.PropertiesUtils;
//import teampixl.com.pixlpos.database.DataStore;
//import teampixl.com.pixlpos.models.Order;
//import teampixl.com.pixlpos.models.Users;
//
//import java.time.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.*;


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

//    private StanfordCoreNLP pipeline;

    private OrderAPI() {
//        Properties props = PropertiesUtils.asProperties(
//                "annotators", "tokenize,ssplit,pos,lemma,ner",
//                "ner.applyNumericClassifiers", "true",
//                "ner.useSUTime", "true"
//        );
//        pipeline = new StanfordCoreNLP(props);
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
     * Initializes a new order for a user. If the user already has a pending order, it returns that order.
     *
     * @param userId the user ID
     * @return the initialized Order object
     */
    public Order initializeOrder(String userId) {
        List<Order> orders = dataStore.readOrders();
        Optional<Order> pendingOrder = orders.stream()
                .filter(order -> userId.equals(order.getMetadata().metadata().get("user_id")))
                .filter(order -> Order.OrderStatus.PENDING.equals(order.getMetadata().metadata().get("order_status")))
                .findFirst();

        if (pendingOrder.isPresent()) {
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
                return order;
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Creates a new order and adds it to the database.
     *
     * @param orderNumber the order number
     * @param userId      the user ID
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
     * @param orderId    the order ID
     * @param menuItemId the menu item ID
     * @param quantity   the quantity to add
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
     * @param orderId     the order ID
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
     * @param orderId    the order ID
     * @param menuItemId the menu item ID
     * @param quantity   the quantity to remove
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

//    private enum Intent {
//        LIST_ORDERS,
//        CALCULATE_REVENUE,
//        COMPARE_REVENUE,
//        UNKNOWN
//    }

//    /**
//     * Searches for orders based on the query using NLP to parse natural language.
//     *
//     * @param query the search query
//     * @return the QueryResult containing the search results
//     */
//    public QueryResult searchOrders(String query) {
//        if (query == null || query.trim().isEmpty()) {
//            return new QueryResult();
//        }
//
//        SearchCriteria criteria = parseQueryWithNLP(query);
//
//        List<Order> orders = dataStore.readOrders();
//
//        List<Order> matchingOrders = orders.stream()
//                .filter(order -> matchesCriteria(order, criteria))
//                .collect(Collectors.toList());
//
//        QueryResult result = new QueryResult();
//
//        switch (criteria.getIntent()) {
//            case LIST_ORDERS:
//                result.setOrders(matchingOrders);
//                break;
//            case CALCULATE_REVENUE:
//                double totalRevenue = matchingOrders.stream()
//                        .mapToDouble(Order::getTotal)
//                        .sum();
//                result.setTotalRevenue(totalRevenue);
//                break;
//            case COMPARE_REVENUE:
//                if (criteria.getDateRanges().size() >= 2) {
//                    DateRange range1 = criteria.getDateRanges().get(0);
//                    DateRange range2 = criteria.getDateRanges().get(1);
//
//                    double revenue1 = calculateRevenueForDateRange(range1);
//                    double revenue2 = calculateRevenueForDateRange(range2);
//                    double revenueDifference = revenue1 - revenue2;
//
//                    result.setRevenueComparison(new RevenueComparison(range1, revenue1, range2, revenue2, revenueDifference));
//                }
//                break;
//            default:
//                result.setOrders(matchingOrders);
//                break;
//        }
//
//        return result;
//    }
//
//    private SearchCriteria parseQueryWithNLP(String query) {
//        SearchCriteria criteria = new SearchCriteria();
//        criteria.setIntent(Intent.UNKNOWN);
//
//        String lowerQuery = query.toLowerCase();
//
//        if (lowerQuery.contains("revenue") || lowerQuery.contains("order total") || lowerQuery.contains("total revenue")) {
//            criteria.setIntent(Intent.CALCULATE_REVENUE);
//        }
//        if (lowerQuery.contains("compare") || lowerQuery.contains("vs") || lowerQuery.contains("versus")) {
//            criteria.setIntent(Intent.COMPARE_REVENUE);
//        }
//        if (criteria.getIntent() == Intent.UNKNOWN) {
//            criteria.setIntent(Intent.LIST_ORDERS);
//        }
//
//        Annotation annotation = new Annotation(query);
//        pipeline.annotate(annotation);
//
//        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
//
//        for (CoreMap sentence : sentences) {
//            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
//                String word = token.get(CoreAnnotations.TextAnnotation.class);
//                String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
//
//                switch (ner) {
//                    case "DATE":
//                        extractDate(token, criteria);
//                        break;
//                    case "NUMBER":
//                        extractNumber(word, criteria);
//                        break;
//                    case "ORDINAL":
//                        extractOrdinal(word, criteria);
//                        break;
//                    case "DURATION":
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            List<CoreMap> timexAnnsAll = sentence.get(TimeAnnotations.TimexAnnotations.class);
//            for (CoreMap timexAnn : timexAnnsAll) {
//                String timeValue = timexAnn.get(TimeAnnotations.TimexAnnotation.class).value();
//                parseTimeExpression(timeValue, criteria);
//            }
//        }
//        parseDateRangeExpressions(query, criteria);
//        parseOrderStatus(query, criteria);
//
//        return criteria;
//    }
//
//    private void extractDate(CoreLabel token, SearchCriteria criteria) {
//        String dateStr = token.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
//        parseTimeExpression(dateStr, criteria);
//    }
//
//    private void extractNumber(String word, SearchCriteria criteria) {
//        try {
//            int number = Integer.parseInt(word);
//            criteria.setOrderNumber(number);
//        } catch (NumberFormatException ignored) {
//        }
//    }
//
//    private void extractOrdinal(String word, SearchCriteria criteria) {
//        word = word.replaceAll("(?<=\\d)(st|nd|rd|th)", "");
//        extractNumber(word, criteria);
//    }
//
//    private void parseTimeExpression(String timeValue, SearchCriteria criteria) {
//        if (timeValue == null) return;
//
//        try {
//            if (timeValue.contains("/")) {
//                String[] dates = timeValue.split("/");
//                LocalDate startDate = LocalDate.parse(dates[0]);
//                LocalDate endDate = LocalDate.parse(dates[1]);
//                criteria.addDateRange(new DateRange(startDate, endDate));
//            } else {
//                LocalDate date;
//                if (timeValue.contains("T")) {
//                    date = LocalDateTime.parse(timeValue).toLocalDate();
//                } else {
//                    date = LocalDate.parse(timeValue);
//                }
//                criteria.addDateRange(new DateRange(date, date));
//            }
//        } catch (Exception e) {
//            // Ignore parsing errors
//        }
//    }
//
//    private void parseDateRangeExpressions(String query, SearchCriteria criteria) {
//        // Pattern to match date ranges like "from 17th to 21st"
//        Pattern dateRangePattern = Pattern.compile("\\bfrom\\s+(.*?)\\s+to\\s+(.*?)\\b", Pattern.CASE_INSENSITIVE);
//        Matcher matcher = dateRangePattern.matcher(query);
//        if (matcher.find()) {
//            String startStr = matcher.group(1);
//            String endStr = matcher.group(2);
//            LocalDate startDate = parseNaturalLanguageDate(startStr);
//            LocalDate endDate = parseNaturalLanguageDate(endStr);
//            if (startDate != null && endDate != null) {
//                criteria.addDateRange(new DateRange(startDate, endDate));
//            }
//        }
//    }
//
//    private LocalDate parseNaturalLanguageDate(String dateStr) {
//        Annotation annotation = new Annotation(dateStr);
//        pipeline.annotate(annotation);
//        List<CoreMap> timexAnnsAll = annotation.get(TimeAnnotations.TimexAnnotations.class);
//        for (CoreMap timexAnn : timexAnnsAll) {
//            String timeValue = timexAnn.get(TimeAnnotations.TimexAnnotation.class).value();
//            try {
//                LocalDate date;
//                if (timeValue.contains("T")) {
//                    date = LocalDateTime.parse(timeValue).toLocalDate();
//                } else {
//                    date = LocalDate.parse(timeValue);
//                }
//                return date;
//            } catch (Exception ignored) {
//            }
//        }
//        return null;
//    }
//
//    private void parseOrderStatus(String query, SearchCriteria criteria) {
//        for (Order.OrderStatus status : Order.OrderStatus.values()) {
//            if (query.toLowerCase().contains(status.toString().toLowerCase())) {
//                criteria.setOrderStatus(status);
//                break;
//            }
//        }
//    }
//
//    private boolean matchesCriteria(Order order, SearchCriteria criteria) {
//        if (criteria.getOrderNumber() != null) {
//            if (order.getOrderNumber() != criteria.getOrderNumber()) {
//                return false;
//            }
//        }
//
//        if (criteria.getOrderStatus() != null) {
//            if (!order.getMetadata().metadata().get("order_status").equals(criteria.getOrderStatus())) {
//                return false;
//            }
//        }
//
//        // Check date ranges
//        if (!criteria.getDateRanges().isEmpty()) {
//            boolean matchesDateRange = criteria.getDateRanges().stream().anyMatch(range -> {
//                LocalDate orderDate = Instant.ofEpochMilli((int) order.getMetadata().metadata().get("created_at"))
//                        .atZone(ZoneId.systemDefault())
//                        .toLocalDate();
//                return !orderDate.isBefore(range.getStartDate()) && !orderDate.isAfter(range.getEndDate());
//            });
//            if (!matchesDateRange) {
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    private double calculateRevenueForDateRange(DateRange range) {
//        List<Order> orders = dataStore.readOrders();
//
//        return orders.stream()
//                .filter(order -> {
//                    LocalDate orderDate = Instant.ofEpochMilli((int) order.getMetadata().metadata().get("created_at"))
//                            .atZone(ZoneId.systemDefault())
//                            .toLocalDate();
//                    return !orderDate.isBefore(range.getStartDate()) && !orderDate.isAfter(range.getEndDate());
//                })
//                .mapToDouble(Order::getTotal)
//                .sum();
//    }
//
//    private static class SearchCriteria {
//        private Integer orderNumber;
//        private Order.OrderStatus orderStatus;
//        private Intent intent;
//        private List<DateRange> dateRanges = new ArrayList<>();
//
//        public Integer getOrderNumber() { return orderNumber; }
//        public void setOrderNumber(Integer orderNumber) { this.orderNumber = orderNumber; }
//        public Order.OrderStatus getOrderStatus() { return orderStatus; }
//        public void setOrderStatus(Order.OrderStatus orderStatus) { this.orderStatus = orderStatus; }
//        public Intent getIntent() { return intent; }
//        public void setIntent(Intent intent) { this.intent = intent; }
//        public List<DateRange> getDateRanges() { return dateRanges; }
//        public void addDateRange(DateRange range) { this.dateRanges.add(range); }
//    }
//
//    public static class DateRange {
//        private LocalDate startDate;
//        private LocalDate endDate;
//
//        public DateRange(LocalDate startDate, LocalDate endDate) {
//            if (startDate.isAfter(endDate)) {
//                this.startDate = endDate;
//                this.endDate = startDate;
//            } else {
//                this.startDate = startDate;
//                this.endDate = endDate;
//            }
//        }
//
//        public LocalDate getStartDate() { return startDate; }
//        public LocalDate getEndDate() { return endDate; }
//    }
//
//
//    public static class QueryResult {
//        private List<Order> orders;
//        private Double totalRevenue;
//        private RevenueComparison revenueComparison;
//
//        public List<Order> getOrders() { return orders; }
//        public void setOrders(List<Order> orders) { this.orders = orders; }
//        public Double getTotalRevenue() { return totalRevenue; }
//        public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
//        public RevenueComparison getRevenueComparison() { return revenueComparison; }
//        public void setRevenueComparison(RevenueComparison revenueComparison) { this.revenueComparison = revenueComparison; }
//    }
//
//    public static class RevenueComparison {
//        private DateRange range1;
//        private double revenue1;
//        private DateRange range2;
//        private double revenue2;
//        private double difference;
//
//        public RevenueComparison(DateRange range1, double revenue1, DateRange range2, double revenue2, double difference) {
//            this.range1 = range1;
//            this.revenue1 = revenue1;
//            this.range2 = range2;
//            this.revenue2 = revenue2;
//            this.difference = difference;
//        }
//
//        public DateRange getRange1() { return range1; }
//        public double getRevenue1() { return revenue1; }
//        public DateRange getRange2() { return range2; }
//        public double getRevenue2() { return revenue2; }
//        public double getDifference() { return difference; }
//    }
}