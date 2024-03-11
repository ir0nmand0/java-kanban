package manager;
import model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TaskManager {

    void loadTask();

    void loadHistory();

    void addTask(Task task);

    void addSubtask(Epic epic, Subtask subtask);

    void addEpic(Epic epic);

    void updateTask(Task oldTask, Task task);

    void updateSubtask(Epic epic, Subtask oldSubtask, Subtask subtask);

    Optional<Task> getTask(int id);

    Map<Integer, Epic> getMapEpics();

    List<Task> getTasks();

    Optional<Epic> getEpic(int id);

    List<Epic> getEpics();

    void removeTask(int id);

    void removeTask(Task task);

    void removeSubtask(int epicId, int subtaskId);

    void removeSubtask(Epic epic, Subtask subtask);

    void removeSubtask(int idSubtask);

    boolean containsKeyInEpics(int id);

    boolean containsKeyInTasks(int id);

    void removeEpic(int id);

    void removeEpic(Epic epic);

    void clearTasks();

    void clearEpics();

    Optional<Subtask> getSubtask(int idSubtask);

    List<Subtask> getSubtasks();

    List<Subtask> getSubtasks(int idEpic);

    List<Subtask> getSubtasks(Epic epic);

    List<Task> getPrioritizedTasks();
}
