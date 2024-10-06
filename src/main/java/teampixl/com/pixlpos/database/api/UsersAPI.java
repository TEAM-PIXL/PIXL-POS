package teampixl.com.pixlpos.database.api;

import teampixl.com.pixlpos.database.api.util.*;
import teampixl.com.pixlpos.database.DataStore;
import teampixl.com.pixlpos.authentication.AuthenticationManager;
import teampixl.com.pixlpos.authentication.PasswordUtils;
import teampixl.com.pixlpos.models.Users;

import java.util.List;
import java.util.ArrayList;
import javafx.util.Pair;
import java.util.Objects;
import java.util.stream.Collectors;
import java.lang.reflect.Method;

/**
 * The UsersAPI class is a singleton class that is responsible for managing the users in memory. It is responsible for getting, posting, putting, and deleting users. It also validates transactions and ensures that the data is consistent and correct.
 * It employs key search and reverse key search to manage user data and metadata. It also provides search functionality to search for users based on their metadata.
 */
public class UsersAPI {
    private static UsersAPI instance;
    private static DataStore dataStore = DataStore.getInstance();

    private UsersAPI() { }

    /**
     * Gets the instance of the UsersAPI.
     *
     * @return the instance of the UsersAPI
     */
    public static synchronized UsersAPI getInstance() {
        if (instance == null) {
            instance = new UsersAPI();
        }
        return instance;
    }

    /**
     * Constructor for the UsersAPI class.
     *
     * @param dataStore the data store to use
     */
    public UsersAPI(DataStore dataStore) { UsersAPI.dataStore = dataStore; }

    /**
     * Validates the username of a user.
     *
     * @param USERNAME the username to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateUsersByUsername(String USERNAME) {
        if (USERNAME == null) { return StatusCode.USERNAME_NULL; }
        if (USERNAME.length() < 4) { return StatusCode.USERNAME_TOO_SHORT; }
        if (USERNAME.length() > 20) { return StatusCode.USERNAME_TOO_LONG; }
        if (USERNAME.chars().anyMatch(Character::isSpaceChar)) { return StatusCode.USERNAME_CONTAINS_SPACES; }
        if (!USERNAME.matches("^[a-zA-Z0-9]*$")) { return StatusCode.USERNAME_INVALID_CHARACTERS; }
        if (USERNAME.chars().allMatch(Character::isDigit)) { return StatusCode.USERNAME_ONLY_DIGITS; }
        boolean USER_EXISTS = dataStore.readUsers().stream()
                .anyMatch(user -> user.getMetadata().metadata().get("username").toString().equals(USERNAME));
        return USER_EXISTS ? StatusCode.USERNAME_TAKEN : StatusCode.SUCCESS;
    }

    /**
     * Validates the email address of a user.
     *
     * @param EMAIL the email address to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateUsersByEmailAddress(String EMAIL) {
        if (EMAIL == null) { return StatusCode.EMAIL_NULL; }
        if (EMAIL.chars().filter(ch -> ch == '@').count() != 1) { return StatusCode.EMAIL_INVALID_FORMAT; }
        if (EMAIL.chars().anyMatch(Character::isSpaceChar)) { return StatusCode.EMAIL_CONTAINS_SPACES; }
        boolean USER_EXISTS = dataStore.readUsers().stream()
                .anyMatch(user -> user.getData().get("email").toString().equals(EMAIL));
        return USER_EXISTS ? StatusCode.EMAIL_TAKEN : StatusCode.SUCCESS;
    }
    /**
     * Validates the first name of a user.
     *
     * @param FIRST_NAME the first name to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateUsersByFirstName(String FIRST_NAME) {
        if (FIRST_NAME == null) { return StatusCode.INVALID_FIRST_NAME; }
        if (FIRST_NAME.trim().isEmpty()) { return StatusCode.INVALID_FIRST_NAME; }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the last name of a user.
     *
     * @param LAST_NAME the last name to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateUsersByLastName(String LAST_NAME) {
        if (LAST_NAME == null) { return StatusCode.INVALID_LAST_NAME; }
        if (LAST_NAME.trim().isEmpty()) { return StatusCode.INVALID_LAST_NAME; }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the name of a user.
     *
     * @param FIRST_NAME the first name to validate
     * @param LAST_NAME the last name to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateUsersByName(String FIRST_NAME, String LAST_NAME) {
        if (FIRST_NAME == null || LAST_NAME == null) { return StatusCode.INVALID_NAME; }
        if (FIRST_NAME.trim().isEmpty() || LAST_NAME.trim().isEmpty()) { return StatusCode.INVALID_NAME; }
        boolean USER_EXISTS = dataStore.readUsers().stream()
                .anyMatch(user -> user.getMetadata().metadata().get("first_name").toString().equals(FIRST_NAME) &&
                        user.getMetadata().metadata().get("last_name").toString().equals(LAST_NAME));
        return USER_EXISTS ? StatusCode.USER_ALREADY_EXISTS : StatusCode.SUCCESS;
    }

    /**
     * Validates the password of a user.
     *
     * @param PASSWORD the password to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateUsersByPassword(String PASSWORD) {
        if (PASSWORD == null) { return StatusCode.INVALID_PASSWORD; }
        if (PASSWORD.length() < 8) { return StatusCode.PASSWORD_TOO_SHORT; }
        if (PASSWORD.length() > 20) { return StatusCode.PASSWORD_TOO_LONG; }
        if (PASSWORD.chars().noneMatch(Character::isDigit)) { return StatusCode.PASSWORD_NO_DIGITS; }
        if (PASSWORD.chars().noneMatch(Character::isUpperCase)) { return StatusCode.PASSWORD_NO_UPPERCASE; }
        if (PASSWORD.chars().noneMatch(Character::isLowerCase)) { return StatusCode.PASSWORD_NO_LOWERCASE; }
        if (!PASSWORD.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) { return StatusCode.PASSWORD_NO_SPECIAL_CHAR; }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the role of a user.
     *
     * @param ROLE the role to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateUsersByRole(Users.UserRole ROLE) {
        if (ROLE == null) { return StatusCode.INVALID_USER_ROLE; } return StatusCode.SUCCESS;
    }

    /**
     * Validates the status of a user.
     *
     * @param USERNAME the status to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateUsersByStatus(String USERNAME) {
        Users USER = getUser(USERNAME);
        if (USER == null) { return StatusCode.USER_NOT_FOUND; }
        if (!(boolean) USER.getMetadata().metadata().get("is_active")) { return StatusCode.USER_INACTIVE; }
        return StatusCode.SUCCESS;
    }

    /**
     * Validates the additional information of a user.
     *
     * @param ADDITIONAL_INFO the additional information to validate
     * @return the status code indicating the result of the validation
     */
    public StatusCode validateUsersByAdditionalInfo(String ADDITIONAL_INFO) {
        if (ADDITIONAL_INFO == null) { return StatusCode.INVALID_USER_ADDITIONAL_INFO; } return StatusCode.SUCCESS;
    }

    /* ---> IMPORTANT INTERNAL FUNCTION FOR SAFELY MANAGING AND VALIDATING A USER UPDATE <---- */
    private Pair<List<StatusCode>, Users> validateAndGetUser(String FIELD, Object VALUE, String USERNAME) {
        List<StatusCode> VALIDATIONS = new ArrayList<>();
        try {
            Class<?> VALUE_TYPE = VALUE.getClass();
            System.out.println(VALUE_TYPE);
            if (VALUE_TYPE == Boolean.class) {
                VALUE_TYPE = boolean.class;
            }
            Method VALIDATION_METHOD = this.getClass().getMethod("validateUsersBy" + FIELD, VALUE_TYPE);
            StatusCode VALIDATION_RESULT = (StatusCode) VALIDATION_METHOD.invoke(this, VALUE);
            VALIDATIONS.add(VALIDATION_RESULT);
            if (!Exceptions.isSuccessful(VALIDATIONS)) {
                return new Pair<>(VALIDATIONS, null);
            }
        } catch (Exception e) {
            VALIDATIONS.add(StatusCode.INTERNAL_FAILURE);
            return new Pair<>(VALIDATIONS, null);
        }

        Users USER = getUser(USERNAME);

        if (USER == null) {
            return new Pair<>(List.of(StatusCode.USER_NOT_FOUND), null);
        }

        VALIDATIONS.add(StatusCode.SUCCESS);
        return new Pair<>(VALIDATIONS, USER);
    }

    /**
     * Gets a user ID from memory. Core method for user retrieval and key search.
     * This method is useful when dealing with metadata references in other models. It helps transform user input into a user ID.
     *
     * @param USERNAME the query to search for the user
     * @return the user id matching the query
     */
    public String keySearch(String USERNAME) {
        return dataStore.readUsers().stream()
                .filter(user -> user.getMetadata().metadata().get("username").toString().equals(USERNAME))
                .findFirst()
                .map(user -> user.getMetadata().metadata().get("id").toString())
                .orElse(null);
    }

    /**
     * Gets a username from memory. Core method for user retrieval and reverse key search.
     * This method is useful when dealing with metadata references in other models. It helps transform user ID into a username.
     *
     * @param ID the query to search for the user
     * @return the username matching the query
     */
    public String reverseKeySearch(String ID) {
        return dataStore.readUsers().stream()
                .filter(user -> user.getMetadata().metadata().get("id").toString().equals(ID))
                .findFirst()
                .map(user -> user.getMetadata().metadata().get("username").toString())
                .orElse(null);
    }

    /**
     * Transforms an ID into a Users object in memory. Core method for user retrieval.
     * This method is useful when dealing with metadata references in other models.
     *
     * @param ID the query to search for the user
     * @return the user matching the query
     */
    public Users keyTransform(String ID) {
        return dataStore.readUsers().stream()
                .filter(user -> user.getMetadata().metadata().get("id").toString().equals(ID))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets a user from memory.
     *
     * @param USERNAME the query to search for the user
     * @return the user matching the query
     */
    public Users getUser(String USERNAME) {
        String ID = keySearch(USERNAME);
        return keyTransform(ID);
    }

    /**
     * Gets a user's first name from memory.
     *
     * @param USERNAME the query to search for the user
     * @return the user matching the query
     */
    public String getUsersFirstNameByUsername(String USERNAME) {
        return dataStore.readUsers().stream()
                .filter(user -> user.getMetadata().metadata().get("username").toString().equals(USERNAME))
                .findFirst()
                .map(user -> user.getMetadata().metadata().get("first_name").toString())
                .orElse(null);
    }

    /**
     * Gets a user's last name from memory.
     *
     * @param USERNAME the query to search for the user
     * @return the user matching the query
     */
    public String getUsersLastNameByUsername(String USERNAME) {
        return dataStore.readUsers().stream()
                .filter(user -> user.getMetadata().metadata().get("username").toString().equals(USERNAME))
                .findFirst()
                .map(user -> user.getMetadata().metadata().get("last_name").toString())
                .orElse(null);
    }

    /**
     * Gets a user's role from memory.
     *
     * @param USERNAME the query to search for the user
     * @return the user matching the query
     */
    public Users.UserRole getUsersRoleByUsername(String USERNAME) {
        return dataStore.readUsers().stream()
                .filter(user -> user.getMetadata().metadata().get("username").toString().equals(USERNAME))
                .findFirst()
                .map(user -> (Users.UserRole) user.getMetadata().metadata().get("role"))
                .orElse(null);
    }

    /**
     * Gets a user's email from memory.
     *
     * @param USERNAME the query to search for the user
     * @return the user matching the query
     */
    public String getUsersEmailByUsername(String USERNAME) {
        return dataStore.readUsers().stream()
                .filter(user -> user.getMetadata().metadata().get("username").toString().equals(USERNAME))
                .findFirst()
                .map(user -> user.getData().get("email").toString())
                .orElse(null);
    }

    /**
     * Gets a user's additional info from memory.
     *
     * @param USERNAME the query to search for the user
     * @return the user matching the query
     */
    public String getUsersAdditionalInfoByUsername(String USERNAME) {
        return dataStore.readUsers().stream()
                .filter(user -> user.getMetadata().metadata().get("username").toString().equals(USERNAME))
                .findFirst()
                .map(user -> user.getData().get("additional_info").toString())
                .orElse(null);
    }

    /**
     * Posts a new user to the database.
     *
     * @param FIRST_NAME the first name of the user
     * @param LAST_NAME the last name of the user
     * @param USERNAME the username of the user
     * @param PASSWORD the password of the user
     * @param EMAIL the email of the user
     * @param ROLE the role of the user
     * @param ADDITIONAL_INFO the additional information of the user
     * @return the status code indicating the result of the operation
     */
    public List<StatusCode> postUsers(String FIRST_NAME, String LAST_NAME, String USERNAME, String PASSWORD, String EMAIL, Users.UserRole ROLE, String ADDITIONAL_INFO) {
        try {
            List<StatusCode> VALIDATIONS = new java.util.ArrayList<>();
            VALIDATIONS.add(validateUsersByFirstName(FIRST_NAME));
            VALIDATIONS.add(validateUsersByLastName(LAST_NAME));
            VALIDATIONS.add(validateUsersByName(FIRST_NAME, LAST_NAME));
            VALIDATIONS.add(validateUsersByUsername(USERNAME));
            VALIDATIONS.add(validateUsersByPassword(PASSWORD));
            VALIDATIONS.add(validateUsersByEmailAddress(EMAIL));
            VALIDATIONS.add(validateUsersByRole(ROLE));
            VALIDATIONS.add(validateUsersByAdditionalInfo(ADDITIONAL_INFO));
            if (!Exceptions.isSuccessful(VALIDATIONS)) {
                return VALIDATIONS;
            }

            boolean REGISTERED = AuthenticationManager.register(FIRST_NAME, LAST_NAME, USERNAME, PASSWORD, EMAIL, ROLE);
            if (!REGISTERED) { VALIDATIONS.add(StatusCode.USER_REGISTRATION_FAILED); return VALIDATIONS; }
            Users USER = getUser(USERNAME);
            USER.setDataValue("additional_info", ADDITIONAL_INFO);
            dataStore.updateUser(USER);
            return VALIDATIONS;
        } catch (Exception e) {
            return List.of(StatusCode.USER_POST_FAILED);
        }
    }

    /**
     * Posts a new user to the database.
     *
     * @param FIRST_NAME the first name of the user
     * @param LAST_NAME the last name of the user
     * @param USERNAME the username of the user
     * @param PASSWORD the password of the user
     * @param EMAIL the email of the user
     * @param ROLE the role of the user
     * @return the status code indicating the result of the operation
     */
    public List<StatusCode> postUsers(String FIRST_NAME, String LAST_NAME, String USERNAME, String PASSWORD, String EMAIL, Users.UserRole ROLE) {
        return postUsers(FIRST_NAME, LAST_NAME, USERNAME, PASSWORD, EMAIL, ROLE, "");
    }

    /**
     * Updates a user from the database.
     *
     * @param USERNAME the query to search for the user
     * @param NEW_USERNAME the new username to update
     * @return status code indicating the result of the operation
     */
    public List<StatusCode> putUsersUsername(String USERNAME, String NEW_USERNAME) {
        try {
            Pair<List<StatusCode>, Users> RESULT = validateAndGetUser("Username", NEW_USERNAME, USERNAME);
            List<StatusCode> VALIDATIONS = RESULT.getKey();
            if (!Exceptions.isSuccessful(VALIDATIONS)) { return VALIDATIONS; }

            Users USER = RESULT.getValue();

            USER.updateMetadata("username", NEW_USERNAME);
            dataStore.updateUser(USER);
            return VALIDATIONS;
        } catch (Exception e) {
            return List.of(StatusCode.USER_PUT_FAILED);
        }
    }

    /**
     * Updates a user from the database.
     *
     * @param USERNAME the query to search for the user
     * @param NEW_EMAIL the new email to update
     * @return status code indicating the result of the operation
     */
    public List<StatusCode> putUsersEmailAddress(String USERNAME, String NEW_EMAIL) {
        try {
            Pair<List<StatusCode>, Users> RESULT = validateAndGetUser("EmailAddress", NEW_EMAIL, USERNAME);
            List<StatusCode> VALIDATIONS = RESULT.getKey();
            if (!Exceptions.isSuccessful(VALIDATIONS)) { return VALIDATIONS; }

            Users USER = RESULT.getValue();

            USER.setDataValue("email", NEW_EMAIL);
            dataStore.updateUser(USER);
            return VALIDATIONS;
        } catch (Exception e) {
            return List.of(StatusCode.USER_PUT_FAILED);
        }
    }

    /**
     * Updates a user from the database.
     *
     * @param USERNAME the query to search for the user
     * @param NEW_FIRST_NAME the new first name to update
     * @return status code indicating the result of the operation
     */
    public List<StatusCode> putUsersFirstName(String USERNAME, String NEW_FIRST_NAME) {
        try {
            Pair<List<StatusCode>, Users> RESULT = validateAndGetUser("FirstName", NEW_FIRST_NAME, USERNAME);
            List<StatusCode> VALIDATIONS = RESULT.getKey();
            if (!Exceptions.isSuccessful(VALIDATIONS)) { return VALIDATIONS; }

            Users USER = RESULT.getValue();

            USER.updateMetadata("first_name", NEW_FIRST_NAME);
            dataStore.updateUser(USER);
            return VALIDATIONS;
        } catch (Exception e) {
            return List.of(StatusCode.USER_PUT_FAILED);
        }
    }

    /**
     * Updates a user from the database.
     *
     * @param USERNAME the query to search for the user
     * @param NEW_LAST_NAME the new last name to update
     * @return status code indicating the result of the operation
     */
    public List<StatusCode> putUsersLastName(String USERNAME, String NEW_LAST_NAME) {
        try {
            Pair<List<StatusCode>, Users> RESULT = validateAndGetUser("LastName", NEW_LAST_NAME, USERNAME);
            List<StatusCode> VALIDATIONS = RESULT.getKey();
            if (!Exceptions.isSuccessful(VALIDATIONS)) { return VALIDATIONS; }

            Users USER = RESULT.getValue();

            USER.updateMetadata("last_name", NEW_LAST_NAME);
            dataStore.updateUser(USER);
            return VALIDATIONS;
        } catch (Exception e) {
            return List.of(StatusCode.USER_PUT_FAILED);
        }
    }

    /**
     * Updates a user from the database.
     *
     * @param USERNAME the query to search for the user
     * @param NEW_PASSWORD the new password to update
     * @return status code indicating the result of the operation
     */
    public List<StatusCode> putUsersPassword(String USERNAME, String NEW_PASSWORD) {
        try {
            Pair<List<StatusCode>, Users> RESULT = validateAndGetUser("Password", NEW_PASSWORD, USERNAME);
            List<StatusCode> VALIDATIONS = RESULT.getKey();
            if (!Exceptions.isSuccessful(VALIDATIONS)) { return VALIDATIONS; }

            Users USER = RESULT.getValue();
            String PASSWORD_HASH = PasswordUtils.hashPassword(NEW_PASSWORD);
            USER.setDataValue("password", PASSWORD_HASH);

            dataStore.updateUser(USER);
            return VALIDATIONS;
        } catch (Exception e) {
            return List.of(StatusCode.USER_PUT_FAILED);
        }
    }

    /**
     * Updates a user from the database.
     *
     * @param USERNAME the query to search for the user
     * @param NEW_ROLE the new role to update
     * @return status code indicating the result of the operation
     */
    public List<StatusCode> putUsersRole(String USERNAME, Users.UserRole NEW_ROLE) {
        try {
            Pair<List<StatusCode>, Users> RESULT = validateAndGetUser("Role", NEW_ROLE, USERNAME);
            List<StatusCode> VALIDATIONS = RESULT.getKey();
            if (!Exceptions.isSuccessful(VALIDATIONS)) { return VALIDATIONS; }

            Users USER = RESULT.getValue();

            USER.updateMetadata("role", NEW_ROLE);
            dataStore.updateUser(USER);
            return VALIDATIONS;
        } catch (Exception e) {
            return List.of(StatusCode.USER_PUT_FAILED);
        }
    }

    /**
     * Updates a user from the database.
     *
     * @param USERNAME the query to search for the user
     * @param NEW_STATUS the new status to update
     * @return status code indicating the result of the operation
     */
    public List<StatusCode> putUsersStatus(String USERNAME, boolean NEW_STATUS) {
        try {
            List<StatusCode> VALIDATIONS = new ArrayList<>();

            String ID = keySearch(USERNAME);
            if (ID == null) { return List.of(StatusCode.USER_NOT_FOUND); }
            Users USER = keyTransform(ID);

            USER.updateMetadata("is_active", NEW_STATUS);
            dataStore.updateUser(USER);
            VALIDATIONS.add(StatusCode.SUCCESS);
            return VALIDATIONS;
        } catch (Exception e) {
            return List.of(StatusCode.USER_PUT_FAILED);
        }
    }

    /**
     * Updates a user from the database.
     *
     * @param USERNAME the query to search for the user
     * @param ADDITIONAL_INFO the new additional information to update
     * @return status code indicating the result of the operation
     */
    public List<StatusCode> putUsersAdditionalInfo(String USERNAME, String ADDITIONAL_INFO) {
        try {
            Pair<List<StatusCode>, Users> RESULT = validateAndGetUser("AdditionalInfo", ADDITIONAL_INFO, USERNAME);
            List<StatusCode> VALIDATIONS = RESULT.getKey();
            if (!Exceptions.isSuccessful(VALIDATIONS)) { return VALIDATIONS; }

            Users USER = RESULT.getValue();

            USER.setDataValue("additional_info", ADDITIONAL_INFO);
            dataStore.updateUser(USER);
            return VALIDATIONS;
        } catch (Exception e) {
            return List.of(StatusCode.USER_PUT_FAILED);
        }
    }

    /**
     * Deletes a user from the database.
     *
     * @param USERNAME the query to search for the user
     * @return status code indicating the result of the operation
     */
    public List<StatusCode> deleteUser(String USERNAME) {
        try {
            Pair<List<StatusCode>, Users> RESULT = validateAndGetUser("AdditionalInfo", USERNAME, USERNAME);
            List<StatusCode> VALIDATIONS = RESULT.getKey();
            if (!Exceptions.isSuccessful(VALIDATIONS)) { return VALIDATIONS; }

            Users USER = RESULT.getValue();


            dataStore.deleteUser(USER);
            return VALIDATIONS;
        } catch (Exception e) {
            return List.of(StatusCode.USER_DELETION_FAILED);
        }
    }

    /**
     * Gets all users from memory that match the query input based on the user's metadata.
     *
     * @param query the query to search for the user
     * @return list of users matching the query
     */
    public List<Users> searchUsers(String query) {
        String[] TOKENS = query.trim().split("\\s+");

        if (TOKENS.length > 2) {
            return List.of();
        }

        return dataStore.readUsers().parallelStream()
                .filter(user -> {
                    if (TOKENS.length == 2) {
                        String firstName = TOKENS[0].toLowerCase();
                        String lastName = TOKENS[1].toLowerCase();
                        return user.getMetadata().metadata().get("first_name").toString().toLowerCase().contains(firstName) &&
                                user.getMetadata().metadata().get("last_name").toString().toLowerCase().contains(lastName);
                    } else {
                        String singleQuery = TOKENS[0].toLowerCase();
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
