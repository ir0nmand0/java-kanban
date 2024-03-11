package test;

import manager.FileBackedTaskManager;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {

    private static TaskManager fileBackedTaskManager;
    private static TaskManager taskManager;
    private static HistoryManager historyManager;
    private static Task task1;
    private static Task task2;
    private static Task task3;
    private static Task task4;
    private static Epic epic1;
    private static List<Task> allHistory;
    private static Subtask subtask1;
    private static Subtask subtask2;
    private static Subtask subtask3;
    private static Subtask subtask4;

    @BeforeAll
    public static void firstInit() {
        historyManager = Managers.getHistoryManager();
        taskManager = Managers.getTaskManager();
        fileBackedTaskManager = Managers.getFileBackedTaskManager();
        task1 = new Task("Task1 TaskTest", "TaskTest1 description", Status.NEW);
        task2 = new Task("Task2 TaskTest", "TaskTest2 description", Status.IN_PROGRESS);
        task3 = new Task("Task3 TaskTest", "TaskTest3 description", Status.NEW,
                LocalDateTime.now(), Duration.ofHours(1));
        task4 = new Task("Task4 TaskTest", "TaskTest4 description", Status.IN_PROGRESS,
                LocalDateTime.now().minusHours(10), Duration.ofHours(3));
        allHistory = new ArrayList<>();
        epic1 = new Epic("Epic EpicTest1", "EpicTest1 description");
        subtask1 = new Subtask(
                "Subtask Subtask1",
                "Subtask1 description",
                Status.NEW,
                epic1
        );
        subtask2 = new Subtask(
                "Subtask Subtask2",
                "Subtask2 description",
                Status.IN_PROGRESS,
                epic1
        );
        subtask3 = new Subtask(
                "Subtask Subtask3",
                "Subtask3 description",
                Status.DONE,
                LocalDateTime.now().plusHours(3),
                Duration.ofHours(1),
                epic1
        );
        subtask4 = new Subtask(
                "Subtask Subtask4",
                "Subtask4 description",
                Status.IN_PROGRESS,
                LocalDateTime.now().plusHours(6),
                Duration.ofHours(3),
                epic1
        );

        fileBackedTaskManager.addTask(task1);
        allHistory.add(task1);
        fileBackedTaskManager.addTask(task2);
        allHistory.add(task2);
        fileBackedTaskManager.addTask(task3);
        allHistory.add(task3);
        fileBackedTaskManager.addTask(task4);
        allHistory.add(task4);
        fileBackedTaskManager.addEpic(epic1);
        allHistory.add(epic1);
        fileBackedTaskManager.addSubtask(epic1, subtask1);
        allHistory.add(subtask1);
        fileBackedTaskManager.addSubtask(epic1, subtask2);
        allHistory.add(subtask2);
        fileBackedTaskManager.addSubtask(epic1, subtask3);
        allHistory.add(subtask3);
        fileBackedTaskManager.addSubtask(epic1, subtask4);
        allHistory.add(subtask4);
        fileBackedTaskManager.getTask(task1.getId());
        fileBackedTaskManager.getTask(task2.getId());
        fileBackedTaskManager.getEpic(epic1.getId());
        fileBackedTaskManager.getSubtask(subtask1.getId());
        fileBackedTaskManager.getSubtask(subtask2.getId());
        fileBackedTaskManager.getTask(task3.getId());
        fileBackedTaskManager.getTask(task4.getId());
        fileBackedTaskManager.getSubtask(subtask3.getId());
        fileBackedTaskManager.getSubtask(subtask4.getId());
    }

    @Test
    public void conflictTimeInTask() {
        Task task = new Task("Task1 TaskTest", "TaskTest1 description", Status.NEW,
                task3.getStartTime().get().plusMinutes(1), Duration.ofHours(5));
        fileBackedTaskManager.addTask(task);

        assertTrue(fileBackedTaskManager.getTasks().stream()
                .filter(e -> e.getId() == task.getId()).findAny().isEmpty(),
                "Задача с конфликтом времени добавлена");

        Subtask subtask = new Subtask(
                "Subtask Subtask3",
                "Subtask3 description",
                Status.DONE,
                subtask3.getStartTime().get(),
                Duration.ofHours(3),
                epic1);

        fileBackedTaskManager.addSubtask(epic1, subtask);

        assertTrue(fileBackedTaskManager.getSubtasks().stream()
                        .filter(e -> e.getId() == subtask.getId()).findAny().isEmpty(),
                "Подзадача с конфликтом времени добавлена");
    }

    @Test
    public void getPrioritizedTasks() {

        assertEquals(fileBackedTaskManager.getPrioritizedTasks().getFirst().toString(), task4.toString(),
                "Таски не отсортированы по времени");
    }

    @Test
    public void loadFromFile() {
        taskManager.clearTasks();
        taskManager.clearEpics();
        historyManager.clear();

        assertEquals(taskManager.getTasks().size(), 0);
        assertEquals(taskManager.getEpics().size(), 0);
        assertEquals(taskManager.getSubtasks().size(),0);
        assertEquals(historyManager.getHistory().size(), 0);

        fileBackedTaskManager.loadTask();
        taskManager.getEpics();
        taskManager.getSubtasks();

        assertEquals(taskManager.getTasks().size() + taskManager.getEpics().size()
                + taskManager.getSubtasks().size(), allHistory.size());

        fileBackedTaskManager.loadHistory();

        assertEquals(historyManager.getHistory().size(), allHistory.size());
    }

    @AfterAll
    public static void removeAllFile() throws IOException {

        if (!(fileBackedTaskManager instanceof FileBackedTaskManager backedTaskManager)) {
            return;
        }

        Path task = Paths.get(backedTaskManager.getNameBackedTaskManager());
        Path history = Paths.get(backedTaskManager.getNameHistoryManager());

        if (!Files.isDirectory(task)) {
            Files.deleteIfExists(task);
        }

        if (!Files.isDirectory(history)) {
            Files.deleteIfExists(history);
        }
    }


}