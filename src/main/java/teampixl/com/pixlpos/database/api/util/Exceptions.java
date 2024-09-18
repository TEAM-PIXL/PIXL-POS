package teampixl.com.pixlpos.database.api.util;

import java.util.List;

public class Exceptions {
    public static void throwException(String MESSAGE) {
        throw new RuntimeException(MESSAGE);
    }

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
        return MESSAGE + " " + STATUS_CODES;
    }
}
