package CF.protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import CF.sendable.ISendable;

import java.time.LocalDateTime;

/**
 * This class is used to convert objects to JSON and vice versa. It uses the {@link Gson} library to do so. It also
 * registers a {@link LocalDateTimeAdapter} to convert {@link LocalDateTime} objects to JSON and vice versa.
 * <p>
 *     {@link ISendable} objects can be converted.
 * </p>
 */
public class PayloadConverter {
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private PayloadConverter() {
        throw new IllegalStateException("Utility class");
    }

    public static <T extends ISendable> String toJSON(T object) {
        return gson.toJson(object);
    }

    public static <T extends ISendable> T fromJSON(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }
}