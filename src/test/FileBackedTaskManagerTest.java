package test;

import model.Task;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static manager.Managers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest extends TasksEpicsSubtasks {
    private static final List<Task> allHistory = new ArrayList<>();

    @BeforeEach
    public void addTasks() {
        FILE_BACKED_TASK_MANAGER.addTask(task1);
        allHistory.add(task1);
        FILE_BACKED_TASK_MANAGER.addTask(task2);
        allHistory.add(task2);
        FILE_BACKED_TASK_MANAGER.addTask(task3);
        allHistory.add(task3);
        FILE_BACKED_TASK_MANAGER.addTask(task4);
        allHistory.add(task4);
        FILE_BACKED_TASK_MANAGER.addEpic(epic1);
        allHistory.add(epic1);
        FILE_BACKED_TASK_MANAGER.addSubtask(epic1, subtask1);
        allHistory.add(subtask1);
        FILE_BACKED_TASK_MANAGER.addSubtask(epic1, subtask2);
        allHistory.add(subtask2);
        FILE_BACKED_TASK_MANAGER.addSubtask(epic1, subtask3);
        allHistory.add(subtask3);
        FILE_BACKED_TASK_MANAGER.addSubtask(epic1, subtask4);
        allHistory.add(subtask4);
        FILE_BACKED_TASK_MANAGER.getTask(task1.getId());
        FILE_BACKED_TASK_MANAGER.getTask(task2.getId());
        FILE_BACKED_TASK_MANAGER.getTask(task3.getId());
        FILE_BACKED_TASK_MANAGER.getTask(task4.getId());
        FILE_BACKED_TASK_MANAGER.getEpic(epic1.getId());
        FILE_BACKED_TASK_MANAGER.getSubtask(subtask1.getId());
        FILE_BACKED_TASK_MANAGER.getSubtask(subtask2.getId());
        FILE_BACKED_TASK_MANAGER.getSubtask(subtask3.getId());
        FILE_BACKED_TASK_MANAGER.getSubtask(subtask4.getId());
    }

    @Test
    public void conflictTimeInTask() {
        FILE_BACKED_TASK_MANAGER.addTask(taskWithConflictTime);

        assertTrue(FILE_BACKED_TASK_MANAGER.getTasks().stream()
                .filter(e -> e.getId() == taskWithConflictTime.getId()).findAny().isEmpty(),
                "Задача с конфликтом времени добавлена");

        FILE_BACKED_TASK_MANAGER.addSubtask(epic1, subtaskWithConflictTime);

        assertTrue(FILE_BACKED_TASK_MANAGER.getSubtasks().stream()
                        .filter(e -> e.getId() == subtaskWithConflictTime.getId()).findAny().isEmpty(),
                "Подзадача с конфликтом времени добавлена");
    }

    @Test
    public void getPrioritizedTasks() {
        Task taskFromSort = FILE_BACKED_TASK_MANAGER.getPrioritizedTasks().getFirst();
        assertTrue(taskFromSort.equalsWithoutId(task3), "Таски не отсортированы по времени");
    }

    @Test
    public void loadFromFile() throws IOException {
        TASK_MANAGER.clearTasks();
        TASK_MANAGER.clearEpics();
        HISTORY_MANAGER.clear();

        assertEquals(TASK_MANAGER.getTasks().size(), 0);
        assertEquals(TASK_MANAGER.getEpics().size(), 0);
        assertEquals(TASK_MANAGER.getSubtasks().size(),0);
        assertEquals(HISTORY_MANAGER.getHistory().size(), 0);
        
        allHistory.clear();
        addTasks();

        FILE_BACKED_TASK_MANAGER.loadTask();

        assertEquals(TASK_MANAGER.getTasks().size() + TASK_MANAGER.getEpics().size()
                + TASK_MANAGER.getSubtasks().size(), allHistory.size());

        assertEquals(HISTORY_MANAGER.getHistory().size(), allHistory.size());
    }

    @AfterAll
    public static void removeAllFile() throws IOException {
        Path task = Paths.get(FILE_BACKED_TASK_MANAGER.getNameBackedTaskManager());
        Path history = Paths.get(FILE_BACKED_TASK_MANAGER.getNameHistoryManager());

        if (!Files.isDirectory(task)) {
            Files.deleteIfExists(task);
        }

        if (!Files.isDirectory(history)) {
            Files.deleteIfExists(history);
        }
    }


}