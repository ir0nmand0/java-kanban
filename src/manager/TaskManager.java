package manager;
import model.*;
import java.util.List;
import java.util.Map;

public interface TaskManager {

    void loadTask() throws ManagerSaveException;

    void loadHistory();

    void addTask(Task task);

    void addSubtask(Epic epic, Subtask subtask);

    void addEpic(Epic epic);

    void updateTask(Task oldTask, Task task);

    void updateSubtask(Epic epic, Subtask oldSubtask, Subtask subtask);

    Task getTask(Integer id);

    Map<Integer, Epic> getMapEpics();

    List<Task> getTasks();

    Epic getEpic(Integer id);

    List<Epic> getEpics();

    void removeTask(Integer id);

    void removeTask(Task task);

    void removeSubtask(Integer epicId, Integer subtaskId);

    void removeSubtask(Epic epic, Subtask subtask);

    void removeSubtask(Integer idSubtask);

    boolean containsKeyInEpics(int id);

    boolean containsKeyInTasks(int id);

    void removeEpic(Integer id);

    void removeEpic(Epic epic);

    void clearTasks();

    void clearEpics();

    Subtask getSubtask(Integer idSubtask);

    List<Subtask> getSubtasks();

    List<Subtask> getSubtasks(Integer idEpic);

    List<Subtask> getSubtasks(Epic epic);
}
