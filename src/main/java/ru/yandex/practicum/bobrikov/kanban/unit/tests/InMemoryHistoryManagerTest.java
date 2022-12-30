package ru.yandex.practicum.bobrikov.kanban.unit.tests;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.bobrikov.kanban.exceptions.ManagerSaveException;
import ru.yandex.practicum.bobrikov.kanban.managers.Managers;
import ru.yandex.practicum.bobrikov.kanban.managers.taskmanager.TaskManager;
import ru.yandex.practicum.bobrikov.kanban.model.Task;

import java.util.ArrayList;

class InMemoryHistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    void add() throws ManagerSaveException {
        taskManager.deleteTasks();
        Task task1 = createTask(1);
        Task task2 = createTask(2);

        // Просматриваем задачи
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task1.getId());

        // Все должно быть 2 задачи в списке просмотра
        // и первая задача должна быть с идентификтором 2
        ArrayList<Task> history = taskManager.getHistory();
        assert history.size() == 2 && history.get(0).getName().contains("Задача: 2");
    }

    private Task createTask(int taskNumber) throws ManagerSaveException {
        Task newTask = new Task("Задача: " + taskNumber, "Описание задачи: " + taskNumber);
        taskManager.addTask(newTask);
        return newTask;
    }
}