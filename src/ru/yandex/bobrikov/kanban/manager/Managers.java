package ru.yandex.bobrikov.kanban.manager;

import ru.yandex.bobrikov.kanban.manager.file.FileBackedTaskManager;
import ru.yandex.bobrikov.kanban.manager.memory.history.InMemoryHistoryManager;
import ru.yandex.bobrikov.kanban.manager.memory.InMemoryTaskManager;

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

