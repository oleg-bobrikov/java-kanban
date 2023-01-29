package kanban.managers.taskmanager;

import kanban.exceptions.ManagerSaveException;
import kanban.exceptions.PeriodIntersectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import kanban.managers.historymanager.HistoryManager;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import kanban.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
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

    protected T taskManager;
    protected HistoryManager historyManager;

    protected abstract T createInstance();

    @BeforeEach
    protected void init() {
        taskManager = createInstance();
        historyManager = taskManager.getHistoryManager();
    }

    @Test
    public void epicStatusShouldBeNewWhenSubtaskListIsEmpty() {
        Epic epic1 = addEpic1();
        assertEquals(TaskStatus.NEW, epic1.getStatus());
    }

    @Test
    public void epicStatusShouldBeNewWhenAllSubtasksAreNew() {
        Epic epic1 = addEpic1();

        Subtask subTask1 = addSubTask1(epic1);
        subTask1.setStatus(TaskStatus.NEW);

        Subtask subTask2 = addSubTask2(epic1);
        subTask2.setStatus(TaskStatus.NEW);

        assertEquals(TaskStatus.NEW, epic1.getStatus(), "Неверный статус эпика. Ожидается NEW.");
    }

    @Test
    public void epicStatusShouldBeDoneWhenAllSubtasksHaveDone() {
        Epic epic1 = addEpic1();

        Subtask subTask1 = addSubTask1(epic1);
        subTask1.setStatus(TaskStatus.DONE);

        Subtask subTask2 = addSubTask2(epic1);
        subTask2.setStatus(TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, epic1.getStatus(), "Неверный статус эпика.");
    }

    @Test
    public void epicStatusShouldBeInProgressWhenSubtasksAreNewAndDone() {
        Epic epic1 = addEpic1();

        Subtask subTask1 = addSubTask1(epic1);
        subTask1.setStatus(TaskStatus.NEW);

        Subtask subTask2 = addSubTask2(epic1);
        subTask2.setStatus(TaskStatus.DONE);

        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus(), "Неверный статус эпика.");
    }

    @Test
    public void epicStatusShouldBeInProgressWhenAnySubtaskIsInProgress() {
        Epic epic1 = addEpic1();

        Subtask subTask1 = addSubTask1(epic1);
        subTask1.setStatus(TaskStatus.IN_PROGRESS);

        Subtask subTask2 = addSubTask2(epic1);
        subTask2.setStatus(TaskStatus.NEW);

        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus(), "Неверный статус эпика.");

        subTask2.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus(), "Неверный статус эпика.");
    }

    @Test
    void addSubTask() {
        Epic epic1 = addEpic1();
        addSubTask1(epic1);
        assertEquals(1, taskManager.getSubtasks().size());
    }

    @Test
    void addTask() {
        Task task = new Task(TASK_NAME_1, TASK_DESCRIPTION_1);

        final int taskId = taskManager.addTask(task).getId();
        final Task savedTask = taskManager.getTask(taskId);
        task.setId(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addEpic() {
        Epic newEpic = addEpic1();
        Epic foundEpic = taskManager.getEpic(newEpic.getId());

        assertEquals(newEpic, foundEpic, "Эпики не совпадают.");
    }

    @Test
    void updateTask() {

        Task task = new Task(TASK_NAME_1, TASK_DESCRIPTION_1);
        final int taskId = taskManager.addTask(task).getId();
        final Task savedTask = taskManager.getTask(taskId);
        task.setId(taskId);

        task.setName(TASK_NAME_2);
        task.setDescription(TASK_DESCRIPTION_2);
        task.setStatus(TaskStatus.IN_PROGRESS);

        taskManager.updateTask(task);
        assertEquals(task, savedTask, "Задачи не совпадают.");

        // Неверный идентификатор задачи
        task.setId(1000);
        assertNull(taskManager.updateTask(task));
    }

    @Test
    void updateEpic() {
        //Создать новый эпик
        Epic epic = new Epic(EPIC_NAME_1, EPIC_DESCRIPTION_1);
        LocalDateTime now = LocalDateTime.now();
        taskManager.addEpic(epic);
        Subtask subtask = addSubTask1(epic);
        subtask.setStartTime(now.plusHours(1));
        subtask.setDuration(60);
        taskManager.updateSubTask(subtask);

        Epic foundEpic = taskManager.addEpic(epic);
        foundEpic.getId();
        // Изменить поля
        epic.setName(EPIC_NAME_2);
        epic.setDescription(EPIC_DESCRIPTION_2);
        Epic updatedEpic = taskManager.updateEpic(epic);
        // Проверить поля
        assertEquals(EPIC_NAME_2, updatedEpic.getName());
        assertEquals(EPIC_DESCRIPTION_2, updatedEpic.getDescription());
        assertEquals(now.plusHours(1), updatedEpic.getStartTime());
        assertEquals(60, updatedEpic.getDuration());
        // Очистим данные
        taskManager.deleteEpics();

        // Создадим подзадачу 1 вместе с эпиком 1 в статусе DONE
        Epic epic1 = addEpic1();
        Subtask subtask1 = addSubTask1(epic1);
        subtask1.setStatus(TaskStatus.DONE);

        //Создадим подзадачу 2 и эпик 2 в статусе IN_PROGRESS
        Epic epic2 = addEpic2();
        Subtask subtask2 = addSubTask2(epic2);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);

        // Подменим id Эпика 2 на эпик 1
        epic2.setId(epic1.getId());

        // Обновим эпик 1
        taskManager.updateEpic(epic2);

        // Проверка №1 Подзадача 1 должна удалится из списка подзадач и эпика 1
        assertNull(taskManager.getSubtask(subtask1.getId()), "Подзадача 1 не удалена.");
        assertFalse(epic2.getSubTasks().containsValue(subtask1), "Подзадача 1 не удалена из эпика 2");

        // Проверка №2 Эпик 1 должен содержать подзадачу 2 и статус должен быть IN_PROGRESS
        assertTrue(epic1.getSubTasks().containsValue(subtask2), "Эпик 2 не содержит подзадачу 2.");
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus(), "Неверный статус эпика");

        // Проверка на неверный идентификатор эпика
        epic2.setId(1000);
        assertNull(taskManager.updateEpic(epic2));
    }

    @Test
    void getTask() {
        Task task = addTask1();
        final int taskId = task.getId();
        Task foundTask = taskManager.getTask(taskId);
        assertEquals(task, foundTask); // Проверить историю просмотра

        // Неверный идентификатор задачи
        assertNull(taskManager.getTask(taskId + 1000));
    }

    @Test
    void getTasks() {
        Task task = addTask1();
        // Создаь историю просмотра
        taskManager.getTask(task.getId());

        assertEquals(1, taskManager.getTasks().size(), "Неверное количество просмотренных задач.");

        assertTrue(historyManager.getHistory().contains(task), "Задача не найдена в истории просмотра.");
    }

    @Test
    void getEpic() {
        final Epic epic = new Epic(EPIC_NAME_1, EPIC_DESCRIPTION_1);
        final int epicId = taskManager.addEpic(epic).getId();
        assertEquals(epic, taskManager.getEpic(epicId));
    }

    @Test
    void getEpics() {
        addEpic1();
        addEpic2();

        assertEquals(2, taskManager.getEpics().size(), "неверное количество эпиков");
    }

    @Test
    void getSubTask() {

        Epic epic1 = addEpic1();
        Subtask subtask = addSubTask1(epic1);
        final int subtaskId = subtask.getId();
        Subtask foundSubtask = taskManager.getSubtask(subtaskId);

        assertEquals(subtask, foundSubtask, "Задачи не совпадают");
        assertTrue(historyManager.getHistory().contains(foundSubtask), "Не найдена задача в истории просмотра.");

    }

    @Test
    void getSubTasksByEpic() {
        Epic epic1 = addEpic1();
        addSubTask1(epic1);
        addSubTask2(epic1);
        assertEquals(2, taskManager.getSubtasksByEpic(epic1).size(), "Количество подзадач не совпадает.");
    }

    @Test
    void getSubTasks() {
        Epic epic1 = addEpic1();
        addSubTask1(epic1);
        assertEquals(1, taskManager.getSubtasks().size(), "Неверное количество подзадач.");
    }

    @Test
    void deleteTask() {
        Task task1 = addTask1();
        final int taskId = task1.getId();

        // Создать историю просмотра
        taskManager.getTask(taskId);

        taskManager.deleteTask(taskId);
        assertTrue(taskManager.getTasks().isEmpty(), "Список задач не пустой");

        // Неверный идентификатор задачи
        Task task2 = addTask2();
        final int task2Id = task2.getId();
        // Создать историю просмотра
        taskManager.getTask(task2Id);
        taskManager.deleteTask(task2Id + 1000);
        assertFalse(taskManager.getTasks().isEmpty());
        assertFalse(taskManager.getHistory().isEmpty());

    }

    @Test
    void deleteEpic() {
        Epic epic1 = addEpic1();
        Subtask subTask1 = addSubTask1(epic1);

        // Создать историю просмотра
        taskManager.getSubtask(subTask1.getId());
        taskManager.getEpic(epic1.getId());
        Set<Task> testCollection = Set.of(subTask1, epic1);

        // Удалить эпик
        taskManager.deleteEpic(epic1.getId());
        assertNull(taskManager.getEpic(epic1.getId()), "Эпик не удален.");
        assertNull(taskManager.getSubtask(subTask1.getId()), "Подзадача не удалена.");
        assertFalse(historyManager.getHistory().containsAll(testCollection), "История просмотра не очищена.");

    }

    @Test
    void deleteEpics() throws ManagerSaveException {
        Epic epic1 = addEpic1();
        Subtask subTask1 = addSubTask1(epic1);

        // Добавить историю просмотра
        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subTask1.getId());

        Epic epic2 = addEpic2();
        Subtask subTask2 = addSubTask2(epic2);
        // Добавить историю просмотра
        taskManager.getEpic(epic2.getId());
        taskManager.getSubtask(subTask2.getId());

        taskManager.deleteEpics();

        assertTrue(taskManager.getEpics().isEmpty(), "Список эпиков не пустой.");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Список подзадач не пустой.");

    }

    @Test
    void deleteTasks() {
        Task task1 = addTask1();
        Task task2 = addTask2();

        // Создать историю просмотра
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());

        taskManager.deleteTasks();
        assertTrue(taskManager.getTasks().isEmpty(), "Список задач не пустой");

        assertFalse(historyManager.getHistory().contains(task1), "Задача 1 не удалена из истории просмотра.");
        assertFalse(historyManager.getHistory().contains(task2), "Задача 2 не удалена из истории просмотра.");
    }

    @Test
    void deleteSubTasks() {
        Epic epic1 = addEpic1();
        addSubTask1(epic1);

        Epic epic2 = addEpic2();
        addSubTask2(epic2);

        // Удалить подзадачи
        taskManager.deleteSubtasks();

        assertEquals(0, taskManager.getSubtasks().size(), "Неверное количество подзадач.");
        assertTrue(historyManager.getHistory().isEmpty(), "Не очистилась история просмотра");

    }


    Task addTask1() {
        Task newTask = new Task(TASK_NAME_1, TASK_DESCRIPTION_1);
        taskManager.addTask(newTask);
        return newTask;
    }

    Task addTask2() {
        Task newTask = new Task(TASK_NAME_2, TASK_DESCRIPTION_2);
        taskManager.addTask(newTask);
        return newTask;
    }


    Epic addEpic1() {
        Epic newEpic = new Epic(EPIC_NAME_1, EPIC_DESCRIPTION_1);
        taskManager.addEpic(newEpic);
        return newEpic;
    }

    Epic addEpic2() {
        Epic newEpic = new Epic(EPIC_NAME_2, EPIC_DESCRIPTION_2);
        taskManager.addEpic(newEpic);
        return newEpic;
    }


    Subtask addSubTask1(Epic epic) {
        Subtask newSubtask = new Subtask(SUBTASK_NAME_1, SUBTASK_DESCRIPTION_1, epic);
        taskManager.addSubtask(newSubtask);
        return newSubtask;
    }

    Subtask addSubTask2(Epic epic) {
        Subtask newSubtask = new Subtask(SUBTASK_NAME_2, SUBTASK_DESCRIPTION_2, epic);
        taskManager.addSubtask(newSubtask);
        return newSubtask;
    }

    @Test
    void deleteSubTask() {
        Epic epic1 = addEpic1();
        Subtask subTask = addSubTask1(epic1);
        final int subtaskId = subTask.getId();

        // Создать историю просмотра
        taskManager.getSubtask(subtaskId);

        // Удалить подзадачу
        taskManager.deleteSubTask(subtaskId);
        assertEquals(0, taskManager.getSubtasks().size(), "Неверное количество подзадач.");
        assertTrue(historyManager.getHistory().isEmpty(), "Не очистилась история просмотра");
    }

    @Test
    void updateSubTask() {
        // Создание подзадачи вместе с эпиком
        Epic oldEpic = addEpic1();
        Subtask oldSubtask = addSubTask1(oldEpic);
        final int oldSubtaskId = oldSubtask.getId();

        //Изменить имя, описание и владельца
        Epic newEpic = addEpic2();
        Subtask newSubtask = new Subtask(SUBTASK_NAME_2, SUBTASK_DESCRIPTION_2, newEpic);
        newSubtask.setEpic(newEpic);

        // Новой подзадачи присвоить страый id
        newSubtask.setId(oldSubtaskId);

        // Обновить подзадачу
        taskManager.updateSubTask(newSubtask);
        Subtask updatedSubtask = taskManager.getSubtask(oldSubtaskId);

        assertEquals(newSubtask.getId(), updatedSubtask.getId());
        assertEquals(newSubtask.getName(), updatedSubtask.getName());
        assertEquals(newSubtask.getDescription(), updatedSubtask.getDescription());
        assertEquals(newSubtask.getStatus(), updatedSubtask.getStatus());
        assertEquals(newSubtask.getStartTime(), updatedSubtask.getStartTime());
        assertEquals(newSubtask.getDuration(), updatedSubtask.getDuration());

        //Проверка на неверный идентификатор подзадачи
        newSubtask.setId(1000);
        assertNull(taskManager.updateSubTask(newSubtask));

    }


    @Test
    void getPrioritizedTasks() {
        Task task1 = addTask1();
        LocalDateTime now = LocalDateTime.now();
        task1.setStartTime(now);
        taskManager.updateTask(task1); //Порядок: Задача1

        Task task2 = addTask2();
        task2.setStartTime(now.minusHours(1));
        taskManager.updateTask(task2); //Порядок: Задача2, Задача1

        Epic epic1 = addEpic1();
        Subtask subtask1 = addSubTask1(epic1);
        subtask1.setStartTime(now.minusHours(2));
        taskManager.updateSubTask(subtask1);  //Порядок: Подзадача1, Задача2, Задача1

        Subtask subtask2 = addSubTask2(epic1);
        subtask2.setStartTime(now.plusHours(2));//Порядок: Подзадача1, Задача2, Задача1, Подзадача 2

        ArrayList<Task> actual = taskManager.getPrioritizedTasks();
        ArrayList<Task> expected = new ArrayList<>(List.of(subtask1, task2, task1, subtask2));

        assertEquals(actual, expected);
    }

    @Test
    void ShouldBePeriodIntersectionException() {
        Task task1 = addTask1();
        LocalDateTime now = LocalDateTime.now();
        task1.setStartTime(now);
        task1.setDuration(60);
        taskManager.updateTask(task1);

        Task task2 = addTask2();
        task2.setStartTime(now.minusHours(1));
        task2.setDuration(90);
        boolean hasCaughtException = false;
        try {
            taskManager.updateTask(task2);
        } catch (PeriodIntersectionException exception) {
            hasCaughtException = true;
        }
        assertTrue(hasCaughtException);
    }
}



