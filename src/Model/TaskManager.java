package Model;

import java.util.Collection;
import java.util.HashMap;

public class TaskManager {
    private int lastUsedId = 0; //Последний используемый идентификатор задачи, подзадачи, эпика

    private final HashMap<Integer, Task> tasks = new HashMap<>();

    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public Task addTask(Task task) {
        // Обновление свойств
        task.id = ++lastUsedId;
        task.status = Enum.TaskStatus.NEW;
        tasks.put(task.id, task);

        return task;
    }

    public SubTask addSubTask(SubTask subTask) {
        // Проверка на существоание эпика
        if (subTask == null || subTask.epic == null) {
            return null;
        }

        // Добавление подзадачи в список подзадач
        subTask.id = ++lastUsedId;
        subTasks.put(subTask.id, subTask);

        // Добавление подзадачи в эпик
        subTask.epic.subTasks.put(subTask.id, subTask);

        // Обновление статуса эпика
        updateEpicStatus(subTask.epic);

        return subTask;
    }

    private void updateEpicStatus(Epic epic) {

        if (epic == null) return;
        boolean hasInProgress = false;
        boolean hasDone = false;
        boolean hasNew = false;
        for (SubTask subTask : epic.subTasks.values()) {
            if (subTask.status == Enum.TaskStatus.IN_PROGRESS) {
                hasInProgress = true;
                break;
            } else if (subTask.status == Enum.TaskStatus.DONE) {
                hasDone = true;
            } else {
                hasNew = true;
            }
        }
        if (hasInProgress || hasDone && hasNew) {
            epic.status = Enum.TaskStatus.IN_PROGRESS;
        } else if (hasDone) {
            epic.status = Enum.TaskStatus.DONE;
        } else {
            epic.status = Enum.TaskStatus.NEW;
        }
    }

    public Epic addEpic(Epic epic) {
        //Обновление свойств
        epic.id = ++lastUsedId;
        epic.status = Enum.TaskStatus.NEW;

        //Добавление эпика в список
        epics.put(epic.id, epic);

        return epic;
    }

    public void updateStatus(Task task, Enum.TaskStatus status) {
        if (task.getClass().toString().contains("SubTask")) {
            task.status = status;
            updateEpicStatus(((SubTask) task).epic);
        } else if (!task.getClass().toString().contains("Epic")) {
            task.status = status;
        }
    }

    // перегруженный метод
    public void deleteTask(Task task) {
        if (task.getClass().toString().contains("SubTask")) {
            Epic epic = ((SubTask) task).epic;
            epic.subTasks.remove(task.id);
            if (epic.subTasks.isEmpty()) {
                epics.remove(epic.id);
            } else {
                updateEpicStatus(epic);
            }
        } else if (task.getClass().toString().contains("Epic")) {
            Epic epic = (Epic) task;
            for (SubTask subTask : epic.subTasks.values()) {
                subTasks.remove(subTask.id);
            }
            epics.remove(epic.id);
        } else {
            tasks.remove(task.id);
        }
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteSubTasks() {
        epics.clear();
        subTasks.clear();
    }

    public void deleteEpics() {
        deleteSubTasks();
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

    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask == null) return;

        Epic epic = subTask.epic;
        epic.subTasks.remove(id);
        if (epic.subTasks.isEmpty()) {
            epics.remove(epic.id);
        } else {
            updateEpicStatus(epic);
        }
    }

    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) return;
        for (Integer subTaskId : epic.subTasks.keySet()) {
            deleteSubTask(subTaskId);
        }
        epics.remove(id);
    }

    public void deleteEpic(Epic epic) {
        deleteTask(epic);
    }

    public void updateTask(Task task) {
        Task taskToUpdate = tasks.get(task.id);
        if (taskToUpdate == null) return;
        //  можно использовать метод tasks.put(task.id, task),
        // но в этом случае старый объект останется в памяти и нужно вызывать garbage collector
        taskToUpdate.name = task.name;
        taskToUpdate.description = task.description;
        taskToUpdate.status = task.status;
    }

    public void updateSubTask(SubTask subTask) {
        // проверка на существование подзадачи
        SubTask subTaskToUpdate = subTasks.get(subTask.id);
        if (subTaskToUpdate == null) return;

        // проверка на существоание эпика
        if (epics.get(subTask.epic.id) == null) return;

        // удаление подзадачи из старого эпика,
        // так как новая подзадача может быть перемещена в дргуой эпик
        subTaskToUpdate.epic.subTasks.remove(subTask.id);
        if (subTask.epic.subTasks.isEmpty()) {
            epics.remove(subTask.epic.id);
        }
        // обновление новой подзадачи
        // можно использовать метод subTasks.put(subTask.id, subTask),
        // но в этом случае старый объект subTaskToUpdate останется в памяти и нужно вызывать garbage collector
        Epic epicToUpdate = epics.get(subTaskToUpdate.epic.id);
        subTaskToUpdate.name = subTask.name;
        subTaskToUpdate.description = subTask.description;
        subTaskToUpdate.status = subTask.status;
        subTaskToUpdate.epic = epicToUpdate;

        // обновление эпика
        epicToUpdate.subTasks.put(subTaskToUpdate.id, subTaskToUpdate);
        updateEpicStatus(subTaskToUpdate.epic);
    }

    public void updateEpic(Epic epic) {
        // проверка на существование эпика
        if (epics.get(epic.id) == null) return;

        // проверка на существоание подзадач
        for (SubTask subTask : epic.subTasks.values()) {
            if (subTasks.get(subTask.id) == null) return;
        }

        Epic epicToUpdate = epics.get(epic.id);

        // удаление старых подзадач
        epicToUpdate.subTasks.clear();

        // добавление новых подзадач
        for (SubTask subTask : epic.subTasks.values()) {
            epicToUpdate.subTasks.put(subTask.id, subTasks.get(subTask.id));
        }
        updateEpicStatus(epicToUpdate);

    }

    public Collection<SubTask> getSubTasksByEpic(Epic epic) {
        return epic.getSubTasks().values();
    }


}

