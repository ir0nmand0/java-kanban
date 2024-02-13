package test;

import manager.*;
import model.*;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
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
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
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

        taskManager.setTask(task1);
        allHistory.add(task1);
        taskManager.setTask(task2);
        allHistory.add(task2);
        taskManager.setEpic(epic1);
        allHistory.add(epic1);
        taskManager.setSubtask(epic1, subtask1);
        allHistory.add(subtask1);
        taskManager.setSubtask(epic1, subtask2);
        allHistory.add(subtask2);
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
    }

    @Test
    public void getHistory() {
        final List<Task> history = taskManager.getHistory();

        assertNotEquals(history.size(), 0, "История задач не получена");
        assertEquals(history, allHistory, "Истории задач не равны");
    }

    @Test
    public void remove() {
        historyManager.remove(task1.getId());
        allHistory.remove(task1);

        final List<Task> history = taskManager.getHistory();

        assertEquals(history.size(), allHistory.size(), "Задача не удалена");
        assertEquals(history, allHistory, "Задача не удалена");
    }

}