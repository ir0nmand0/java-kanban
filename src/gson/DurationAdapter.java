package gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (Objects.isNull(duration)) {
            jsonWriter.nullValue();
            return;
        }

        jsonWriter.value(duration.toSeconds());
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        switch (jsonReader.peek()) {
            case JsonToken.NULL -> {
                jsonReader.nextNull();
                return null;
            }
            case JsonToken.NUMBER -> {
                try {
                    return Duration.ofSeconds(jsonReader.nextLong());
                } catch (IllegalStateException | NumberFormatException exception) {
                    return null;
                }
            }
            default -> {
                return null;
            }
        }
    }
}
