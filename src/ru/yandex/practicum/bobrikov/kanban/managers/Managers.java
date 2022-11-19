package ru.yandex.practicum.bobrikov.kanban.managers;

import ru.yandex.practicum.bobrikov.kanban.managers.historymanager.HistoryManager;
import ru.yandex.practicum.bobrikov.kanban.managers.historymanager.InMemoryHistoryManager;
import ru.yandex.practicum.bobrikov.kanban.managers.taskmanager.InMemoryTaskManager;
import ru.yandex.practicum.bobrikov.kanban.managers.taskmanager.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}

