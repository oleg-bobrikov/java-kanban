package ru.yandex.practicum.bobrikov.kanban.managers.historymanager;

import ru.yandex.practicum.bobrikov.kanban.model.Task;

import java.util.ArrayList;

public interface HistoryManager {
    void add(Task task);
    void remove(Task task);
    ArrayList<Task> getHistory();
}

