package ru.yandex.bobrikov.kanban.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.bobrikov.kanban.adapter.DurationAdapter;
import ru.yandex.bobrikov.kanban.adapter.EpicAdapter;
import ru.yandex.bobrikov.kanban.adapter.LocalDateTimeAdapter;
import ru.yandex.bobrikov.kanban.adapter.SubtaskAdapter;
import ru.yandex.bobrikov.kanban.manager.file.FileBackedTaskManager;
import ru.yandex.bobrikov.kanban.manager.memory.history.InMemoryHistoryManager;
import ru.yandex.bobrikov.kanban.manager.server.HttpTaskManager;
import ru.yandex.bobrikov.kanban.manager.server.KVServer;
import ru.yandex.bobrikov.kanban.task.Epic;
import ru.yandex.bobrikov.kanban.task.Subtask;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;


public class Managers {
    public static HttpTaskManager httpTaskManager;
    private static KVServer kvServer;
    private static final File taskManagerFile = new File("taskManager.txt");
    private static Gson gson;
    private static Gson simpleGson;

    public static HttpTaskManager getDefault() throws IOException {
        if (httpTaskManager != null) {
            return httpTaskManager;
        }
        if (kvServer == null) {
            kvServer = new KVServer();
            kvServer.start();
        }
        httpTaskManager = new HttpTaskManager(kvServer.getAddress(), taskManagerFile, 8080);
        httpTaskManager.start();
        return httpTaskManager;
    }

    public static KVServer getKVServer() {
        return kvServer;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(getDefaultHistory(), file);
    }

    public static Gson getGson(HttpTaskManager taskManager) {
        if (gson != null) {
            return gson;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(Subtask.class, new SubtaskAdapter(taskManager));
        gsonBuilder.registerTypeAdapter(Epic.class, new EpicAdapter(taskManager));
        gson = gsonBuilder.create();
        return gson;
    }

    public static Gson getGson() {
        if (simpleGson != null) {
            return simpleGson;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        simpleGson = gsonBuilder.create();
        return simpleGson;
    }
}

