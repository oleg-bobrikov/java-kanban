package kanban.view;

import kanban.controller.TaskManager;
import kanban.model.Epic;
import kanban.model.SubTask;
import kanban.model.Task;
import kanban.model.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        createTasks(taskManager);
        Task task1 = taskManager.getTask(1);
        Task task2 = taskManager.getTask(2);
        Epic epic1 = taskManager.getEpic(3);
        SubTask subTask1 = taskManager.getSubTask(4);
        SubTask subTask2 = taskManager.getSubTask(5);
        SubTask subTask3 = taskManager.getSubTask(7);


        // Тестовый сценарий №1
        // На печать должны быть выведены все задачи,
        // эпики  - вместе с подзадачами, указаны верные статусы
        System.out.println("Тестовый сценарий №1");
        printTaskList(taskManager);

        // Тестовый сценарий №2
        // Должны измениться статусы у задач, подзадач и эпиков
        System.out.println();
        System.out.println();
        System.out.println("Тестовый сценарий №2");
        task1.setStatus(TaskStatus.DONE);
        task2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTaskStatus(subTask1, TaskStatus.DONE);
        taskManager.updateSubTaskStatus(subTask2, TaskStatus.IN_PROGRESS);
        taskManager.updateSubTaskStatus(subTask3, TaskStatus.DONE);

        printTaskList(taskManager);

        // Тестовый сценарий №3
        // Удаление задач
        System.out.println();
        System.out.println();
        System.out.println("Тестовый сценарий №3");
        taskManager.deleteTask(2);
        taskManager.deleteEpic(epic1.getId());
        printTaskList(taskManager);

    }

    private static void printTaskList(TaskManager taskManager) {
        System.out.println("Список задач:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic);

            for (SubTask subTask : epic.getSubTasks().values()) {
                System.out.println(subTask);
            }
        }
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

        epic = new Epic("Подготовитсья к экзамену по английскому языку", "SkyEng");
        taskManager.addEpic(epic);

        subTask = new SubTask("Составить план-график", "необходимо учесть приздники", epic);
        taskManager.addSubTask(subTask);
    }
}
