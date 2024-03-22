package gson;

import com.google.gson.*;
import model.Epic;

import java.lang.reflect.Type;

public class EpicDeserializer implements JsonDeserializer<Epic> {
    @Override
    public Epic deserialize(JsonElement jsonElement,
                            Type type,
                            JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement.isJsonNull() || !jsonElement.isJsonObject()) {
            return null;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (jsonObject.isEmpty()) {
            return null;
        }

        JsonElement jsonName = jsonObject.get("name");
        JsonElement jsonDescription = jsonObject.get("description");

        if (jsonName.isJsonNull() || jsonDescription.isJsonNull()) {
            return null;
        }

        return new Epic(jsonName.getAsString(), jsonDescription.getAsString());
    }
}
