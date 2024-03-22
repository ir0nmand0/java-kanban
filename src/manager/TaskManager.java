package manager;
import model.*;

import java.util.List;
import java.util.Optional;

public interface TaskManager {

    boolean timeIsConflict(Task task);

    void loadTask();

    boolean addTask(Task task);

    boolean addSubtask(Epic epic, Subtask subtask);

    boolean addEpic(Epic epic);

    boolean updateTask(Task oldTask, Task task);

    boolean addSubtask(Subtask subtask);

    boolean updateSubtask(Epic epic, Subtask oldSubtask, Subtask subtask);

    Optional<Task> getTask(int id);

    List<Task> getTasks();

    boolean updateSubtask(Subtask oldSubtask, Subtask subtask);

    Optional<Epic> getEpic(int id);

    Optional<Epic> getEpicWithoutHistory(int id);

    List<Epic> getEpics();

    Optional<Task> getTaskWithoutHistory(int id);

    void removeTask(int id);

    void removeTask(Task task);

    void removeSubtask(int epicId, int subtaskId);

    void removeSubtask(Epic epic, Subtask subtask);

    void removeSubtask(Subtask subtask);

    void removeSubtask(int idSubtask);

    boolean containsKeyInEpics(int id);

    boolean containsKeyInSubtasks(int id);

    boolean containsKeyInTasks(int id);

    void removeEpic(int id);

    void removeEpic(Epic epic);

    void clearTasks();

    void clearEpics();

    Optional<Subtask> getSubtask(int idSubtask);

    Optional<Subtask> getSubtaskWithoutHistory(int id);

    List<Subtask> getSubtasks();

    List<Subtask> getSubtasks(int idEpic);

    List<Subtask> getSubtasks(Epic epic);

    List<Task> getPrioritizedTasks();

    boolean idIsEmpty(int id);
}
