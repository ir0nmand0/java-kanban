package model;
import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {

    private final Map <Integer, Subtask> subtasks = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtask(Subtask subtask) {
        Integer taskId = subtask.getId();

        if (subtasks.containsKey(taskId)) {
            return;
        }

        subtasks.put(taskId, subtask);
        updateStatus();
    }

    public void updateSubtask(Subtask oldSubtask, Subtask subtask) {
        Integer taskId = subtask.getId();
        Integer oldTaskId = oldSubtask.getId();

        if (!subtasks.containsKey(oldTaskId)) {
            return;
        }

        if (subtasks.containsKey(taskId)) {
            return;
        }

        subtasks.remove(oldTaskId);
        subtasks.put(taskId, subtask);
        updateStatus();
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
        return "Epic{" +
                "subtasks=" + subtasks +
                '}';
    }
}
