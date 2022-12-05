package ru.yandex.practicum.bobrikov.kanban.managers.historymanager;

import ru.yandex.practicum.bobrikov.kanban.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    // Учебная реализация вместо LinkedHashSet<Task>
    private final CustomLinkedList<Task> taskHistory = new CustomLinkedList<>();

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
}
