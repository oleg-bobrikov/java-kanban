package ru.yandex.practicum.bobrikov.kanban;

import ru.yandex.practicum.bobrikov.kanban.model.Epic;
import ru.yandex.practicum.bobrikov.kanban.model.SubTask;
import ru.yandex.practicum.bobrikov.kanban.model.Task;
import ru.yandex.practicum.bobrikov.kanban.managers.Managers;
import ru.yandex.practicum.bobrikov.kanban.managers.taskmanager.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // Тестовый сценарий №1
        // создайте две задачи, эпик с тремя подзадачами и эпик без подзадач;
        createTasks(taskManager);

        // Тестовый сценарий №2
        // запросите созданные задачи несколько раз в разном порядке;
        // после каждого запроса выведите историю и убедитесь, что в ней нет повторов;

        Task task1 = taskManager.getTask(1);
        printHistory(taskManager); // Задачи:{1}

        Task task2 = taskManager.getTask(2);
        printHistory(taskManager); // Задачи:{1, 2}

        Epic epic3 = taskManager.getEpic(3);
        printHistory(taskManager); // Задачи:{1, 2, 3, 4}

        SubTask subTask4 = taskManager.getSubTask(4);
        printHistory(taskManager); // Задачи:{1, 2, 3, 4}

        SubTask subTask5 = taskManager.getSubTask(5);
        printHistory(taskManager); // Задачи:{1, 2, 3, 4, 5}

        SubTask subTask6 = taskManager.getSubTask(6);
        printHistory(taskManager); // Задачи:{1, 2, 3, 4, 5, 6}

        taskManager.getSubTask(4);
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
