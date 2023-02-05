package ru.yandex.bobrikov.kanban.manager.memory.history;

import ru.yandex.bobrikov.kanban.manager.HistoryManager;
import ru.yandex.bobrikov.kanban.task.Task;

import java.util.ArrayList;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {
    private static final long serialVersionUID = 2502696358512035861L;
    // Учебная реализация вместо LinkedHashSet<Task>
    private final CustomLinkedList<Task> taskHistory = new CustomLinkedList<>();

    public InMemoryHistoryManager() {
    }

    @Override
    public void add(Task task) {
        taskHistory.remove(task);
        taskHistory.add(task);
    }

    @Override
    public void remove(Task task) {
        taskHistory.remove(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return taskHistory.getList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InMemoryHistoryManager)) return false;
        InMemoryHistoryManager that = (InMemoryHistoryManager) o;
        return Objects.equals(taskHistory, that.taskHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskHistory);
    }
}
