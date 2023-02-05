package ru.yandex.bobrikov.kanban.manager.memory;

import ru.yandex.bobrikov.kanban.manager.exception.PeriodIntersectionException;
import ru.yandex.bobrikov.kanban.manager.TaskManager;
import ru.yandex.bobrikov.kanban.task.Epic;
import ru.yandex.bobrikov.kanban.task.Subtask;
import ru.yandex.bobrikov.kanban.task.Task;
import ru.yandex.bobrikov.kanban.manager.HistoryManager;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager, Serializable {
    private static final long serialVersionUID = 5L;
    private int lastAssignedId; //Последний назначенный идентификатор задачи, подзадачи, эпика
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final TreeSet<Task> tasksByStartTime = new TreeSet<>(Comparator.comparing(task ->
            task.getStartTime() == null ? LocalDateTime.MAX : task.getStartTime()));

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.lastAssignedId = 0;
        this.historyManager = historyManager;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    private boolean hasPeriodIntersection(Task task) {
        if (task.getStartTime() == null) {
            return false;
        }

        return tasksByStartTime.stream().anyMatch(task1 ->
                task1.getStartTime() != null
                        && task1.getEndTime() != null
                        && !(task.getEndTime().isBefore(task1.getStartTime()) ||
                        task.getStartTime().isAfter(task1.getEndTime())
                ));

    }

    @Override
    public Task addTask(Task task) {

        // восстановить Id
        if (task.getId() == 0) {
            task.setId(++lastAssignedId);
        } else if (lastAssignedId < task.getId()) {
            lastAssignedId = task.getId();
        }
        // проверить пересечения время начала и окончания задачи
        if (hasPeriodIntersection(task)) {
            throw new PeriodIntersectionException("Задачу нельзя добавить, есть перечение периодов!");
        }
        tasks.put(task.getId(), task);

        // Добавление задачи в отсортированный список по времени начала задачи
        tasksByStartTime.removeIf(task1 -> task1.getId() == task.getId());
        tasksByStartTime.add(task);

        return task;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        // Проверка на существоание эпика
        if (subtask == null || subtask.getEpic() == null) {
            return null;
        }
        Epic epic = epics.get(subtask.getEpic().getId());
        // проверяем существоание эпика по id
        if (epic == null) {
            return null;
        }

        // восстановить Id
        if (subtask.getId() == 0) {
            subtask.setId(++lastAssignedId);
        } else if (lastAssignedId < subtask.getId()) {
            lastAssignedId = subtask.getId();
        }
        // проверить пересечения время начала и окончания задачи
        if (hasPeriodIntersection(subtask)) {
            throw new PeriodIntersectionException("Задачу нельзя добавить, есть перечение периодов!");
        }
        // Добавление подзадачи в список подзадач
        subtasks.put(subtask.getId(), subtask);

        // Добавление подзадачи в эпик
        epic.getSubTasks().put(subtask.getId(), subtask);

        // Обновление статуса эпика
        epic.updateStatus();

        // Добавление подзадачи в отсортированный список по времени начала задачи
        tasksByStartTime.removeIf(subtask1 -> subtask1.getId() == subtask.getId());
        tasksByStartTime.add(subtask);

        return subtask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        //Обновить Id
        if (epic.getId() == 0) {
            epic.setId(++lastAssignedId);

        } else if (lastAssignedId < epic.getId()) {
            lastAssignedId = epic.getId();
        }

        //Добавление эпика в список
        epics.put(epic.getId(), epic);

        return epic;
    }

    @Override
    public void deleteTasks() {
        // Удалить историю просмотра
        for (Task task : tasks.values()) {
            historyManager.remove(task);
        }
        // Удалить отсортированные задачи
        tasksByStartTime.removeIf(Task.class::isInstance);

        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        // Удалить историю просмотра
        for (Subtask subTask : subtasks.values()) {
            historyManager.remove(subTask);
        }

        // Удалить только подзадачи из эпиков.
        // Эпики должны сохраниться.
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
        }
        // Удалить отсортированные задачи
        tasksByStartTime.removeIf(Subtask.class::isInstance);

        subtasks.clear();
    }

    @Override
    public void deleteEpics() {
        // Удалить историю просмотра
        for (Subtask subTask : subtasks.values()) {
            historyManager.remove(subTask);
        }

        for (Epic epic : epics.values()) {
            historyManager.remove(epic);
        }
        // Удалить отсортированные задачи
        tasksByStartTime.removeIf(Subtask.class::isInstance);
        subtasks.clear();

        epics.clear();
    }

    @Override
    public Task getTask(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = epics.get(epicId);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int subTaskId) {
        Subtask subTask = subtasks.get(subTaskId);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(tasksByStartTime);
    }

    @Override
    public void deleteTask(int id) {
        // Удаление задачи из истории просмотров
        final Task task = tasks.get(id);
        if (task == null) {
            return;
        }
        historyManager.remove(task);

        // Удалить задачу из отсортированного списка
        tasksByStartTime.remove(task);

        // удалить задачу
        tasks.remove(id);
    }

    @Override
    public boolean deleteSubTask(int id) {
        // Проверка существования подзадачи
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return false;
        }
        // Удаление подзадачи из истории просмотров
        historyManager.remove(subtask);

        // Удаление подзадачи из эпика
        Epic epic = subtask.getEpic();
        epic.getSubTasks().remove(id);
        epic.updateStatus();

        //Удаление подзадачи
        subtasks.remove(subtask.getId());

        // Удаление подзадачи из отсортированного список по времени начала задачи
        tasksByStartTime.remove(subtask);

        return true;
    }

    @Override
    public void deleteEpic(int id) {
        // Проверка существования эпика
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        // Удаление подзадач из истории просмотра
        for (Subtask subTask : epic.getSubTasks().values())
            historyManager.remove(subTask);

        // Удаление эпика из истории просмотров
        historyManager.remove(epic);

        // Удаление подзадач
        for (Integer subTaskId : epic.getSubTasks().keySet()) {
            Subtask subtask = subtasks.get(subTaskId);
            // Удаление подзадачи из истории просмотров
            historyManager.remove(subtask);

            // Удаление подзадачи
            subtasks.remove(subTaskId);
            tasksByStartTime.remove(subtask);
        }
        // удаление эпика
        epics.remove(id);
    }

    @Override
    public Task updateTask(Task task) {
        // Проверка на существование задачи
        final Task taskToUpdate = tasks.get(task.getId());
        if (taskToUpdate == null) {
            return null;
        }
        tasksByStartTime.removeIf(task1 -> task1.getId() == taskToUpdate.getId());

        // Обновить поля
        taskToUpdate.setName(task.getName());
        taskToUpdate.setDescription(task.getDescription());
        taskToUpdate.setStatus(task.getStatus());
        taskToUpdate.setStartTime(task.getStartTime());
        taskToUpdate.setDuration(task.getDuration());

        // проверить пересечения времени начала и окончания задачи
        if (hasPeriodIntersection(task)) {
            throw new PeriodIntersectionException("Задачу нельзя добавить, есть перечение периодов!");
        }
        tasksByStartTime.add(taskToUpdate);
        return taskToUpdate;
    }

    @Override
    public Subtask updateSubTask(Subtask srcSubtask) {

        final int subtaskId = srcSubtask.getId();
        final int epicId = srcSubtask.getEpic().getId();

        // проверить существование подзадачи
        final Subtask dstSubtask = subtasks.get(subtaskId);
        if (dstSubtask == null) {
            return null;
        }

        // проверить существоание эпика
        Epic dstEpic = epics.get(epicId);
        if (dstEpic == null) {
            return null;
        }

        // Удалить подзадачу из эпика, если она ему уже не принадлежит
        Epic srcEpic = srcSubtask.getEpic();
        if (!dstSubtask.getEpic().equals(srcEpic)) {
            srcEpic.getSubTasks().remove(subtaskId);

            // Обновить старый эпик
            srcEpic.updateStatus();
        }

        // обновить подзадачу
        dstSubtask.setName(srcSubtask.getName());
        dstSubtask.setDescription(srcSubtask.getDescription());
        dstSubtask.setStartTime(srcSubtask.getStartTime());
        dstSubtask.setDuration(srcSubtask.getDuration());
        dstSubtask.setStatus(srcSubtask.getStatus());
        dstSubtask.setEpic(dstEpic);

        // обновить эпик подзадачи
        dstEpic.getSubTasks().put(subtaskId, dstSubtask);
        dstEpic.updateStatus();

        // удалить задачу из отсортированного спика
        tasksByStartTime.removeIf(task -> task.getId() == dstSubtask.getId());

        // проверить пересечения времени начала и окончания подзадач
        if (hasPeriodIntersection(dstSubtask)) {
            throw new PeriodIntersectionException("Подзадачу нельзя добавить, есть перечение периодов!");
        }

        // обновить порядок задач
        tasksByStartTime.add(dstSubtask);

        return dstSubtask;
    }

    @Override
    public Epic updateEpic(Epic srcEpic) {
        // проверить существование эпика
        Epic epicToUpdate = epics.get(srcEpic.getId());
        if (epicToUpdate == null) {
            return null;
        }

        // проверить существоание переданных подзадач в менеджере задач
        for (Subtask subTask : srcEpic.getSubTasks().values()) {
            if (subtasks.get(subTask.getId()) == null) {
                return null;
            }
        }

        // удалить из менеджера подзадачи, которые есть в эпике для обновления, но нет в источнике
        epicToUpdate.getSubTasks().keySet().stream()
                .filter(subtaskId -> !srcEpic.getSubTasks().containsKey(subtaskId))
                .forEach(subtaskId -> {
                    srcEpic.getSubTasks().remove(subtaskId);
                    subtasks.remove(subtaskId);
                });

        // добавить или обновить подзадачи
        srcEpic.getSubTasks().keySet()
                .forEach(id -> epicToUpdate.getSubTasks().put(id, subtasks.get(id)));

        // Обновление полей
        epicToUpdate.setName(srcEpic.getName());
        epicToUpdate.setDescription(srcEpic.getDescription());
        epicToUpdate.updateStatus();
        return epicToUpdate;
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        return new ArrayList<>(epic.getSubTasks().values());
    }

    @Override
    public String toString() {
        return "InMemoryTaskManager{" +
                "lastAssignedId=" + lastAssignedId +
                ", tasks=" + tasks +
                ", subTasks=" + subtasks +
                ", epics=" + epics +
                ", historyManager=" + historyManager +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InMemoryTaskManager)) return false;
        InMemoryTaskManager that = (InMemoryTaskManager) o;
        return lastAssignedId == that.lastAssignedId &&
                Objects.equals(getTasks(), that.getTasks()) &&
                Objects.equals(getSubtasks(), that.getSubtasks()) &&
                Objects.equals(getEpics(), that.getEpics()) &&
                Objects.equals(getHistory(), that.getHistory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastAssignedId, getTasks(), getSubtasks(), getEpics(), getHistoryManager());
    }

}

