package kanban.controller;

import kanban.model.Epic;
import kanban.model.SubTask;
import kanban.model.Task;
import kanban.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int lastUsedId = 0; //Последний используемый идентификатор задачи, подзадачи, эпика
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public Task addTask(Task task) {
        // Обновление свойств
        task.setId(++lastUsedId);
        task.setStatus(TaskStatus.NEW);
        tasks.put(task.getId(), task);

        return task;
    }

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
        // восстановление ссылочной целостности
        subTask.setId(++lastUsedId);
        subTask.setEpic(epic);
        // Добавление подзадачи в список подзадач
        subTasks.put(subTask.getId(), subTask);

        // Добавление подзадачи в эпик
        epic.getSubTasks().put(subTask.getId(), subTask);

        // Обновление статуса эпика
        updateEpicStatus(epic);

        return subTask;
    }

    private void updateEpicStatus(Epic epic) {

        if (epic == null){
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

    public Epic addEpic(Epic epic) {
        //Обновление свойств
        epic.setId(++lastUsedId);
        epic.setStatus(TaskStatus.NEW);

        //Добавление эпика в список
        epics.put(epic.getId(), epic);

        return epic;
    }

    public void updateSubTaskStatus(SubTask subTask, TaskStatus status) {
        subTask.setStatus(status);
        updateEpicStatus(subTask.getEpic());
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteSubTasks() {
        // Удаление только подзадач из эпиков.
        // Эпики должны сохраниться.
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
        }
        subTasks.clear();
    }

    public void deleteEpics() {
        subTasks.clear();
        epics.clear();
    }

    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    public Epic getEpic(int epicId) {
        return epics.get(epicId);
    }

    public SubTask getSubTask(int subTaskId) {

        return subTasks.get(subTaskId);
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public boolean deleteSubTask(int id) {
        // Проверка существования подзадачи
        SubTask subTask = subTasks.get(id);
        if (subTask == null) {
            return false;
        }
        // Удаление подзадачи из эпика
        Epic epic = subTask.getEpic();
        epic.getSubTasks().remove(id);
        updateEpicStatus(epic);

        //Удаление подзадачи
        subTasks.remove(subTask.getId());

        return true;
    }

    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        // Удаление подзадач
        for (Integer subTaskId : epic.getSubTasks().keySet()) {
            subTasks.remove(subTaskId);
        }
        // удаление эпика
        epics.remove(id);
    }

    public Task updateTask(Task task) {
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

    public SubTask updateSubTask(SubTask newSubTask) {

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

    public Epic updateEpic(Epic newEpic) {
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

    public ArrayList<SubTask> getSubTasksByEpic(Epic epic) {
        return new ArrayList<>(epic.getSubTasks().values());
    }
}

