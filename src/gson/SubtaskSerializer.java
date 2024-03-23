package gson;

import com.google.gson.*;
import model.Subtask;

import java.lang.reflect.Type;
import java.util.Objects;

import static http.HttpTaskServer.gsonSubtask;

public class SubtaskSerializer implements JsonSerializer<Subtask> {
    @Override
    public JsonElement serialize(Subtask subtask, Type type, JsonSerializationContext jsonSerializationContext) {
        if (Objects.isNull(subtask)) {
            return null;
        }

        try {
            return JsonParser.parseString(gsonSubtask.toJson(subtask)
            );
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Не удалось распарсить данные", e);
        }

    }
}
