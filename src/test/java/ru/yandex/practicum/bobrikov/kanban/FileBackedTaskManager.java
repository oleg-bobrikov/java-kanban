package ru.yandex.practicum.bobrikov.kanban;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.bobrikov.kanban.managers.Managers;
import ru.yandex.practicum.bobrikov.kanban.managers.taskmanager.InMemoryTaskManager;
import ru.yandex.practicum.bobrikov.kanban.managers.taskmanager.TaskManager;
import ru.yandex.practicum.bobrikov.kanban.model.Epic;
import ru.yandex.practicum.bobrikov.kanban.model.SubTask;
import ru.yandex.practicum.bobrikov.kanban.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

class MainTest {

    @Test
    void main() {
        File taskManagerFile = new File("taskManager.txt");
        try {
            Files.deleteIfExists(taskManagerFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
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


        // Тестовый сценарий №3
        // Создайте новый FileBackedTasksManager менеджер из этого же файла.
        try {
            Files.copy(taskManagerFile.toPath(), Path.of("taskManager_copy.txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        InMemoryTaskManager newTaskManager = Managers.getFileBackedTaskManager(newTaskManagerFile);


        assert taskManager.equals(newTaskManager);

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
