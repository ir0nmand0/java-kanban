package gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import manager.Managers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        if (Objects.isNull(localDateTime)) {
            jsonWriter.nullValue();
            return;
        }

        jsonWriter.value(localDateTime.format(Managers.DATE_TIME_FORMATTER));
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        switch (jsonReader.peek()) {
            case JsonToken.NULL -> {
                jsonReader.nextNull();
                return null;
            }
            case JsonToken.STRING -> {
                try {
                    return LocalDateTime.parse(jsonReader.nextString(), Managers.DATE_TIME_FORMATTER);
                } catch (DateTimeParseException exception) {
                    return null;
                }
            }
            default -> {
                return null;
            }
        }
    }
}
