package manager;

import model.Task;

import java.util.Objects;

public class InMemoryNode<T extends Task> {
    private Node<T> head;
    private Node<T> tail;

    public InMemoryNode() {
        this.head = null;
        this.tail = null;
    }

    public Node<T> add(T task) {
        if (Objects.nonNull(head) && tail.getTask().getId() == task.getId()) {
            return null;
        }

        Node<T> node = new Node<>(task);

        if (Objects.isNull(head)) {
            head = node;
        } else {
            tail.setNext(node);
        }

        node.setPrev(tail);
        tail = node;

        return node;
    }

    public void remove(Node<T> node) {
        if (Objects.isNull(head)) {
            return;
        }

        Node<T> next = node.getNext();
        Node<T> prev = node.getPrev();

        if (Objects.nonNull(next)) {
            next.setPrev(prev);
        }

        if (Objects.nonNull(prev)) {
            prev.setNext(next);
        }

        if (head.equals(node)) {
            head = next;
        }

        if (tail.equals(node)) {
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
