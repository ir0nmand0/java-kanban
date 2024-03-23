package http.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import gson.*;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static http.HttpTaskServer.writeResponse;

public class ListHandler implements HttpHandler {
    private final TaskManager taskManager = Managers.getTaskManager();
    private final HistoryManager historyManager = Managers.getHistoryManager();


    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
            .excludeFieldsWithModifiers(Modifier.STATIC)
            .excludeFieldsWithoutExposeAnnotation()
            .serializeNulls()
            .create();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equals("GET")) {
            writeResponse(exchange, "", 404);
            return;
        }

        String[] requestPath = exchange.getRequestURI().getPath().split("/");

        if (requestPath.length != 2) {
            writeResponse(exchange, "", 404);
            return;
        }

        List<Task> list = new ArrayList<>();

        switch (requestPath[1]) {
            case "prioritized" -> {
                list = taskManager.getPrioritizedTasks();
            }
            case "history" -> {
                list = historyManager.getHistory();
            }
        }

        if (list.isEmpty()) {
            writeResponse(exchange, "", 404);
            return;
        }

        writeResponse(exchange, gson.toJson(list, new ListTaskToken().getType()), 200);
    }
}
