package model;

import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {

    private Map <Integer, Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subtasks = new HashMap<>();
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(Map<Integer, Subtask> subtasks) {
        if (!subtasks.isEmpty()) {
            subtasks.putAll(this.subtasks);
        }

        this.subtasks = subtasks;
        updateStatus();
    }

    public boolean addSubtasks(Subtask subtask) {
        Integer taskId = subtask.getId();

        if (subtasks.containsKey(taskId)) {
            return false;
        }

        subtasks.put(taskId, subtask);
        updateStatus();
        return true;
    }

    public boolean updateSubtasks(Subtask oldSubtask, Subtask subtask) {
        Integer taskId = subtask.getId();
        Integer oldTaskId = oldSubtask.getId();

        if (!subtasks.containsKey(oldTaskId)) {
            return false;
        }

        if (subtasks.containsKey(taskId)) {
            return false;
        }

        subtasks.remove(oldTaskId);
        subtasks.put(taskId, subtask);
        updateStatus();
        return true;
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

    @Override
    public String toString() {
        return "model.Epic{" +
                "subtasks=" + subtasks +
                '}';
    }
}
