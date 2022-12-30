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


    public FileBackedTaskManager(HistoryManager historyManager, File file) throws ManagerSaveException {
        super(historyManager);
        this.file = file;
        loadFromFile();
    }

    public void loadFromFile() throws ManagerSaveException {
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file.toPath()))) {
                try {
                    ArrayList<Task> tasks = (ArrayList<Task>) in.readObject();
                    for (Task task : tasks) {
                        super.addTask(task);
                    }

                    ArrayList<Epic> epics = (ArrayList<Epic>) in.readObject();
                    for (Epic epic : epics) {
                        super.addEpic(epic);
                    }
                    ArrayList<SubTask> subTasks = (ArrayList<SubTask>) in.readObject();
                    for (SubTask subTask : subTasks) {
                        super.addSubTask(subTask);
                    }

                    ArrayList<Task> taskHistory = (ArrayList<Task>) in.readObject();
                    for (Task task : taskHistory) {
                        super.getHistoryManager().add(task);
                    }

                } catch (IOException | ClassNotFoundException e) {
                    throw new ManagerSaveException(e.getMessage());
                }

            } catch (IOException e) {
                throw new ManagerSaveException(e.getMessage());
            }

        }
    }

    private void save() throws ManagerSaveException {
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
    public Task addTask(Task task) throws ManagerSaveException {
        Task newTask = super.addTask(task);
        save();
        return newTask;
    }

    @Override
    public Task getTask(int taskId) throws ManagerSaveException {
        Task newTask = super.getTask(taskId);
        save();
        return newTask;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) throws ManagerSaveException {
        SubTask newSubTask = super.addSubTask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public Epic addEpic(Epic epic) throws ManagerSaveException {
        Epic newEpic = super.addEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public void deleteTasks() throws ManagerSaveException {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubTasks() throws ManagerSaveException {
        super.deleteSubTasks();
        save();
    }

    @Override
    public void deleteEpics() throws ManagerSaveException {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteTask(int id) throws ManagerSaveException {
        super.deleteTask(id);
        save();
    }

    @Override
    public boolean deleteSubTask(int id) throws ManagerSaveException {
        boolean hasRemoved = super.deleteSubTask(id);
        save();
        return hasRemoved;
    }

    @Override
    public void deleteEpic(int id) throws ManagerSaveException {
        super.deleteEpic(id);
        save();
    }

    @Override
    public Task updateTask(Task task) throws ManagerSaveException {
        Task newTask = super.updateTask(task);
        save();
        return newTask;
    }

    @Override
    public SubTask updateSubTask(SubTask newSubTask) throws ManagerSaveException {
        SubTask updatedSubTask = super.updateSubTask(newSubTask);
        save();
        return updatedSubTask;
    }

    @Override
    public Epic updateEpic(Epic newEpic) throws ManagerSaveException {
        Epic updatedEpic = super.updateEpic(newEpic);
        save();
        return updatedEpic;
    }
}
