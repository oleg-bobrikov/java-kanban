package ru.yandex.bobrikov.kanban.manager.memory.history;

import ru.yandex.bobrikov.kanban.manager.HistoryManager;
import ru.yandex.bobrikov.kanban.task.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {
    private static final long serialVersionUID = 2502696358512035861L;

    private final LinkedHashMap<Integer, Task> taskHistory = new LinkedHashMap<>();

    public InMemoryHistoryManager() {
    }

    @Override
    public void add(Task task) {
        taskHistory.remove(task.getId());
        taskHistory.put(task.getId(), task);
    }

    @Override
    public void remove(Task task) {
        taskHistory.remove(task.getId());
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(taskHistory.values());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryHistoryManager that = (InMemoryHistoryManager) o;
        return taskHistory.equals(that.taskHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskHistory);
    }
}
