package manager;
import model.Task;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        if (Objects.isNull(task)) {
            return;
        }

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

        return Stream.iterate(inMemoryNode.getHead(), Objects::nonNull, Node::getNext).map(Node::getTask).toList();
    }

    @Override
    public void remove(Task task) {
        if (Objects.isNull(task)) {
            return;
        }

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
        map.keySet().stream().filter(task -> history.containsKey(task)).mapToInt(i -> i).forEach(this::remove);
    }

    @Override
    public String toString() {
        return getHistory().stream().map(task -> String.format("%d,", task.getId())).collect(Collectors.joining());
    }
}
