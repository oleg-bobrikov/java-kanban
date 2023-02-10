package ru.yandex.bobrikov.kanban.manager;

import ru.yandex.bobrikov.kanban.manager.file.FileBackedTaskManager;
import ru.yandex.bobrikov.kanban.manager.memory.history.InMemoryHistoryManager;
import ru.yandex.bobrikov.kanban.manager.server.HttpTaskManager;
import ru.yandex.bobrikov.kanban.manager.server.HttpTaskServer;
import ru.yandex.bobrikov.kanban.manager.server.KVServer;

import java.io.File;
import java.io.IOException;

public class Managers {
    private static HttpTaskManager httpTaskManager;
    private static KVServer kvServer;
    private static HttpTaskServer httpTaskServer;

    public static TaskManager getDefault() throws IOException {
        if (httpTaskManager != null) {
            return httpTaskManager;
        }
        if (kvServer == null) {
            kvServer = new KVServer();
            kvServer.start();
        }
        httpTaskManager = new HttpTaskManager();
        return httpTaskManager;
    }

    public static HttpTaskServer getDefaultHttpTaskServer() throws IOException {
        if (httpTaskServer != null) {
            return httpTaskServer;
        }

        httpTaskServer = new HttpTaskServer(getDefault());
        httpTaskServer.start();
        return httpTaskServer;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(getDefaultHistory(), file);
    }


}

