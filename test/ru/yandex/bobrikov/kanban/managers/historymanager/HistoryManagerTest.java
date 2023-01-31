package ru.yandex.bobrikov.kanban.managers.historymanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.bobrikov.kanban.managers.Managers;
import ru.yandex.bobrikov.kanban.model.Task;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    public void init() {

        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add() {
        // Создать задачу
        Task task1 = createTask1();

        // Проверить отсутствие дублирвоания задач
        historyManager.add(task1);
        historyManager.add(task1);
        assertEquals(1, historyManager.getHistory().size(), "Неверное количество задач");
    }

    @Test
    void remove() {
        // Создать задачу
        Task task1 = createTask1();

        // Добавить задачу
        historyManager.add(task1);

        // Удалить задачу
        historyManager.remove(task1);

        assertEquals(0, historyManager.getHistory().size(), "Неверное количество задач.");
    }

    private Task createTask1() {
        String TASK_NAME_1 = "Задача1";
        String TASK_DESCRIPTION_1 = "Описание1";

        return new Task(TASK_NAME_1, TASK_DESCRIPTION_1);
    }

}