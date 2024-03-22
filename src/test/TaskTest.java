package test;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static manager.Managers.TASK_MANAGER;
import static org.junit.jupiter.api.Assertions.*;

public class TaskTest extends TasksEpicsSubtasks {
    private int sizeTasks;
    private int indexTask;

    @BeforeEach
    public void addTotaskTest() {
        TASK_MANAGER.addTask(task1);
        TASK_MANAGER.addTask(task3);
        TASK_MANAGER.addTask(task4);
        TASK_MANAGER.addTask(task5);
        this.sizeTasks = TASK_MANAGER.getTasks().size();
        this.indexTask = TASK_MANAGER.getTasks().indexOf(task1);
    }

    @Test
    public void checkAddTask() {
        TASK_MANAGER.addTask(task1);

        assertTrue(TASK_MANAGER.getTask(task1.getId()).isPresent(), "Задача не найдена.");
    }

    @Test
    public void getTasks() {
        final List<Task> tasks = TASK_MANAGER.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(sizeTasks, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(indexTask), "Задачи не совпадают.");
    }

    @Test
    public void updateTask() {
        TASK_MANAGER.updateTask(task1, task2);

        assertTrue(TASK_MANAGER.getTask(task2.getId()).isPresent(), "Задача не найдена.");

        this.indexTask = TASK_MANAGER.getTasks().indexOf(task2);
    }

    @Test
    public void removeTask() {
        final Task taskFirst = TASK_MANAGER.getTasks().getFirst();
        final Task taskMiddle = TASK_MANAGER.getTasks().get(indexTask / 2);
        final Task taskLast = TASK_MANAGER.getTasks().getLast();

        TASK_MANAGER.removeTask(taskFirst);

        assertTrue(TASK_MANAGER.getTask(taskFirst.getId()).isEmpty());

        TASK_MANAGER.removeTask(taskMiddle);

        assertTrue(TASK_MANAGER.getTask(taskMiddle.getId()).isEmpty());

        TASK_MANAGER.removeTask(taskLast);

        assertTrue(TASK_MANAGER.getTask(taskLast.getId()).isEmpty());

        this.indexTask = TASK_MANAGER.getTasks().size();
    }

    @Test
    public void clearTasks() {
        TASK_MANAGER.clearTasks();
        assertEquals(TASK_MANAGER.getTasks().size(), 0, "Задачи не удалены.");
    }

}