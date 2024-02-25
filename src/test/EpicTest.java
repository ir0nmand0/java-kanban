package test;
import model.*;
import manager.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    private Integer sizeEpics;
    private Integer indexEpic;
    private Integer sizeAllSubtasks;
    private Integer indexSubtaskFromAllEpics;
    private Integer indexSubtaskFromOneEpic;
    private Integer sizeSubtasks;
    private final TaskManager taskManager = Managers.getTaskManager();
    private final Epic epic1 = new Epic("Epic EpicTest1", "EpicTest1 description");
    private final Epic epic2 = new Epic("Epic EpicTest2", "EpicTest2 description");
    private final Subtask subtask1 = new Subtask(
            "Subtask Subtask1",
            "Subtask1 description",
            Status.NEW,
            epic1
    );
    private final Subtask subtask2 = new Subtask(
            "Subtask Subtask2",
            "Subtask2 description",
            Status.IN_PROGRESS,
            epic1
    );
    private final Subtask subtask3 = new Subtask(
            "Subtask Subtask3",
            "Subtask3 description",
            Status.NEW,
            epic1
    );
    private final Subtask subtask4 = new Subtask(
            "Subtask Subtask4",
            "Subtask4 description",
            Status.NEW,
            epic2
    );
    private final Subtask subtask5 = new Subtask(
            "Subtask Subtask5",
            "Subtask5 description",
            Status.IN_PROGRESS,
            epic2
    );
    private final Subtask subtask6 = new Subtask(
            "Subtask Subtask6",
            "Subtask6 description",
            Status.NEW,
            epic2
    );

    @BeforeEach
    public void addToEpicTest() {
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(epic1, subtask1);
        taskManager.addSubtask(epic1, subtask2);
        taskManager.addSubtask(epic2, subtask4);
        taskManager.addSubtask(epic2, subtask5);

        this.sizeEpics = taskManager.getEpics().size();
        this.indexEpic = taskManager.getEpics().indexOf(epic1);
        this.sizeAllSubtasks = taskManager.getSubtasks().size();
        this.indexSubtaskFromAllEpics = taskManager.getSubtasks().indexOf(subtask1);
        this.sizeSubtasks = taskManager.getSubtasks(epic1).size();
        this.indexSubtaskFromOneEpic = taskManager.getSubtasks(epic1).indexOf(subtask1);
    }

    @Test
    public void addEpic() {
        final Integer epicId = epic1.getId();
        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic1, savedEpic, "Задачи не совпадают.");
    }

    @Test
    public void addSubtask() {
        final Integer subtaskId2 = subtask2.getId();
        final Integer subtaskId5 = subtask5.getId();
        final Subtask savedSubtask2 = taskManager.getSubtask(subtaskId2);
        final Subtask savedSubtask5 = taskManager.getSubtask(subtaskId5);

        assertNotNull(savedSubtask2, "Подзадача не найдена.");
        assertNotNull(savedSubtask5, "Подзадача не найдена.");
        assertEquals(savedSubtask2, subtask2, "Подзадачи не совпадают.");
        assertEquals(savedSubtask5, subtask5, "Подзадачи не совпадают.");
    }

    @Test
    public void getEpics() {
        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(sizeEpics, epics.size(), "Неверное количество задач.");
        assertEquals(epic1, epics.get(indexEpic), "Задачи не совпадают.");
    }

    @Test
    public void updateSubtask() {
        taskManager.updateSubtask(epic1, subtask2, subtask3);
        taskManager.updateSubtask(epic2, subtask5, subtask6);

        final Integer subtaskId3 = subtask3.getId();
        final Integer subtaskId6 = subtask6.getId();
        final Subtask savedSubtask3 = taskManager.getSubtask(subtaskId3);
        final Subtask savedSubtask6 = taskManager.getSubtask(subtaskId6);

        assertNotNull(savedSubtask3, "Подзадача не найдена.");
        assertNotNull(savedSubtask6, "Подзадача не найдена.");
        assertEquals(savedSubtask3, subtask3, "Подзадачи не совпадают.");
        assertEquals(savedSubtask6, subtask6, "Подзадачи не совпадают.");
    }

    @Test
    public void getAllSubtasks() {
        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(sizeAllSubtasks, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask1, subtasks.get(indexSubtaskFromAllEpics), "Подзадачи не совпадают.");
    }

    @Test
    public void getSubtasksFromEpic() {
        final List<Subtask> subtasks = taskManager.getSubtasks(epic1);

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(sizeSubtasks, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask1, subtasks.get(indexSubtaskFromOneEpic), "Подзадачи не совпадают.");
    }

    @Test
    public void getSubtask() {
        final Integer subtaskId1 = subtask1.getId();
        final Subtask savedSubtask1 = taskManager.getSubtask(subtaskId1);

        assertNotNull(savedSubtask1, "Подзадача не найдена.");
        assertEquals(savedSubtask1, subtask1, "Подзадачи не совпадают.");
    }

    @Test
    public void removeSubtask() {
        final Integer subtaskId3 = subtask3.getId();

        taskManager.removeSubtask(subtaskId3);

        final Subtask savedSubtask3 = taskManager.getSubtask(subtaskId3);

        assertNull(savedSubtask3, "Подзадача не удалена.");
    }

    @Test
    public void removeEpic() {
        final Integer epic1Id = epic1.getId();

        taskManager.removeEpic(epic1Id);

        final Epic savedEpic = taskManager.getEpic(epic1Id);

        assertNull(savedEpic, "Эпик не удален.");
    }

    @Test
    public void clearEpics() {
        taskManager.clearEpics();

        final List<Epic> epics = taskManager.getEpics();

        assertEquals(epics.size(), (Integer) 0, "Эпики не удалены.");
    }

}