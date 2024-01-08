package manager;
import model.*;
import java.util.List;

public interface TaskManager {

    void setTask(Task task);

    void setSubtask(Epic epic, Subtask subtask);

    void setEpic(Epic epic);

    void updateTask(Task oldTask, Task task);

    void updateSubtask(Epic epic, Subtask oldSubtask, Subtask subtask);

    Task getTask(Integer id);

    List<Task> getTasks();

    Epic getEpic(Integer id);

    List<Epic> getEpics();

    void removeTask(Integer id);

    void removeTask(Task task);

    void removeSubtask(Integer epicId, Integer subtaskId);

    void removeSubtask(Epic epic, Subtask subtask);

    void removeSubtask(Integer idSubtask);

    void removeEpic(Integer id);

    void removeEpic(Epic epic);

    void clearTasks();

    void clearEpics();

    Subtask getSubtask(Integer idSubtask);

    List<Subtask> getSubtasks();

    List<Subtask> getSubtasks(Integer idEpic);

    List<Subtask> getSubtasks(Epic epic);

    List<Task> getHistory();

}
