package test;

import manager.*;
import model.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static manager.Managers.HISTORY_MANAGER;
import static manager.Managers.TASK_MANAGER;
import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest extends TasksEpicsSubtasks {
    private static final List<Task> allHistory = new ArrayList<>();

    @BeforeAll
    public static void firstInit() {
        TASK_MANAGER.clearTasks();
        TASK_MANAGER.clearEpics();
        HISTORY_MANAGER.clear();
        allHistory.clear();
        TASK_MANAGER.addTask(task1);
        allHistory.add(task1);
        TASK_MANAGER.addTask(task2);
        allHistory.add(task2);
        TASK_MANAGER.addTask(task3);
        allHistory.add(task3);
        TASK_MANAGER.addTask(task4);
        allHistory.add(task4);
        TASK_MANAGER.addEpic(epic1);
        allHistory.add(epic1);
        TASK_MANAGER.addSubtask(epic1, subtask1);
        allHistory.add(subtask1);
        TASK_MANAGER.addSubtask(epic1, subtask2);
        allHistory.add(subtask2);
        TASK_MANAGER.getTask(task1.getId());
        TASK_MANAGER.getTask(task2.getId());
        TASK_MANAGER.getTask(task3.getId());
        TASK_MANAGER.getTask(task4.getId());
        TASK_MANAGER.getTask(task5.getId());
        TASK_MANAGER.getEpic(epic1.getId());
        TASK_MANAGER.getSubtask(subtask1.getId());
        TASK_MANAGER.getSubtask(subtask2.getId());
    }

    @Test
    public void getHistory() {
        firstInit();
        assertNotEquals(HISTORY_MANAGER.getHistory().size(), 0, "История задач не получена");
        assertEquals(allHistory.stream().map(Task::getId).toList(),
                HISTORY_MANAGER.getHistory().stream().map(Task::getId).toList(), "Истории задач не равны");
    }

    @Test
    public void historyIsEmpty() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        assertEquals(inMemoryHistoryManager.getHistory().size(), 0,
                "История задач не пуста");
    }

    @Test
    public void remove() {
        Task taskFirst = TASK_MANAGER.getTasks().getFirst();
        Task taskMiddle = TASK_MANAGER.getTasks().get(TASK_MANAGER.getTasks().size() / 2);
        Subtask taskLast = TASK_MANAGER.getSubtasks().getLast();

        HISTORY_MANAGER.remove(taskFirst.getId());
        allHistory.remove(taskFirst);

        assertTrue(HISTORY_MANAGER.getHistory().stream()
                .filter(e -> e.getId() == taskFirst.getId()).findFirst().isEmpty(),
                "Задача из начала истории не удалена");

        HISTORY_MANAGER.remove(taskMiddle.getId());
        allHistory.remove(taskMiddle);

        assertTrue(HISTORY_MANAGER.getHistory().stream()
                        .filter(e -> e.getId() == taskMiddle.getId()).findFirst().isEmpty(),
                "Задача из середины истории не удалена");

        HISTORY_MANAGER.remove(taskLast.getId());
        allHistory.remove(taskLast);

        assertTrue(HISTORY_MANAGER.getHistory().stream()
                        .filter(e -> e.getId() == taskLast.getId()).findFirst().isEmpty(),
                "Задача из конца истории не удалена");

    }

}