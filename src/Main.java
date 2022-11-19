import ru.yandex.practicum.bobrikov.kanban.model.Epic;
import ru.yandex.practicum.bobrikov.kanban.model.SubTask;
import ru.yandex.practicum.bobrikov.kanban.model.Task;
import ru.yandex.practicum.bobrikov.kanban.managers.Managers;
import ru.yandex.practicum.bobrikov.kanban.managers.taskmanager.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // Тестовый сценарий №1
        createTasks(taskManager);
        Task task1 = taskManager.getTask(1);
        printHistory(taskManager);

        Task task2 = taskManager.getTask(2);
        printHistory(taskManager);

        Epic epic1 = taskManager.getEpic(3);
        printHistory(taskManager);

        SubTask subTask1 = taskManager.getSubTask(4);
        printHistory(taskManager);

        SubTask subTask2 = taskManager.getSubTask(5);
        printHistory(taskManager);

        SubTask subTask3 = taskManager.getSubTask(7);
        printHistory(taskManager);

        Task task3 = taskManager.getTask(8);
        printHistory(taskManager);

        Task task4 = taskManager.getTask(9);
        printHistory(taskManager);

        Task task5 = taskManager.getTask(10);
        printHistory(taskManager);

        Task task6 = taskManager.getTask(11);
        printHistory(taskManager);

        // Первая задача из истории просмотра должна удалиться
        Task task7 = taskManager.getTask(12);
        printHistory(taskManager);
    }

    private static void printHistory(TaskManager taskManager) {
        System.out.println("Список просмотренных задач:");
        for (Task task : taskManager.getHistoryManager().getHistory()) {
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
