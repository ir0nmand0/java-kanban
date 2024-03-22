package http.handler;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import gson.*;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static http.HttpTaskServer.*;

public class EpicsHandler implements HttpHandler {
    private final TaskManager fileTaskManager = Managers.getFileTaskManager();
    private enum Endpoint {
        GET_EPICS, GET_EPIC, GET_SUBTASKS_BY_EPIC, ADD_EPIC, DELETE, UNKNOWN
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String[] requestPath = exchange.getRequestURI().getPath().split("/");
        Endpoint endpoint = getEndpoint(requestPath, exchange.getRequestMethod());
        int id = -1;

        if (endpoint.equals(Endpoint.GET_EPIC) || endpoint.equals(Endpoint.GET_SUBTASKS_BY_EPIC)
                || endpoint.equals(Endpoint.DELETE)) {
            try {
                id = Integer.parseInt(requestPath[2]);

                if (id <= 0) {
                    endpoint = Endpoint.UNKNOWN;
                }

            } catch (NumberFormatException ignored) {
                endpoint = Endpoint.UNKNOWN;
            }
        }

        switch (endpoint) {
            case GET_EPICS -> {
                List<Epic> epics = fileTaskManager.getEpics();

                if (epics.isEmpty()) {
                    writeResponse(exchange, "", 404);
                    return;
                }

                writeResponse(exchange, gsonEpic.toJson(epics, new ListTaskToken().getType()), 200);
            }
            case GET_EPIC -> {

                Optional<Epic> epic = fileTaskManager.getEpic(id);

                if (epic.isEmpty()) {
                    writeResponse(exchange, "", 404);
                    return;
                }

                writeResponse(exchange, gsonEpic.toJson(epic.get()), 200);
            }
            case GET_SUBTASKS_BY_EPIC -> {

                Optional<Epic> epic = fileTaskManager.getEpicWithoutHistory(id);

                if (epic.isEmpty()) {
                    writeResponse(exchange, "", 404);
                    return;
                }

                List<Subtask> subtasks = fileTaskManager.getSubtasks(epic.get());

                if (subtasks.isEmpty()) {
                    writeResponse(exchange, String.format("", id),
                            404);
                    return;
                }

                writeResponse(exchange, gsonSubtask.toJson(subtasks,  new ListTaskToken().getType()), 200);
            }

            case ADD_EPIC -> {

                JsonElement jsonElement = null;

                try {
                    jsonElement = JsonParser.parseString(new String(exchange.getRequestBody().readAllBytes(),
                            Managers.DEFAULT_CHARSET));
                } catch (JsonSyntaxException e) {
                    throw new RuntimeException("Не удалось распарсить данные", e);
                }

                if (Objects.isNull(jsonElement) || jsonElement.isJsonNull() || !jsonElement.isJsonObject()) {
                    writeResponse(exchange,"", 406);
                    return;
                }

                Epic epic = gsonEpic.fromJson(jsonElement, Epic.class);

                if (Objects.isNull(epic)) {
                    writeResponse(exchange,"", 400);
                }

                fileTaskManager.addEpic(epic);
                writeResponse(exchange, "", 201);
            }

            case DELETE -> {
                if (fileTaskManager.containsKeyInEpics(id)) {
                    fileTaskManager.removeEpic(id);
                    writeResponse(exchange, "", 200);
                    return;
                }

                writeResponse(exchange, "", 404);
            }

            default -> writeResponse(exchange, "", 404);
        }

    }

    private Endpoint getEndpoint(String[] requestPath, String requestMethod) {

        if (requestPath.length == 2 && requestPath[1].equals("epics")) {
            switch (requestMethod) {
                case "GET" -> {
                    return Endpoint.GET_EPICS;
                }
                case "POST" -> {
                    return Endpoint.ADD_EPIC;
                }
            }
        }

        if (requestPath.length == 3 && requestPath[1].equals("epics")) {
            switch (requestMethod) {
                case "GET" -> {
                    return Endpoint.GET_EPIC;
                }
                case "DELETE" -> {
                    return Endpoint.DELETE;
                }
            }
        }

        if (requestPath.length == 4 && requestPath[1].equals("epics")
                && requestPath[3].equals("subtasks")
                && requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASKS_BY_EPIC;
        }

        return Endpoint.UNKNOWN;
    }
}
