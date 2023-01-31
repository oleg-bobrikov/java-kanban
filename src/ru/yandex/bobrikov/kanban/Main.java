package ru.yandex.bobrikov.kanban;

import ru.yandex.bobrikov.kanban.managers.Managers;
import ru.yandex.bobrikov.kanban.managers.taskmanager.TaskManager;
import ru.yandex.bobrikov.kanban.model.Epic;
import ru.yandex.bobrikov.kanban.model.Subtask;
import ru.yandex.bobrikov.kanban.model.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // Тестовый сценарий №1
        // создайте две задачи, эпик с тремя подзадачами и эпик без подзадач;
        createTasks(taskManager);

        // Тестовый сценарий №2
        // запросите созданные задачи несколько раз в разном порядке;
        // после каждого запроса выведите историю и убедитесь, что в ней нет повторов;

        taskManager.getTask(1);
        printHistory(taskManager); // Задачи:{1}

        taskManager.getTask(2);
        printHistory(taskManager); // Задачи:{1, 2}

        taskManager.getEpic(3);
        printHistory(taskManager); // Задачи:{1, 2, 3, 4}

        taskManager.getSubtask(4);
        printHistory(taskManager); // Задачи:{1, 2, 3, 4}

        taskManager.getSubtask(5);
        printHistory(taskManager); // Задачи:{1, 2, 3, 4, 5}

        taskManager.getSubtask(6);
        printHistory(taskManager); // Задачи:{1, 2, 3, 4, 5, 6}

        taskManager.getSubtask(4);
        printHistory(taskManager); // Задачи:{1, 2, 3, 5, 6, 4}

        taskManager.getTask(1);
        printHistory(taskManager); // Задачи:{2, 3, 5, 6, 4, 1}

        // Тестовый сценарий №3
        // удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться;
        taskManager.deleteTask(1);
        printHistory(taskManager); // Задачи:{2, 3, 5, 6, 4}

        // Тестовый сценарий №4
        // удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи.

        taskManager.deleteEpic(3);
        printHistory(taskManager); // Задачи:{2}
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

}
