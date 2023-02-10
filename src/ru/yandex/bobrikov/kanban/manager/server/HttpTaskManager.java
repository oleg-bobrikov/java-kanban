package ru.yandex.bobrikov.kanban.manager.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.yandex.bobrikov.kanban.manager.Managers;
import ru.yandex.bobrikov.kanban.task.Epic;
import ru.yandex.bobrikov.kanban.task.Subtask;
import ru.yandex.bobrikov.kanban.task.Task;
import ru.yandex.bobrikov.kanban.manager.file.FileBackedTaskManager;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;


public class HttpTaskManager extends FileBackedTaskManager {
    private KVTaskClient kvTaskClient;
    private final Gson gson;

    public HttpTaskManager(File file) {
        super(Managers.getDefaultHistory(), file);
        // Отключить save() реализацию супрекласса
        this.isFileBackedTaskManager = false;
        this.gson = Managers.getGson(this);
    }

    @Override
    public void save() {
        if (kvTaskClient == null){
            kvTaskClient = new KVTaskClient(new InetSocketAddress("localhost",8078));
        }
        HashMap<String, String> structure = new HashMap<>();
        structure.put("tasks", gson.toJson(getTasks().toArray()));
        structure.put("subtasks", gson.toJson(getSubtasks().toArray()));
        structure.put("epics", gson.toJson(getEpics().toArray()));
        structure.put("history", gson.toJson(getHistory().toArray()));

        kvTaskClient.put("HttpTaskManager", gson.toJson(structure));
    }

    @Override
    public void load() {
        if (kvTaskClient == null){
            kvTaskClient = new KVTaskClient(new InetSocketAddress("localhost",8078));
        }
        String json = kvTaskClient.load("structure");

        if (json != null) {
            HashMap<String, String> structure = gson.fromJson(json, new TypeToken<HashMap<String, String>>() {
            }.getType());
            Task[] savedTasks = gson.fromJson(structure.get("tasks"), new TypeToken<Task[]>() {
            }.getType());
            Arrays.stream(savedTasks).forEach(this::addTask);

            Epic[] savedEpics = gson.fromJson(structure.get("epics"), new TypeToken<Epic[]>() {
            }.getType());
            Arrays.stream(savedEpics).forEach(this::addEpic);

            Subtask[] savedSubtasks = gson.fromJson(structure.get("subtasks"), new TypeToken<Subtask[]>() {
            }.getType());
            Arrays.stream(savedSubtasks).forEach(this::addSubtask);

            Task[] savedHistory = gson.fromJson(structure.get("history"), new TypeToken<Task[]>() {
            }.getType());
            Arrays.stream(savedHistory).forEach(this.getHistoryManager()::add);
        }

    }

    @Override
    public Task addTask(Task task) {
        Task addedTask = super.addTask(task);
        save();
        return addedTask;
    }

    @Override
    public Task getTask(int taskId) {
        Task findTask = super.getTask(taskId);
        if (findTask == null) {
            return null;
        }
        save();
        return findTask;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask addedSubtask = super.addSubtask(subtask);
        save();
        return addedSubtask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic addedEpic = super.addEpic(epic);
        save();
        return addedEpic;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public boolean deleteSubTask(int id) {
        boolean isDeleted = super.deleteSubTask(id);
        save();
        return isDeleted;
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        if (updatedTask == null) {
            return null;
        }
        save();
        return updatedTask;
    }

    @Override
    public Subtask updateSubTask(Subtask srcSubtask) {
        Subtask updatedSubtask = super.updateSubTask(srcSubtask);
        if (updatedSubtask == null) {
            return null;
        }
        save();
        return updatedSubtask;
    }

    @Override
    public Epic updateEpic(Epic srcEpic) {
        Epic updatedEpic = super.updateEpic(srcEpic);
        if (updatedEpic == null) {
            return null;
        }
        save();
        return updatedEpic;
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic foundEpic = super.getEpic(epicId);
        save();
        return foundEpic;
    }

    @Override
    public Subtask getSubtask(int subTaskId) {
        Subtask foundSubtask = super.getSubtask(subTaskId);
        save();
        return foundSubtask;
    }
}
