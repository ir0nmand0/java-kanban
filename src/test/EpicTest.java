package test;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest extends TasksEpicsSubtasks {
    private int sizeEpics;
    private int indexEpic;
    private int sizeAllSubtasks;
    private int indexSubtaskFromOneEpic;
    private int sizeSubtasks;

    @BeforeEach
    public void addToEpicTest() {
        taskManager.clearEpics();
        epic1.clearSubtasks();
        epic2.clearSubtasks();
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(epic1, subtask1);
        taskManager.addSubtask(epic1, subtask2);
        taskManager.addSubtask(epic1, subtask3);
        taskManager.addSubtask(epic2, subtask5);
        taskManager.addSubtask(epic2, subtask6);
        this.sizeEpics = taskManager.getEpics().size();
        this.indexEpic = taskManager.getEpics().indexOf(epic1);
        this.sizeAllSubtasks = taskManager.getSubtasks().size();
        this.sizeSubtasks = taskManager.getSubtasks(epic1).size();
        this.indexSubtaskFromOneEpic = taskManager.getSubtasks(epic1).indexOf(subtask1);
    }

    @Test
    public void addEpic() {
        assertTrue(taskManager.getEpic(epic1.getId()).isPresent(), "Задача не найдена.");
    }

    @Test
    public void addSubtask() {
        assertTrue(taskManager.getSubtask(subtask2.getId()).isPresent(), "Подзадача не найдена");
        assertTrue(taskManager.getSubtask(subtask6.getId()).isPresent(), "Подзадача не найдена");
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
        taskManager.updateSubtask(epic2, subtask6, subtask7);

        assertTrue(taskManager.getSubtask(subtask7.getId()).isPresent(), "Подзадача не найдена.");
    }

    @Test
    public void getAllSubtasks() {
        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(sizeAllSubtasks, subtasks.size(), "Неверное количество подзадач.");
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
        assertTrue(taskManager.getSubtask(subtask1.getId()).isPresent(), "Подзадача не найдена.");
    }

    @Test
    public void removeSubtask() {
        final int subtaskId3 = subtask3.getId();

        taskManager.removeSubtask(subtaskId3);

        assertTrue(taskManager.getSubtask(subtaskId3).isEmpty(), "Подзадача не удалена.");
    }

    @Test
    public void removeEpic() {
        final int epic1Id = epic1.getId();

        taskManager.removeEpic(epic1Id);

        assertTrue(taskManager.getEpic(epic1Id).isEmpty(), "Эпик не удален.");
    }

    @Test
    public void clearEpics() {
        taskManager.clearEpics();

        final List<Epic> epics = taskManager.getEpics();

        assertEquals(epics.size(), 0, "Эпики не удалены.");
    }

    @Test
    public void timeInNull() {
        assertTrue(epic1.getStartTime().isPresent(), "В эпике нет времени");
        taskManager.clearEpics();
        assertTrue(epic1.getStartTime().isEmpty(), "Время эпика не null");
    }

    //ТЗ: Все подзадачи со статусом NEW
    @Test
    public void allStatusNew() {
        taskManager.clearTasks();
        taskManager.clearEpics();
        Epic epic = new Epic("Epic EpicTest1", "EpicTest1 description");
        Subtask subtask1 = new Subtask(
                "Subtask Subtask1",
                "Subtask1 description",
                Status.NEW,
                epic
        );
        Subtask subtask2 = new Subtask(
                "Subtask Subtask2",
                "Subtask2 description",
                Status.NEW,
                epic
        );
        Subtask subtask3 = new Subtask(
                "Subtask Subtask3",
                "Subtask3 description",
                Status.NEW,
                LocalDateTime.now().plusHours(3),
                Duration.ofHours(1),
                epic
        );
        Subtask subtask5 = new Subtask(
                "Subtask Subtask4",
                "Subtask4 description",
                Status.NEW,
                LocalDateTime.now().plusHours(6),
                Duration.ofHours(3),
                epic
        );

        taskManager.addEpic(epic);
        taskManager.addSubtask(epic, subtask1);
        taskManager.addSubtask(epic, subtask2);
        taskManager.addSubtask(epic, subtask3);
        taskManager.addSubtask(epic, subtask5);

        assertEquals(epic.getStatus(), Status.NEW, "Эпик не " + Status.NEW);
    }

    //ТЗ: Все подзадачи со статусом DONE
    @Test
    public void allStatusDone() {
        taskManager.clearTasks();
        taskManager.clearEpics();
        Epic epic = new Epic("Epic EpicTest1", "EpicTest1 description");
        Subtask subtask1 = new Subtask(
                "Subtask Subtask1",
                "Subtask1 description",
                Status.DONE,
                epic
        );
        Subtask subtask2 = new Subtask(
                "Subtask Subtask2",
                "Subtask2 description",
                Status.DONE,
                epic
        );
        Subtask subtask3 = new Subtask(
                "Subtask Subtask3",
                "Subtask3 description",
                Status.DONE,
                LocalDateTime.now().plusHours(3),
                Duration.ofHours(1),
                epic
        );
        Subtask subtask5 = new Subtask(
                "Subtask Subtask4",
                "Subtask4 description",
                Status.DONE,
                LocalDateTime.now().plusHours(6),
                Duration.ofHours(3),
                epic
        );

        taskManager.addEpic(epic);
        taskManager.addSubtask(epic, subtask1);
        taskManager.addSubtask(epic, subtask2);
        taskManager.addSubtask(epic, subtask3);
        taskManager.addSubtask(epic, subtask5);
        assertEquals(epic.getStatus(), Status.DONE, "Эпик не " + Status.DONE);
    }

    //ТЗ: Все подзадачи со статусом DONE и NEW
    @Test
    public void statusDoneAndNew() {
        taskManager.clearTasks();
        taskManager.clearEpics();
        Epic epic = new Epic("Epic EpicTest1", "EpicTest1 description");
        Subtask subtask1 = new Subtask(
                "Subtask Subtask1",
                "Subtask1 description",
                Status.NEW,
                epic
        );
        Subtask subtask2 = new Subtask(
                "Subtask Subtask2",
                "Subtask2 description",
                Status.DONE,
                epic
        );
        Subtask subtask3 = new Subtask(
                "Subtask Subtask3",
                "Subtask3 description",
                Status.DONE,
                LocalDateTime.now().plusHours(3),
                Duration.ofHours(1),
                epic
        );
        Subtask subtask5 = new Subtask(
                "Subtask Subtask4",
                "Subtask4 description",
                Status.NEW,
                LocalDateTime.now().plusHours(6),
                Duration.ofHours(3),
                epic
        );

        taskManager.addEpic(epic);
        taskManager.addSubtask(epic, subtask1);
        taskManager.addSubtask(epic, subtask2);
        taskManager.addSubtask(epic, subtask3);
        taskManager.addSubtask(epic, subtask5);

        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Эпик не " + Status.IN_PROGRESS);
    }

    //ТЗ: Все подзадачи со статусом IN_PROGRESS 🤦‍♂️
    @Test
    public void statusInProgress() {
        taskManager.clearTasks();
        taskManager.clearEpics();
        Epic epic = new Epic("Epic EpicTest1", "EpicTest1 description");
        Subtask subtask1 = new Subtask(
                "Subtask Subtask1",
                "Subtask1 description",
                Status.IN_PROGRESS,
                epic
        );
        Subtask subtask2 = new Subtask(
                "Subtask Subtask2",
                "Subtask2 description",
                Status.IN_PROGRESS,
                epic
        );
        Subtask subtask3 = new Subtask(
                "Subtask Subtask3",
                "Subtask3 description",
                Status.IN_PROGRESS,
                LocalDateTime.now().plusHours(3),
                Duration.ofHours(1),
                epic
        );
        Subtask subtask5 = new Subtask(
                "Subtask Subtask4",
                "Subtask4 description",
                Status.IN_PROGRESS,
                LocalDateTime.now().plusHours(6),
                Duration.ofHours(3),
                epic
        );

        taskManager.addEpic(epic);
        taskManager.addSubtask(epic, subtask1);
        taskManager.addSubtask(epic, subtask2);
        taskManager.addSubtask(epic, subtask3);
        taskManager.addSubtask(epic, subtask5);

        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Эпик не " + Status.IN_PROGRESS);
    }
}