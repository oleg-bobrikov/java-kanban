package ru.yandex.practicum.bobrikov.kanban.managers.historymanager;

import ru.yandex.practicum.bobrikov.kanban.model.Task;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedHashSet<Task> taskHistory = new LinkedHashSet<>();

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
        return new ArrayList<>(taskHistory);
    }
}
