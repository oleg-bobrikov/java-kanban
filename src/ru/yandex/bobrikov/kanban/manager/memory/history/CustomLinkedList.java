package ru.yandex.bobrikov.kanban.manager.memory.history;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CustomLinkedList<T> implements Serializable {
    private Node<T> head = null;
    private Node<T> tile = null;

    private final HashMap<T, Node<T>> linkedHashMap = new HashMap<>();

    @Override
    public String toString() {
        return "CustomLinkedList{" +
                "head=" + head +
                ", tile=" + tile +
                ", linkedHashMap=" + linkedHashMap +
                '}';
    }

    public void linkLast(T t) {
        Node<T> node = new Node<>(t);
        if (linkedHashMap.isEmpty()) {
            this.head = node;
        } else {
            this.tile.setNext(node);
            node.setPrevious(this.tile);
        }
        this.tile = node;

        linkedHashMap.put(t, node);
    }

    public ArrayList<T> getList() {
        ArrayList<T> result = new ArrayList<>();
        Node<T> currentHead = head;
        while (currentHead != null) {
            result.add(currentHead.getValue());
            currentHead = currentHead.getNext();
        }
        return result;
    }

    public boolean add(T t) {

        boolean isNew = !remove(t);

        linkLast(t);

        return isNew;
    }

    public boolean remove(T t) {
        Node<T> node = linkedHashMap.get(t);
        boolean hasRemoved = false;
        if (node != null) {
            Node<T> previous = node.getPrevious();
            Node<T> next = node.getNext();
            if (linkedHashMap.size() == 1) {
                head = null;
                tile = null;
            } else if (node.equals(tile)) {
                tile = previous;
                previous.setNext(null);
            } else if (node.equals(head)) {
                this.head = node.getNext();
                next.setPrevious(null);
            } else {
                previous.setNext(next);
                next.setPrevious(previous);
            }

            linkedHashMap.remove(t);
            hasRemoved = true;
        }

        return hasRemoved;
    }

    public void clear() {
        linkedHashMap.clear();
        head = null;
        tile = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomLinkedList)) return false;
        CustomLinkedList<?> that = (CustomLinkedList<?>) o;
        return Objects.equals(head, that.head) &&
                Objects.equals(tile, that.tile) &&
                Objects.equals(linkedHashMap, that.linkedHashMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(head, tile, linkedHashMap);
    }
}
