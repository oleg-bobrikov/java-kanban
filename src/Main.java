
import Model.Enum;
import Model.Epic;
import Model.SubTask;
import Model.Task;
import Model.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        createTasks(taskManager);
        Task task1 = taskManager.getTask(1);
        Task task2 = taskManager.getTask(2);
        Epic epic1 = taskManager.getEpic(3);
        SubTask subTask1 = taskManager.getSubTask(4);
        SubTask subTask2 = taskManager.getSubTask(5);
        Epic epic2 = taskManager.getEpic(6);
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
        taskManager.updateStatus(task1, Enum.TaskStatus.DONE);
        taskManager.updateStatus(task2, Enum.TaskStatus.IN_PROGRESS);
        taskManager.updateStatus(subTask1, Enum.TaskStatus.DONE);
        taskManager.updateStatus(subTask2, Enum.TaskStatus.IN_PROGRESS);
        taskManager.updateStatus(subTask3, Enum.TaskStatus.DONE);

        // У эпиков запрещено менять статус.Он должен изменяться автоматически
        taskManager.updateStatus(epic1, Enum.TaskStatus.NEW);
        taskManager.updateStatus(epic2, Enum.TaskStatus.NEW);

        printTaskList(taskManager);

        // Тестовый сценарий №3
        // Удаление задач
        System.out.println();
        System.out.println();
        System.out.println("Тестовый сценарий №3");
        taskManager.deleteTask(task2);
        taskManager.deleteEpic(epic1);
        printTaskList(taskManager);

        // Тестовый сценарий №4
        // Методы, исполнение которых требуется по техническому заданию
        taskManager.getTasks();
        taskManager.getSubTasks();
        taskManager.getEpics();
        taskManager.deleteTasks();
        taskManager.deleteSubTasks();

        createTasks(taskManager);
        taskManager.deleteEpics();
        taskManager.deleteTasks();

        createTasks(taskManager);
        taskManager.deleteTask(1);
        taskManager.deleteSubTask(7);
        taskManager.deleteEpic(3);

        createTasks(taskManager);
        taskManager.getSubTasksByEpic(taskManager.getEpic(1));
        taskManager.updateTask(taskManager.getTask(1));
        taskManager.updateSubTask(taskManager.getSubTask(4));
        taskManager.updateEpic(taskManager.getEpic(3));
    }

    private static void printTaskList(TaskManager taskManager) {
        System.out.println("Список задач:");
        System.out.println("---------------------------------------------");

        for (Task task : taskManager.getTasks().values()) {
            System.out.println("Задача:");
            System.out.println("Уникальный идентификационный номер: " + task.getId());
            System.out.println("Наименование: " + task.getName());
            System.out.println("Описание: " + task.getDescription());
            System.out.println("Статус: " + task.getStatus());
            System.out.println("---------------------------------------------");
        }

        for (Epic epic : taskManager.getEpics().values()) {
            System.out.println("*****************************************");
            System.out.println("Эпик:");
            System.out.println("Уникальный идентификационный номер: " + epic.getId());
            System.out.println("Наименование: " + epic.getName());
            System.out.println("Описание: " + epic.getDescription());
            System.out.println("Статус: " + epic.getStatus());
            System.out.println("*****************************************");
            for (SubTask subTask : epic.getSubTasks().values()) {
                System.out.println("Подзадача:");
                System.out.println("Уникальный идентификационный номер: " + subTask.getId());
                System.out.println("Наименование: " + subTask.getName());
                System.out.println("Описание: " + subTask.getDescription());
                System.out.println("Статус: " + subTask.getStatus());
                System.out.println("---------------------------------------------");
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
