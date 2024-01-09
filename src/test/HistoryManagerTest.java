package test;

import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    private static final TaskManager taskManager = Managers.getDefault();
    private static final Task task1 = new Task("Task1 TaskTest", "TaskTest1 description", Status.NEW);
    private static final Task task2 = new Task("Task2 TaskTest", "TaskTest2 description", Status.IN_PROGRESS);
    private static final Epic epic1 = new Epic("Epic EpicTest1", "EpicTest1 description");
    private static final List<Task> allHistory = new ArrayList<>();
    private static final Subtask subtask1 = new Subtask(
            "Subtask Subtask1",
            "Subtask1 description",
            Status.NEW,
            epic1
    );
    private static final Subtask subtask2 = new Subtask(
            "Subtask Subtask2",
            "Subtask2 description",
            Status.IN_PROGRESS,
            epic1
    );

    @BeforeAll
    public static void addToHistoryManager() {
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

}