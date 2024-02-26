package manager;

import model.Task;

public class InMemoryNode<T extends Task> {
    private Node<T> head;
    private Node<T> tail;

    public InMemoryNode() {
        this.head = null;
        this.tail = null;
    }

    public Node<T> add(T task) {
        if (head != null && tail.getTask().getId() == task.getId()) {
            return null;
        }

        Node<T> node = new Node<>(task);

        if (head == null) {
            head = node;
        } else {
            tail.setNext(node);
        }

        node.setPrev(tail);
        tail = node;

        return node;
    }

    public void remove(Node<T> node) {
        if (head == null) {
            return;
        }

        Node<T> next = node.getNext();
        Node<T> prev = node.getPrev();

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

    public Node<T> getHead() {
        return head;
    }

    public Node<T> getTail() {
        return tail;
    }

    public void clear() {
        tail = null;
        head = null;
    }

}
