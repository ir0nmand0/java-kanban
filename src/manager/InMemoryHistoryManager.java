package manager;
import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Task> history = new LinkedHashMap<>();

    public void add(Task task) {
        remove(task);
        history.put(task.getId(), task);
    }

    public List<Task> getHistory() {
        return new ArrayList<>(history.values());
    }

    public void remove(Task task) {
        if (!history.containsKey(task.getId())) {
            return;
        }

        history.remove(task.getId());
    }

    public void remove(int id) {
        if (!history.containsKey(id)) {
            return;
        }

        history.remove(id);
    }

}
