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
Subclasses: UsersStack

UsersCRUD:
// Handle users validation operations for the database
public StatusCode validateUsersByUsername(String username);
public StatusCode validateUsersByEmailAddress(String email);
public StatusCode validateUsersByFirstName(String firstName);
public StatusCode validateUsersByLastName(String lastName);
public StatusCode validateUsersByName(String firstName, String lastName);
public StatusCode validateUsersByPassword(String username, String password);
public StatusCode validateUsersRole(String role);
public StatusCode validateUsersStatus(String status);
public StatusCode validateUsersAdditionalInfo(String additionalInfo);

// Handle users CRUD operations for the database
public StatusCode postUsers(String username, String email, String firstName, String lastName, String password, String role, String additionalInfo);

public StatusCode getUsers(String username);
public StatusCode getUsersByEmail(String email);

public StatusCode putUsersUsername(String username, String newUsername);
public StatusCode putUsersEmail(String username, String newEmail);
public StatusCode putUsersFirstName(String username, String newFirstName);
public StatusCode putUsersLastName(String username, String newLastName);
public StatusCode putUsersPassword(String username, String newPassword);
public StatusCode putUsersRole(String username, String newRole);
public StatusCode putUsersStatus(String username, String newStatus);
public StatusCode putUsersAdditionalInfo(String username, String newAdditionalInfo);

public StatusCode deleteUsers(String query);

public StatusCode searchUsers(String query);
```
