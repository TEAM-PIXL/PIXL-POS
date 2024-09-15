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
        return dataStore.getUsers().stream()
                .anyMatch(user -> user.getMetadata().metadata().get("username").toString().equals(username))
                ? null : StatusCode.INVALID_USERNAME;
    }

    public StatusCode validateUsersByEmailAddress(String email) {
        return dataStore.getUsers().stream()
                .anyMatch(user -> user.getData().get("email").toString().equals(email))
                ? null : StatusCode.INVALID_EMAIL;
    }

    public StatusCode validateUsersByFirstName(String firstName) {
        return dataStore.getUsers().stream()
                .anyMatch(user -> user.getMetadata().metadata().get("first_name").toString().equals(firstName))
                ? null : StatusCode.INVALID_FIRST_NAME;
    }

    public StatusCode validateUsersByLastName(String lastName) {
        return dataStore.getUsers().stream()
                .anyMatch(user -> user.getMetadata().metadata().get("last_name").toString().equals(lastName))
                ? null : StatusCode.INVALID_LAST_NAME;
    }

    public StatusCode validateUsersPassword(String username, String password) {
        return dataStore.getUsers().stream()
                .anyMatch(user -> user.getMetadata().metadata().get("username").toString().equals(username) &&
                        AuthenticationManager.login( username, password))
                ? null : StatusCode.INVALID_PASSWORD;
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
