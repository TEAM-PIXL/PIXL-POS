package teampixl.com.pixlpos.database.api.userapi;

import teampixl.com.pixlpos.constructs.Users;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.database.api.StatusCode;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class is responsible for managing the users in the database. It is responsible for getting, posting, putting, and deleting users.
 */
public class UsersAPI {
    private static DataStore dataStore = DataStore.getInstance();

    /**
     * Constructor for the UsersAPI class.
     *
     * @param dataStore the data store to use
     */
    public UsersAPI(DataStore dataStore) {
        UsersAPI.dataStore = dataStore;
    }

    /**
     * Validates the username of a user.
     *
     * @param username the username to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateUsersByUsername(String username) {
        if (username == null) {
            return StatusCode.INVALID_USERNAME;
        }
        boolean userExists = dataStore.getUsers().stream()
                .anyMatch(user -> user.getMetadata().metadata().get("username").toString().equals(username));
        return userExists ? StatusCode.USER_ALREADY_EXISTS : StatusCode.SUCCESS;
    }

    /**
     * Validates the email address of a user.
     *
     * @param email the email address to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateUsersByEmailAddress(String email) {
        if (email == null || email.chars().filter(ch -> ch == '@').count() != 1) {
            return StatusCode.INVALID_EMAIL;
        }
        boolean userExists = dataStore.getUsers().stream()
                .anyMatch(user -> user.getData().get("email").toString().equals(email));
        return userExists ? StatusCode.USER_ALREADY_EXISTS : StatusCode.SUCCESS;
    }

    /**
     * Validates the first name of a user.
     *
     * @param firstName the first name to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateUsersByFirstName(String firstName) {
        if (firstName == null) {
            return StatusCode.INVALID_FIRST_NAME;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the last name of a user.
     *
     * @param lastName the last name to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateUsersByLastName(String lastName) {
            if (lastName == null) {
                return StatusCode.INVALID_LAST_NAME;
            }
            return StatusCode.SUCCESS;
    }

    /**
     * Validates the name of a user.
     *
     * @param firstName the first name to validate
     * @param lastName the last name to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateUsersByName(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            return StatusCode.INVALID_NAME;
        }
        boolean userExists = dataStore.getUsers().stream()
                .anyMatch(user -> user.getMetadata().metadata().get("first_name").toString().equals(firstName) &&
                        user.getMetadata().metadata().get("last_name").toString().equals(lastName));
        return userExists ? StatusCode.USER_ALREADY_EXISTS : StatusCode.SUCCESS;
    }

    /**
     * Validates the password of a user.
     *
     * @param username the username of the user
     * @param password the password to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateUsersPassword(String username, String password) {
        if (username == null || password == null) {
            return StatusCode.INVALID_PASSWORD;
        }
        return dataStore.getUsers().stream()
                .anyMatch(user -> user.getMetadata().metadata().get("username").toString().equals(username) &&
                        AuthenticationManager.login(username, password))
                ? StatusCode.SUCCESS : StatusCode.INVALID_PASSWORD;
    }

    /**
     * Validates the role of a user.
     *
     * @param role the role to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateUsersRole(String role) {
        if (Users.UserRole.valueOf(role) == null) {
            return StatusCode.INVALID_USER_ROLE;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the status of a user.
     *
     * @param status the status to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateUsersStatus(String status) {
        if (status == null || (!status.equals("true") && !status.equals("false"))) {
            return StatusCode.INVALID_USER_STATUS;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the additional information of a user.
     *
     * @param additionalInfo the additional information to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateUsersAdditionalInfo(String additionalInfo) {
        if (additionalInfo == null) {
            return StatusCode.INVALID_USER_ADDITIONAL_INFO;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Posts a new user to the database.
     *
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @param username the username of the user
     * @param password the password of the user
     * @param email the email of the user
     * @param role the role of the user
     * @param additionalInfo the additional information of the user
     * @return the status code indicating the result of the operation
     */
    public StatusCode postUsers(String firstName, String lastName, String username, String password, String email, String role, String additionalInfo) {
        try {
            List<StatusCode> validations = List.of(
                    validateUsersByUsername(username),
                    validateUsersByEmailAddress(email),
                    validateUsersByFirstName(firstName),
                    validateUsersByLastName(lastName),
                    validateUsersRole(role),
                    validateUsersByName(firstName, lastName),
                    validateUsersPassword(username, password)
            );

            for (StatusCode code : validations) {
                if (code != StatusCode.SUCCESS) {
                    return code;
                }
            }

            Users.UserRole userRole = Users.UserRole.valueOf(role);
            Users user = new Users(firstName, lastName, username, password, email, userRole, additionalInfo);
            dataStore.addUser(user);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.FAILURE;
        }
    }

    /**
     * Posts a new user to the database.
     *
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @param username the username of the user
     * @param password the password of the user
     * @param email the email of the user
     * @param role the role of the user
     * @return the status code indicating the result of the operation
     */
    public StatusCode postUsers(String firstName, String lastName, String username, String password, String email, String role) {
        return postUsers(firstName, lastName, username, password, email, role, "");
    }

    private String getUsersByUsername(String username) {
        return dataStore.getUsers().stream()
                .filter(user -> user.getMetadata().metadata().get("username").toString().equals(username))
                .findFirst()
                .map(user -> user.getMetadata().metadata().get("id").toString())
                .orElse(null);
    }

    private String getUsersByEmailAddress(String email) {
        return dataStore.getUsers().stream()
                .filter(user -> user.getData().get("email").toString().equals(email))
                .findFirst()
                .map(user -> user.getMetadata().metadata().get("id").toString())
                .orElse(null);
    }

    /**
     * Gets a user from the database.
     *
     * @param username the query to search for the user
     * @param newUsername the new username to update
     * @return status code indicating the result of the operation
     */
    public StatusCode putUsersUsername(String username, String newUsername) {
        try {
            String id = getUsersByUsername(username);
            if (id == null) {
                return StatusCode.USER_NOT_FOUND;
            }
            Users user = dataStore.getUsers().stream()
                    .filter(u -> u.getMetadata().metadata().get("id").toString().equals(id))
                    .findFirst()
                    .orElse(null);
            if (user == null) {
                return StatusCode.USER_NOT_FOUND;
            }
            user.getMetadata().metadata().put("username", newUsername);
            dataStore.updateUser(user);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.USER_UPDATE_FAILED;
        }
    }

    /**
     * Gets a user from the database.
     *
     * @param username the query to search for the user
     * @param newEmail the new email to update
     * @return status code indicating the result of the operation
     */
    public StatusCode putUsersEmailAddress(String username, String newEmail) {
        try {
            String id = getUsersByUsername(username);
            if (id == null) {
                return StatusCode.USER_NOT_FOUND;
            }
            Users user = dataStore.getUsers().stream()
                    .filter(u -> u.getMetadata().metadata().get("id").toString().equals(id))
                    .findFirst()
                    .orElse(null);
            if (user == null) {
                return StatusCode.USER_NOT_FOUND;
            }
            user.getData().put("email", newEmail);
            dataStore.updateUser(user);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.USER_UPDATE_FAILED;
        }
    }

    /**
     * Gets a user from the database.
     *
     * @param username the query to search for the user
     * @param newFirstName the new first name to update
     * @return status code indicating the result of the operation
     */
    public StatusCode putUsersFirstName(String username, String newFirstName) {
        try {
            String id = getUsersByUsername(username);
            if (id == null) {
                return StatusCode.USER_NOT_FOUND;
            }
            Users user = dataStore.getUsers().stream()
                    .filter(u -> u.getMetadata().metadata().get("id").toString().equals(id))
                    .findFirst()
                    .orElse(null);
            if (user == null) {
                return StatusCode.USER_NOT_FOUND;
            }
            user.getMetadata().metadata().put("first_name", newFirstName);
            dataStore.updateUser(user);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.USER_UPDATE_FAILED;
        }
    }

    /**
     * Gets a user from the database.
     *
     * @param username the query to search for the user
     * @param newLastName the new last name to update
     * @return status code indicating the result of the operation
     */
    public StatusCode putUsersLastName(String username, String newLastName) {
        try {
            String id = getUsersByUsername(username);
            if (id == null) {
                return StatusCode.USER_NOT_FOUND;
            }
            Users user = dataStore.getUsers().stream()
                    .filter(u -> u.getMetadata().metadata().get("id").toString().equals(id))
                    .findFirst()
                    .orElse(null);
            if (user == null) {
                return StatusCode.USER_NOT_FOUND;
            }
            user.getMetadata().metadata().put("last_name", newLastName);
            dataStore.updateUser(user);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.USER_UPDATE_FAILED;
        }
    }

    /**
     * Gets a user from the database.
     *
     * @param username the query to search for the user
     * @param newPassword the new password to update
     * @return status code indicating the result of the operation
     */
    public StatusCode putUsersPassword(String username, String newPassword) {
        try {
            String id = getUsersByUsername(username);
            if (id == null) {
                return StatusCode.USER_NOT_FOUND;
            }
            Users user = dataStore.getUsers().stream()
                    .filter(u -> u.getMetadata().metadata().get("id").toString().equals(id))
                    .findFirst()
                    .orElse(null);
            if (user == null) {
                return StatusCode.USER_NOT_FOUND;
            }
            dataStore.updateUserPassword(user, newPassword);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.USER_UPDATE_FAILED;
        }
    }

    /**
     * Gets a user from the database.
     *
     * @param username the query to search for the user
     * @param newRole the new role to update
     * @return status code indicating the result of the operation
     */
    public StatusCode putUsersRole(String username, String newRole) {
        try {
            String id = getUsersByUsername(username);
            if (id == null) {
                return StatusCode.USER_NOT_FOUND;
            }
            Users user = dataStore.getUsers().stream()
                    .filter(u -> u.getMetadata().metadata().get("id").toString().equals(id))
                    .findFirst()
                    .orElse(null);
            if (user == null) {
                return StatusCode.USER_NOT_FOUND;
            }
            user.getMetadata().metadata().put("role", Users.UserRole.valueOf(newRole));
            dataStore.updateUser(user);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.USER_UPDATE_FAILED;
        }
    }

    /**
     * Gets a user from the database.
     *
     * @param username the query to search for the user
     * @param newStatus the new status to update
     * @return status code indicating the result of the operation
     */
    public StatusCode putUsersStatus(String username, String newStatus) {
        try {
            String id = getUsersByUsername(username);
            if (id == null) {
                return StatusCode.USER_NOT_FOUND;
            }
            Users user = dataStore.getUsers().stream()
                    .filter(u -> u.getMetadata().metadata().get("id").toString().equals(id))
                    .findFirst()
                    .orElse(null);
            if (user == null) {
                return StatusCode.USER_NOT_FOUND;
            }
            user.getMetadata().metadata().put("is_active", Boolean.parseBoolean(newStatus));
            dataStore.updateUser(user);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.USER_UPDATE_FAILED;
        }
    }

    /**
     * Gets a user from the database.
     *
     * @param username the query to search for the user
     * @param newAdditionalInfo the new additional information to update
     * @return status code indicating the result of the operation
     */
    public StatusCode putUsersAdditionalInfo(String username, String newAdditionalInfo) {
        try {
            String id = getUsersByUsername(username);
            if (id == null) {
                return StatusCode.USER_NOT_FOUND;
            }
            Users user = dataStore.getUsers().stream()
                    .filter(u -> u.getMetadata().metadata().get("id").toString().equals(id))
                    .findFirst()
                    .orElse(null);
            if (user == null) {
                return StatusCode.USER_NOT_FOUND;
            }
            user.getData().put("additional_info", newAdditionalInfo);
            dataStore.updateUser(user);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.USER_UPDATE_FAILED;
        }
    }

    /**
     * Gets a user from the database.
     *
     * @param query the query to search for the user
     * @return status code indicating the result of the operation
     */
    public StatusCode deleteUser(String query) {
        try {
            List<Users> users = searchUsers(query);
            if (users.isEmpty()) {
                return StatusCode.USER_NOT_FOUND;
            }
            else if (users.size() > 1) {
                return StatusCode.MULTIPLE_USERS_FOUND;
            }
            else {
                dataStore.removeUser(users.getFirst());
                return StatusCode.SUCCESS;
            }
        } catch (Exception e) {
            return StatusCode.FAILURE;
        }
    }

    /**
     * Gets a user from the database.
     *
     * @param query the query to search for the user
     * @return list of users matching the query
     */
    public static List<Users> searchUsers(String query) {
        String[] parts = query.trim().split("\\s+");

        if (parts.length > 2) {
            return List.of();
        }

        return dataStore.getUsers().parallelStream()
                .filter(user -> {
                    if (parts.length == 2) {
                        String firstName = parts[0].toLowerCase();
                        String lastName = parts[1].toLowerCase();
                        return user.getMetadata().metadata().get("first_name").toString().toLowerCase().contains(firstName) &&
                                user.getMetadata().metadata().get("last_name").toString().toLowerCase().contains(lastName);
                    } else {
                        String singleQuery = parts[0].toLowerCase();
                        return user.getMetadata().metadata().values().stream()
                                .filter(Objects::nonNull)
                                .anyMatch(value -> value.toString().toLowerCase().contains(singleQuery)) ||
                                user.getData().values().stream()
                                        .filter(Objects::nonNull)
                                        .anyMatch(value -> value.toString().toLowerCase().contains(singleQuery));
                    }
                })
                .collect(Collectors.toList());
    }

}
