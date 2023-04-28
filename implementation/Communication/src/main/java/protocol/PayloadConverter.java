package protocol;

import com.google.gson.Gson;
import sendable.ISendable;

public class PayloadConverter {
    public static final Gson gson = new Gson();

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