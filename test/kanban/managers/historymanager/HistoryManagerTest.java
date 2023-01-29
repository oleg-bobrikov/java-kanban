package kanban.managers.historymanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import kanban.managers.taskmanager.TaskManager;
import kanban.model.Task;

import static org.junit.jupiter.api.Assertions.*;

abstract class HistoryManagerTest<T extends TaskManager> {
    private final String TASK_NAME_1 = "Задача1";
    private final String TASK_DESCRIPTION_1 = "Описание1";
    private final String TASK_NAME_3 = "Задача3";
    private final String TASK_DESCRIPTION_3 = "Описание3";

    private T taskManager;
    private HistoryManager historyManager;

    protected abstract T createInstance();

    @BeforeEach
    public void init() {
        taskManager = createInstance();
        historyManager = taskManager.getHistoryManager();
    }

    @Test
    void add() {
        //Пустая история задач
        assertTrue(historyManager.getHistory().isEmpty(), "Непустая история задач");

        // Создать задачу
        Task task1 = addTask1();

        //Дублирование
        historyManager.add(task1);
        historyManager.add(task1);
        assertNotEquals(2, historyManager.getHistory().size(), "Есть Дубли");
        assertEquals(1, historyManager.getHistory().size(), "Неверное количество задач");
    }

    @Test
    void remove() {
        // Создать задачу
        Task task1 = addTask1();
        final int taskId = task1.getId();

        // Добавить задачу
        historyManager.add(task1);

        // Удалить задачу
        taskManager.deleteTask(taskId);

        assertEquals(0, historyManager.getHistory().size(), "Неверное количество задач.");
    }

    @Test
    void getHistory() {
        assertTrue(historyManager.getHistory().isEmpty(), "История не пустая.");
        Task task1 = addTask1();
        final int taskId = task1.getId();
        taskManager.getTask(taskId);
        taskManager.getTask(taskId);
        assertNotEquals(2, historyManager.getHistory().size(), "Дублирование.");
    }

    private Task addTask1() {
        Task newTask = new Task(TASK_NAME_1, TASK_DESCRIPTION_1);
        taskManager.addTask(newTask);
        return newTask;
    }

}