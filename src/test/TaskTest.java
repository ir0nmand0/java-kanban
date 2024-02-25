package test;
import model.*;
import manager.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    private final TaskManager taskManager = Managers.getTaskManager();
    private final Task task1 = new Task("Task1 TaskTest", "TaskTest1 description", Status.NEW);
    private final Task task2 = new Task("Task2 TaskTest", "TaskTest2 description", Status.IN_PROGRESS);
    private final Task task3 = new Task("Task3 TaskTest", "TaskTest3 description", Status.NEW);
    private Integer sizeTasks;
    private Integer indexTask;

    @BeforeEach
    public void addTotaskTest() {
        taskManager.addTask(task1);
        taskManager.addTask(task3);
        this.sizeTasks = taskManager.getTasks().size();
        this.indexTask = taskManager.getTasks().indexOf(task1);
    }

    @Test
    public void checkAddTask() {

        taskManager.addTask(task1);

        final int taskId = task1.getId();
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");
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

        final int taskId2 = task2.getId();
        final Task savedTask2 = taskManager.getTask(taskId2);

        assertNotNull(savedTask2, "Задача не найдена.");
        assertEquals(savedTask2, task2, "Задачи не совпадают.");

        this.indexTask = taskManager.getTasks().indexOf(task2);
    }

    @Test
    public void removeTask() {
        taskManager.removeTask(task1);
        assertNull(taskManager.getTask(task1.getId()));

        this.indexTask = taskManager.getTasks().size();
    }

    @Test
    public void clearTasks() {
        taskManager.clearTasks();
        assertEquals(taskManager.getTasks().size(), 0, "Задачи не удалены.");
    }

}