package manager;
import model.Task;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;

    //Реализовал связанный список с мапой, можно было обойтись односвязным, но решил попрактиковаться
    private final Map<Integer, Node> history = new HashMap<>();

    public void add(Task task) {
        if (head != null && tail.getTask().getId() == task.getId()) {
            return;
        }

        if (history.containsKey(task.getId())) {
            removeNode(history.get(task.getId()));
        }

        Node node = new Node(task);

        if (head == null) {
            head = node;
        } else {
            tail.setNext(node);
        }

        node.setPrev(tail);
        tail = node;
        history.put(task.getId(), node);
    }

    private void removeNode(Node node) {
        if (head == null) {
            return;
        }

        Node next = node.getNext();
        Node prev = node.getPrev();

        if (next != null) {
            next.setPrev(prev);
        }

        if (prev != null) {
            prev.setNext(next);
        }

        if (head == node) {
            head = next;
        }

        if (tail == node) {
            tail = prev;
        }
    }

    public List<Task> getHistory() {
        if (head == null) {
            new ArrayList<>();
        }

        List<Task> list = new ArrayList<>();
        for (Node node = head; node != null; node = node.getNext()) {
            list.add(node.getTask());
        }

        return list;
    }

    public void remove(Task task) {
        if (!history.containsKey(task.getId())) {
            return;
        }

        removeNode(history.get(task.getId()));
        history.remove(task.getId());
    }

    public void remove(int id) {
        if (!history.containsKey(id)) {
            return;
        }

        removeNode(history.get(id));
        history.remove(id);
    }

}
