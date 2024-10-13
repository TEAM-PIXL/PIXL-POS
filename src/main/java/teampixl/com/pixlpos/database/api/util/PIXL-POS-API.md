## API Documentation for PIXL-POS

### Table of Contents
1. [Introduction](#introduction)
2. [API Endpoints](#api-endpoints)
    - [Users Module](#users-module)
    - [Ingredients Module](#ingredients-module)
    - [Stock Module](#stock-module)
    - [Menu Module](#menu-module)
    - [Orders Module](#orders-module)
3. [API Documentation](#api-documentation)
4. [API Response Model](#api-response-model)
5. [API Status Codes](#api-status-codes)

### Introduction
This document provides the API documentation for PIXL-POS. The API is used to interact with the PIXL-POS database and perform various operations such as creating, updating, and deleting records.
The API is CRUD-based. The aim of this document is to provide a detailed description of the API endpoints, request parameters, response format, and error codes. This aims to help developers understand how to interact with the API and build applications that use the PIXL-POS database.
The idea is to apply the concepts of getter and setter methods to the database operations. The API is designed to be simple, intuitive, and easy to use. Based on the complex data, metadata structure, and the database schema, the API is designed to be flexible and extensible.
This comes with notable additions of complexity, however. Additionally, due to the use of the Singleton pattern, the API is designed to be thread-safe and efficient. The API is designed to be scalable and performant, with the ability to handle large volumes of data and requests. 
Again though, this comes with additions to complexity. The aim of the Singleton pattern in this case is metadata caching. To ensure that the metadata is up-to-date and consistent across all instances of the API, the metadata is cached in memory and updated periodically. This ensures that the API is always in sync with the database schema and can handle changes to the schema without any downtime or disruption to the service.
In order to manage the volume of and complexity of operations required to interact with the database, the API is designed to be modular and extensible. The API is divided into multiple modules, each responsible for a specific set of operations. This allows developers to interact with the API in a more granular and focused way, reducing the complexity of the API and making it easier to use.

### API Endpoints

#### Users Module
The Users module is responsible for handling all operations related to users in the database. This includes creating, updating, deleting, and searching for users. The module is divided into two main classes: UsersAPI and UserStack. The UsersAPI class is the superclass that contains all the CRUD operations for users, while the UserStack class is the subclass that contains the stack operations for users.
The UsersAPI class is responsible for handling all the validation and CRUD operations for users, while the UserStack class is responsible for handling the stack operations for users. This is just a way to store the logged-in users in memory and manage the stack operations for users.
<p>
The UsersAPI class contains the following methods:

User Validation Methods:
```java
public StatusCode validateUsersByUsername(String USERNAME); // Validates the username of a user
public StatusCode validateUsersByEmailAddress(String EMAIL); // Validates the email address of a user
public StatusCode validateUsersByFirstName(String FIRST_NAME); // Validates the first name of a user
public StatusCode validateUsersByLastName(String LAST_NAME); // Validates the last name of a user
public StatusCode validateUsersByName(String FIRST_NAME, String LAST_NAME); // Validates the name of a user
public StatusCode validateUsersByPassword(String PASSWORD); // Validates the password of a user
public StatusCode validateUsersByRole(Users.UserRole ROLE); // Validates the role of a user
public StatusCode validateUsersByStatus(String USERNAME); // Validates the status of a user
public StatusCode validateUsersByAdditionalInfo(String ADDITIONAL_INFO); // Validates the additional information of a user
```

User CRUD Methods:
```java
// Handle users CRUD operations for the database
public List<StatusCode> postUsers(String FIRST_NAME, String LAST_NAME, String USERNAME, String PASSWORD, String EMAIL, Users.UserRole ROLE, String ADDITIONAL_INFO); // Posts a new user to the database
public List<StatusCode> postUsers(String FIRST_NAME, String LAST_NAME, String USERNAME, String PASSWORD, String EMAIL, Users.UserRole ROLE); // Posts a new user to the database

public Users getUser(String USERNAME); // Gets a user from memory
public String getUsersFirstNameByUsername(String USERNAME); // Gets a user's first name from memory
public String getUsersLastNameByUsername(String USERNAME); // Gets a user's last name from memory
public Users.UserRole getUsersRoleByUsername(String USERNAME); // Gets a user's role from memory
public String getUsersEmailByUsername(String USERNAME); // Gets a user's email from memory
public String getUsersAdditionalInfoByUsername(String USERNAME); // Gets a user's additional info from memory

public List<StatusCode> putUsersUsername(String USERNAME, String NEW_USERNAME); // Updates a user's username
public List<StatusCode> putUsersEmailAddress(String USERNAME, String NEW_EMAIL); // Updates a user's email address
public List<StatusCode> putUsersFirstName(String USERNAME, String NEW_FIRST_NAME); // Updates a user's first name
public List<StatusCode> putUsersLastName(String USERNAME, String NEW_LAST_NAME); // Updates a user's last name
public List<StatusCode> putUsersPassword(String USERNAME, String NEW_PASSWORD); // Updates a user's password
public List<StatusCode> putUsersRole(String USERNAME, Users.UserRole NEW_ROLE); // Updates a user's role
public List<StatusCode> putUsersStatus(String USERNAME, boolean NEW_STATUS); // Updates a user's status
public List<StatusCode> putUsersAdditionalInfo(String USERNAME, String NEW_ADDITIONAL_INFO); // Updates a user's additional information

public List<StatusCode> deleteUser(String USERNAME); // Deletes a user from the database

public List<Users> searchUsers(String QUERY); // Searches for users based on a query
```
The UserStack class contains the following methods:

```java
public void setCurrentUser(String USERNAME); // Sets the current user in memory
public Users getCurrentUser(); // Gets the current user from memory
public String getCurrentUserId(); // Gets the current user's ID from memory
public void clearCurrentUser(); // Clears the current user from memory
```

#### Ingredients Module
The Ingredients module is responsible for handling all operations related to ingredients in the database. This includes creating, updating, deleting, and searching for ingredients. The module is divided into the IngredientsAPI class, which contains all the CRUD operations for ingredients. 
<p>
The IngredientsAPI class contains the following methods: 

Ingredient Validation Methods:
```java
public StatusCode validateIngredientByName(String INGREDIENT_NAME); // Validates the name of an ingredient
public StatusCode validateIngredientByNotes(String NOTES); // Validates the notes of an ingredient
```

Ingredient CRUD Methods:
```java
// Handle ingredients CRUD operations for the database
public List<StatusCode> postIngredient(String INGREDIENT_NAME, String NOTES); // Posts a new ingredient to the database
public List<StatusCode> postIngredient(String INGREDIENT_NAME); // Posts a new ingredient to the database without notes

public Ingredients getIngredient(String INGREDIENT_NAME); // Gets an ingredient from memory
public List<Ingredients> getIngredients(); // Reads all ingredients from memory

public List<StatusCode> putIngredientName(String INGREDIENT_NAME, String NEW_INGREDIENT_NAME); // Updates the name of an existing ingredient
public List<StatusCode> putIngredientNotes(String INGREDIENT_NAME, String NEW_NOTES); // Updates the notes of an existing ingredient

public List<StatusCode> deleteIngredient(String INGREDIENT_NAME); // Deletes an ingredient from the database

public List<Ingredients> searchIngredients(String QUERY); // Searches for ingredients based on a query
```

Ingredient Utility Methods:
```java
public String keySearch(String INGREDIENT_NAME); // Gets an ingredient ID from memory based on its name
public String reverseKeySearch(String ID); // Gets an ingredient name from memory based on its ID
public Ingredients keyTransform(String ID); // Transforms an ingredient ID into an Ingredients object
```

#### Stock Module
The Stock module is responsible for handling all operations related to stock in the database. This includes creating, updating, deleting, and searching for stock items. The module is divided into the StockAPI class, which contains all the CRUD operations for stock items.
<p>
The StockAPI class contains the following methods:

Stock Validation Methods:
```java
public StatusCode validateStockByIngredientID(String INGREDIENT_ID); // Validates the ingredient ID associated with the stock
public StatusCode validateStockByQuantity(Object QUANTITY); // Validates the quantity of the stock
public StatusCode validateStockByUnitType(Stock.UnitType UNIT_TYPE); // Validates the unit type of the stock
public StatusCode validateStockByOnOrder(); // Validates the onOrder status of the stock
public StatusCode validateStockByStockStatus(Stock.StockStatus STOCK_STATUS); // Validates the stock status
```

Stock CRUD Methods:
```java
// Handle stock CRUD operations for the database
public List<StatusCode> postStock(String INGREDIENT_ID, Stock.StockStatus STOCK_STATUS, Stock.UnitType UNIT_TYPE, Object NUMERAL, boolean ON_ORDER); // Creates a new stock entry and adds it to the database

public List<Stock> getStock(); // Gets all stock items from the database
public Stock getStockByIngredientID(String INGREDIENT_ID); // Gets the Stock object associated with the given ingredient ID

public List<StatusCode> putStockNumeral(String INGREDIENT_ID, Object NEW_NUMERAL); // Updates the quantity of an existing stock
public List<StatusCode> putStockUnitType(String INGREDIENT_ID, Stock.UnitType NEW_UNIT_TYPE); // Updates the unit type of existing stock
public List<StatusCode> putStockOnOrder(String INGREDIENT_ID, boolean NEW_ON_ORDER); // Updates the onOrder status of an existing stock
public List<StatusCode> putStockStatus(String INGREDIENT_ID, Stock.StockStatus NEW_STOCK_STATUS); // Updates the stock status of an existing stock
public List<StatusCode> putStockPricePerUnit(String INGREDIENT_ID, double NEW_PRICE_PER_UNIT); // Updates the price per unit of an existing stock
public List<StatusCode> putStockLowStockThreshold(String INGREDIENT_ID, double NEW_LOW_STOCK_THRESHOLD); // Updates the low stock threshold of an existing stock
public List<StatusCode> putStockDesiredQuantity(String INGREDIENT_ID, double NEW_DESIRED_QUANTITY); // Updates the desired quantity of an existing stock

public List<StatusCode> deleteStock(String INGREDIENT_ID); // Deletes a stock entry from the database

public List<Stock> searchStock(String QUERY); // Searches for stock items in the database based on a query string
```

Stock Utility Methods:
```java
public String keySearch(String INGREDIENT_ID); // Retrieves the stock ID based on the ingredient ID
public String reverseKeySearch(String STOCK_ID); // Retrieves the ingredient ID based on the stock ID
public Stock keyTransform(String STOCK_ID); // Transforms a stock ID into a Stock object
```

#### Menu Module
The Menu module is responsible for handling all operations related to menu items in the database. This includes creating, updating, deleting, and searching for menu items. The module is divided into the MenuAPI class, which contains all the CRUD operations for menu items. 
<p>
The MenuAPI class contains the following methods:

Menu Validation Methods:
```java
public StatusCode validateMenuItemById(Object MENU_ITEM_ID); // Validates the menu item ID
public StatusCode validateMenuItemByName(String MENU_ITEM_NAME); // Validates the name of a menu item
public StatusCode validateMenuItemByPrice(Double MENU_ITEM_PRICE); // Validates the price of a menu item
public StatusCode validateMenuItemByItemType(MenuItem.ItemType MENU_ITEM_TYPE); // Validates the item type of a menu item
public StatusCode validateMenuItemByDescription(String MENU_ITEM_DESCRIPTION); // Validates the description of a menu item
public StatusCode validateMenuItemByNotes(String MENU_ITEM_NOTES); // Validates the notes of a menu item
public StatusCode validateMenuItemByStatus(Boolean ACTIVE_ITEM); // Validates the status of a menu item
public StatusCode validateMenuItemByDietaryRequirement(MenuItem.DietaryRequirement DIETARY_REQUIREMENT); // Validates the dietary requirement of a menu item
```

Menu CRUD Methods:
```java
// Handle menu CRUD operations for the database
public List<StatusCode> postMenuItem(String MENU_ITEM_NAME, Double MENU_ITEM_PRICE, boolean ACTIVE_ITEM, MenuItem.ItemType MENU_ITEM_TYPE, String MENU_ITEM_DESCRIPTION, String MENU_ITEM_NOTES, MenuItem.DietaryRequirement DIETARY_REQUIREMENT); // Creates a new menu item and adds it to the database
public List<StatusCode> postMenuItem(String MENU_ITEM_NAME, Double MENU_ITEM_PRICE, MenuItem.ItemType MENU_ITEM_TYPE, String MENU_ITEM_DESCRIPTION); // Overloaded method to create a new menu item with minimal parameters

public MenuItem getMenuItem(String MENU_ITEM_NAME); // Gets a menu item from memory
public List<MenuItem> getMenuItems(); // Reads all menu items from memory

public List<StatusCode> putMenuItemName(String MENU_ITEM_NAME, String NEW_MENU_ITEM_NAME); // Updates the name of an existing menu item
public List<StatusCode> putMenuItemPrice(String MENU_ITEM_NAME, Double NEW_MENU_ITEM_PRICE); // Updates the price of an existing menu item
public List<StatusCode> putMenuItemItemType(String MENU_ITEM_NAME, MenuItem.ItemType NEW_MENU_ITEM_TYPE); // Updates the item type of an existing menu item
public List<StatusCode> putMenuItemDescription(String MENU_ITEM_NAME, String NEW_MENU_ITEM_DESCRIPTION); // Updates the description of an existing menu item
public List<StatusCode> putMenuItemNotes(String MENU_ITEM_NAME, String NEW_MENU_ITEM_NOTES); // Updates the notes of an existing menu item
public List<StatusCode> putMenuItemStatus(String MENU_ITEM_NAME, boolean ACTIVE_ITEM); // Updates the status of an existing menu item

public List<StatusCode> deleteMenuItem(String MENU_ITEM_NAME); // Deletes a menu item from the database

public List<MenuItem> searchMenuItem(String QUERY); // Searches for menu items in the database based on a query string
```

Menu Utility Methods:
```java
public String keySearch(String MENU_ITEM_NAME); // Retrieves the menu item ID based on the item name
public String reverseKeySearch(String MENU_ITEM_ID); // Retrieves the menu item name based on the item ID
public MenuItem keyTransform(String MENU_ITEM_ID); // Transforms a menu item ID into a MenuItem object
```

#### Orders Module
The Order module is responsible for handling all operations related to orders in the database. This includes creating, updating, deleting, and searching for orders. The module is divided into the OrderAPI class, which contains all the CRUD operations for orders. 
<p>
The OrderAPI class contains the following methods: 

Order Validation Methods:
```java
public StatusCode validateOrderById(Object ORDER_ID); // Validates the order ID
public StatusCode validateOrderByNumber(Integer ORDER_NUMBER); // Validates the order number
public StatusCode validateOrderByUserId(String USER_ID); // Validates the user ID associated with the order
public StatusCode validateOrderByStatus(Object ORDER_STATUS); // Validates the order status
public StatusCode validateOrderByTableNumber(Integer TABLE_NUMBER); // Validates the table number associated with the order
public StatusCode validateOrderByCustomers(Integer CUSTOMERS); // Validates the number of customers associated with the order
public StatusCode validateOrderBySpecialRequests(String SPECIAL_REQUESTS); // Validates the special requests associated with the order
public List<StatusCode> validateOrderByItems(Map<MenuItem, Integer> MENU_ITEMS); // Validates the menu items associated with the order
```

Order CRUD Methods:
```java
// Handle order CRUD operations for the database
public List<StatusCode> postOrder(Order ORDER); // Creates a new order and adds it to the database

public Order getOrderByNumber(Integer ORDER_NUMBER); // Gets an order from memory based on the order number
public List<Order> getOrders(); // Reads all orders from memory

public List<StatusCode> putOrderItem(String ORDER_ID, String MENU_ITEM_ID, Integer QUANTITY); // Adds a menu item to an existing order
public List<StatusCode> putOrderItems(String ORDER_ID, Map<MenuItem, Integer> MENU_ITEMS); // Updates the order with new menu items
public List<StatusCode> putOrderStatus(String ORDER_ID, Order.OrderStatus NEW_ORDER_STATUS); // Updates the status of an existing order
public List<StatusCode> putOrderTableNumber(String ORDER_ID, Integer TABLE_NUMBER); // Updates the table number of an existing order
public List<StatusCode> putOrderCustomers(String ORDER_ID, Integer CUSTOMERS); // Updates the number of customers associated with an existing order
public List<StatusCode> putOrderSpecialRequests(String ORDER_ID, String SPECIAL_REQUESTS); // Updates the special requests associated with an existing order
public List<StatusCode> putOrderType(String ORDER_ID, Order.OrderType ORDER_TYPE); // Updates the order type of an existing order
public List<StatusCode> putOrderPaymentMethod(String ORDER_ID, Order.PaymentMethod PAYMENT_METHOD); // Updates the payment method of an existing order

public List<StatusCode> deleteOrderItem(String ORDER_ID, String MENU_ITEM_ID, Integer QUANTITY); // Removes a menu item from an existing order
public List<StatusCode> deleteOrder(String ORDER_ID); // Deletes an order from the database

public List<Order> searchOrder(String QUERY); // Searches for orders in the database based on a query string

public Order initializeOrder(); // Initializes a new order
```

Order Utility Methods:
```java
public String keySearch(Integer ORDER_NUMBER); // Retrieves the order ID based on the order number
public Integer reverseKeySearch(String ORDER_ID); // Retrieves the order number based on the order ID
public Order keyTransform(String ORDER_ID); // Transforms an order ID into an Order object
public Map<MenuItem, Integer> getOrderItemsById(String ORDER_ID); // Retrieves menu items associated with an order ID
```

### API Response Model
In the context of this project, a StatusCode is an enumeration that represents the result of an operation. It indicates whether the operation was successful or if there were any errors. Here is a general overview of the different types of operations (GET, PUT, POST, DELETE, SEARCH) and what they return:

**Status Codes**
- SUCCESS: The operation was successful.
- FAILURE: The operation failed.
- INTERNAL_FAILURE: An internal error occurred.
- Specific Status Codes: There are specific status codes for different entities like USER_NOT_FOUND, INVALID_ORDER_ID, INGREDIENT_POST_FAILED, etc.

**Operations and Their Returns**

GET Operations:
- Description: Retrieve data from the server.
- Method Structure: get\<EntityName\>By\<EntityProperty\>
- Example Methods: getOrderByNumber, getOrders, getOrderItemsById, searchIngredients
- Returns: Typically return the requested object(s) (e.g., Order, List<Order>, Map<MenuItem, Integer>, List<Ingredients>) or null if the object(s) are not found.
- Handling a Null Return: Check if the return value is null and handle it accordingly.
- Example Usage:

```java
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.database.api.util.OrderAPI;
import teampixl.com.pixlpos.database.api.models.Order;

OrderAPI orderAPI = OrderAPI.getInstance();

Order order = orderAPI.getOrderByNumber(123);
if(order !=null){
    // Process the order
}else {
    String ERROR_MESSAGE = Exceptions.handleStatusCode(StatusCode.ORDER_NOT_FOUND); // Prints the error message
}
```

POST Operations:
- Description: Create a new object on the server.
- Method Structure: post\<EntityName\>
- Example Methods: postOrder, postIngredient, postMenuItem
- Returns: Typically return a list of status codes indicating the result of the operation.
- Handling the Return: Check the status codes in the list and handle them accordingly.
- Example Usage:

```java
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.database.api.models.Order;

OrderAPI orderAPI = OrderAPI.getInstance();

Order order = orderAPI.initializeOrder();
List<StatusCode> statusCodes = orderAPI.postOrder(order);
if(Excpetions.isSuccessful(statusCodes)){
    // Process the order
}else {
    String MESSAGE = "Failed to post order with the following status codes: ";
    String ERROR_MESSAGE = Exceptions.returnStatus(MESSAGE, statusCodes); // Returns the error message
}
```

PUT Operations:
- Description: Update an existing object on the server.
- Method Structure: put\<EntityName\>\<EntityProperty\>
- Example Methods: putOrderStatus, putIngredientName, putMenuItemPrice
- Returns: Typically return a list of status codes indicating the result of the operation.
- Handling the Return: Check the status codes in the list and handle them accordingly.
- Example Usage:

```java
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.database.api.models.Order;
import teampixl.com.pixlpos.models.Order;

OrderAPI orderAPI = OrderAPI.getInstance();

String ORDER_ID = orderAPI.keySearch(123);
List<StatusCode> statusCodes = orderAPI.putOrderStatus(ORDER_ID, Order.OrderStatus.COMPLETED);
if(Excpetions.isSuccessful(statusCodes)){
    // Process the order
}else {
    String MESSAGE = "Failed to update order status with the following status codes: ";
    String ERROR_MESSAGE = Exceptions.returnStatus(MESSAGE, statusCodes); // Returns the error message
}
```

DELETE Operations:
- Description: Delete an existing object on the server.
- Method Structure: delete\<EntityName\>
- Example Methods: deleteOrder, deleteIngredient, deleteMenuItem
- Returns: Typically return a list of status codes indicating the result of the operation.
- Handling the Return: Check the status codes in the list and handle them accordingly.
- Example Usage:

```java
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.database.api.models.Order;

OrderAPI orderAPI = OrderAPI.getInstance();

String ORDER_ID = orderAPI.keySearch(123);
List<StatusCode> statusCodes = orderAPI.deleteOrder(ORDER_ID);
if(Excpetions.isSuccessful(statusCodes)){
    // Process the order
}else {
    String MESSAGE = "Failed to delete order with the following status codes: ";
    String ERROR_MESSAGE = Exceptions.returnStatus(MESSAGE, statusCodes); // Returns the error message
}
```

SEARCH Operations:
- Description: Search for objects on the server based on a query string.
- Method Structure: search\<EntityName\>
- Example Methods: searchOrders, searchIngredients, searchMenuItem
- Returns: Typically return a list of objects that match the search criteria.
- Handling the Return: Check if the list is empty and handle it accordingly.
- Example Usage:

```java
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.database.api.models.Order;

OrderAPI orderAPI = OrderAPI.getInstance();

QUERY = "123";

List<Order> orders = orderAPI.searchOrders(QUERY);
if(orders.size() > 0){
    // Process the orders
}else {
    String ERROR_MESSAGE = Exceptions.handleStatusCode(StatusCode.NO_ORDERS_FOUND); // Prints the error message
}
```

UTILITY Operations:
- Description: Utility methods to transform keys, search for keys, and reverse search for keys.
- Method Structure: keySearch, reverseKeySearch, keyTransform
- Example Methods: keySearch, reverseKeySearch, keyTransform
- Returns: Typically return the key or transformed object.
- Handling the Return: Check if the return value is null and handle it accordingly.
- Example Usage:

```java
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.database.api.models.Order;

OrderAPI orderAPI = OrderAPI.getInstance();

String ORDER_ID = orderAPI.keySearch(123);
if(ORDER_ID != null){
    // Process the order
}else {
    String ERROR_MESSAGE = Exceptions.handleStatusCode(StatusCode.ORDER_NOT_FOUND); // Prints the error message
}
```

Handling Status Codes:
To handle the status codes returned by these operations, you can use the Exceptions.isSuccessful method to check if all status codes indicate success. If not, you can use the Exceptions.returnStatus method to generate a detailed error message.

Example Usage:
```java
import teampixl.com.pixlpos.database.api.OrderAPI;
import teampixl.com.pixlpos.database.api.util.Exceptions;
import teampixl.com.pixlpos.database.api.util.StatusCode;
import teampixl.com.pixlpos.database.api.models.Order;

OrderAPI orderAPI = OrderAPI.getInstance();

List<StatusCode> statusCodes = orderAPI.postOrder(order);
if(Excpetions.isSuccessful(statusCodes)){
    // Process the order
}else {
    String MESSAGE = "Failed to post order with the following status codes: ";
    String ERROR_MESSAGE = Exceptions.returnStatus(MESSAGE, statusCodes); // Returns the error message
}
```

### API Status Codes

Status codes are used to inform the client of the outcome of an API request. These codes provide insight into whether the request was successful, encountered validation errors, or failed due to internal issues. The following categories outline the types of status codes used in the API, explaining their general purpose and meaning.

**General Status Codes**
These status codes indicate issues related to the internal processing of the request, often related to server-side errors or misconfigurations.
- SUCCESS: The request was processed successfully without errors.
- FAILURE: The request failed due to an unspecified reason and is not further categorized.
- INTERNAL_FAILURE: An internal server error or system error occurred during the processing of the request.
- INTERNAL_METHOD_NOT_FOUND: The API endpoint or method requested does not exist or is not available.

Usage: These status codes are used to provide general feedback on the outcome of the request and are often used in conjunction with more specific status codes to provide additional context.

**General Validation Status Codes**
These status codes indicate issues related to the validation of the request parameters or data, such as missing or invalid fields. They help the client identify issues with their request before processing it further. They are generally more specific than the general status codes but ambiguous compared to entity-specific status codes.
- Structure: INVALID_\<PARAMETER\>
- Example: INVALID_USERNAME, INVALID_EMAIL_ADDRESS, INVALID_PASSWORD
- Level: General
- Field: Request Parameters, Data, Validation

Usage: These status codes are used to provide feedback on the validation of the request parameters or data and help the client identify issues with their request before processing it further.

**Validation Entity-Specific Status Codes**
These status codes indicate issues related to the validation of specific entities, such as users, ingredients, stock items, menu items, or orders. They provide detailed feedback on the validation of the entity's properties and help the client identify issues with specific entities.
- Structure: \<PARAMETER\>_\[TYPE_ERROR\]
- Example: USERNAME_TOO_SHORT, EMAIL_ADDRESS_INVALID_FORMAT, PASSWORD_TOO_WEAK
- Level: Entity-Specific
- Field: Entity Properties, Validation

Usage: These status codes are used to provide detailed feedback on the validation of specific entities and help the client identify issues with specific entities.

**Application Entity-Specific Status Codes**
These status codes indicate issues related to the processing of specific entities, such as users, ingredients, stock items, menu items, or orders. They provide detailed feedback on the processing of the entity's properties and help the client identify issues with specific entities.
- Structure: \<ENTITY\>_\<OPERATION\>_\<ERROR\>
- Example: INGREDIENT_POST_FAILED, MENU_ITEM_UPDATE_FAILED
- Level: Entity-Specific
- Field: Entity Properties, Processing

Usage: These status codes are used to provide detailed feedback on the processing of specific entities and help the client identify issues with specific entities.

**Entity Not Found Status Codes**
These status codes indicate that the requested entity was not found in the database or memory. They help the client identify issues with the availability of specific entities and handle such cases appropriately.
- Structure: \<ENTITY\>_NOT_FOUND, \<ENTITY\>_\<PARAMETER\>_NOT_FOUND, NO_\<ENTITY\>
- Example: ORDER_NOT_FOUND, INGREDIENT_ID_NOT_FOUND, NO_ORDERS_ITEMS
- Level: Entity-Specific
- Field: Entity Availability, Database

Usage: These status codes are used to inform the client that the requested entity was not found in the database or memory and help the client handle such cases appropriately.

**Entity Already Exists Status Codes**
These status codes indicate that the requested entity already exists in the database or memory. They help the client identify issues with duplicate entities and handle such cases appropriately.
- Structure: \<ENTITY\>_ALREADY_EXISTS, \<ENTITY\>_\<PARAMETER\>_ALREADY_EXISTS
- Example: USER_ALREADY_EXISTS, INGREDIENT_NAME_ALREADY_EXISTS
- Level: Entity-Specific
- Field: Entity Availability, Database

Usage: These status codes are used to inform the client that the requested entity already exists in the database or memory and help the client handle such cases appropriately.

Summary: Status codes are used to inform the client of the outcome of an API request. They provide insight into whether the request was successful, encountered validation errors, or failed due to internal issues. By categorizing status codes based on their purpose and meaning, clients can better understand the feedback provided by the API and take appropriate action based on the status codes returned.

