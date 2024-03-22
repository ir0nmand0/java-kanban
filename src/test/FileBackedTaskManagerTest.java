package test;

import manager.FileBackedTaskManager;
import model.Task;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest extends TasksEpicsSubtasks {
    private static final List<Task> allHistory = new ArrayList<>();

    @BeforeEach
    public void addTasks() {
        fileTaskManager.addTask(task1);
        allHistory.add(task1);
        fileTaskManager.addTask(task2);
        allHistory.add(task2);
        fileTaskManager.addTask(task3);
        allHistory.add(task3);
        fileTaskManager.addTask(task4);
        allHistory.add(task4);
        fileTaskManager.addEpic(epic1);
        allHistory.add(epic1);
        fileTaskManager.addSubtask(epic1, subtask1);
        allHistory.add(subtask1);
        fileTaskManager.addSubtask(epic1, subtask2);
        allHistory.add(subtask2);
        fileTaskManager.addSubtask(epic1, subtask3);
        allHistory.add(subtask3);
        fileTaskManager.addSubtask(epic1, subtask4);
        allHistory.add(subtask4);
        fileTaskManager.getTask(task1.getId());
        fileTaskManager.getTask(task2.getId());
        fileTaskManager.getTask(task3.getId());
        fileTaskManager.getTask(task4.getId());
        fileTaskManager.getEpic(epic1.getId());
        fileTaskManager.getSubtask(subtask1.getId());
        fileTaskManager.getSubtask(subtask2.getId());
        fileTaskManager.getSubtask(subtask3.getId());
        fileTaskManager.getSubtask(subtask4.getId());
    }

    @Test
    public void conflictTimeInTask() {
        fileTaskManager.addTask(taskWithConflictTime);

        assertTrue(fileTaskManager.getTasks().stream()
                .filter(e -> e.getId() == taskWithConflictTime.getId()).findAny().isEmpty(),
                "Задача с конфликтом времени добавлена");

        fileTaskManager.addSubtask(epic1, subtaskWithConflictTime);

        assertTrue(fileTaskManager.getSubtasks().stream()
                        .filter(e -> e.getId() == subtaskWithConflictTime.getId()).findAny().isEmpty(),
                "Подзадача с конфликтом времени добавлена");
    }

    @Test
    public void getPrioritizedTasks() {
        Task taskFromSort = fileTaskManager.getPrioritizedTasks().getFirst();
        assertTrue(taskFromSort.equalsWithoutId(task3), "Таски не отсортированы по времени");
    }

    @Test
    public void loadFromFile() throws IOException {
        taskManager.clearTasks();
        taskManager.clearEpics();
        historyManager.clear();

        assertEquals(taskManager.getTasks().size(), 0);
        assertEquals(taskManager.getEpics().size(), 0);
        assertEquals(taskManager.getSubtasks().size(),0);
        assertEquals(historyManager.getHistory().size(), 0);
        
        allHistory.clear();
        addTasks();

        fileTaskManager.loadTask();

        assertEquals(taskManager.getTasks().size() + taskManager.getEpics().size()
                + taskManager.getSubtasks().size(), allHistory.size());

        assertEquals(historyManager.getHistory().size(), allHistory.size());
    }

    @AfterAll
    public static void removeAllFile() throws IOException {

        if (fileTaskManager instanceof FileBackedTaskManager fileBackedTaskManager) {
            Path task = Paths.get(fileBackedTaskManager.getNameBackedTaskManager());
            Path history = Paths.get(fileBackedTaskManager.getNameHistoryManager());

            if (!Files.isDirectory(task)) {
                Files.deleteIfExists(task);
            }

            if (!Files.isDirectory(history)) {
                Files.deleteIfExists(history);
            }
        }
    }


}