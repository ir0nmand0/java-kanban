package test;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest extends TasksEpicsSubtasks {
    private int sizeTasks;
    private int indexTask;

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