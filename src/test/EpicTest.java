package test;
import model.*;
import manager.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
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
        assertTrue(taskManager.getEpic(epic1.getId()).isPresent(), "Задача не найдена.");
    }

    @Test
    public void addSubtask() {
        assertTrue(taskManager.getSubtask(subtask2.getId()).isPresent(), "Подзадача не найдена");
        assertTrue(taskManager.getSubtask(subtask5.getId()).isPresent(), "Подзадача не найдена");
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

        assertTrue(taskManager.getSubtask(subtask3.getId()).isPresent(), "Подзадача не найдена.");
        assertTrue(taskManager.getSubtask(subtask6.getId()).isPresent(), "Подзадача не найдена.");
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
        final Integer epic1Id = epic1.getId();

        taskManager.removeEpic(epic1Id);

        assertTrue(taskManager.getEpic(epic1Id).isEmpty(), "Эпик не удален.");
    }

    @Test
    public void clearEpics() {
        taskManager.clearEpics();

        final List<Epic> epics = taskManager.getEpics();

        assertEquals(epics.size(), (Integer) 0, "Эпики не удалены.");
    }

    //ТЗ: Все подзадачи со статусом NEW
    @Test
    public void allStatusNew() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
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
        Subtask subtask4 = new Subtask(
                "Subtask Subtask4",
                "Subtask4 description",
                Status.NEW,
                LocalDateTime.now().plusHours(6),
                Duration.ofHours(3),
                epic
        );

        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(epic, subtask1);
        inMemoryTaskManager.addSubtask(epic, subtask2);
        inMemoryTaskManager.addSubtask(epic, subtask3);
        inMemoryTaskManager.addSubtask(epic, subtask4);

        assertTrue(epic.getStatus().equals(Status.NEW), "Эпик не " + Status.NEW);
    }

    //ТЗ: Все подзадачи со статусом DONE
    @Test
    public void allStatusDone() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
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
        Subtask subtask4 = new Subtask(
                "Subtask Subtask4",
                "Subtask4 description",
                Status.DONE,
                LocalDateTime.now().plusHours(6),
                Duration.ofHours(3),
                epic
        );

        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(epic, subtask1);
        inMemoryTaskManager.addSubtask(epic, subtask2);
        inMemoryTaskManager.addSubtask(epic, subtask3);
        inMemoryTaskManager.addSubtask(epic, subtask4);

        assertTrue(epic.getStatus().equals(Status.DONE), "Эпик не " + Status.DONE);
    }

    //ТЗ: Все подзадачи со статусом DONE и NEW
    @Test
    public void statusDoneAndNew() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
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
        Subtask subtask4 = new Subtask(
                "Subtask Subtask4",
                "Subtask4 description",
                Status.NEW,
                LocalDateTime.now().plusHours(6),
                Duration.ofHours(3),
                epic
        );

        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(epic, subtask1);
        inMemoryTaskManager.addSubtask(epic, subtask2);
        inMemoryTaskManager.addSubtask(epic, subtask3);
        inMemoryTaskManager.addSubtask(epic, subtask4);

        assertTrue(epic.getStatus().equals(Status.IN_PROGRESS), "Эпик не " + Status.IN_PROGRESS);
    }

    //ТЗ: Все подзадачи со статусом IN_PROGRESS 🤦‍♂️
    @Test
    public void statusInProgress() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
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
        Subtask subtask4 = new Subtask(
                "Subtask Subtask4",
                "Subtask4 description",
                Status.IN_PROGRESS,
                LocalDateTime.now().plusHours(6),
                Duration.ofHours(3),
                epic
        );

        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(epic, subtask1);
        inMemoryTaskManager.addSubtask(epic, subtask2);
        inMemoryTaskManager.addSubtask(epic, subtask3);
        inMemoryTaskManager.addSubtask(epic, subtask4);

        assertTrue(epic.getStatus().equals(Status.IN_PROGRESS), "Эпик не " + Status.IN_PROGRESS);
    }
}