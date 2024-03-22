package http.handler;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import gson.*;
import manager.Managers;
import model.Task;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static http.HttpTaskServer.writeResponse;
import static manager.Managers.FILE_BACKED_TASK_MANAGER;
import static manager.Managers.TASK_MANAGER;
import static http.HttpTaskServer.gsonTask;

public class TasksHandler implements HttpHandler {
    private enum Endpoint {
        GET_TASKS, GET_TASK, ADD_TASK, UPDATE_TASK, DELETE, UNKNOWN
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] requestPath = exchange.getRequestURI().getPath().split("/");
        Endpoint endpoint = getEndpoint(requestPath, exchange.getRequestMethod());
        int id = -1;
        JsonElement jsonElement = JsonNull.INSTANCE;
        JsonObject jsonObject = null;

        if (endpoint.equals(Endpoint.GET_TASK) || endpoint.equals(Endpoint.DELETE)) {
            try {
                id = Integer.parseInt(requestPath[2]);

                if (id <= 0) {
                    endpoint = Endpoint.UNKNOWN;
                }

            } catch (NumberFormatException ignored) {
                endpoint = Endpoint.UNKNOWN;
            }
        }

        if (endpoint.equals(Endpoint.ADD_TASK) || endpoint.equals(Endpoint.UPDATE_TASK)) {

            try {
                jsonElement = JsonParser.parseString(new String(exchange.getRequestBody().readAllBytes(),
                        Managers.DEFAULT_CHARSET));
            } catch (JsonSyntaxException e) {
            }

            if (Objects.isNull(jsonElement) || jsonElement.isJsonNull() || !jsonElement.isJsonObject()) {
                writeResponse(exchange,"", 406);
                return;
            }

            jsonObject = jsonElement.getAsJsonObject();

            if (jsonObject.isEmpty()) {
                writeResponse(exchange,"", 406);
                return;
            }

            if (jsonObject.has("id")) {
                endpoint = Endpoint.UPDATE_TASK;
            }
        }

        switch (endpoint) {
            case GET_TASKS -> {
                List<Task> tasks = FILE_BACKED_TASK_MANAGER.getTasks();

                if (tasks.isEmpty()) {
                    writeResponse(exchange, "", 404);
                    return;
                }

                writeResponse(exchange, gsonTask.toJson(tasks, new ListTaskToken().getType()), 200);
            }
            case GET_TASK -> {

                Optional<Task> task = FILE_BACKED_TASK_MANAGER.getTask(id);

                if (task.isEmpty()) {
                    writeResponse(exchange, "", 404);
                    return;
                }

                writeResponse(exchange, gsonTask.toJson(task.get()), 200);
            }
            case ADD_TASK -> {

                Task task = gsonTask.fromJson(jsonElement, Task.class);

                if (Objects.isNull(task)) {
                    writeResponse(exchange, "", 400);
                    return;
                }

                if (TASK_MANAGER.timeIsConflict(task)) {
                    writeResponse(exchange, "", 406);
                    return;
                }

                FILE_BACKED_TASK_MANAGER.addTask(task);

                writeResponse(exchange, "", 201);
            }

            case UPDATE_TASK -> {
                
                JsonElement jsonId = jsonObject.get("id");
                Optional<Task> oldTask = Optional.empty();

                if (!jsonId.isJsonNull()) {
                    try {
                        oldTask = TASK_MANAGER.getTaskWithoutHistory(jsonId.getAsInt());

                        if (oldTask.isEmpty()) {
                            writeResponse(exchange, "", 404);
                            return;
                        }

                    } catch (IllegalStateException | NumberFormatException exception) {

                    }
                }

                Task task = gsonTask.fromJson(jsonElement, Task.class);
                
                if (Objects.isNull(task)) {
                    writeResponse(exchange, "", 400);
                    return;
                }

                if (TASK_MANAGER.timeIsConflict(task)) {
                    writeResponse(exchange, "", 406);
                    return;
                }

                if (FILE_BACKED_TASK_MANAGER.updateTask(oldTask.get(), task)) {
                    writeResponse(exchange, "", 201);
                    return;
                }

                writeResponse(exchange, "", 506);
            }

            case DELETE -> {
                if (FILE_BACKED_TASK_MANAGER.containsKeyInTasks(id)) {
                    FILE_BACKED_TASK_MANAGER.removeTask(id);
                    writeResponse(exchange, "", 200);
                    return;
                }

                writeResponse(exchange, "", 404);
            }

            default -> writeResponse(exchange, "", 404);
        }

    }

    private Endpoint getEndpoint(String[] requestPath, String requestMethod) {

        if (requestPath.length == 2 && requestPath[1].equals("tasks")) {
            switch (requestMethod) {
                case "GET" -> {
                    return Endpoint.GET_TASKS;
                }
                case "POST" -> {
                    return Endpoint.ADD_TASK;
                }
            }

        }

        if (requestPath.length == 3 && requestPath[1].equals("tasks")) {
            switch (requestMethod) {
                case "GET" -> {
                    return Endpoint.GET_TASK;
                }
                case "DELETE" -> {
                    return Endpoint.DELETE;
                }
            }
        }

        return Endpoint.UNKNOWN;
    }
}
