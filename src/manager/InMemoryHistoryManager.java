package manager;
import model.Task;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private InMemoryNode<Task> inMemoryNode;
    private Map<Integer, Node<Task>> history;

    public InMemoryHistoryManager() {
        inMemoryNode = new InMemoryNode<>();
        history = new HashMap<>();
    }

    public InMemoryHistoryManager(final InMemoryNode<Task> inMemoryNode, final Map<Integer, Node<Task>> history) {
        load(inMemoryNode, history);
    }

    @Override
    public void load(final InMemoryNode<Task> inMemoryNode, final Map<Integer, Node<Task>> history) {
        this.inMemoryNode = inMemoryNode;
        this.history = history;
    }

    @Override
    public void add(Task task) {
        if (history.containsKey(task.getId())) {
            inMemoryNode.remove(history.get(task.getId()));
        }

        Node<Task> node = inMemoryNode.add(task);

        if (node == null) {
            return;
        }

        history.put(task.getId(), node);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> list = new ArrayList<>();

        if (inMemoryNode.getHead() == null) {
            return list;
        }

        for (Node<Task> node = inMemoryNode.getHead(); node != null; node = node.getNext()) {
            list.add(node.getTask());
        }

        return list;
    }

    @Override
    public void remove(Task task) {
        if (!history.containsKey(task.getId())) {
            return;
        }

        inMemoryNode.remove(history.get(task.getId()));
        history.remove(task.getId());
    }

    @Override
    public void remove(int id) {
        if (!history.containsKey(id)) {
            return;
        }

        inMemoryNode.remove(history.get(id));
        history.remove(id);
    }

    @Override
    public void clear() {
        inMemoryNode.clear();
        history.clear();
    }

    @Override
    public void clear(Map<Integer,? extends Task> map) {
        for (Map.Entry<Integer,? extends Task> entry : map.entrySet()) {
            if (history.containsKey(entry.getKey())) {
                remove(entry.getKey());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Task task : getHistory()) {
            stringBuilder.append(String.format("%d,", task.getId()));
        }

        return stringBuilder.toString();
    }
}
