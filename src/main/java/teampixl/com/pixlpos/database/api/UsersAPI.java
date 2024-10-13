package teampixl.com.pixlpos.database.api;

import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.authentication.PasswordUtils;
import teampixl.com.pixlpos.database.api.util.*;
import teampixl.com.pixlpos.models.Users;

import javafx.util.Pair;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * The UsersAPI class is a singleton class that manages user operations.
 * It handles validation, retrieval, creation, updating, and deletion of users.
 * Internal operations are optimized for asynchronous execution where applicable.
 */
public class UsersAPI {
    private static UsersAPI instance;
    private final DataStore dataStore;
    private final ExecutorService executorService;

    private UsersAPI() {
        dataStore = DataStore.getInstance();
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * Gets the singleton instance of the UsersAPI.
     *
     * @return the instance of UsersAPI
     */
    public static synchronized UsersAPI getInstance() {
        if (instance == null) {
            instance = new UsersAPI();
        }
        return instance;
    }

    /* ===================== Validation Methods ===================== */

    public StatusCode validateUsersByUsername(String username) {
        try {
            if (username == null) return StatusCode.USERNAME_NULL;
            if (username.length() < 4) return StatusCode.USERNAME_TOO_SHORT;
            if (username.length() > 20) return StatusCode.USERNAME_TOO_LONG;
            if (username.chars().anyMatch(Character::isSpaceChar)) return StatusCode.USERNAME_CONTAINS_SPACES;
            if (!username.matches("^[a-zA-Z0-9]*$")) return StatusCode.USERNAME_INVALID_CHARACTERS;
            if (username.chars().allMatch(Character::isDigit)) return StatusCode.USERNAME_ONLY_DIGITS;

            CompletableFuture<Boolean> userExistsFuture = CompletableFuture.supplyAsync(() ->
                    dataStore.readUsers().stream()
                            .anyMatch(user -> user.getMetadataValue("username").equals(username)), executorService);

            boolean userExists = userExistsFuture.get();
            return userExists ? StatusCode.USERNAME_TAKEN : StatusCode.SUCCESS;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return StatusCode.INTERNAL_FAILURE;
        }
    }

    public StatusCode validateUsersByEmailAddress(String email) {
        try {
            if (email == null) return StatusCode.EMAIL_NULL;
            if (email.chars().filter(ch -> ch == '@').count() != 1) return StatusCode.EMAIL_INVALID_FORMAT;
            if (email.chars().anyMatch(Character::isSpaceChar)) return StatusCode.EMAIL_CONTAINS_SPACES;

            CompletableFuture<Boolean> userExistsFuture = CompletableFuture.supplyAsync(() ->
                    dataStore.readUsers().stream()
                            .anyMatch(user -> user.getDataValue("email").equals(email)), executorService);

            boolean userExists = userExistsFuture.get();
            return userExists ? StatusCode.EMAIL_TAKEN : StatusCode.SUCCESS;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return StatusCode.INTERNAL_FAILURE;
        }
    }

    public StatusCode validateUsersByFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) return StatusCode.INVALID_FIRST_NAME;
        return StatusCode.SUCCESS;
    }

    public StatusCode validateUsersByLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) return StatusCode.INVALID_LAST_NAME;
        return StatusCode.SUCCESS;
    }

    public StatusCode validateUsersByName(String firstName, String lastName) {
        try {
            if (firstName == null || lastName == null) return StatusCode.INVALID_NAME;
            if (firstName.trim().isEmpty() || lastName.trim().isEmpty()) return StatusCode.INVALID_NAME;

            CompletableFuture<Boolean> userExistsFuture = CompletableFuture.supplyAsync(() ->
                    dataStore.readUsers().stream()
                            .anyMatch(user -> user.getMetadataValue("first_name").equals(firstName) &&
                                              user.getMetadataValue("last_name").equals(lastName)), executorService);

            boolean userExists = userExistsFuture.get();
            return userExists ? StatusCode.USER_ALREADY_EXISTS : StatusCode.SUCCESS;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return StatusCode.INTERNAL_FAILURE;
        }
    }

    public StatusCode validateUsersByPassword(String password) {
        if (password == null) return StatusCode.INVALID_PASSWORD;
        if (password.length() < 8) return StatusCode.PASSWORD_TOO_SHORT;
        if (password.length() > 20) return StatusCode.PASSWORD_TOO_LONG;
        if (password.chars().noneMatch(Character::isDigit)) return StatusCode.PASSWORD_NO_DIGITS;
        if (password.chars().noneMatch(Character::isUpperCase)) return StatusCode.PASSWORD_NO_UPPERCASE;
        if (password.chars().noneMatch(Character::isLowerCase)) return StatusCode.PASSWORD_NO_LOWERCASE;
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) return StatusCode.PASSWORD_NO_SPECIAL_CHAR;
        return StatusCode.SUCCESS;
    }

    public StatusCode validateUsersByRole(Users.UserRole role) {
        if (role == null) return StatusCode.INVALID_USER_ROLE;
        return StatusCode.SUCCESS;
    }

    public StatusCode validateUsersByStatus(String username) {
        Users user = getUser(username);
        if (user == null) return StatusCode.USER_NOT_FOUND;
        if (!(boolean) user.getMetadataValue("is_active")) return StatusCode.USER_INACTIVE;
        return StatusCode.SUCCESS;
    }

    public StatusCode validateUsersByAdditionalInfo(String additionalInfo) {
        if (additionalInfo == null) return StatusCode.INVALID_USER_ADDITIONAL_INFO;
        return StatusCode.SUCCESS;
    }

    /* ===================== Core Methods ===================== */

    private Pair<List<StatusCode>, Users> validateAndGetUser(String field, Object value, String username) {
        List<StatusCode> validations = new ArrayList<>();
        try {
            Class<?> valueType = value.getClass();
            if (valueType == Boolean.class) {
                valueType = boolean.class;
            }
            Method validationMethod = this.getClass().getMethod("validateUsersBy" + field, valueType);
            StatusCode validationResult = (StatusCode) validationMethod.invoke(this, value);
            validations.add(validationResult);
            if (!Exceptions.isSuccessful(validations)) {
                return new Pair<>(validations, null);
            }
        } catch (Exception e) {
            validations.add(StatusCode.INTERNAL_FAILURE);
            return new Pair<>(validations, null);
        }

        Users user = getUser(username);
        if (user == null) {
            return new Pair<>(List.of(StatusCode.USER_NOT_FOUND), null);
        }

        validations.add(StatusCode.SUCCESS);
        return new Pair<>(validations, user);
    }

    /* ===================== User Retrieval Methods ===================== */

    public String keySearch(String username) {
        try {
            CompletableFuture<String> keyFuture = CompletableFuture.supplyAsync(() ->
                    dataStore.readUsers().stream()
                            .filter(user -> user.getMetadataValue("username").equals(username))
                            .findFirst()
                            .map(user -> (String) user.getMetadataValue("id"))
                            .orElse(null), executorService);

            return keyFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String reverseKeySearch(String id) {
        try {
            CompletableFuture<String> usernameFuture = CompletableFuture.supplyAsync(() ->
                    dataStore.readUsers().stream()
                            .filter(user -> user.getMetadataValue("id").equals(id))
                            .findFirst()
                            .map(user -> (String) user.getMetadataValue("username"))
                            .orElse(null), executorService);

            return usernameFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Users keyTransform(String id) {
        try {
            CompletableFuture<Users> userFuture = CompletableFuture.supplyAsync(() ->
                    dataStore.readUsers().stream()
                            .filter(user -> user.getMetadataValue("id").equals(id))
                            .findFirst()
                            .orElse(null), executorService);

            return userFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Users getUser(String username) {
        String id = keySearch(username);
        return keyTransform(id);
    }

    public String getUsersFirstNameByUsername(String username) {
        Users user = getUser(username);
        return user != null ? (String) user.getMetadataValue("first_name") : null;
    }

    public String getUsersLastNameByUsername(String username) {
        Users user = getUser(username);
        return user != null ? (String) user.getMetadataValue("last_name") : null;
    }

    public Users.UserRole getUsersRoleByUsername(String username) {
        Users user = getUser(username);
        return user != null ? (Users.UserRole) user.getMetadataValue("role") : null;
    }

    public String getUsersEmailByUsername(String username) {
        Users user = getUser(username);
        return user != null ? (String) user.getDataValue("email") : null;
    }

    public String getUsersAdditionalInfoByUsername(String username) {
        Users user = getUser(username);
        return user != null ? (String) user.getDataValue("additional_info") : null;
    }

    /* ===================== User Creation Method ===================== */

    public List<StatusCode> postUsers(String firstName, String lastName, String username, String password, String email, Users.UserRole role, String additionalInfo) {
        try {
            List<StatusCode> validations = new ArrayList<>();
            List<Callable<StatusCode>> validationTasks = new ArrayList<>();

            validationTasks.add(() -> validateUsersByFirstName(firstName));
            validationTasks.add(() -> validateUsersByLastName(lastName));
            validationTasks.add(() -> validateUsersByName(firstName, lastName));
            validationTasks.add(() -> validateUsersByUsername(username));
            validationTasks.add(() -> validateUsersByPassword(password));
            validationTasks.add(() -> validateUsersByEmailAddress(email));
            validationTasks.add(() -> validateUsersByRole(role));
            validationTasks.add(() -> validateUsersByAdditionalInfo(additionalInfo));

            List<Future<StatusCode>> futures = executorService.invokeAll(validationTasks);
            for (Future<StatusCode> future : futures) {
                validations.add(future.get());
            }

            if (!Exceptions.isSuccessful(validations)) {
                return validations;
            }

            boolean registered = AuthenticationManager.register(firstName, lastName, username, password, email, role);
            if (!registered) {
                validations.add(StatusCode.USER_REGISTRATION_FAILED);
                return validations;
            }
            Users user = getUser(username);
            user.setDataValue("additional_info", additionalInfo);
            dataStore.updateUser(user);
            return validations;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return List.of(StatusCode.USER_POST_FAILED);
        }
    }

    public List<StatusCode> postUsers(String firstName, String lastName, String username, String password, String email, Users.UserRole role) {
        return postUsers(firstName, lastName, username, password, email, role, "");
    }

    /* ===================== User Update Methods ===================== */

    public List<StatusCode> putUsersUsername(String username, String newUsername) {
        try {
            Pair<List<StatusCode>, Users> result = validateAndGetUser("Username", newUsername, username);
            List<StatusCode> validations = result.getKey();
            if (!Exceptions.isSuccessful(validations)) return validations;

            Users user = result.getValue();

            user.updateMetadata("username", newUsername);
            dataStore.updateUser(user);
            return validations;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(StatusCode.USER_PUT_FAILED);
        }
    }

    public List<StatusCode> putUsersEmailAddress(String username, String newEmail) {
        try {
            Pair<List<StatusCode>, Users> result = validateAndGetUser("EmailAddress", newEmail, username);
            List<StatusCode> validations = result.getKey();
            if (!Exceptions.isSuccessful(validations)) return validations;

            Users user = result.getValue();

            user.setDataValue("email", newEmail);
            dataStore.updateUser(user);
            return validations;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(StatusCode.USER_PUT_FAILED);
        }
    }

    public List<StatusCode> putUsersFirstName(String username, String newFirstName) {
        try {
            Pair<List<StatusCode>, Users> result = validateAndGetUser("FirstName", newFirstName, username);
            List<StatusCode> validations = result.getKey();
            if (!Exceptions.isSuccessful(validations)) return validations;

            Users user = result.getValue();

            user.updateMetadata("first_name", newFirstName);
            dataStore.updateUser(user);
            return validations;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(StatusCode.USER_PUT_FAILED);
        }
    }

    public List<StatusCode> putUsersLastName(String username, String newLastName) {
        try {
            Pair<List<StatusCode>, Users> result = validateAndGetUser("LastName", newLastName, username);
            List<StatusCode> validations = result.getKey();
            if (!Exceptions.isSuccessful(validations)) return validations;

            Users user = result.getValue();

            user.updateMetadata("last_name", newLastName);
            dataStore.updateUser(user);
            return validations;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(StatusCode.USER_PUT_FAILED);
        }
    }

    public List<StatusCode> putUsersPassword(String username, String newPassword) {
        try {
            Pair<List<StatusCode>, Users> result = validateAndGetUser("Password", newPassword, username);
            List<StatusCode> validations = result.getKey();
            if (!Exceptions.isSuccessful(validations)) return validations;

            Users user = result.getValue();
            String passwordHash = PasswordUtils.hashPassword(newPassword);
            user.setDataValue("password", passwordHash);

            dataStore.updateUser(user);
            return validations;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(StatusCode.USER_PUT_FAILED);
        }
    }

    public List<StatusCode> putUsersRole(String username, Users.UserRole newRole) {
        try {
            Pair<List<StatusCode>, Users> result = validateAndGetUser("Role", newRole, username);
            List<StatusCode> validations = result.getKey();
            if (!Exceptions.isSuccessful(validations)) return validations;

            Users user = result.getValue();

            user.updateMetadata("role", newRole);
            dataStore.updateUser(user);
            return validations;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(StatusCode.USER_PUT_FAILED);
        }
    }

    public List<StatusCode> putUsersStatus(String username, boolean newStatus) {
        try {
            Pair<List<StatusCode>, Users> result = validateAndGetUser("Status", newStatus, username);
            List<StatusCode> validations = result.getKey();
            if (!Exceptions.isSuccessful(validations)) return validations;

            Users user = result.getValue();

            user.updateMetadata("is_active", newStatus);
            dataStore.updateUser(user);
            validations.add(StatusCode.SUCCESS);
            return validations;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(StatusCode.USER_PUT_FAILED);
        }
    }

    public List<StatusCode> putUsersAdditionalInfo(String username, String additionalInfo) {
        try {
            Pair<List<StatusCode>, Users> result = validateAndGetUser("AdditionalInfo", additionalInfo, username);
            List<StatusCode> validations = result.getKey();
            if (!Exceptions.isSuccessful(validations)) return validations;

            Users user = result.getValue();

            user.setDataValue("additional_info", additionalInfo);
            dataStore.updateUser(user);
            return validations;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(StatusCode.USER_PUT_FAILED);
        }
    }

    /* ===================== User Deletion Method ===================== */

    public List<StatusCode> deleteUser(String username) {
        try {
            Users user = getUser(username);
            if (user == null) return List.of(StatusCode.USER_NOT_FOUND);

            dataStore.deleteUser(user);
            return List.of(StatusCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(StatusCode.USER_DELETION_FAILED);
        }
    }

    /* ===================== User Search Method ===================== */

    public List<Users> searchUsers(String query) {
        try {
            CompletableFuture<List<Users>> searchFuture = CompletableFuture.supplyAsync(() -> {
                String[] tokens = query.trim().split("\\s+");

                if (tokens.length > 2) {
                    return Collections.emptyList();
                }

                return dataStore.readUsers().stream()
                        .filter(user -> {
                            if (tokens.length == 2) {
                                String firstName = tokens[0].toLowerCase();
                                String lastName = tokens[1].toLowerCase();
                                return user.getMetadataValue("first_name").toString().toLowerCase().contains(firstName) &&
                                       user.getMetadataValue("last_name").toString().toLowerCase().contains(lastName);
                            } else {
                                String singleQuery = tokens[0].toLowerCase();
                                return user.getMetadata().metadata().values().stream()
                                               .filter(Objects::nonNull)
                                               .anyMatch(value -> value.toString().toLowerCase().contains(singleQuery)) ||
                                       user.getData().values().stream()
                                               .filter(Objects::nonNull)
                                               .anyMatch(value -> value.toString().toLowerCase().contains(singleQuery));
                            }
                        })
                        .collect(Collectors.toList());
            }, executorService);

            return searchFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /* ===================== Shutdown Executor Service ===================== */

    public void shutdown() {
        executorService.shutdown();
    }
}

