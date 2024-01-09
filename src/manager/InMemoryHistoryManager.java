package manager;
import model.Task;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    //За подсказку с O(1) спасибо, а про связанные списки забыл, хотя и писал их когда-то на c++
    private final List<Task> history = new LinkedList<>();

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
