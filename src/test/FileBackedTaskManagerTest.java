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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {

    private static TaskManager fileBackedTaskManager;
    private static TaskManager taskManager;
    private static HistoryManager historyManager;
    private static Task task1;
    private static Task task2;
    private static Epic epic1;
    private static List<Task> allHistory;
    private static Subtask subtask1;
    private static Subtask subtask2;

    @BeforeAll
    public static void firstInit() {
        historyManager = Managers.getHistoryManager();
        taskManager = Managers.getTaskManager();
        fileBackedTaskManager = Managers.getFileBackedTaskManager();
        task1 = new Task("Task1 TaskTest", "TaskTest1 description", Status.NEW);
        task2 = new Task("Task2 TaskTest", "TaskTest2 description", Status.IN_PROGRESS);
        epic1 = new Epic("Epic EpicTest1", "EpicTest1 description");
        allHistory = new ArrayList<>();
        subtask1 = new Subtask(
                "Subtask Subtask1",
                "Subtask1 description",
                Status.NEW,
                epic1
        );
        subtask2 = new Subtask(
                "Subtask Subtask2",
                "Subtask2 description",
                Status.IN_PROGRESS, epic1
        );

        fileBackedTaskManager.addTask(task1);
        allHistory.add(task1);
        fileBackedTaskManager.addTask(task2);
        allHistory.add(task2);
        fileBackedTaskManager.addEpic(epic1);
        allHistory.add(epic1);
        fileBackedTaskManager.addSubtask(epic1, subtask1);
        allHistory.add(subtask1);
        fileBackedTaskManager.addSubtask(epic1, subtask2);
        allHistory.add(subtask2);
        fileBackedTaskManager.getTask(task1.getId());
        fileBackedTaskManager.getTask(task2.getId());
        fileBackedTaskManager.getEpic(epic1.getId());
        fileBackedTaskManager.getSubtask(subtask1.getId());
        fileBackedTaskManager.getSubtask(subtask2.getId());
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

        assertEquals(taskManager.getTasks().size() + taskManager.getEpics().size()
                + taskManager.getSubtasks().size(), allHistory.size());

        fileBackedTaskManager.loadHistory();

        assertEquals(historyManager.getHistory().size(), allHistory.size());

    }

    @AfterAll
    public static void removeAllFile() throws IOException {

        if (!(fileBackedTaskManager instanceof FileBackedTaskManager tmp)) {
            return;
        }

        Path task = Paths.get(tmp.getNameBackedTaskManager());
        Path history = Paths.get(tmp.getNameHistoryManager());

        if (!Files.isDirectory(task)) {
            Files.deleteIfExists(task);
        }

        if (!Files.isDirectory(history)) {
            Files.deleteIfExists(history);
        }
    }


}