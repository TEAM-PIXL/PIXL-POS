package teampixl.com.pixlpos.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    /**
     * Converts a Unix timestamp to a readable date and time.
     * @param unixTimestamp Unix timestamp to convert.
     * @return Readable date and time.
     */
    public static String convertUnixTimestampToReadable(long unixTimestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(unixTimestamp), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        return dateTime.format(formatter);
    }
}