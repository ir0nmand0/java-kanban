package gson;

import com.google.gson.*;
import model.Epic;

import java.lang.reflect.Type;
import java.util.Objects;

public class EpicInSubtaskSerializer implements JsonSerializer<Epic> {
    @Override
    public JsonElement serialize(Epic epic, Type type, JsonSerializationContext jsonSerializationContext) {
        if (Objects.isNull(epic)) {
            return null;
        }

        return new JsonPrimitive(epic.getId());
    }
}
