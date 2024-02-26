package manager;
import model.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Epic> epics;
    protected HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.tasks = new LinkedHashMap<>();
        this.epics = new LinkedHashMap<>();
        this.historyManager = Managers.getHistoryManager();
    }

    public InMemoryTaskManager(final Map<Integer, Task> tasks, final Map<Integer, Epic> epics) {
        load(tasks, epics);
    }

    private void load(final Map<Integer, Task> tasks, final Map<Integer, Epic> epics) {
        this.tasks = tasks;
        this.epics = epics;
    }

    @Override
    public void loadTask() {
        Managers.getFileBackedTaskManager().loadTask();
    }

    @Override
    public void loadHistory() {
        Managers.getFileBackedTaskManager().loadHistory();
    }

    @Override
    public void addTask(Task task) {
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
        historyManager.remove(oldTask);
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        Integer epicId = epic.getId();

        if (epics.containsKey(epicId)) {
            return;
        }

        epics.put(epicId, epic);
    }

    @Override
    public void addSubtask(Epic epic, Subtask subtask) {
        Integer epicId = epic.getId();

        if (checkEpicAndSubtask(epicId, subtask)) {
            return;
        }

        epics.get(epicId).addSubtask(subtask);
    }

    @Override
    public void updateSubtask(Epic epic, Subtask oldSubtask, Subtask subtask) {
        Integer epicId = epic.getId();

        if (checkEpicAndSubtask(epicId, subtask)) {
            return;
        }

        epics.get(epicId).updateSubtask(oldSubtask, subtask);
        historyManager.remove(oldSubtask);
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
        return new ArrayList<>(epics.values());
    }

    @Override
    public Map<Integer, Epic> getMapEpics() {
        return epics;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
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
            historyManager.remove(id);
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

        if (epics.get(epicId).getMapSubtasks().containsKey(subtaskId)) {
            epics.get(epicId).getMapSubtasks().remove(subtaskId);
            historyManager.remove(subtaskId);
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
            if (!entry.getValue().getMapSubtasks().isEmpty()) {
                for (Map.Entry<Integer, Subtask> entry1 : entry.getValue().getMapSubtasks().entrySet()) {
                    if (entry1.getValue().getId() == subtaskId) {
                        removeSubtask(entry.getValue().getId(), entry1.getValue().getId());
                        return;
                    }
                }
            }
        }
    }

    @Override
    public boolean containsKeyInEpics(int id) {
        return epics.containsKey(id);
    }

    @Override
    public boolean containsKeyInTasks(int id) {
        return tasks.containsKey(id);
    }

    @Override
    public void removeEpic(Integer id) {
        if (!epics.containsKey(id)) {
            return;
        }

        if (!epics.get(id).getMapSubtasks().isEmpty()) {
            epics.get(id).getMapSubtasks().clear();
        }

        epics.remove(id);
        historyManager.remove(id);
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

        historyManager.clear(tasks);
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        if (epics.isEmpty()) {
            return;
        }

        for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
            if (!entry.getValue().getMapSubtasks().isEmpty()) {
                entry.getValue().getMapSubtasks().clear();
            }
        }

        historyManager.clear(epics);
        epics.clear();
    }

    @Override
    public Subtask getSubtask(Integer idSubtask) {
        if(epics.isEmpty()) {
            return null;
        }

        for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
            if (!entry.getValue().getMapSubtasks().isEmpty()) {
                for (Map.Entry<Integer, Subtask> entry1: entry.getValue().getMapSubtasks().entrySet()) {
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
            if (!entry.getValue().getMapSubtasks().isEmpty()) {
                for (Map.Entry<Integer, Subtask> entry1: entry.getValue().getMapSubtasks().entrySet()) {
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

        for (Map.Entry<Integer, Subtask> entry : epics.get(idEpic).getMapSubtasks().entrySet()) {
            subtasks.add(entry.getValue());
        }

        return subtasks;
    }

    @Override
    public List<Subtask> getSubtasks(Epic epic) {
        List<Subtask> subtasks = new ArrayList<>();

        for (Map.Entry<Integer, Subtask> entry : epics.get(epic.getId()).getMapSubtasks().entrySet()) {
            subtasks.add(entry.getValue());
        }

        return subtasks;
    }
}
