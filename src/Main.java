import kanban.model.Epic;
import kanban.model.SubTask;
import kanban.model.Task;
import kanban.managers.HistoryManager;
import kanban.managers.Managers;
import kanban.managers.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = taskManager.getHistoryManager();

        // Тестовый сценарий №1
        createTasks(taskManager);
        Task task1 = taskManager.getTask(1);
        printHistory(historyManager);

        Task task2 = taskManager.getTask(2);
        printHistory(historyManager);

        Epic epic1 = taskManager.getEpic(3);
        printHistory(historyManager);

        SubTask subTask1 = taskManager.getSubTask(4);
        printHistory(historyManager);

        SubTask subTask2 = taskManager.getSubTask(5);
        printHistory(historyManager);

        SubTask subTask3 = taskManager.getSubTask(7);
        printHistory(historyManager);

        Task task3 = taskManager.getTask(8);
        printHistory(historyManager);

        Task task4 = taskManager.getTask(9);
        printHistory(historyManager);

        Task task5 = taskManager.getTask(10);
        printHistory(historyManager);

        Task task6 = taskManager.getTask(11);
        printHistory(historyManager);

        // Первая задача из истории просмотра должна удалиться
        Task task7 = taskManager.getTask(12);
        printHistory(historyManager);
    }

    private static void printHistory(HistoryManager historyManager) {
        System.out.println("Список просмотренных задач:");
        for (Task task : historyManager.getHistory()) {
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

        epic = new Epic("Подготовитсья к экзамену по английскому языку", "SkyEng");
        taskManager.addEpic(epic);

        subTask = new SubTask("Составить план-график", "необходимо учесть приздники", epic);
        taskManager.addSubTask(subTask);

        task = new Task("Освежить теорию GIT в памяти","");
        taskManager.addTask(task);

        task = new Task("Повторить полиморфизм","");
        taskManager.addTask(task);

        task = new Task("Отложить деньги на обучение","");
        taskManager.addTask(task);

        task = new Task("Установить WakaTime","");
        taskManager.addTask(task);

        task = new Task("Удалить CodeTime","");
        taskManager.addTask(task);
    }
}
