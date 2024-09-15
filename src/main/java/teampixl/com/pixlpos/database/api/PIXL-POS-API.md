## API Documentation for PIXL-POS

### Table of Contents
1. [Introduction](#introduction)
2. [API Endpoints](#api-endpoints)
3. [API Documentation](#api-documentation)
4. [API Response](#api-response)
5. [API Error Codes](#api-error-codes)

### Introduction
This document provides the API documentation for PIXL-POS. The API is used to interact with the PIXL-POS database and perform various operations such as creating, updating, and deleting records.
The API is CRUD-based. The aim of this document is to provide a detailed description of the API endpoints, request parameters, response format, and error codes. This aims to help developers understand how to interact with the API and build applications that use the PIXL-POS database.
The idea is to apply the concepts of getter and setter methods to the database operations. The API is designed to be simple, intuitive, and easy to use. Based on the complex data, metadata structure, and the database schema, the API is designed to be flexible and extensible.
This comes with notable additions of complexity however. Additionally, due to the use of the Singleton pattern, the API is designed to be thread-safe and efficient. The API is designed to be scalable and performant, with the ability to handle large volumes of data and requests. 
Again though, this comes with additions to complexity. The aim of the Singleton pattern in this case is metadata caching. To ensure that the metadata is up-to-date and consistent across all instances of the API, the metadata is cached in memory and updated periodically. This ensures that the API is always in sync with the database schema and can handle changes to the schema without any downtime or disruption to the service.
In order to manage the volume of and complexity of operations required to interact with the database, the API is designed to be modular and extensible. The API is divided into multiple modules, each responsible for a specific set of operations. This allows developers to interact with the API in a more granular and focused way, reducing the complexity of the API and making it easier to use.

### API Endpoints

#### Users Module
```
Superclass: UsersAPI
Subclasses: UsersCRUD, UsersMetadata, UsersValidation

UsersCRUD:
// Handle getting user by various parameters and returning user ID to be used by backend methods.
public String getUsersByUsername(String username); - Returns user id of the user with the specified username.
public String getUsersByEmail(String email); - Returns user id of the user with the specified email.
public String getUsersByFirstName(String firstName); - Returns user id of the user with the specified first name.
public String getUsersByLastName(String lastName); - Returns user id of the user with the specified last name.

// Handle put operations for user data and metadata.
public void putUsersUsername(String userId, String username); - Updates the username of the user with the specified ID.
public void putUsersEmail(String userId, String email); - Updates the email address of the user with the specified ID.
public void putUsersFirstName(String userId, String firstName); - Updates the first name of the user with the specified ID.
public void putUsersLastName(String userId, String lastName); - Updates the last name of the user with the specified ID.
public void putUsersPassword(String userId, String password); - Updates the password of the user with the specified ID.
public void putUsersRole(String userId, String role); - Updates the role of the user with the specified ID.
public void putUsersStatus(String userId, String status); - Updates the status of the user with the specified ID.
public void putUsersAdditionalInfo(String userId, String additionalInfo); - Updates the additional information of the user with the specified ID.

// Handle validation operations for user data and metadata.
public boolean validateUsername(String username); - Validates the username format.
public boolean validateEmail(String email); - Validates the email address format.
public boolean validateFirstName(String firstName); - Validates the first name format.
public boolean validateLastName(String lastName); - Validates the last name format.
public boolean validatePassword(String password); - Validates the password format.
public boolean validateRole(String role); - Validates the role format.
public boolean validateStatus(String status); - Validates the status format.
public boolean validateAdditionalInfo(String additionalInfo); - Validates the additional information format.
```

public List<Users> getAllUsers(); - Returns a list of all users in the database.
public Users getUserById(String userId); - Returns the user with the specified ID from the database.
public Users getUserByUsername(String username); - Returns the user with the specified username from the database.
public Users getUserByEmail(String email); - Returns the user with the specified email address from the database.
public getUsernameById(String userId); - Returns the username of the user with the specified ID from the database.
public getEmailById(String userId); - Returns the email address of the user with the specified ID from the database.
