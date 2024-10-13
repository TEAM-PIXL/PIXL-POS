package teampixl.com.pixlpos.database.api.util;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to handle exceptions and status codes for the API
 */
public class Exceptions {

    /**
     * Throw a new exception with a message
     * @param MESSAGE The message to throw with the exception
     */
    public static void throwException(String MESSAGE) {
        throw new RuntimeException(MESSAGE);
    }

    /**
     * Throw a new exception with a message and a cause
     * @param MESSAGE The message to throw with the exception
     * @param CAUSE The cause of the exception
     */
    public static void throwException(String MESSAGE, Throwable CAUSE) {
        throw new RuntimeException(MESSAGE, CAUSE);
    }

    /**
     * Check if all status codes are successful
     * @param STATUS_CODES A list of status codes to check if all are successful
     * @return boolean if all status codes are successful or not
     */
    public static boolean isSuccessful(List<StatusCode> STATUS_CODES) {
        return STATUS_CODES.stream().allMatch(StatusCode -> StatusCode == teampixl.com.pixlpos.database.api.util.StatusCode.SUCCESS);
    }

    /**
     * Return a string with the message and status codes in order to display to the user and aid in debugging
     * @param MESSAGE A defined message to return with the status codes
     * @param STATUS_CODES A list of status codes to return based on list passed
     * @return string with message and status codes
     */
    public static String returnStatus(String MESSAGE, List<StatusCode> STATUS_CODES) {
        ArrayList<StatusCode> ERROR_CODES = new ArrayList<>();
        STATUS_CODES.forEach(StatusCode -> {
            if (StatusCode != teampixl.com.pixlpos.database.api.util.StatusCode.SUCCESS) {
                ERROR_CODES.add(StatusCode);
            }
        });
        return MESSAGE + " " + ERROR_CODES;
    }

    private static String generateErrorMessage( StatusCode STATUS ) {
        return "The following error occurred: " + STATUS;
    }

    /**
     * Handle the status code and return the error message
     * @param STATUS The status code to handle
     * @return the error message
     */
    public static String handleStatusCode( StatusCode STATUS ) {
        System.out.println( generateErrorMessage( STATUS ) );
        return generateErrorMessage( STATUS );
    }
}
