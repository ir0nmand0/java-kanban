import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public void setTasks(String name, String description, Status status) {
        Task task = new Task(name, description, status);

        if (tasks.containsKey(task.hashCode())) {
            return;
        }

        tasks.put(task.hashCode(), task);
    }

    public void addTasks(Task task) {
        if (tasks.containsKey(task.hashCode())) {
            return;
        }

        tasks.put(task.hashCode(), task);
    }

    public boolean updateTasks(Task task) {
        if (!tasks.containsKey(task.hashCode())) {
            return false;
        }

        tasks.put(task.hashCode(), task);
        return true;
    }

    public void setEpics(String name, String description) {
        Epic epic = new Epic(name, description);

        if (epics.containsKey(epic.hashCode())) {
            return;
        }

        epics.put(epic.hashCode(), epic);
    }

    public boolean addEpics(Epic epic) {
        Integer epicId = epic.hashCode();

        if (epics.containsKey(epicId)) {
            return false;
        }

        epics.put(epicId, epic);
        return true;
    }

    public boolean addSubtasks(Epic epic, Subtask subtask) {
        Integer epicId = epic.hashCode();

        if (checkEpicAndSubtaskId(epicId, subtask)) {
            return false;
        }

        return epics.get(epicId).addSubtasks(subtask);
    }

    public boolean updateSubtasks(Epic epic, Subtask oldSubtask, Subtask subtask) {
        Integer epicId = epic.hashCode();

        if (checkEpicAndSubtaskId(epicId, subtask)) {
            return false;
        }

        return epics.get(epicId).updateSubtasks(oldSubtask, subtask);
    }

    private boolean checkEpicAndSubtaskId(int epicId, Subtask subtask) {

        if (!epics.containsKey(epicId)) {
            return true;
        }

        if (subtask.getEpic().hashCode() == 0) {
            return true;
        }

        return false;

    }

    public void setSubtasks(Integer epicId, String name, String description, Status status) {
        if (!epics.containsKey(epicId)) {
            return;
        }

        HashMap <Integer, Subtask> subtasks = new HashMap<>();
        Subtask subtask = new Subtask(name, description, status, epics.get(epicId));
        subtasks.put(subtask.hashCode(), subtask);
        epics.get(epicId).setSubtasks(subtasks);
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public String toString() {
        return "TaskManager{" +
                "tasks=" + tasks +
                ",\n\t\t\t\tepics=" + epics +
                '}';
    }

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Task getTaskViaId(Integer id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }

        return null;
    }

    public boolean removeTaskViaId(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            return true;
        }

        return false;
    }

    public boolean removeTask(Task task) {
        return removeTaskViaId(task.hashCode());
    }

    public boolean removeSubtaskViaId(Integer epicId, Integer subtaskId) {
        if (!epics.containsKey(epicId)) {
            return false;
        }

        if (epics.get(epicId).getSubtasks().containsKey(subtaskId)) {
            epics.get(epicId).getSubtasks().remove(subtaskId);
            return true;
        }

        return false;
    }

    public boolean removeSubtask(Epic epic, Subtask subtask) {
        return removeSubtaskViaId(epic.hashCode(), subtask.hashCode());
    }

    public boolean removeEpicViaId(Integer id) {
        if (!epics.containsKey(id)) {
            return false;
        }

        if (!epics.get(id).getSubtasks().isEmpty()) {
            epics.get(id).getSubtasks().clear();
        }

        epics.remove(id);
        return true;
    }

    public boolean removeEpic(Epic epic) {
        return removeEpicViaId(epic.hashCode());
    }

    public void clearTasks() {
        if (tasks.isEmpty()) {
            return;
        }

        tasks.clear();
    }

    public void clearEpics() {
        if (epics.isEmpty()) {
            return;
        }

        for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
            if (!entry.getValue().getSubtasks().isEmpty()) {
                entry.getValue().getSubtasks().clear();
            }
        }

        epics.clear();
    }

    public Map<Integer, Subtask> getSubtasksFromEpicViaId(Integer id) {
        if (epics.containsKey(id)) {
            return epics.get(id).getSubtasks();
        }

        return new HashMap<>();

    }

    public Map<Integer, Subtask> getSubtasksFromEpic(Epic epic) {
        return getSubtasksFromEpicViaId(epic.hashCode());
    }

}
