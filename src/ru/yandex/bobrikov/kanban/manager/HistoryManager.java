package ru.yandex.bobrikov.kanban.manager;

import ru.yandex.bobrikov.kanban.task.Task;

import java.util.ArrayList;

public interface HistoryManager {
    void add(Task task);

    void remove(Task task);

    ArrayList<Task> getHistory();

}

