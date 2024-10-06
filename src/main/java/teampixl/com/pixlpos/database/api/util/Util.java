package teampixl.com.pixlpos.database.api.util;

import javafx.util.Pair;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;


public class Util {

    /**
     * Validate that a string is not null or empty.
     * @param value The string to validate.
     * @param nullStatusCode The status code to return if the string is null.
     * @param emptyStatusCode The status code to return if the string is empty.
     * @return The status code.
     */
    public static StatusCode validateNotNullOrEmpty(String value, StatusCode nullStatusCode, StatusCode emptyStatusCode) {
        if (value == null) {
            return nullStatusCode;
        }
        if (value.trim().isEmpty()) {
            return emptyStatusCode;
        }
        return StatusCode.SUCCESS;
    }

    /**
     * Validate and retrieve an object.
     * @param validationFunction The function to validate the object.
     * @param retrievalFunction The function to retrieve the object.
     * @param value The object to validate.
     * @param id The ID of the object.
     * @param notFoundStatusCode The status code to return if the object is not found.
     * @param <T> The type of the object.
     * @return A pair containing the status codes and the object.
     */
    public static <T> Pair<List<StatusCode>, T> validateAndGetObject(
            Function<Object, StatusCode> validationFunction,
            Function<String, T> retrievalFunction,
            Object value,
            String id,
            StatusCode notFoundStatusCode
    ) {
        List<StatusCode> validations = new ArrayList<>();

        StatusCode validationResult = validationFunction.apply(value);
        validations.add(validationResult);
        if (!Exceptions.isSuccessful(validations)) {
            return new Pair<>(validations, null);
        }

        T object = retrievalFunction.apply(id);

        if (object == null) {
            validations.add(notFoundStatusCode);
            return new Pair<>(validations, null);
        }

        return new Pair<>(validations, object);
    }

}
