package ru.yandex.bobrikov.kanban.manager;

import ru.yandex.bobrikov.kanban.task.Subtask;
import ru.yandex.bobrikov.kanban.task.Task;
import ru.yandex.bobrikov.kanban.task.Epic;

import java.util.ArrayList;

public interface TaskManager {

    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Epic> getEpics();

    Task addTask(Task task);

    Subtask addSubtask(Subtask subTask);

    Epic addEpic(Epic epic);

    void deleteTasks();

    void deleteSubtasks();

    void deleteEpics();

    Task getTask(int taskId);

    Epic getEpic(int epicId);

    Subtask getSubtask(int subTaskId);

    ArrayList<Task> getPrioritizedTasks();

    void deleteTask(int id);

    boolean deleteSubTask(int id);

    void deleteEpic(int id);

    Task updateTask(Task task);

    Subtask updateSubTask(Subtask newSubtask);

    Epic updateEpic(Epic newEpic);

    ArrayList<Subtask> getSubtasksByEpic(Epic epic);

    ArrayList<Task> getHistory();

    HistoryManager getHistoryManager();


}
