package teampixl.com.pixlpos.database.api.util;

import javafx.util.Pair;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;


public class Util {
    public static StatusCode validateNotNullOrEmpty(String value, StatusCode nullStatusCode, StatusCode emptyStatusCode) {
        if (value == null) {
            return nullStatusCode;
        }
        if (value.trim().isEmpty()) {
            return emptyStatusCode;
        }
        return StatusCode.SUCCESS;
    }

    public <T> Pair<List<StatusCode>, T> validateAndGetObject(
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
