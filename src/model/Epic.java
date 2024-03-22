package model;
import com.google.gson.annotations.Expose;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {
    @Expose(serialize = false)
    private final Map<Integer, Subtask> subtasks = new LinkedHashMap<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public void addSubtask(Subtask subtask) {
        if (Objects.isNull(subtask)) {
            return;
        }

        int taskId = subtask.getId();

        if (subtasks.containsKey(taskId)) {
            return;
        }

        subtasks.put(taskId, subtask);
        updateStatus();
        updateTime();
    }

    public void addSubtask(int id, Subtask subtask) {
        if (Objects.isNull(subtask)) {
            return;
        }

        if (subtasks.containsKey(id)) {
            return;
        }

        subtasks.put(id, subtask);
        updateStatus();
        updateTime();
    }

    public void updateSubtask(Subtask oldSubtask, Subtask subtask) {
        if (Objects.isNull(oldSubtask) || Objects.isNull(subtask)) {
            return;
        }

        int taskId = subtask.getId();
        int oldTaskId = oldSubtask.getId();

        if (!subtasks.containsKey(oldTaskId)) {
            return;
        }

        if (subtasks.containsKey(taskId)) {
            return;
        }

        subtasks.remove(oldTaskId);
        subtasks.put(taskId, subtask);
        updateStatus();
        updateTime();
    }

    public void removeSubtask(int id) {
        if (!subtasks.containsKey(id)) {
            return;
        }

        subtasks.remove(id);
        updateStatus();
        updateTime();
    }

    public boolean subtasksContainsKey(int id) {
        return subtasks.containsKey(id);
    }

    public boolean subtasksIsEmpty() {
        return subtasks.isEmpty();
    }

    public void clearSubtasks() {
        subtasks.clear();
        updateTime();
        Status status = Status.DONE;
    }

    public Optional<Subtask> getSubtask(int id) {
        return Optional.ofNullable(subtasks.get(id));
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    private void updateStatus() {
        int sumInProgress = 0;

        for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
            if (entry.getValue().getStatus().equals(Status.IN_PROGRESS)) {
                ++sumInProgress;
            } else if (entry.getValue().getStatus().equals(Status.DONE)) {
                --sumInProgress;
            }
        }

        int sizeSubtask = subtasks.size();

        if (sizeSubtask == 0 || sumInProgress == 0) {
            status = Status.NEW;
        } else if (sizeSubtask + sumInProgress == 0) {
            status = Status.DONE;
        } else {
            status = Status.IN_PROGRESS;
        }
    }

    private void updateTime() {
        startTime = subtasks.values().stream()
                        .map(Task::getStartTime)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .min(LocalDateTime::compareTo)
                        .orElse(null);

        if (startTime == null) {
            return;
        }

        duration = Duration.ofSeconds(subtasks.values().stream()
                .map(Task::getDuration)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Duration::toSeconds)
                .mapToLong(Long::longValue)
                .sum());
    }

    @Override
    public String toString() {
        return Epic.class.getSimpleName() + "{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", subtasks=" + subtaskToList() +
                '}';
    }

    private List<? extends Task> subtaskToList() {
        if (subtasks.isEmpty()) {
            return new ArrayList<>();
        }

        return subtasks.values().stream().toList();
    }
}
