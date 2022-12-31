package ru.yandex.practicum.bobrikov.kanban;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.bobrikov.kanban.exceptions.ManagerSaveException;
import ru.yandex.practicum.bobrikov.kanban.managers.Managers;
import ru.yandex.practicum.bobrikov.kanban.managers.historymanager.HistoryManager;
import ru.yandex.practicum.bobrikov.kanban.managers.taskmanager.TaskManager;
import ru.yandex.practicum.bobrikov.kanban.model.Epic;
import ru.yandex.practicum.bobrikov.kanban.model.SubTask;
import ru.yandex.practicum.bobrikov.kanban.model.Task;
import ru.yandex.practicum.bobrikov.kanban.model.TaskStatus;

import java.util.ArrayList;
import java.util.Set;

class InMemoryTaskManagerTest {
    private final String TASK_NAME_1 = "Задача1";
    private final String TASK_DESCRIPTION_1 = "Описание1";
    private final String TASK_NAME_2 = "Задача2";
    private final String TASK_DESCRIPTION_2 = "Описание2";
    private final String SUBTASK_NAME_1 = "ПодЗадача1";
    private final String SUBTASK_DESCRIPTION_1 = "Описание подзадачи 1";
    private final String SUBTASK_NAME_2 = "ПодЗадача2";
    private final String SUBTASK_DESCRIPTION_2 = "Описание подзадачи 2";
    private final String EPIC_NAME_1 = "Эпик 1";
    private final String EPIC_DESCRIPTION_1 = "Описание эпика 1";
    private final String EPIC_NAME_2 = "Эпик 2";
    private final String EPIC_DESCRIPTION_2 = "Описание эпика 2";
    private final TaskManager taskManager = Managers.getDefault();

    private final HistoryManager historyManager = taskManager.getHistoryManager();

    private Task createTask1() throws ManagerSaveException {
        Task newTask = new Task(TASK_NAME_1, TASK_DESCRIPTION_1);
        taskManager.addTask(newTask);
        return newTask;
    }

    private Task createTask2() throws ManagerSaveException {
        Task newTask = new Task(TASK_NAME_2, TASK_DESCRIPTION_2);
        taskManager.addTask(newTask);
        return newTask;
    }

    private Epic createEpic1() throws ManagerSaveException {
        Epic newEpic = new Epic(EPIC_NAME_1, EPIC_DESCRIPTION_1);
        taskManager.addEpic(newEpic);
        return newEpic;
    }

    private Epic createEpic2() throws ManagerSaveException {
        Epic newEpic = new Epic(EPIC_NAME_2, EPIC_DESCRIPTION_2);
        taskManager.addEpic(newEpic);
        return newEpic;
    }

    private SubTask createSubTask1() throws ManagerSaveException {
        Epic epic1 = createEpic1();
        SubTask newSubTask = new SubTask(SUBTASK_NAME_1, SUBTASK_DESCRIPTION_1, epic1);
        taskManager.addSubTask(newSubTask);
        return newSubTask;
    }

    private SubTask createSubTask2() throws ManagerSaveException {
        Epic epic2 = createEpic2();
        SubTask newSubTask = new SubTask(SUBTASK_NAME_2, SUBTASK_DESCRIPTION_2, epic2);
        taskManager.addSubTask(newSubTask);
        return newSubTask;
    }

    public void clearTaskManager() {
        taskManager.getTasks().clear();
        taskManager.getEpics().clear();
        taskManager.getSubTasks().clear();
    }

    @Test
    void getTasks() throws ManagerSaveException {
        clearTaskManager();
        Task task = createTask1();
        // Создаь историю просмотра
        taskManager.getTask(task.getId());

        assert taskManager.getTasks().size() == 1 &&
                historyManager.getHistory().contains(task); // Проверить историю просмотра
    }

    @Test
    void deleteTasks() throws ManagerSaveException {
        clearTaskManager();
        Task task1 = createTask1();
        Task task2 = createTask1();
        Task task3 = createTask1();

        // Создать историю просмотра
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());
        Set<Task> testCollection = Set.of(task1, task2, task3);

        taskManager.deleteTasks();
        assert taskManager.getTasks().size() == 0 && !historyManager.getHistory().containsAll(testCollection);

    }

    @Test
    void addTask() throws ManagerSaveException {
        clearTaskManager();
        createTask1();
        if (taskManager.getTasks().isEmpty()) {
            assert false;
        } else {
            Task foundTask = taskManager.getTask(1);
            assert foundTask != null &&
                    foundTask.getName().equals(TASK_NAME_1) &&
                    foundTask.getDescription().equals(TASK_DESCRIPTION_1);
        }
    }


    @Test
    void updateTask() throws ManagerSaveException {
        clearTaskManager();
        createTask1();
        Task task1 = taskManager.getTask(1);
        Task task2 = new Task(TASK_NAME_2, TASK_DESCRIPTION_2);
        task2.setId(1);
        taskManager.updateTask(task2);
        assert task1.getName().equals(task2.getName()) &&
                task1.getDescription().equals(task2.getDescription());
    }


    @Test
    void getTask() throws ManagerSaveException {
        clearTaskManager();
        Task task = createTask1();
        Task foundTask = taskManager.getTask(1);
        assert task.equals(foundTask) &&
                historyManager.getHistory().contains(task); // Проверить историю просмотра
    }

    @Test
    void deleteTask() throws ManagerSaveException {
        clearTaskManager();
        Task task1 = createTask1();
        createTask2();
        // Создать историю просмотра
        taskManager.getTask(task1.getId());

        taskManager.deleteTask(1);
        assert taskManager.getTask(2) != null &&
                taskManager.getTask(1) == null &&
                !historyManager.getHistory().contains(task1); // Проверить историю просмотра
    }

    @Test
    void getSubTasks() throws ManagerSaveException {
        clearTaskManager();
        createSubTask1();
        assert taskManager.getSubTasks().size() == 1;
    }

    @Test
    void addSubTask() throws ManagerSaveException {
        clearTaskManager();
        createSubTask1();
        assert taskManager.getSubTasks().size() == 1;
    }

    @Test
    void deleteSubTask() throws ManagerSaveException {
        clearTaskManager();
        SubTask subTask = createSubTask1();
        Epic epic = subTask.getEpic();
        // Создать историю просмотра
        taskManager.getSubTask(subTask.getId());

        // Удалить подзадача
        taskManager.deleteSubTask(subTask.getId());

        // Тест
        assert taskManager.getSubTask(subTask.getId()) == null &&
                epic.getSubTasks().get(subTask.getId()) == null &&
                !historyManager.getHistory().contains(subTask); // Проверить очистку истории просмотра
    }

    @Test
    void updateSubTask() throws ManagerSaveException {
        clearTaskManager();
        // Создание подзадачи вместе с эпиком
        SubTask oldSubTask = createSubTask1();
        Epic oldEpic = oldSubTask.getEpic();
        // Создание нового эпики и подзадачи
        Epic newEpic = createEpic2();
        SubTask newSubTask = new SubTask(SUBTASK_NAME_2, SUBTASK_DESCRIPTION_2, newEpic);
        // Новой подзадачи присваиваем страый id
        newSubTask.setId(oldSubTask.getId());
        taskManager.updateSubTask(newSubTask);
        // Проверка
        assert oldSubTask.getName().equals(SUBTASK_NAME_2) && // Имя должно обновиться
                oldSubTask.getDescription().equals(SUBTASK_DESCRIPTION_2) && // Описание должно обновиться
                oldSubTask.getEpic().equals(newEpic) && // Эпик должен обновиться
                oldEpic.getSubTasks().isEmpty() && // Старый эпик должен остаться без подзадач
                !newEpic.getSubTasks().isEmpty(); // Новый эпик должен сохранить подзадачу
    }

    @Test
    void deleteSubTasks() throws ManagerSaveException {
        SubTask subTask2 = createSubTask2();

        // Создать историю просмотра
        taskManager.getSubTask(subTask2.getId());

        // Удалить подзадачи
        taskManager.deleteSubTasks();

        // Тест
        assert taskManager.getSubTasks().isEmpty() && // Список подзадач должен быть пустой,
                taskManager.getEpics().contains(subTask2.getEpic()) && // а эпик 2 должен остаться
                !historyManager.getHistory().contains(subTask2); // и подзадача должна быть удалена из истории просмотра
    }

    @Test
    void getSubTask() throws ManagerSaveException {
        clearTaskManager();
        SubTask subTask = createSubTask1();
        SubTask foundSubTask = taskManager.getSubTask(subTask.getId());

        assert subTask.equals(foundSubTask) &&
                historyManager.getHistory().contains(foundSubTask); // Проверить историю просмотра
    }

    @Test
    void getEpics() throws ManagerSaveException {
        clearTaskManager();
        createEpic1();
        createEpic2();

        assert taskManager.getEpics().size() == 2;
    }

    @Test
    void addEpic() throws ManagerSaveException {
        clearTaskManager();
        taskManager.getEpics().clear();
        createEpic1();

        assert !taskManager.getEpics().isEmpty();
    }

    @Test
    void deleteEpic() throws ManagerSaveException {
        clearTaskManager();
        SubTask subTask = createSubTask1();
        Epic epic = subTask.getEpic();
        // Создать историю просмотра
        taskManager.getSubTask(subTask.getId());
        taskManager.getEpic(epic.getId());
        Set<Task> testCollection = Set.of(subTask, epic);
        // Удалить эпик
        taskManager.deleteEpic(epic.getId());

        assert taskManager.getEpic(epic.getId()) == null &&
                taskManager.getSubTask(subTask.getId()) == null &&
                !historyManager.getHistory().containsAll(testCollection); // Проверить очистку истории просмотра

    }

    @Test
    void updateEpic() throws ManagerSaveException {
        clearTaskManager();

        // Создадим подзадачу 1 вместе с эпиком 1 в статусе DONE
        SubTask subtask1 = createSubTask1();
        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subtask1);
        Epic epic1 = subtask1.getEpic();

        //Создадим подзадачу 2 и эпик 2 в статусе IN_PROGRESS
        SubTask subtask2 = createSubTask2();
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        Epic epic2 = subtask2.getEpic();

        // Подменим id Эпика 2 на эпик 1
        epic2.setId(epic1.getId());

        // Обновим эпик 1
        taskManager.updateEpic(epic2);

        // Проверка №1 Подзадача 1 должна удалится из списка подзадач и эпика 1
        if (taskManager.getSubTask(subtask1.getId()) != null || epic1.getSubTasks().containsValue(subtask1)) {
            assert false;
            return;
        }
        // Проверка №2 Эпик 1 должен содержать подзадачу 2 и статус должен быть IN_PROGRESS
        if (!epic1.getSubTasks().containsValue(subtask2) || epic1.getStatus() != TaskStatus.IN_PROGRESS) {
            assert false;
            return;
        }
        assert true;
    }

    @Test
    void deleteEpics() throws ManagerSaveException {
        clearTaskManager();
        createEpic1();
        createEpic2();
        taskManager.deleteEpics();

        assert taskManager.getEpics().isEmpty() && taskManager.getSubTasks().isEmpty();
    }

    @Test
    void getEpic() throws ManagerSaveException {
        clearTaskManager();
        Epic epic1 = createEpic1();
        Epic foundEpic = taskManager.getEpic(epic1.getId());

        assert epic1.equals(foundEpic) &&
            historyManager.getHistory().contains(foundEpic); // Проверить историю просмотра

    }

    @Test
    void getSubTasksByEpic() throws ManagerSaveException {
        clearTaskManager();
        SubTask subtask1 = createSubTask1();
        SubTask subTask2 = new SubTask(SUBTASK_NAME_2, SUBTASK_DESCRIPTION_2, subtask1.getEpic());
        taskManager.addSubTask(subTask2);
        ArrayList<SubTask> subtasks = taskManager.getSubTasksByEpic(subtask1.getEpic());
        assert subtasks.size() == 2;
    }

}