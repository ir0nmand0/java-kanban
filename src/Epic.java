import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {

    private HashMap <Integer, Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subtasks = new HashMap<>();
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(HashMap<Integer, Subtask> subtasks) {
        if (!subtasks.isEmpty()) {
            subtasks.putAll(this.subtasks);
        }

        this.subtasks = subtasks;
        updateStatus();
    }

    public boolean addSubtasks(Subtask subtask) {
        Integer taskId = subtask.hashCode();

        if (subtasks.containsKey(taskId)) {
            return false;
        }

        subtasks.put(taskId, subtask);
        updateStatus();
        return true;
    }

    public boolean updateSubtasks(Subtask oldSubtask, Subtask subtask) {
        Integer taskId = subtask.hashCode();
        Integer oldTaskId = oldSubtask.hashCode();

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
        int sumNew = 0;
        int sumDone = 0;
        for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
            switch (entry.getValue().getStatus()) {
                case NEW:
                    ++sumNew;
                    break;
                case DONE:
                    ++sumDone;
                    break;
            }

            int sizeSubtasks = subtasks.size();

            if (sumNew == sizeSubtasks || sizeSubtasks == 0) {
                status = Status.NEW;
            } else if (sumDone == sizeSubtasks) {
                status = Status.DONE;
            } else {
                status = Status.IN_PROGRESS;
            }

        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
                '}';
    }
}
