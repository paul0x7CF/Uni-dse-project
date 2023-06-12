package CF.protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import CF.sendable.ISendable;

import java.time.LocalDateTime;

public class PayloadConverter {
    public static final Gson gson = new Gson();

    private PayloadConverter() {
        throw new IllegalStateException("Utility class");
    }

    /**TODO: @Günther: Check
     * added this code:
     *  Gson gson = new GsonBuilder()
     *                 .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
     *                 .create();
     */
    public static <T extends ISendable> String toJSON(T object) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        return gson.toJson(object);
    }

    /**TODO: @Günther: Check
     * added this code:
     *  Gson gson = new GsonBuilder()
     *                 .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
     *                 .create();
     */
    public static <T extends ISendable> T fromJSON(String json, Class<T> type) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        return gson.fromJson(json, type);
    }
}