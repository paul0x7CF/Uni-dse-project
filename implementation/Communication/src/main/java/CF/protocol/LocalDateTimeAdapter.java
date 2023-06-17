package CF.protocol;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class will convert a LocalDateTime object to a JSON string and vice versa. The {@link PayloadConverter} class
 * will use this class to convert the {@link LocalDateTime} fields of messages' payload.
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        String formattedDate = src.format(formatter);
        return new JsonPrimitive(formattedDate);
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String dateString = json.getAsString();
        return LocalDateTime.parse(dateString, formatter);
    }
}
