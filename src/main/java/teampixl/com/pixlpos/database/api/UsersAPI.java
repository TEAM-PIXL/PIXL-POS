package teampixl.com.pixlpos.database.api;

import teampixl.com.pixlpos.constructs.Users;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.authentication.AuthenticationManager;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UsersAPI {
    private static DataStore dataStore = DataStore.getInstance();

    public UsersAPI(DataStore dataStore) {
        UsersAPI.dataStore = dataStore;
    }

    public StatusCode validateUsersByUsername(String username) {
        if (username == null) {
            return StatusCode.INVALID_USERNAME;
        }
        boolean userExists = dataStore.getUsers().stream()
                .anyMatch(user -> user.getMetadata().metadata().get("username").toString().equals(username));
        return userExists ? StatusCode.USER_ALREADY_EXISTS : StatusCode.SUCCESS;
    }

    public StatusCode validateUsersByEmailAddress(String email) {
        if (email == null || email.chars().filter(ch -> ch == '@').count() != 1) {
            return StatusCode.INVALID_EMAIL;
        }
        boolean userExists = dataStore.getUsers().stream()
                .anyMatch(user -> user.getData().get("email").toString().equals(email));
        return userExists ? StatusCode.USER_ALREADY_EXISTS : StatusCode.SUCCESS;
    }

    public StatusCode validateUsersByFirstName(String firstName) {
        return firstName != null ? null : StatusCode.INVALID_FIRST_NAME;
    }

    public StatusCode validateUsersByLastName(String lastName) {
            return lastName != null ? null : StatusCode.INVALID_LAST_NAME;
    }

    public StatusCode validateUsersByName(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            return StatusCode.INVALID_NAME;
        }
        boolean userExists = dataStore.getUsers().stream()
                .anyMatch(user -> user.getMetadata().metadata().get("first_name").toString().equals(firstName) &&
                        user.getMetadata().metadata().get("last_name").toString().equals(lastName));
        return userExists ? StatusCode.USER_ALREADY_EXISTS : StatusCode.SUCCESS;
    }

    public StatusCode validateUsersPassword(String username, String password) {
        if (username == null || password == null) {
            return StatusCode.INVALID_PASSWORD;
        }
        return dataStore.getUsers().stream()
                .anyMatch(user -> user.getMetadata().metadata().get("username").toString().equals(username) &&
                        AuthenticationManager.login(username, password))
                ? StatusCode.SUCCESS : StatusCode.INVALID_PASSWORD;
    }

    public StatusCode validateUsersRole(String role) {
        return Users.UserRole.valueOf(role) != null ? null : StatusCode.INVALID_USER_ROLE;
    }

    public StatusCode validateUsersStatus(String status) {
        return Boolean.parseBoolean(status) ? null : StatusCode.INVALID_USER_STATUS;
    }

    public StatusCode validateUsersAdditionalInfo(String additionalInfo) {
        return additionalInfo != null ? null : StatusCode.INVALID_USER_ADDITIONAL_INFO;
    }

    public StatusCode postUsers(String firstName, String lastName, String username, String password, String email, String role) {
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
            Users user = new Users(firstName, lastName, username, password, email, userRole);
            dataStore.addUser(user);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.FAILURE;
        }
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

    private String getUsersByFirstName(String firstName) {
        return dataStore.getUsers().stream()
                .filter(user -> user.getMetadata().metadata().get("first_name").toString().equals(firstName))
                .findFirst()
                .map(user -> user.getMetadata().metadata().get("id").toString())
                .orElse(null);
    }

    private String getUsersByLastName(String lastName) {
        return dataStore.getUsers().stream()
                .filter(user -> user.getMetadata().metadata().get("last_name").toString().equals(lastName))
                .findFirst()
                .map(user -> user.getMetadata().metadata().get("id").toString())
                .orElse(null);
    }

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
