package test;

import com.google.gson.*;
import gson.*;
import http.HttpTaskServer;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;

import static manager.Managers.FILE_BACKED_TASK_MANAGER;
import static http.HttpTaskServer.*;
import static manager.Managers.HISTORY_MANAGER;
import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest extends TasksEpicsSubtasks {

    private static HttpTaskServer httpTaskServer = new HttpTaskServer();
    private static HttpClient client = HttpClient.newHttpClient();
    private final String httpHostWithPort = "http://localhost:8080/";
    private final String urlTasks = httpHostWithPort + "tasks";
    private final String urlTaskIdWithoutTime = urlTasks + "/" + task1.getId();
    private final String urlTaskIdWithTime = urlTasks + "/" + task3.getId();
    private final String urlTaskWithFakeId = urlTasks + "/" + Integer.MAX_VALUE;
    private final String urlEpics = httpHostWithPort + "epics";
    private final String urlEpicId = urlEpics + "/" + epicWithoutSubtask.getId();
    private final String urlEpicWithFakeId = urlEpics + "/" + Integer.MIN_VALUE;
    private final int idEpic1 = epic1.getId();
    private final String urlSubtasksInEpicId = urlEpics + "/" + idEpic1 + "/subtasks";
    private final String urlSubtasksInEpicWithFakeId = urlEpics + "/" + Integer.MAX_VALUE + "/subtasks";
    private final String urlSubtasks = httpHostWithPort + "subtasks";
    private final String urlSubtaskIdWithoutTime = urlSubtasks + "/" + subtask1.getId();
    private final String urlSubtaskIdWithTime = urlSubtasks + "/" + subtask3.getId();
    private final String urlTaskPrioritized = httpHostWithPort + "prioritized";
    private final String urlTaskHistory = httpHostWithPort + "history";
    private final String taskWithoutIdAndWithConflictTime = "\t{\n" +
            "\t\t\"name\": \"Task withoutId\",\n" +
            "\t\t\"description\": \"Task withoutIdAndConflictTime description\",\n" +
            "\t\t\"status\": \"IN_PROGRESS\",\n" +
            "\t\t\"duration\": 3600,\n" +
            "\t\t\"startTime\": \"04.10.1957, 22:29:34\"\n" +
            "\t}";

    private final String subtaskWithoutIdAndWithConflictTime = "\t{\n" +
            "\t\t\"epicId\": " + idEpic1 + ",\n" +
            "\t\t\"name\": \"Subtask withoutId\",\n" +
            "\t\t\"description\": \"Subtask withoutIdAndConflictTime description\",\n" +
            "\t\t\"status\": \"IN_PROGRESS\",\n" +
            "\t\t\"duration\": 3600,\n" +
            "\t\t\"startTime\": \"04.10.1957, 22:39:34\"\n" +
            "\t}";

    private final String taskWithoutIdAndWithTime = "\t{\n" +
            "\t\t\"name\": \"Task withTime\",\n" +
            "\t\t\"description\": \"Task withoutIdAndWithTime description\",\n" +
            "\t\t\"status\": \"IN_PROGRESS\",\n" +
            "\t\t\"duration\": 3600,\n" +
            "\t\t\"startTime\": \"04.10.1997, 22:29:34\"\n" +
            "\t}";

    private final String subtaskWithoutIdAndWithTime = "\t{\n" +
            "\t\t\"epicId\": " + idEpic1 + ",\n" +
            "\t\t\"name\": \"subtask withTime\",\n" +
            "\t\t\"description\": \"subtask withoutIdAndWithTime description\",\n" +
            "\t\t\"status\": \"IN_PROGRESS\",\n" +
            "\t\t\"duration\": 3600,\n" +
            "\t\t\"startTime\": \"04.10.1945, 22:29:34\"\n" +
            "\t}";

    private final String taskWithoutIdAndTime = "\t{\n" +
            "\t\t\"name\": \"Task withoutTime\",\n" +
            "\t\t\"description\": \"Task withoutTime description\",\n" +
            "\t\t\"status\": \"IN_PROGRESS\",\n" +
            "\t\t\"duration\": null,\n" +
            "\t\t\"startTime\": null\n" +
            "\t}";

    private final String subtaskWithoutIdAndTime = "\t{\n" +
            "\t\t\"epicId\": " + idEpic1 + ",\n" +
            "\t\t\"name\": \"subtask withoutTime\",\n" +
            "\t\t\"description\": \"subtask withoutTime description\",\n" +
            "\t\t\"status\": \"IN_PROGRESS\",\n" +
            "\t\t\"duration\": null,\n" +
            "\t\t\"startTime\": null\n" +
            "\t}";

    private final String taskWithIdAndTimeConflict = "{\n" +
            "  \"name\": \"Task3 conflict\",\n" +
            "  \"description\": \"TaskTest3 conflict time\",\n" +
            "  \"id\": "+ task3.getId() +",\n" +
            "  \"status\": \"NEW\",\n" +
            "  \"duration\": 18000,\n" +
            "  \"startTime\": \"04.10.1957, 22:29:34\"\n" +
            "}";

    private final String subtaskWithIdAndTimeConflict = "{\n" +
            "\t\t\"epicId\": " + idEpic1 + ",\n" +
            "  \"name\": \"subtask conflict\",\n" +
            "  \"description\": \"subtask conflict time\",\n" +
            "  \"id\": "+ subtask3.getId() +",\n" +
            "  \"status\": \"NEW\",\n" +
            "  \"duration\": 18000,\n" +
            "  \"startTime\": \"05.10.1957, 22:29:34\"\n" +
            "}";

    private final String taskWithFakeId = "{\n" +
            "  \"name\": \"Task fakeId\",\n" +
            "  \"description\": \"TaskTest fakeId\",\n" +
            "  \"id\": "+ Integer.MAX_VALUE +",\n" +
            "  \"status\": \"NEW\",\n" +
            "  \"duration\": 18000,\n" +
            "  \"startTime\": \"04.10.2007, 22:29:34\"\n" +
            "}";

    private final String subtaskWithFakeId = "{\n" +
            "\t\t\"epicId\": " + idEpic1 + ",\n" +
            "  \"name\": \"subtask fakeId\",\n" +
            "  \"description\": \"subtask fakeId\",\n" +
            "  \"id\": "+ Integer.MAX_VALUE +",\n" +
            "  \"status\": \"NEW\",\n" +
            "  \"duration\": 18000,\n" +
            "  \"startTime\": \"04.10.2007, 22:29:34\"\n" +
            "}";
    private final String taskWithIdAndTime = "{\n" +
            "  \"name\": \"Task taskUpdate\",\n" +
            "  \"description\": \"TaskTest update\",\n" +
            "  \"id\": "+ task3.getId() +",\n" +
            "  \"status\": \"NEW\",\n" +
            "  \"duration\": 18000,\n" +
            "  \"startTime\": \"04.10.2007, 22:29:34\"\n" +
            "}";
    private final String subtaskWithIdAndTime = "{\n" +
            "\t\t\"epicId\": " + idEpic1 + ",\n" +
            "  \"name\": \"Task taskUpdate\",\n" +
            "  \"description\": \"TaskTest update\",\n" +
            "  \"id\": "+ subtask3.getId() +",\n" +
            "  \"status\": \"NEW\",\n" +
            "  \"duration\": 18000,\n" +
            "  \"startTime\": \"04.10.2017, 22:29:34\"\n" +
            "}";

    private void addTasks() {
        FILE_BACKED_TASK_MANAGER.clearTasks();
        FILE_BACKED_TASK_MANAGER.clearEpics();
        FILE_BACKED_TASK_MANAGER.addTask(task1);
        FILE_BACKED_TASK_MANAGER.addTask(task2);
        FILE_BACKED_TASK_MANAGER.addTask(task3);
        FILE_BACKED_TASK_MANAGER.addTask(task4);
        FILE_BACKED_TASK_MANAGER.addEpic(epic1);
        FILE_BACKED_TASK_MANAGER.addEpic(epicWithoutSubtask);
        FILE_BACKED_TASK_MANAGER.addSubtask(epic1, subtask1);
        FILE_BACKED_TASK_MANAGER.addSubtask(epic1, subtask2);
        FILE_BACKED_TASK_MANAGER.addSubtask(epic1, subtask3);
        FILE_BACKED_TASK_MANAGER.addSubtask(epic1, subtask4);
        FILE_BACKED_TASK_MANAGER.getTask(task1.getId());
        FILE_BACKED_TASK_MANAGER.getTask(task2.getId());
        FILE_BACKED_TASK_MANAGER.getTask(task3.getId());
        FILE_BACKED_TASK_MANAGER.getTask(task4.getId());
        FILE_BACKED_TASK_MANAGER.getEpic(epic1.getId());
        FILE_BACKED_TASK_MANAGER.getSubtask(subtask1.getId());
        FILE_BACKED_TASK_MANAGER.getSubtask(subtask2.getId());
        FILE_BACKED_TASK_MANAGER.getSubtask(subtask3.getId());
        FILE_BACKED_TASK_MANAGER.getSubtask(subtask4.getId());
    }

    @BeforeEach
    public void startHttpServerAndAddTasks() throws IOException {
        httpTaskServer.startHttpServer();
        addTasks();
    }

    @AfterEach
    public void stopHttpServer() {
        httpTaskServer.stopHttpServer();
    }

    @Test
    public void getTasks() {
        JsonElement jsonElement = httpClientGetJson(urlTasks, 200);

        assertNotNull(jsonElement, "jsonElement не получен");

        List<Task> tasks = gsonTask.fromJson(jsonElement, new ListTaskToken().getType());

        assertEquals(tasks.size(), FILE_BACKED_TASK_MANAGER.getTasks().size(),
                "Размер массива полученного и Json не равен размеру полученного из менеджера");
    }

    @Test
    public void getEpics() {
        JsonElement jsonElement = httpClientGetJson(urlEpics, 200);

        assertNotNull(jsonElement, "jsonElement не получен");

        List<Epic> epics = gsonEpic.fromJson(jsonElement, new ListTaskToken().getType());

        assertEquals(epics.size(), FILE_BACKED_TASK_MANAGER.getEpics().size(),
                "Размер массива полученного и Json не равен размеру полученного из менеджера");
    }

    @Test
    public void getTaskWithoutTime() {
        JsonElement jsonElement = httpClientGetJson(urlTaskIdWithoutTime, 200);

        assertNotNull(jsonElement, "jsonElement не получен");

        Task task = gsonTask.fromJson(jsonElement, Task.class);

        assertTrue(task.equalsWithoutId(task1), "Задачи не без времени равны");
    }

    @Test
    public void getTaskWithTime() {
        JsonElement jsonElement = httpClientGetJson(urlTaskIdWithTime, 200);

        assertNotNull(jsonElement, "jsonElement не получен");

        Task task = gsonTask.fromJson(jsonElement, Task.class);

        assertTrue(task.equalsWithoutId(task3), "Задачи cо временем не равны");
    }

    @Test
    public void getEpic() {
        JsonElement jsonElement = httpClientGetJson(urlEpicId, 200);

        assertNotNull(jsonElement, "jsonElement не получен");

        Epic epic = gsonEpic.fromJson(jsonElement, Epic.class);

        assertTrue(epic.equalsWithoutId(epicWithoutSubtask), "Эпики не равны");
    }

    @Test
    public void nonGetTaskWithFakeId() {
        JsonElement jsonElement = httpClientGetJson(urlTaskWithFakeId, 404);

        assertNull(jsonElement, "получен jsonElement не существующей задачи");
    }

    @Test
    public void noneGetEpicsWithFakeId() {
        JsonElement jsonElement = httpClientGetJson(urlEpicWithFakeId, 404);

        assertNull(jsonElement, "получен jsonElement не существующего эпика");
    }

    @Test
    public void getSubtasksFromEpicsId() {
        JsonElement jsonElement = httpClientGetJson(urlEpicWithFakeId, 404);

        assertNull(jsonElement, "получен jsonElement не существующего эпика");
    }

    @Test
    public void nonGetSubtasksFromEpicWithFakeId() {
        JsonElement jsonElement = httpClientGetJson(urlSubtasksInEpicWithFakeId, 404);

        assertNull(jsonElement, "получен jsonElement не существующих подзадач");
    }

    @Test
    public void getSubtaskFromEpicId() {
        JsonElement jsonElement = httpClientGetJson(urlSubtasksInEpicId, 200);

        assertNotNull(jsonElement, "jsonElement не получен");

        List<Subtask> subtasks = gsonSubtask.fromJson(jsonElement, new ListTaskToken().getType());

        assertEquals(subtasks.size(), FILE_BACKED_TASK_MANAGER.getEpic(idEpic1).get().getSubtasks().size(),
                "Размер массива полученного и Json не равен размеру полученного из менеджера");

    }

    @Test
    public void addTaskWithoutTime() {
        assertTrue(httpClientPostJson(urlTasks, 201, taskWithoutIdAndTime), "Задача не добавлена");
    }

    @Test
    public void addSubtaskToEpicWithTime() {
        assertTrue(httpClientPostJson(urlEpics, 201, subtaskWithoutIdAndTime), "Подзадача добавлена");
    }

    @Test
    public void addTaskWithTime() {
        assertTrue(httpClientPostJson(urlTasks, 201, taskWithoutIdAndWithTime), "Задача не добавлена");
    }

    @Test
    public void addSubtaskWithTime() {
        assertTrue(httpClientPostJson(urlTasks, 201, subtaskWithoutIdAndWithTime), "Подзадача не добавлена");
    }

    @Test
    public void nonAddTaskWithConflictTime() {
        assertTrue(httpClientPostJson(urlTasks, 406, taskWithoutIdAndWithConflictTime),
                "Задача с конфликтом времени добавлена");
    }

    @Test
    public void nonAddSubtaskWithConflictTime() {
        assertTrue(httpClientPostJson(urlSubtasks, 406, subtaskWithoutIdAndWithConflictTime),
                "Подзадача с конфликтом времени добавлена");
    }

    @Test
    public void nonUpdateTaskWithConflictTime() {
        assertTrue(httpClientPostJson(urlTasks, 406, taskWithIdAndTimeConflict),
                "Задача с конфликтом времени обновлена");
    }

    @Test
    public void nonUpdateSubtaskWithConflictTime() {
        assertTrue(httpClientPostJson(urlSubtasks, 406, subtaskWithIdAndTimeConflict),
                "Подзадача с конфликтом времени обновлена");
    }

    @Test
    public void nonUpdateTaskWithFakeId() {
        assertTrue(httpClientPostJson(urlTasks, 404, taskWithFakeId),
                "Задача с несуществующим ID обновлена");
    }

    @Test
    public void nonUpdateSubtaskWithFakeId() {
        assertTrue(httpClientPostJson(urlSubtasks, 404, subtaskWithFakeId),
                "Подзадача с несуществующим ID обновлена");
    }

    @Test
    public void updateTaskWithIdAndTime() {
        assertTrue(httpClientPostJson(urlTasks, 201, taskWithIdAndTime),
                "Задача не обновлена");
    }

    @Test
    public void updateSubtaskWithIdAndTime() {
        assertTrue(httpClientPostJson(urlSubtasks, 201,subtaskWithIdAndTime),
                "Подзадача не обновлена");
    }

    @Test
    public void deleteTask() {
        assertTrue(httpClientDelete(urlTaskIdWithTime, 200));
        assertTrue(httpClientDelete(urlTaskIdWithoutTime, 200));
    }

    @Test
    public void deleteEpic() {
        assertTrue(httpClientDelete(urlEpicId, 200));
    }

    @Test
    public void deleteSubtask() {
        assertTrue(httpClientDelete(urlSubtaskIdWithoutTime, 200));
        assertTrue(httpClientDelete(urlSubtaskIdWithTime, 200));
    }

    @Test
    public void getPrioritized() {
        JsonElement jsonElement = httpClientGetJson(urlTaskPrioritized, 200);

        assertNotNull(jsonElement, "jsonElement не получен");

        List<Task> prioritized = gsonTask.fromJson(jsonElement, new ListTaskToken().getType());

        assertEquals(prioritized.size(), FILE_BACKED_TASK_MANAGER.getPrioritizedTasks().size(),
                "Размер массива полученного и Json не равен размеру полученного из менеджера");

        assertTrue(FILE_BACKED_TASK_MANAGER.getPrioritizedTasks().getFirst().equals(task3),
                "Задачи не отсортированы");
    }

    @Test
    public void getHistory() {
        JsonElement jsonElement = httpClientGetJson(urlTaskHistory, 200);

        assertNotNull(jsonElement, "jsonElement не получен");

        List<Task> history = gsonTask.fromJson(jsonElement, new ListTaskToken().getType());

        assertEquals(history.size(), HISTORY_MANAGER.getHistory().size(),
                "Размер массива полученного и Json не равен размеру полученного из менеджера");
    }

    @AfterAll
    public static void removeAllFile() throws IOException {
        FileBackedTaskManagerTest.removeAllFile();
    }

    private boolean httpClientDelete(String stringUrl, int code) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(stringUrl))
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == code;

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }

        return false;
    }

    private boolean httpClientPostJson(String stringUrl, int code, String body) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(stringUrl))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == code;

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }

        return false;
    }

    private JsonElement httpClientGetJson(String stringUrl, int code) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(stringUrl))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != code) {
                System.out.println("stringUrl = " + stringUrl);
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
                return null;
            }

            JsonElement jsonElement = null;

            try {
                jsonElement = JsonParser.parseString(response.body());
            } catch (JsonSyntaxException e) {
            }

            if (Objects.isNull(jsonElement)) {
                return null;
            }


            if (jsonElement.isJsonObject() || jsonElement.isJsonArray()) {
                return jsonElement;
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }

        return null;
    }


}