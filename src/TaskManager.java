import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private Integer id = 1;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public void setTasks(String name, String description, Status status) {
        Integer newId = getNewId();
        tasks.put(newId, new Task(name, description, newId, status));
    }

    public void addTasks(Task task) {
        tasks.put(getNewId(), task);
    }

    public boolean updateTasks(Task task) {
        if (!tasks.containsKey(task.hashCode())) {
            return false;
        }

        tasks.put(task.hashCode(), task);
        return true;
    }

    public void setEpics(String name, String description) {
        Integer newId = getNewId();
        epics.put(newId, new Epic(name, description, newId));
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

    public boolean updateSubtasks(Epic epic, Subtask subtask) {
        Integer epicId = epic.hashCode();

        if (checkEpicAndSubtaskId(epicId, subtask)) {
            return false;
        }

        return epics.get(epicId).updateSubtasks(subtask);
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
        Integer newId = getNewId();
        subtasks.put(newId, new Subtask(name, description, newId, status, epics.get(epicId)));
        epics.get(epicId).setSubtasks(subtasks);
    }

    public Integer getNewId() {
        if (id == Integer.MAX_VALUE) {
            return 0;
        }

        return id++;
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
