package ru.yandex.bobrikov.kanban.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.bobrikov.kanban.manager.Managers;
import ru.yandex.bobrikov.kanban.manager.server.HttpTaskManager;
import ru.yandex.bobrikov.kanban.task.Epic;
import ru.yandex.bobrikov.kanban.task.Subtask;
import ru.yandex.bobrikov.kanban.task.Task;

import java.io.IOException;
import java.util.Arrays;


public class HttpTaskManagerAdapter extends TypeAdapter<HttpTaskManager> {
    private final HttpTaskManager taskManager;
    private final Gson gson;

    public HttpTaskManagerAdapter(HttpTaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = Managers.getGson(taskManager);
    }

    @Override
    public void write(JsonWriter jsonWriter, HttpTaskManager httpTaskManager) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("tasks")
                .value(gson.toJson(taskManager.getTasks().toArray()))
                .name("epics")
                .value(gson.toJson(taskManager.getEpics().toArray()))
                .name("subtasks")
                .value(gson.toJson(taskManager.getSubtasks().toArray()))
                .name("prioritizedTasks")
                .value(gson.toJson(taskManager.getPrioritizedTasks().toArray()))
                .name("history")
                .value(gson.toJson(taskManager.getHistory().toArray()));
        jsonWriter.endObject();
    }

    @Override
    public HttpTaskManager read(JsonReader in) throws IOException {
        String json;
        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "tasks":
                    json = in.nextString();
                    Task[] savedTasks = gson.fromJson(json, new TypeToken<Task[]>() {
                    }.getType());
                    Arrays.stream(savedTasks).forEach(taskManager::addTask);
                    break;
                case "epics":
                    json = in.nextString();
                    Epic[] savedEpics = gson.fromJson(json, new TypeToken<Epic[]>() {
                    }.getType());
                    Arrays.stream(savedEpics).forEach(taskManager::addEpic);
                    break;
                case "subtasks":
                    json = in.nextString();
                    Subtask[] savedSubtasks = gson.fromJson(json, new TypeToken<Subtask[]>() {
                    }.getType());
                    Arrays.stream(savedSubtasks).forEach(taskManager::addSubtask);
                    break;
                case "history":
                    json = in.nextString();
                    Task[] savedPrioritizedTasks = gson.fromJson(json, new TypeToken<Task[]>() {
                    }.getType());
                    Arrays.stream(savedPrioritizedTasks).forEach(taskManager.getHistoryManager()::add);
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();
        return taskManager;
    }


}