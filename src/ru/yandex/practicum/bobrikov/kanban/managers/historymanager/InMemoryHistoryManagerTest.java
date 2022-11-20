package ru.yandex.practicum.bobrikov.kanban.managers.historymanager;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.bobrikov.kanban.managers.Managers;
import ru.yandex.practicum.bobrikov.kanban.managers.taskmanager.TaskManager;
import ru.yandex.practicum.bobrikov.kanban.model.Task;

import java.util.ArrayList;

class InMemoryHistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    void add() {
        ArrayList<Integer> taskIdentifiers = new ArrayList<>();
        for (int i = 1; i <= 11; i++) {
            Task task = createTask(i);
            taskIdentifiers.add(task.getId());
        }
        // Просматриваем задачи
        for (Integer taskId : taskIdentifiers) {
            taskManager.getTask(taskId);
        }
        // Ожидаем в списке просмотренных задач ровно 10 вместо 11
        // и первая задача должна затереться второй
        ArrayList<Task> history = taskManager.getHistory();
        assert history.size() == 10 && history.get(0).getName().contains("Задача: 2");
    }

    private Task createTask(int taskNumber) {
        Task newTask = new Task("Задача: " + taskNumber, "Описание задачи: " + taskNumber);
        taskManager.addTask(newTask);
        return newTask;
    }
}