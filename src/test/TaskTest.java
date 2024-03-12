package test;
import model.*;
import manager.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    private final TaskManager taskManager = Managers.getTaskManager();
    private final Task task1 = new Task("Task1 TaskTest", "TaskTest1 description", Status.NEW);
    private final Task task2 = new Task("Task2 TaskTest", "TaskTest2 description", Status.IN_PROGRESS);
    private final Task task3 = new Task("Task3 TaskTest", "TaskTest3 description", Status.NEW);
    private final Task task4 = new Task("Task4 TaskTest", "TaskTest4 description",Status.NEW,
                     LocalDateTime.now(), Duration.ofHours(1));
    private final Task task5 = new Task("Task5 TaskTest", "TaskTest5 description", Status.IN_PROGRESS,
                     LocalDateTime.now().minusHours(10), Duration.ofHours(3));
    private Integer sizeTasks;
    private Integer indexTask;

    @BeforeEach
    public void addTotaskTest() {
        taskManager.addTask(task1);
        taskManager.addTask(task3);
        taskManager.addTask(task4);
        taskManager.addTask(task5);
        this.sizeTasks = taskManager.getTasks().size();
        this.indexTask = taskManager.getTasks().indexOf(task1);
    }

    @Test
    public void checkAddTask() {
        taskManager.addTask(task1);

        assertTrue(taskManager.getTask(task1.getId()).isPresent(), "Задача не найдена.");
    }

    @Test
    public void getTasks() {
        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(sizeTasks, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(indexTask), "Задачи не совпадают.");
    }

    @Test
    public void updateTask() {
        taskManager.updateTask(task1, task2);

        assertTrue(taskManager.getTask(task2.getId()).isPresent(), "Задача не найдена.");

        this.indexTask = taskManager.getTasks().indexOf(task2);
    }

    @Test
    public void removeTask() {
        final Task taskFirst = taskManager.getTasks().getFirst();
        final Task taskMiddle = taskManager.getTasks().get(indexTask / 2);
        final Task taskLast = taskManager.getTasks().getLast();

        taskManager.removeTask(taskFirst);

        assertTrue(taskManager.getTask(taskFirst.getId()).isEmpty());

        taskManager.removeTask(taskMiddle);

        assertTrue(taskManager.getTask(taskMiddle.getId()).isEmpty());

        taskManager.removeTask(taskLast);

        assertTrue(taskManager.getTask(taskLast.getId()).isEmpty());

        this.indexTask = taskManager.getTasks().size();
    }

    @Test
    public void clearTasks() {
        taskManager.clearTasks();
        assertEquals(taskManager.getTasks().size(), 0, "Задачи не удалены.");
    }

}