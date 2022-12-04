package ru.yandex.practicum.bobrikov.kanban.managers.historymanager;

class Node<T> {
    private final T value;
    private Node<T> previous;
    private Node<T> next;

    public Node(T value) {
        this.value = value;
        this.previous = null;
        this.next = null;
    }

    public T getValue() {
        return value;
    }

    public Node<T> getPrevious() {
        return previous;
    }

    public void setPrevious(Node<T> previous) {
        this.previous = previous;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Node<T> node = (Node<T>) o;
        return this.previous == node.previous && this.value == node.value && this.next == node.next;
    }
}

