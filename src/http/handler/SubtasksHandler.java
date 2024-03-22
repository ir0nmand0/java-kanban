package http.handler;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import gson.*;
import manager.Managers;
import model.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static http.HttpTaskServer.writeResponse;
import static manager.Managers.FILE_BACKED_TASK_MANAGER;
import static manager.Managers.TASK_MANAGER;
import static http.HttpTaskServer.gsonSubtask;

public class SubtasksHandler implements HttpHandler {
    private enum Endpoint {
        GET_SUBTASKS, GET_SUBTASK, ADD_SUBTASK, DELETE_SUBTASK, UPDATE_SUBTASK, UNKNOWN
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String[] requestPath = exchange.getRequestURI().getPath().split("/");
        Endpoint endpoint = getEndpoint(requestPath, exchange.getRequestMethod());
        int id = -1;
        JsonElement jsonElement = JsonNull.INSTANCE;
        JsonObject jsonObject = null;

        if (endpoint.equals(Endpoint.GET_SUBTASK) || endpoint.equals(Endpoint.DELETE_SUBTASK)) {
            try {
                id = Integer.parseInt(requestPath[2]);

                if (id <= 0) {
                    endpoint = Endpoint.UNKNOWN;
                }

            } catch (NumberFormatException ignored) {
                endpoint = Endpoint.UNKNOWN;
            }
        }

        if (endpoint.equals(Endpoint.ADD_SUBTASK) || endpoint.equals(Endpoint.UPDATE_SUBTASK)) {

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
                endpoint = Endpoint.UPDATE_SUBTASK;
            }
        }

        switch (endpoint) {
            case GET_SUBTASKS -> {
                List<Subtask> subtasks = FILE_BACKED_TASK_MANAGER.getSubtasks();

                if (subtasks.isEmpty()) {
                    writeResponse(exchange, "", 404);
                    return;
                }

                writeResponse(exchange, gsonSubtask.toJson(subtasks, new ListTaskToken().getType()), 200);
            }
            case GET_SUBTASK -> {

                Optional<Subtask> subtask = FILE_BACKED_TASK_MANAGER.getSubtask(id);

                if (subtask.isEmpty()) {
                    writeResponse(exchange, "", 404);
                    return;
                }

                writeResponse(exchange, gsonSubtask.toJson(subtask.get()), 200);
            }
            case ADD_SUBTASK -> {

                Subtask subtask = gsonSubtask.fromJson(jsonElement, Subtask.class);

                if (Objects.isNull(subtask)) {
                    writeResponse(exchange, "", 400);
                    return;
                }

                if (TASK_MANAGER.timeIsConflict(subtask)) {
                    writeResponse(exchange, "", 406);
                    return;
                }

                FILE_BACKED_TASK_MANAGER.addSubtask(subtask.getEpic(), subtask);

                writeResponse(exchange, "", 200);
            }

            case UPDATE_SUBTASK -> {

                if (Objects.isNull(jsonObject)) {
                    writeResponse(exchange, "", 404);
                    return;
                }

                JsonElement jsonId = jsonObject.get("id");
                Optional<Subtask> oldSubtask = Optional.empty();

                if (!jsonId.isJsonNull()) {
                    try {
                        oldSubtask = TASK_MANAGER.getSubtaskWithoutHistory(jsonId.getAsInt());

                        if (oldSubtask.isEmpty()) {
                            writeResponse(exchange, "", 404);
                            return;
                        }

                    } catch (IllegalStateException | NumberFormatException exception) {

                    }
                }

                Subtask subtask = gsonSubtask.fromJson(jsonElement, Subtask.class);

                if (Objects.isNull(subtask)) {
                    writeResponse(exchange, "", 400);
                    return;
                }

                if (TASK_MANAGER.timeIsConflict(subtask)) {
                    writeResponse(exchange, "", 406);
                    return;
                }

                if (FILE_BACKED_TASK_MANAGER.updateSubtask(subtask.getEpic(), oldSubtask.get(), subtask)) {
                    writeResponse(exchange, "", 201);
                    return;
                }

                writeResponse(exchange, "", 506);
            }

            case DELETE_SUBTASK -> {
                if (FILE_BACKED_TASK_MANAGER.containsKeyInSubtasks(id)) {
                    FILE_BACKED_TASK_MANAGER.removeSubtask(id);
                    writeResponse(exchange, "", 200);
                    return;
                }

                writeResponse(exchange, "", 404);
            }

            default -> writeResponse(exchange, "", 404);
        }

    }

    private Endpoint getEndpoint(String[] requestPath, String requestMethod) {

        if (requestPath.length == 2 && requestPath[1].equals("subtasks")) {
            switch (requestMethod) {
                case "GET" -> {
                    return Endpoint.GET_SUBTASKS;
                }
                case "POST" -> {
                    return Endpoint.ADD_SUBTASK;
                }
            }

        }

        if (requestPath.length == 3 && requestPath[1].equals("subtasks")) {
            switch (requestMethod) {
                case "GET" -> {
                    return Endpoint.GET_SUBTASK;
                }
                case "DELETE" -> {
                    return Endpoint.DELETE_SUBTASK;
                }
            }
        }

        return Endpoint.UNKNOWN;
    }
}
