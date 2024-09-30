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
4. [API Response](#api-response)
5. [API Error Codes](#api-error-codes)

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

#### Stock Module

#### Menu Module

#### Orders Module

### API Documentation

### API Response

### API Error Codes