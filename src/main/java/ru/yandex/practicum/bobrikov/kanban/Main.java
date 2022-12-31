package ru.yandex.practicum.bobrikov.kanban;

import ru.yandex.practicum.bobrikov.kanban.exceptions.ManagerSaveException;
import ru.yandex.practicum.bobrikov.kanban.managers.taskmanager.InMemoryTaskManager;
import ru.yandex.practicum.bobrikov.kanban.model.Epic;
import ru.yandex.practicum.bobrikov.kanban.model.SubTask;
import ru.yandex.practicum.bobrikov.kanban.model.Task;
import ru.yandex.practicum.bobrikov.kanban.managers.Managers;
import ru.yandex.practicum.bobrikov.kanban.managers.taskmanager.TaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Main {
    public static void main(String[] args) throws ManagerSaveException, IOException {
        File taskManagerFile = new File("taskManager.txt");
        Files.deleteIfExists(taskManagerFile.toPath());

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
        Files.copy(taskManagerFile.toPath(), Path.of("taskManager_copy.txt"), StandardCopyOption.REPLACE_EXISTING);
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

    private static void createTasks(TaskManager taskManager) throws ManagerSaveException {
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