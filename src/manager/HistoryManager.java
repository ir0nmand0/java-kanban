package manager;
import model.Task;
import java.util.List;
import java.util.Map;

public interface HistoryManager {

    List<Task> getHistory();

    void load(InMemoryNode<Task> inMemoryNode, Map<Integer, Node<Task>> history);

    void add(Task task);

    void remove(int id);

    void remove(Task task);

    void clear();

    void clear(Map<Integer, ? extends Task> map);
}
