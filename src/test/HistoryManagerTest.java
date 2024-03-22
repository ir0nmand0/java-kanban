package test;

import manager.*;
import model.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest extends TasksEpicsSubtasks {
    private static final List<Task> allHistory = new ArrayList<>();

    @BeforeAll
    public static void firstInit() {
        taskManager.clearTasks();
        taskManager.clearEpics();
        historyManager.clear();
        allHistory.clear();
        taskManager.addTask(task1);
        allHistory.add(task1);
        taskManager.addTask(task2);
        allHistory.add(task2);
        taskManager.addTask(task3);
        allHistory.add(task3);
        taskManager.addTask(task4);
        allHistory.add(task4);
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
        firstInit();
        assertNotEquals(historyManager.getHistory().size(), 0, "История задач не получена");
        assertEquals(allHistory.stream().map(Task::getId).toList(),
                historyManager.getHistory().stream().map(Task::getId).toList(), "Истории задач не равны");
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