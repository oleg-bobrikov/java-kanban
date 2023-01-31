package ru.yandex.bobrikov.kanban.managers;

import ru.yandex.bobrikov.kanban.managers.taskmanager.FileBackedTaskManager;
import ru.yandex.bobrikov.kanban.managers.historymanager.HistoryManager;
import ru.yandex.bobrikov.kanban.managers.historymanager.InMemoryHistoryManager;
import ru.yandex.bobrikov.kanban.managers.taskmanager.InMemoryTaskManager;

import java.io.File;


public class Managers {
    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(getDefaultHistory(), file);
    }
}

