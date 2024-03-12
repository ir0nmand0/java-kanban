package manager;
import model.*;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Epic> epics;
    private Map<Long, Task> tasksByTime;
    protected HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.tasks = new LinkedHashMap<>();
        this.epics = new LinkedHashMap<>();
        this.historyManager = Managers.getHistoryManager();
        this.tasksByTime = new TreeMap<>();
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new LinkedHashMap<>();
        this.epics = new LinkedHashMap<>();
        this.historyManager = historyManager;
        this.tasksByTime = new TreeMap<>();
    }

    public InMemoryTaskManager(final Map<Integer, Task> tasks, final Map<Integer, Epic> epics) {
        load(tasks, epics);
    }

    private void load(final Map<Integer, Task> tasks, final Map<Integer, Epic> epics) {
        this.tasks.clear();
        this.epics.clear();
        this.tasksByTime.clear();

        if(!Objects.isNull(tasks)) {
            this.tasks.values().forEach(this::addTask);
        }

        if(!Objects.isNull(epics)) {
            this.epics.values()
                    .forEach(epic -> epic.getMapSubtasks().values()
                    .forEach(subtask -> addSubtask(epic, subtask)));
        }
    }

    private boolean timeIsConflict(Task task) {
        if (Objects.isNull(task)) {
            return false;
        }

        return task.getStartTime().isPresent() && (checkTimeForConflicts(tasks, task.getStartTime().get())
                || checkTimeForConflicts(epics, task.getStartTime().get()));
    }

    private boolean checkTimeForConflicts(Map<Integer, ? extends Task> tasks, LocalDateTime startTime) {
        return !tasks.isEmpty() && tasks.values().stream().anyMatch(e -> e.getEndTime().isPresent()
                && (e.getStartTime().get().isBefore(startTime) && e.getEndTime().get().isAfter(startTime)
                || e.getStartTime().get().isEqual(startTime))
        );
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
        if (Objects.isNull(task) || tasks.containsKey(task.getId()) || timeIsConflict(task)) {
            return;
        }

        tasks.put(task.getId(), task);

        if (task.getEndTime().isPresent()) {
            tasksByTime.put(task.getStartTimeToEpochSecond(), task);
        }
    }

    @Override
    public void updateTask(Task oldTask, Task task) {
        if (Objects.isNull(oldTask) || !tasks.containsKey(oldTask.getId())) {
            return;
        }

        if (Objects.isNull(task) || tasks.containsKey(task.getId()) || timeIsConflict(task)) {
            return;
        }

        tasks.remove(oldTask.getId());
        historyManager.remove(oldTask);

        if (oldTask.getEndTime().isPresent()) {
            tasksByTime.remove(oldTask.getStartTimeToEpochSecond());
        }

        addTask(task);
    }

    @Override
    public void addEpic(Epic epic) {
        if (Objects.isNull(epic)) {
            return;
        }

        int epicId = epic.getId();

        if (epics.containsKey(epicId) || timeIsConflict(epic)) {
            return;
        }

        epics.put(epicId, epic);
    }

    @Override
    public void addSubtask(Epic epic, Subtask subtask) {
        if (Objects.isNull(epic) || Objects.isNull(subtask)) {
            return;
        }

        int epicId = epic.getId();

        if (checkEpicAndSubtask(epicId, subtask) || timeIsConflict(subtask)) {
            return;
        }

        epics.get(epicId).addSubtask(subtask);

        if (subtask.getEndTime().isPresent()) {
            tasksByTime.put(subtask.getStartTimeToEpochSecond(), subtask);
        }
    }

    @Override
    public void updateSubtask(Epic epic, Subtask oldSubtask, Subtask subtask) {
        if (Objects.isNull(epic) || Objects.isNull(oldSubtask) || Objects.isNull(subtask)) {
            return;
        }

        int epicId = epic.getId();

        if (checkEpicAndSubtask(epicId, subtask)) {
            return;
        }

        epics.get(epicId).updateSubtask(oldSubtask, subtask);
        historyManager.remove(oldSubtask);

        if (oldSubtask.getEndTime().isPresent()) {
            tasksByTime.remove(oldSubtask.getStartTimeToEpochSecond());
        }

        if (subtask.getEndTime().isPresent()) {
            tasksByTime.put(subtask.getStartTimeToEpochSecond(), subtask);
        }
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
    public Optional<Epic> getEpic(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
        }

        return Optional.ofNullable(epics.get(id));
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
    public Optional<Task> getTask(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
        }

        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public void removeTask(int id) {
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
    public void removeSubtask(int epicId, int subtaskId) {
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
        if (Objects.isNull(epic) || Objects.isNull(subtask)) {
            return;
        }

        removeSubtask(epic.getId(), subtask.getId());
    }

    @Override
    public void removeSubtask(int subtaskId) {
        if (epics.isEmpty()) {
            return;
        }

        epics.entrySet().stream().
                filter(entry -> !entry.getValue().getMapSubtasks().isEmpty())
                .forEach(entry -> entry.getValue().getMapSubtasks().entrySet().stream()
                .filter(entry1 -> entry1.getValue().getId() == subtaskId)
                .forEach(entry1 -> removeSubtask(entry.getValue().getId(), entry1.getValue().getId())));
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
    public void removeEpic(int id) {
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
        if (Objects.isNull(epic)) {
            return;
        }

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

        epics.entrySet().stream()
                .filter(entry -> !entry.getValue().getMapSubtasks().isEmpty())
                .forEach(entry -> entry.getValue().getMapSubtasks().clear());

        historyManager.clear(epics);
        epics.clear();
    }

    @Override
    public Optional<Subtask> getSubtask(int idSubtask) {
        return epics.values().stream()
                .filter(epic -> epic.getMapSubtasks().containsKey(idSubtask))
                .findFirst().map(epic -> epic.getMapSubtasks().get(idSubtask))
                .stream().peek(subtask -> historyManager.add(subtask)).findFirst();
    }

    @Override
    public List<Subtask> getSubtasks() {
        return epics.entrySet().stream()
                .filter(entry -> !entry.getValue().getMapSubtasks().isEmpty())
                .flatMap(entry -> entry.getValue().getMapSubtasks().entrySet().stream())
                .map(Map.Entry::getValue).toList();
    }

    @Override
    public List<Subtask> getSubtasks(int idEpic) {
        if (!epics.containsKey(idEpic)) {
            return new ArrayList<>();
        }

        return epics.get(idEpic).getMapSubtasks().values().stream().toList();
    }

    @Override
    public List<Subtask> getSubtasks(Epic epic) {
        if (Objects.isNull(epic)) {
            return new ArrayList<>();
        }

        return epics.get(epic.getId()).getMapSubtasks().values().stream().toList();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return tasksByTime.values().stream().toList();
    }

}
