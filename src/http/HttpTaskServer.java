package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import gson.*;
import http.handler.EpicsHandler;
import http.handler.SubtasksHandler;
import http.handler.TasksHandler;
import http.handler.ListHandler;
import manager.Managers;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class HttpTaskServer {

    public static final Gson gsonTask = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Task.class, new TaskDeserializer())
            .excludeFieldsWithModifiers(Modifier.STATIC)
            .excludeFieldsWithoutExposeAnnotation()
            .serializeNulls()
            .create();

    public static final Gson gsonEpic = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Epic.class, new EpicDeserializer())
            .excludeFieldsWithModifiers(Modifier.STATIC)
            .excludeFieldsWithoutExposeAnnotation()
            .serializeNulls()
            .create();

    public static final Gson gsonSubtask = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Epic.class, new EpicInSubtaskSerializer())
            .registerTypeAdapter(Subtask.class, new SubtaskDeserializer())
            .excludeFieldsWithModifiers(Modifier.STATIC)
            .excludeFieldsWithoutExposeAnnotation()
            .serializeNulls()
            .create();

    public static final Gson gsonDuration =  new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public static final Gson gsonLocalDateTime = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private static final int PORT = 8080;
    private static HttpServer httpServer;

    public static void main(String[] args) throws IOException {
        startHttpServer();
    }

    public static void startHttpServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.createContext("/epics", new EpicsHandler());
        httpServer.createContext("/subtasks", new SubtasksHandler());
        httpServer.createContext("/history", new ListHandler());
        httpServer.createContext("/prioritized", new ListHandler());
        httpServer.start();
    }

    public static void stopHttpServer() {
        httpServer.stop(0);
    }

    public static void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {

        if (Objects.isNull(exchange)) {
            return;
        }

        exchange.sendResponseHeaders(responseCode, 0);

        if (Objects.nonNull(responseString) && !responseString.isEmpty()) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseString.getBytes(Managers.DEFAULT_CHARSET));
            }
        }

        exchange.close();
    }
}
