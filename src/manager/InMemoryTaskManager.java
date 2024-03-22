package manager;
import model.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static manager.Managers.*;
import static manager.Managers.TASK_MANAGER;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new LinkedHashMap<>();
    private final Map<Integer, Epic> epics = new LinkedHashMap<>();
    private final TreeMap<Long, Task> tasksByTime = new TreeMap<>();

    @Override
    public boolean timeIsConflict(Task task) {
        if (Objects.isNull(task)) {
            return false;
        }

        Optional<LocalDateTime> startTime = task.getStartTime();

        if (startTime.isEmpty()) {
            return false;
        }

        Optional<LocalDateTime> endTime = task.getEndTime();

        if (endTime.isEmpty()) {
            return false;
        }

        LocalDateTime localStartTime = startTime.get();
        LocalDateTime localEndTime = endTime.get();

        if (tasksByTime.isEmpty()) {
            return checkTimeForConflicts(tasks, localStartTime) || checkTimeForConflicts(epics, localStartTime);
        }

        Optional<LocalDateTime> firtTime = tasksByTime.firstEntry().getValue().getStartTime();
        Optional<LocalDateTime> lastTime = tasksByTime.lastEntry().getValue().getEndTime();

        if (firtTime.isEmpty() || lastTime.isEmpty()) {
            return checkTimeForConflicts(tasks, startTime.get()) || checkTimeForConflicts(epics, startTime.get());
        }

        LocalDateTime localLastTime = lastTime.get();
        LocalDateTime localFirtTime = firtTime.get();

        if (localStartTime.isAfter(localLastTime) || (localStartTime.isBefore(localFirtTime)
                && localEndTime.isBefore(localFirtTime))) {
            return false;
        }

        return firtTime.get().isEqual(startTime.get()) || firtTime.get().isEqual(endTime.get())
                || lastTime.get().isEqual(startTime.get()) || lastTime.get().isEqual(endTime.get())
                || checkTimeForConflicts(tasks, startTime.get()) || checkTimeForConflicts(epics, startTime.get());
    }

    private boolean checkTimeForConflicts(Map<Integer, ? extends Task> tasks, LocalDateTime startTime) {
        return !tasks.isEmpty() && tasks.values().stream().anyMatch(e -> e.getEndTime().isPresent()
                && (e.getStartTime().get().isBefore(startTime) && e.getEndTime().get().isAfter(startTime)
                || e.getStartTime().get().isEqual(startTime))
        );
    }

    @Override
    public void loadTask() {
        FILE_BACKED_TASK_MANAGER.loadTask();
    }

    @Override
    public boolean addTask(Task task) {
        if (Objects.isNull(task) || !task.getClass().equals(Task.class)
                || tasks.containsKey(task.getId()) || timeIsConflict(task)) {
            return false;
        }

        tasks.put(task.getId(), task);

        if (task.getEndTime().isPresent()) {
            tasksByTime.put(task.getStartTimeToEpochMilli(), task);
        }

        return true;
    }

    @Override
    public boolean updateTask(Task oldTask, Task task) {
        if (Objects.isNull(oldTask) || !oldTask.getClass().equals(Task.class)
                || !tasks.containsKey(oldTask.getId())) {
            return false;
        }

        if (Objects.isNull(task) || !task.getClass().equals(Task.class)
                || tasks.containsKey(task.getId()) || timeIsConflict(task)) {
            return false;
        }

        tasksByTime.remove(oldTask.getStartTimeToEpochMilli());
        tasks.remove(oldTask.getId());
        HISTORY_MANAGER.remove(oldTask);

        tasks.put(task.getId(), task);
        return true;
    }

    @Override
    public boolean addEpic(Epic epic) {
        if (Objects.isNull(epic)) {
            return false;
        }

        int epicId = epic.getId();

        if (epics.containsKey(epicId) || timeIsConflict(epic)) {
            return false;
        }

        epics.put(epicId, epic);
        return true;
    }

    @Override
    public boolean addSubtask(Epic epic, Subtask subtask) {
        if (Objects.isNull(epic) || Objects.isNull(subtask)) {
            return false;
        }

        int epicId = epic.getId();

        if (checkEpicAndSubtask(epicId, subtask) || timeIsConflict(subtask)) {
            return false;
        }

        epics.get(epicId).addSubtask(subtask);

        if (subtask.getEndTime().isPresent()) {
            tasksByTime.put(subtask.getStartTimeToEpochMilli(), subtask);
        }

        return true;
    }

    @Override
    public boolean addSubtask(Subtask subtask) {
        if (Objects.isNull(subtask)) {
            return false;
        }

        Epic epic = subtask.getEpic();

        if (Objects.isNull(epic)) {
            return false;
        }

        return addSubtask(epic, subtask);
    }

    @Override
    public boolean updateSubtask(Epic epic, Subtask oldSubtask, Subtask subtask) {
        if (Objects.isNull(epic) || Objects.isNull(oldSubtask) || Objects.isNull(subtask)) {
            return false;
        }

        int epicId = epic.getId();

        if (checkEpicAndSubtask(epicId, subtask) || timeIsConflict(subtask)) {
            return false;
        }

        tasksByTime.remove(oldSubtask.getStartTimeToEpochMilli());
        epics.get(epicId).updateSubtask(oldSubtask, subtask);
        HISTORY_MANAGER.remove(oldSubtask);

        if (subtask.getEndTime().isPresent()) {
            tasksByTime.put(subtask.getStartTimeToEpochMilli(), subtask);
        }

        return true;
    }

    @Override
    public boolean updateSubtask(Subtask oldSubtask, Subtask subtask) {
        if (Objects.isNull(oldSubtask) || Objects.isNull(subtask)) {
            return false;
        }

        Epic epicFromOldSubtask = oldSubtask.getEpic();
        Epic epicFromNewSubtask = subtask.getEpic();

        if (Objects.isNull(epicFromOldSubtask)
                || Objects.isNull(epicFromNewSubtask)
                || !epicFromOldSubtask.equals(epicFromNewSubtask)) {
            return false;
        }

        return updateSubtask(epicFromOldSubtask, oldSubtask, subtask);
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
            HISTORY_MANAGER.add(epics.get(id));
        }

        return Optional.ofNullable(epics.get(id));
    }

    @Override
    public Optional<Epic> getEpicWithoutHistory(int id) {
        return Optional.ofNullable(epics.get(id));
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Optional<Task> getTask(int id) {
        if (tasks.containsKey(id)) {
            HISTORY_MANAGER.add(tasks.get(id));
        }

        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public Optional<Task> getTaskWithoutHistory(int id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public void removeTask(int id) {
        if (tasks.containsKey(id)) {
            tasksByTime.remove(tasks.get(id).getStartTimeToEpochMilli());
            tasks.remove(id);
            HISTORY_MANAGER.remove(id);
        }
    }

    @Override
    public void removeTask(Task task) {
        if (task.getClass().equals(Task.class)) {
            removeTask(task.getId());
        }
    }

    @Override
    public void removeSubtask(int epicId, int subtaskId) {
        if (!epics.containsKey(epicId)) {
            return;
        }

        Epic epic = epics.get(epicId);

        if (Objects.isNull(epic)) {
            return;
        }

        Optional<Subtask> subtask = epic.getSubtask(subtaskId);

        if (subtask.isEmpty()) {
            return;
        }

        tasksByTime.remove(subtask.get().getStartTimeToEpochMilli());
        epic.removeSubtask(epicId);
        HISTORY_MANAGER.remove(subtaskId);
    }

    @Override
    public void removeSubtask(Epic epic, Subtask subtask) {
        if (Objects.isNull(epic) || Objects.isNull(subtask)) {
            return;
        }

        removeSubtask(epic.getId(), subtask.getId());
    }
    @Override
    public void removeSubtask(Subtask subtask) {
        if (Objects.isNull(subtask)) {
            return;
        }

        Epic epic = subtask.getEpic();

        if (Objects.isNull(epic)) {
            return;
        }

        removeSubtask(epic, subtask);
    }

    @Override
    public void removeSubtask(int subtaskId) {
        if (epics.isEmpty()) {
            return;
        }

        Optional<Epic> epic = epics.entrySet().stream()
                .filter(epicEntry -> epicEntry.getValue().subtasksContainsKey(subtaskId))
                .map(epicEntry -> epicEntry.getValue())
                .findFirst();

        if (epic.isEmpty()) {
            return;
        }

        Optional<Subtask> subtask = epic.get().getSubtask(subtaskId);

        if (subtask.isEmpty()) {
            return;
        }

        tasksByTime.remove(subtask.get().getStartTimeToEpochMilli());
        epic.get().removeSubtask(subtaskId);
    }

    @Override
    public boolean containsKeyInEpics(int id) {
        return epics.containsKey(id);
    }

    @Override
    public boolean containsKeyInSubtasks(int id) {
        return !epics.isEmpty() && epics.entrySet().stream()
                .anyMatch(epicEntry -> epicEntry.getValue().subtasksContainsKey(id));
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

        tasksByTime.remove(epics.get(id).getStartTimeToEpochMilli());

        if (!epics.get(id).subtasksIsEmpty()) {
            epics.get(id).clearSubtasks();
        }

        epics.remove(id);
        HISTORY_MANAGER.remove(id);
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

        tasks.entrySet().forEach(e -> tasksByTime.remove(e.getValue().getStartTimeToEpochMilli()));

        HISTORY_MANAGER.clear(tasks);
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        if (epics.isEmpty()) {
            return;
        }

        epics.entrySet().forEach(e -> tasksByTime.remove(e.getValue().getStartTimeToEpochMilli()));

        epics.entrySet().stream()
                .filter(epicEntry -> !epicEntry.getValue().subtasksIsEmpty())
                .forEach(epicEntry -> epicEntry.getValue().clearSubtasks());

        HISTORY_MANAGER.clear(epics);
        epics.clear();
    }

    @Override
    public Optional<Subtask> getSubtask(int id) {

        Optional<Subtask> subtask = epics.entrySet().stream()
                .filter(epicEntry -> epicEntry.getValue().subtasksContainsKey(id))
                .map(epicEntry -> epicEntry.getValue().getSubtask(id).orElse(null))
                .findFirst();

        if (subtask.isPresent()) {
            HISTORY_MANAGER.add(subtask.get());
        }

        return subtask;
    }

    @Override
    public Optional<Subtask> getSubtaskWithoutHistory(int id) {

        Optional<Subtask> subtask = epics.entrySet().stream()
                .filter(epicEntry -> epicEntry.getValue().subtasksContainsKey(id))
                .map(epicEntry -> epicEntry.getValue().getSubtask(id).orElse(null))
                .findFirst();

        return subtask;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return epics.entrySet().stream().filter(epicEntry -> !epicEntry.getValue().subtasksIsEmpty())
                .map(epicEntry -> epicEntry.getValue().getSubtasks())
                .findAny().orElse(new ArrayList<>());
    }

    @Override
    public List<Subtask> getSubtasks(int idEpic) {
        if (!epics.containsKey(idEpic)) {
            return new ArrayList<>();
        }

        return epics.get(idEpic).getSubtasks();
    }

    @Override
    public List<Subtask> getSubtasks(Epic epic) {
        if (Objects.isNull(epic)) {
            return new ArrayList<>();
        }

        return epics.get(epic.getId()).getSubtasks();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return tasksByTime.values().stream().toList();
    }

    @Override
    public boolean idIsEmpty(int id) {
        return !containsKeyInTasks(id) && !containsKeyInEpics(id) && !containsKeyInSubtasks(id);
    }
}
