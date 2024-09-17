package teampixl.com.pixlpos.database.api.util;

import java.util.List;

public class Exceptions {
    public static void throwException(String message) {
        throw new RuntimeException(message);
    }

    public static void throwException(String message, Throwable cause) {
        throw new RuntimeException(message, cause);
    }

    public static boolean isSuccessful(List<StatusCode> statusCodes) {
        return statusCodes.stream().allMatch(StatusCode -> StatusCode == teampixl.com.pixlpos.database.api.util.StatusCode.SUCCESS);
    }

    public static String returnStatus(String MESSAGE, List<StatusCode> statusCodes) {
        return MESSAGE + " " + statusCodes;
    }
}
