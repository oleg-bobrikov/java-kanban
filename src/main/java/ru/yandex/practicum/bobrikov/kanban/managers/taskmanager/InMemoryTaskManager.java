package ru.yandex.practicum.bobrikov.kanban.managers.taskmanager;


import ru.yandex.practicum.bobrikov.kanban.managers.historymanager.HistoryManager;

import ru.yandex.practicum.bobrikov.kanban.model.Epic;
import ru.yandex.practicum.bobrikov.kanban.model.SubTask;
import ru.yandex.practicum.bobrikov.kanban.model.Task;
import ru.yandex.practicum.bobrikov.kanban.model.TaskStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager, Serializable {
    private static final long serialVersionUID = 5L;
    private int lastAssignedId; //Последний назначенный идентификатор задачи, подзадачи, эпика
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

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
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Task addTask(Task task) {

        // восстановить Id
        if (task.getId() == 0) {
            task.setId(++lastAssignedId);
        } else if (lastAssignedId < task.getId()) {
            lastAssignedId = task.getId();
        }

        tasks.put(task.getId(), task);

        return task;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        // Проверка на существоание эпика
        if (subTask == null || subTask.getEpic() == null) {
            return null;
        }
        Epic epic = epics.get(subTask.getEpic().getId());
        // проверяем существоание эпика по id
        if (epic == null) {
            return null;
        }

        // восстановить Id
        if (subTask.getId() == 0) {
            subTask.setId(++lastAssignedId);
        } else if (lastAssignedId < subTask.getId()) {
            lastAssignedId = subTask.getId();
        }

        // Добавление подзадачи в список подзадач
        subTasks.put(subTask.getId(), subTask);

        // Добавление подзадачи в эпик
        epic.getSubTasks().put(subTask.getId(), subTask);

        // Обновление статуса эпика
        updateEpicStatus(epic);

        return subTask;
    }

    private void updateEpicStatus(Epic epic) {

        if (epic == null) {
            return;
        }
        boolean hasInProgress = false;
        boolean hasDone = false;
        boolean hasNew = false;
        for (SubTask subTask : epic.getSubTasks().values()) {
            if (subTask.getStatus() == TaskStatus.IN_PROGRESS) {
                hasInProgress = true;
                break;
            } else if (subTask.getStatus() == TaskStatus.DONE) {
                hasDone = true;
            } else {
                hasNew = true;
            }
        }
        if (hasInProgress || hasDone && hasNew) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else if (hasDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
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
    public void deleteTasks()  {
        // Удалить историю просмотра
        for (Task task : tasks.values()) {
            historyManager.remove(task);
        }

        tasks.clear();
    }

    @Override
    public void deleteSubTasks()  {
        // Удалить историю просмотра
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask);
        }
        // Удалить только подзадачи из эпиков.
        // Эпики должны сохраниться.
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
        }
        subTasks.clear();
    }

    @Override
    public void deleteEpics()  {
        // Удалить историю просмотра
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask);
        }

        for (Epic epic : epics.values()) {
            historyManager.remove(epic);
        }

        subTasks.clear();
        epics.clear();
    }

    @Override
    public Task getTask(int taskId)  {
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
    public SubTask getSubTask(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public void deleteTask(int id)  {
        // Удаление задачи из истории просмотров
        historyManager.remove(tasks.get(id));
        // Удаление задачи
        tasks.remove(id);
    }

    @Override
    public boolean deleteSubTask(int id)  {
        // Проверка существования подзадачи
        SubTask subTask = subTasks.get(id);
        if (subTask == null) {
            return false;
        }
        // Удаление подзадачи из истории просмотров
        historyManager.remove(subTask);

        // Удаление подзадачи из эпика
        Epic epic = subTask.getEpic();
        epic.getSubTasks().remove(id);
        updateEpicStatus(epic);

        //Удаление подзадачи
        subTasks.remove(subTask.getId());

        return true;
    }

    @Override
    public void deleteEpic(int id)  {
        // Проверка существования эпика
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        // Удаление подзадач из истории просмотра
        for (SubTask subTask : epic.getSubTasks().values())
            historyManager.remove(subTask);

        // Удаление эпика из истории просмотров
        historyManager.remove(epic);

        // Удаление подзадач
        for (Integer subTaskId : epic.getSubTasks().keySet()) {
            // Удаление подзадачи из истории просмотров
            historyManager.remove(subTasks.get(subTaskId));
            // Удаление подзадачи
            subTasks.remove(subTaskId);
        }
        // удаление эпика
        epics.remove(id);
    }

    @Override
    public Task updateTask(Task task)  {
        // Проверка на существование задачи
        Task taskToUpdate = tasks.get(task.getId());
        if (taskToUpdate == null) {
            return null;
        }
        taskToUpdate.setName(task.getName());
        taskToUpdate.setDescription(task.getDescription());
        taskToUpdate.setStatus(task.getStatus());
        return null;
    }

    @Override
    public SubTask updateSubTask(SubTask newSubTask)  {

        // проверка на существование подзадачи
        SubTask oldSubTask = subTasks.get(newSubTask.getId());
        if (oldSubTask == null) {
            return null;
        }
        Epic oldEpic = oldSubTask.getEpic();

        // проверка на существоание нового эпика
        Epic newEpic = epics.get(newSubTask.getEpic().getId());
        if (epics.get(newEpic.getId()) == null) {
            return null;
        }

        // удаление подзадачи из старого эпика, если ее нет в новом.
        // Подзадача может быть перемещена в дргуой эпик
        if (!newEpic.getSubTasks().containsKey(oldSubTask.getId())) {
            oldEpic.getSubTasks().remove(oldSubTask.getId());
            updateEpicStatus(oldEpic);
        }

        // обновление старой подзадачи
        oldSubTask.setName(newSubTask.getName());
        oldSubTask.setDescription(newSubTask.getDescription());
        oldSubTask.setStatus(newSubTask.getStatus());
        oldSubTask.setEpic(newEpic);

        // Обновление старой подзадачи в новом эпике
        newEpic.getSubTasks().put(oldSubTask.getId(), oldSubTask);
        updateEpicStatus(oldSubTask.getEpic());
        return oldSubTask;
    }

    @Override
    public Epic updateEpic(Epic newEpic)  {
        // проверка на существование эпика
        Epic oldEpic = epics.get(newEpic.getId());
        if (oldEpic == null) {
            return null;
        }

        // проверка на существоание подзадач
        for (SubTask subTask : newEpic.getSubTasks().values()) {
            if (subTasks.get(subTask.getId()) == null) {
                return null;
            }
        }

        // удаление подзадач,  которые есть в старом эпике, но которых нет в новом
        for (SubTask subTask : oldEpic.getSubTasks().values()) {
            if (newEpic.getSubTasks().get(subTask.getId()) == null) {
                subTasks.remove(subTask.getId());
                oldEpic.getSubTasks().remove(subTask.getId());
            }
        }

        // добавление или обновление подзадач
        for (SubTask subTask : newEpic.getSubTasks().values()) {
            oldEpic.getSubTasks().put(subTask.getId(), subTasks.get(subTask.getId()));
        }
        updateEpicStatus(oldEpic);
        return oldEpic;

    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpic(Epic epic) {
        return new ArrayList<>(epic.getSubTasks().values());
    }

    @Override
    public String toString() {
        return "InMemoryTaskManager{" +
                "lastAssignedId=" + lastAssignedId +
                ", tasks=" + tasks +
                ", subTasks=" + subTasks +
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
                Objects.equals(getSubTasks(), that.getSubTasks()) &&
                Objects.equals(getEpics(), that.getEpics()) &&
                Objects.equals(getHistory(), that.getHistory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastAssignedId, getTasks(), getSubTasks(), getEpics(), getHistoryManager());
    }
}

