package kanban;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import kanban.managers.Managers;
import kanban.managers.taskmanager.InMemoryTaskManager;
import kanban.managers.taskmanager.TaskManager;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;

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

        taskManager.getTask(1);// История просмотра:{1}
        taskManager.getTask(2);// История просмотра:{1, 2}
        taskManager.getEpic(3);// История просмотра:{1, 2, 3}
        taskManager.getSubtask(4);// История просмотра:{1, 2, 3, 4}
        taskManager.getSubtask(5);// История просмотра:{1, 2, 3, 4, 5}
        taskManager.getSubtask(6);// История просмотра:{1, 2, 3, 4, 5, 6}
        taskManager.getSubtask(4);// Задачи:{1, 2, 3, 5, 6, 4}
        taskManager.getTask(1);// Задачи:{2, 3, 5, 6, 4, 1}

        // Тестовый сценарий №3
        // Создайте новый FileBackedTasksManager менеджер из этого же файла.
        try {
            Files.copy(taskManagerFile.toPath(), Path.of("taskManager_copy.txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        InMemoryTaskManager newTaskManager = Managers.getFileBackedTaskManager(newTaskManagerFile);

        Assertions.assertEquals(taskManager, newTaskManager, "Менеджеры отличаются");
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

}
