package ru.yandex.practicum.bobrikov.kanban.managers.taskmanager;

import ru.yandex.practicum.bobrikov.kanban.exceptions.ManagerSaveException;
import ru.yandex.practicum.bobrikov.kanban.managers.Managers;
import ru.yandex.practicum.bobrikov.kanban.managers.historymanager.HistoryManager;
import ru.yandex.practicum.bobrikov.kanban.model.Epic;
import ru.yandex.practicum.bobrikov.kanban.model.SubTask;
import ru.yandex.practicum.bobrikov.kanban.model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
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

        Task task1 = taskManager.getTask(1);// История просмотра:{1}
        Task task2 = taskManager.getTask(2);// История просмотра:{1, 2}
        Epic epic3 = taskManager.getEpic(3);// История просмотра:{1, 2, 3}
        SubTask subTask4 = taskManager.getSubTask(4);// История просмотра:{1, 2, 3, 4}
        SubTask subTask5 = taskManager.getSubTask(5);// История просмотра:{1, 2, 3, 4, 5}
        SubTask subTask6 = taskManager.getSubTask(6);// История просмотра:{1, 2, 3, 4, 5, 6}
        taskManager.getSubTask(4);// Задачи:{1, 2, 3, 5, 6, 4}
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

        SubTask subTask = new SubTask("Купить шины", "Michelin 185x65 R15", epic);
        taskManager.addSubTask(subTask);

        subTask = new SubTask("Записаться на шиномонтаж", "Можно в Колобокс или Колесо", epic);
        taskManager.addSubTask(subTask);

        subTask = new SubTask("Продать старые шины", "Gislaved 200 185x65 R15", epic);
        taskManager.addSubTask(subTask);

        epic = new Epic("Подготовитсья к экзамену по английскому языку", "SkyEng");
        taskManager.addEpic(epic);
    }
}

