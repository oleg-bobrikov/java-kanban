package ru.yandex.practicum.bobrikov.kanban.managers;

import ru.yandex.practicum.bobrikov.kanban.exceptions.ManagerSaveException;
import ru.yandex.practicum.bobrikov.kanban.managers.historymanager.HistoryManager;
import ru.yandex.practicum.bobrikov.kanban.managers.historymanager.InMemoryHistoryManager;
import ru.yandex.practicum.bobrikov.kanban.managers.taskmanager.FileBackedTaskManager;
import ru.yandex.practicum.bobrikov.kanban.managers.taskmanager.InMemoryTaskManager;
import ru.yandex.practicum.bobrikov.kanban.managers.taskmanager.TaskManager;

import java.io.File;


public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager(File file) throws ManagerSaveException {
        return new FileBackedTaskManager(getDefaultHistory(), file);
    }


}

