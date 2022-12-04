package ru.yandex.practicum.bobrikov.kanban.managers.historymanager;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomLinkedList<T> {
    private Node<T> head = null;
    private Node<T> tile = null;

    private final HashMap<T, Node<T>> linkedHashMap = new HashMap<>();

    public int Size() {
        return linkedHashMap.size();
    }
    public boolean isEmpty() {
        return linkedHashMap.isEmpty();
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

        boolean isPresent = remove(t);

        linkLast(t);

        return !isPresent;
    }

    public boolean remove(T t) {
        Node<T> node = linkedHashMap.get(t);
        boolean isPresent = false;
        if (node != null) {
            Node<T> previous = node.getPrevious();
            Node<T> next = node.getNext();
            if (linkedHashMap.size() == 1) {
                head = null;
                tile = null;
            } else if (node == tile) {
                tile = previous;
                previous.setNext(null);
            } else if (node == head){
               this.head = node.getNext();
               next.setPrevious(null);
            }
            else {
                previous.setNext(next);
                next.setPrevious(previous);
            }

            linkedHashMap.remove(t);
            isPresent = true;
        }

        return isPresent;
    }

}
