package test;

import manager.*;
import model.*;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    private static TaskManager taskManager;
    private static HistoryManager historyManager;
    private static Task task1;
    private static Task task2;
    private static Task task3;
    private static Task task4;
    private static Task task5;
    private static Epic epic1;
    private static List<Task> allHistory;
    private static Subtask subtask1;
    private static Subtask subtask2;

    @BeforeAll
    public static void firstInit() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        historyManager.clear();
        task1 = new Task("Task1 TaskTest", "TaskTest1 description", Status.NEW);
        task2 = new Task("Task2 TaskTest", "TaskTest2 description", Status.IN_PROGRESS);
        task3 = new Task("Task3 TaskTest", "TaskTest3 description", Status.NEW);
        task4 = new Task("Task4 TaskTest", "TaskTest4 description",Status.NEW,
                LocalDateTime.now(), Duration.ofHours(1));
        task5 = new Task("Task5 TaskTest", "TaskTest5 description", Status.IN_PROGRESS,
                LocalDateTime.now().minusHours(10), Duration.ofHours(3));
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

        taskManager.addTask(task1);
        allHistory.add(task1);
        taskManager.addTask(task2);
        allHistory.add(task2);
        taskManager.addTask(task3);
        allHistory.add(task3);
        taskManager.addTask(task4);
        allHistory.add(task4);
        taskManager.addTask(task5);
        allHistory.add(task5);
        taskManager.addEpic(epic1);
        allHistory.add(epic1);
        taskManager.addSubtask(epic1, subtask1);
        allHistory.add(subtask1);
        taskManager.addSubtask(epic1, subtask2);
        allHistory.add(subtask2);
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());
        taskManager.getTask(task4.getId());
        taskManager.getTask(task5.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
    }

    @Test
    public void getHistory() {
        assertNotEquals(historyManager.getHistory().size(), 0, "История задач не получена");
        assertEquals(historyManager.getHistory().size(), allHistory.size(), "Истории задач не равны");
    }

    @Test
    public void historyIsEmpty() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        assertEquals(inMemoryHistoryManager.getHistory().size(), 0,
                "История задач не пуста");
    }

    @Test
    public void remove() {
        Task taskFirst = taskManager.getTasks().getFirst();
        Task taskMiddle = taskManager.getTasks().get(taskManager.getTasks().size() / 2);
        Subtask taskLast = taskManager.getSubtasks().getLast();

        historyManager.remove(taskFirst.getId());
        allHistory.remove(taskFirst);

        assertTrue(historyManager.getHistory().stream()
                .filter(e -> e.getId() == taskFirst.getId()).findFirst().isEmpty(),
                "Задача из начала истории не удалена");

        historyManager.remove(taskMiddle.getId());
        allHistory.remove(taskMiddle);

        assertTrue(historyManager.getHistory().stream()
                        .filter(e -> e.getId() == taskMiddle.getId()).findFirst().isEmpty(),
                "Задача из середины истории не удалена");

        historyManager.remove(taskLast.getId());
        allHistory.remove(taskLast);

        assertTrue(historyManager.getHistory().stream()
                        .filter(e -> e.getId() == taskLast.getId()).findFirst().isEmpty(),
                "Задача из конца истории не удалена");

    }

}