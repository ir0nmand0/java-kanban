package gson;

import com.google.gson.*;
import manager.Managers;
import manager.TaskManager;
import model.Status;
import model.Task;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static http.HttpTaskServer.gsonDuration;
import static http.HttpTaskServer.gsonLocalDateTime;

public class TaskDeserializer implements JsonDeserializer<Task> {
    private final TaskManager fileTaskManager = Managers.getFileTaskManager();
    
    @Override
    public Task deserialize(JsonElement jsonElement,
                            Type type, JsonDeserializationContext context) throws JsonParseException {

        if (jsonElement.isJsonNull() || !jsonElement.isJsonObject()) {
            return null;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (jsonObject.isEmpty()) {
            return null;
        }

        JsonElement jsonName = jsonObject.get("name");
        JsonElement jsonDescription = jsonObject.get("description");
        JsonElement jsonStatus = jsonObject.get("status");

        if (jsonName.isJsonNull()
                || jsonDescription.isJsonNull()
                || jsonStatus.isJsonNull()) {
            return null;
        }

        Status status = fileTaskManager.getStatus(jsonStatus.getAsString());

        JsonElement jsonDuration = jsonObject.get("duration");
        JsonElement jsonStartTime = jsonObject.get("startTime");

        if (jsonDuration.isJsonNull() || jsonStartTime.isJsonNull()) {
            return new Task(jsonName.getAsString(), jsonDescription.getAsString(), status);
        }

        Duration duration = gsonDuration.fromJson(jsonDuration, Duration.class);

        LocalDateTime startTime = gsonLocalDateTime.fromJson(jsonStartTime, LocalDateTime.class);

        if (Objects.isNull(duration) || Objects.isNull(startTime)) {
            return new Task(jsonName.getAsString(), jsonDescription.getAsString(), status);
        }

        return new Task(jsonName.getAsString(), jsonDescription.getAsString(), status, startTime, duration);
    }
}
