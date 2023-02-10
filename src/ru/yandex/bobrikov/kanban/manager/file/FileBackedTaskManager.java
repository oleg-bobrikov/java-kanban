package ru.yandex.bobrikov.kanban.manager.file;

import ru.yandex.bobrikov.kanban.manager.exception.ManagerSaveException;
import ru.yandex.bobrikov.kanban.manager.Managers;
import ru.yandex.bobrikov.kanban.manager.HistoryManager;
import ru.yandex.bobrikov.kanban.manager.memory.InMemoryTaskManager;
import ru.yandex.bobrikov.kanban.manager.TaskManager;
import ru.yandex.bobrikov.kanban.task.Epic;
import ru.yandex.bobrikov.kanban.task.Subtask;
import ru.yandex.bobrikov.kanban.task.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {
    protected transient File file;


    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
        this.loadFromFile();
    }

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
        this.loadFromFile();
    }

    private void loadFromFile() {
        if (file == null || !file.exists()) {
            return;
        }
        ArrayList<Task> tasks;
        ArrayList<Epic> epics;
        ArrayList<Subtask> subtasks;
        ArrayList<Task> taskHistory;
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file.toPath()))) {
            tasks = (ArrayList<Task>) in.readObject();
            epics = (ArrayList<Epic>) in.readObject();
            subtasks = (ArrayList<Subtask>) in.readObject();
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
        for (Subtask subTask : subtasks) {
            super.addSubtask(subTask);
        }
        for (Task task : taskHistory) {
            super.getHistoryManager().add(task);
        }
    }


    protected void save() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            objectOutputStream.writeObject(getTasks());
            objectOutputStream.writeObject(getEpics());
            objectOutputStream.writeObject(getSubtasks());
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
        if (newTask == null) {
            return null;
        }
        save();
        return newTask;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask newSubtask = super.addSubtask(subtask);
        save();
        return newSubtask;
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
        if (newTask == null) {
            return null;
        }
        save();
        return newTask;
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
        Epic epic = super.getEpic(epicId);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int subTaskId) {
        Subtask subTask = super.getSubtask(subTaskId);
        save();
        return subTask;
    }


    public static void main(String[] args) {
        File taskManagerFile = new File("taskManager.txt");
        try {
            Files.deleteIfExists(taskManagerFile.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }

        File newTaskManagerFile = new File("taskManager_copy.txt");

        // Тестовый сценарий №1
        // Заведите несколько разных задач, эпиков и подзадач.
        InMemoryTaskManager taskManager = Managers.getFileBackedTaskManager(taskManagerFile);
        createTasks(taskManager);

        // Тестовый сценарий №2
        // Запросите некоторые из них, чтобы заполнилась история просмотра.

        taskManager.getTask(1);// История просмотра:{1}
        taskManager.getTask(2);// История просмотра:{1, 2}
        taskManager.getEpic(3);// История просмотра:{1, 2, 3}
        taskManager.getSubtask(4);// История просмотра:{1, 2, 3, 4}
        taskManager.getSubtask(5);// История просмотра:{1, 2, 3, 4, 5}
        taskManager.getSubtask(6);// История просмотра:{1, 2, 3, 4, 5, 6}
        taskManager.getSubtask(4);// Задачи:{1, 2, 3, 5, 6, 4}
        taskManager.getTask(1);// Задачи:{2, 3, 5, 6, 4, 1}
        printHistory(taskManager);

        // Тестовый сценарий №3
        // Создайте новый FileBackedTasksManager менеджер из этого же файла.
        try {
            Files.copy(taskManagerFile.toPath(), Path.of("taskManager_copy.txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        InMemoryTaskManager newTaskManager = Managers.getFileBackedTaskManager(newTaskManagerFile);
        printHistory(newTaskManager);

        boolean passed = taskManager.equals(newTaskManager);
        System.out.println(passed ? "Test has passed." : "Test has failed.");

    }

    private static void printHistory(TaskManager taskManager) {
        System.out.println("Список просмотренных задач:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();
    }

    private static void createTasks(TaskManager taskManager) {
        Task task = new Task("Сварить борщ", "на воде, без мяса, добавить чеснока, приготовиь сало");
        taskManager.addTask(task);

        task = new Task("Сделать уборку квартиры", "Обязательно вымыть зеркало в ванной");
        taskManager.addTask(task);

        Epic epic = new Epic("Переобуть машину", "Kia Rio с летней на зимнюю резину");
        taskManager.addEpic(epic);

        Subtask subTask = new Subtask("Купить шины", "Michelin 185x65 R15", epic);
        taskManager.addSubtask(subTask);

        subTask = new Subtask("Записаться на шиномонтаж", "Можно в Колобокс или Колесо", epic);
        taskManager.addSubtask(subTask);

        subTask = new Subtask("Продать старые шины", "Gislaved 200 185x65 R15", epic);
        taskManager.addSubtask(subTask);

        epic = new Epic("Подготовитсья к экзамену по английскому языку", "SkyEng");
        taskManager.addEpic(epic);
    }

    public File getFile() {
        return file;
    }
}

