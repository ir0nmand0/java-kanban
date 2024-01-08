package manager;
import model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>(10);

    public void add(Task task) {
        removeFromHistory();
        history.add(task);
    }

    public List<Task> getHistory() {
        return history;
    }

    private void removeFromHistory() {
        if (history.size() == 10) {
            history.remove(0);
        }
    }

}
