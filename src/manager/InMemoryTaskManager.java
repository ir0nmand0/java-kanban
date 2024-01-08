package manager;
import model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void setTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            return;
        }

        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task oldTask, Task task) {
        if (!tasks.containsKey(oldTask.getId())) {
            return;
        }

        if (tasks.containsKey(task.getId())) {
            return;
        }

        tasks.remove(oldTask.getId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void setEpic(Epic epic) {
        Integer epicId = epic.getId();

        if (epics.containsKey(epicId)) {
            return;
        }

        epics.put(epicId, epic);
    }

    @Override
    public void setSubtask(Epic epic, Subtask subtask) {
        Integer epicId = epic.getId();

        if (checkEpicAndSubtask(epicId, subtask)) {
            return;
        }

        epics.get(epicId).setSubtask(subtask);
    }

    @Override
    public void updateSubtask(Epic epic, Subtask oldSubtask, Subtask subtask) {
        Integer epicId = epic.getId();

        if (checkEpicAndSubtask(epicId, subtask)) {
            return;
        }

        epics.get(epicId).updateSubtask(oldSubtask, subtask);
    }

    private boolean checkEpicAndSubtask(int epicId, Subtask subtask) {
        if (!epics.containsKey(epicId)) {
            return true;
        }

        if (subtask.getEpic().getId() == 0 || subtask.getEpic().getId() != epicId) {
            return true;
        }

        return false;

    }

    @Override
    public Epic getEpic(Integer id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        }

        return null;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> tasks = new ArrayList<>();

        for (Map.Entry<Integer, Epic> entry : this.epics.entrySet()) {
            tasks.add(entry.getValue());
        }

        return tasks;
    }

    @Override
    public String toString() {
        return "task.InMemoryTaskManager{" +
                "tasks=" + tasks +
                ",\n\t\t\t\tepics=" + epics +
                '}';
    }

    @Override
    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();

        for (Map.Entry<Integer, Task> entry : this.tasks.entrySet()) {
            tasks.add(entry.getValue());
        }

        return tasks;
    }

    @Override
    public Task getTask(Integer id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        }

        return null;
    }

    @Override
    public void removeTask(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }

    }

    @Override
    public void removeTask(Task task) {
        removeTask(task.getId());
    }

    @Override
    public void removeSubtask(Integer epicId, Integer subtaskId) {
        if (!epics.containsKey(epicId)) {
            return;
        }

        if (epics.get(epicId).getSubtasks().containsKey(subtaskId)) {
            epics.get(epicId).getSubtasks().remove(subtaskId);
        }

    }

    @Override
    public void removeSubtask(Epic epic, Subtask subtask) {
        removeSubtask(epic.getId(), subtask.getId());
    }

    @Override
    public void removeSubtask(Integer subtaskId) {
        if (epics.isEmpty()) {
            return;
        }

        for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
            if (!entry.getValue().getSubtasks().isEmpty()) {
                for (Map.Entry<Integer, Subtask> entry1 : entry.getValue().getSubtasks().entrySet()) {
                    if (entry1.getValue().getId() == subtaskId) {
                        removeSubtask(entry.getValue().getId(), entry1.getValue().getId());
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void removeEpic(Integer id) {
        if (!epics.containsKey(id)) {
            return;
        }

        if (!epics.get(id).getSubtasks().isEmpty()) {
            epics.get(id).getSubtasks().clear();
        }

        epics.remove(id);
    }

    @Override
    public void removeEpic(Epic epic) {
        removeEpic(epic.getId());
    }

    @Override
    public void clearTasks() {
        if (tasks.isEmpty()) {
            return;
        }

        tasks.clear();
    }

    @Override
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

    @Override
    public Subtask getSubtask(Integer idSubtask) {
        if(epics.isEmpty()) {
            return null;
        }

        for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
            if (!entry.getValue().getSubtasks().isEmpty()) {
                for (Map.Entry<Integer, Subtask> entry1: entry.getValue().getSubtasks().entrySet()) {
                    if (entry1.getValue().getId() == idSubtask) {
                        historyManager.add(entry1.getValue());
                        return entry1.getValue();
                    }
                }
            }
        }

        return null;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> subtasks = new ArrayList<>();

        for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
            if (!entry.getValue().getSubtasks().isEmpty()) {
                for (Map.Entry<Integer, Subtask> entry1: entry.getValue().getSubtasks().entrySet()) {
                    subtasks.add(entry1.getValue());
                }
            }
        }

        return subtasks;
    }

    @Override
    public List<Subtask> getSubtasks(Integer idEpic) {
        if (!epics.containsKey(idEpic)) {
            return new ArrayList<>();
        }

        List<Subtask> subtasks = new ArrayList<>();

        for (Map.Entry<Integer, Subtask> entry : epics.get(idEpic).getSubtasks().entrySet()) {
            subtasks.add(entry.getValue());
        }

        return subtasks;
    }

    @Override
    public List<Subtask> getSubtasks(Epic epic) {
        List<Subtask> subtasks = new ArrayList<>();

        for (Map.Entry<Integer, Subtask> entry : epics.get(epic.getId()).getSubtasks().entrySet()) {
            subtasks.add(entry.getValue());
        }

        return subtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
