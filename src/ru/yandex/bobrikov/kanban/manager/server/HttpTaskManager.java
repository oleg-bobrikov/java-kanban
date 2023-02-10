package ru.yandex.bobrikov.kanban.manager.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ru.yandex.bobrikov.kanban.adapter.DurationAdapter;
import ru.yandex.bobrikov.kanban.adapter.EpicAdapter;
import ru.yandex.bobrikov.kanban.adapter.LocalDateTimeAdapter;
import ru.yandex.bobrikov.kanban.adapter.SubtaskAdapter;
import ru.yandex.bobrikov.kanban.manager.Managers;
import ru.yandex.bobrikov.kanban.task.Epic;
import ru.yandex.bobrikov.kanban.task.Subtask;
import ru.yandex.bobrikov.kanban.task.Task;
import ru.yandex.bobrikov.kanban.manager.file.FileBackedTaskManager;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class HttpTaskManager extends FileBackedTaskManager {
    private final KVTaskClient kvTaskClient;
    private final Gson gson;

    public HttpTaskManager() {
        super(Managers.getDefaultHistory());

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(Subtask.class, new SubtaskAdapter(this));
        gsonBuilder.registerTypeAdapter(Epic.class, new EpicAdapter(this));

        this.gson = gsonBuilder.create();
        kvTaskClient = new KVTaskClient(new InetSocketAddress("localhost", 8078));
        this.load();
    }

    @Override
    protected void save() {
        kvTaskClient.put("tasks", gson.toJson(gson.toJson(getTasks())));
        kvTaskClient.put("subtasks", gson.toJson(gson.toJson(getSubtasks())));
        kvTaskClient.put("epics", gson.toJson(gson.toJson(getEpics())));
        kvTaskClient.put("history", gson.toJson(gson.toJson(getHistory())));
    }

    private void load() {
        String json = kvTaskClient.load("tasks");
        if (json != null) {
            ArrayList<Task> savedTasks = gson.fromJson(json, new TypeToken<ArrayList<Task>>() {
            }.getType());
            savedTasks.forEach(HttpTaskManager.this::addTask);
        }

        json = kvTaskClient.load("epics");
        if (json != null) {
            ArrayList<Epic> savedEpics = gson.fromJson(json, new TypeToken<ArrayList<Epic>>() {
            }.getType());
            savedEpics.forEach(HttpTaskManager.this::addEpic);
        }

        json = kvTaskClient.load("subtasks");
        if (json != null) {
            ArrayList<Subtask> savedSubtasks = gson.fromJson(json, new TypeToken<ArrayList<Subtask>>() {
            }.getType());
            savedSubtasks.forEach(HttpTaskManager.this::addSubtask);
        }

        json = kvTaskClient.load("history");
        if (json != null) {
            ArrayList<Task> savedHistory = gson.fromJson(json, new TypeToken<ArrayList<Task>>() {
            }.getType());
            savedHistory.forEach(this.getHistoryManager()::add);
        }
    }

}
