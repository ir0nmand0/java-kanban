package gson;

import com.google.gson.*;
import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static http.HttpTaskServer.gsonDuration;
import static http.HttpTaskServer.gsonLocalDateTime;

public class SubtaskDeserializer implements JsonDeserializer<Subtask> {
    private final TaskManager fileTaskManager = Managers.getFileTaskManager();
    private final TaskManager taskManager = Managers.getTaskManager();
    
    @Override
    public Subtask deserialize(JsonElement jsonElement,
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
        JsonElement jsonEpicId = jsonObject.has("epicId") ? jsonObject.get("epicId") : null;

        if (jsonName.isJsonNull()
                || jsonDescription.isJsonNull()
                || jsonStatus.isJsonNull()) {
            return null;
        }

        Status status = fileTaskManager.getStatus(jsonStatus.getAsString());

        Optional<Epic> epic = Optional.empty();
        Optional<Subtask> oldSubtask = Optional.empty();

        if (Objects.nonNull(jsonEpicId) && !jsonEpicId.isJsonNull()) {
            try {
                epic = taskManager.getEpicWithoutHistory(jsonEpicId.getAsInt());
            } catch (IllegalStateException | NumberFormatException exception) {
                epic = Optional.empty();
            }
        }

        JsonElement jsonId = jsonObject.get("id");

        if (Objects.nonNull(jsonId) && !jsonId.isJsonNull()) {
            try {
                oldSubtask = taskManager.getSubtaskWithoutHistory(jsonId.getAsInt());

            } catch (IllegalStateException | NumberFormatException exception) {
                oldSubtask = Optional.empty();
            }
        }

        //Если не передан epicId, тогда извлекаем из заменяемого сабтаска
        if (epic.isEmpty() && oldSubtask.isPresent()) {
            epic = Optional.ofNullable(oldSubtask.get().getEpic());
        }

        if (epic.isEmpty()) {
            return null;
        }

        JsonElement jsonDuration = jsonObject.get("duration");
        JsonElement jsonStartTime = jsonObject.get("startTime");

        if (jsonDuration.isJsonNull() || jsonStartTime.isJsonNull()) {
            return new Subtask(jsonName.getAsString(), jsonDescription.getAsString(), status, epic.get());
        }

        Duration duration = gsonDuration.fromJson(jsonDuration, Duration.class);

        LocalDateTime startTime = gsonLocalDateTime.fromJson(jsonStartTime, LocalDateTime.class);

        if (Objects.isNull(duration) || Objects.isNull(startTime)) {
            return new Subtask(jsonName.getAsString(), jsonDescription.getAsString(), status, epic.get());
        }

        return new Subtask(jsonName.getAsString(), jsonDescription.getAsString(),
                status, startTime, duration, epic.get());
    }
}
