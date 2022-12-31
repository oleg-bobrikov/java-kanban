package ru.yandex.practicum.bobrikov.kanban.managers.taskmanager;

import ru.yandex.practicum.bobrikov.kanban.exceptions.ManagerSaveException;
import ru.yandex.practicum.bobrikov.kanban.managers.historymanager.HistoryManager;
import ru.yandex.practicum.bobrikov.kanban.model.Epic;
import ru.yandex.practicum.bobrikov.kanban.model.SubTask;
import ru.yandex.practicum.bobrikov.kanban.model.Task;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;


    public FileBackedTaskManager(HistoryManager historyManager, File file)  {
        super(historyManager);
        this.file = file;
        loadFromFile();
    }

    private void loadFromFile() {
        if (!file.exists()) {
            return;
        }
        ArrayList<Task> tasks;
        ArrayList<Epic> epics;
        ArrayList<SubTask> subTasks;
        ArrayList<Task> taskHistory;
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file.toPath()))) {
            tasks = (ArrayList<Task>) in.readObject();
            epics = (ArrayList<Epic>) in.readObject();
            subTasks = (ArrayList<SubTask>) in.readObject();
            taskHistory = (ArrayList<Task>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        for (Task task : tasks) {
            super.addTask(task);
        }
        for (Epic epic : epics) {
            super.addEpic(epic);
        }
        for (SubTask subTask : subTasks) {
            super.addSubTask(subTask);
        }
        for (Task task : taskHistory) {
            super.getHistoryManager().add(task);
        }
    }


    private void save() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            objectOutputStream.writeObject(getTasks());
            objectOutputStream.writeObject(getEpics());
            objectOutputStream.writeObject(getSubTasks());
            objectOutputStream.writeObject(getHistory());
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    @Override
    public Task addTask(Task task) {
        Task newTask = super.addTask(task);
        save();
        return newTask;
    }

    @Override
    public Task getTask(int taskId) {
        Task newTask = super.getTask(taskId);
        save();
        return newTask;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        SubTask newSubTask = super.addSubTask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic newEpic = super.addEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
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
        boolean hasRemoved = super.deleteSubTask(id);
        save();
        return hasRemoved;
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public Task updateTask(Task task) {
        Task newTask = super.updateTask(task);
        save();
        return newTask;
    }

    @Override
    public SubTask updateSubTask(SubTask newSubTask) {
        SubTask updatedSubTask = super.updateSubTask(newSubTask);
        save();
        return updatedSubTask;
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        Epic updatedEpic = super.updateEpic(newEpic);
        save();
        return updatedEpic;
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = super.getEpic(epicId);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTask(int subTaskId) {
        SubTask subTask = super.getSubTask(subTaskId);
        save();
        return subTask;
    }
}
