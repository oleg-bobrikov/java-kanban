package ru.yandex.practicum.bobrikov.kanban.managers.taskmanager;

import ru.yandex.practicum.bobrikov.kanban.exceptions.ManagerSaveException;
import ru.yandex.practicum.bobrikov.kanban.managers.historymanager.HistoryManager;
import ru.yandex.practicum.bobrikov.kanban.model.Epic;
import ru.yandex.practicum.bobrikov.kanban.model.SubTask;
import ru.yandex.practicum.bobrikov.kanban.model.Task;
import java.util.ArrayList;

public interface TaskManager {

    ArrayList<Task> getTasks();

    ArrayList<SubTask> getSubTasks();

    ArrayList<Epic> getEpics();

    Task addTask(Task task) throws ManagerSaveException;

    SubTask addSubTask(SubTask subTask) throws ManagerSaveException;

    Epic addEpic(Epic epic) throws ManagerSaveException;

    void deleteTasks() throws ManagerSaveException;

    void deleteSubTasks() throws ManagerSaveException;

    void deleteEpics() throws ManagerSaveException;

    Task getTask(int taskId) throws ManagerSaveException;

    Epic getEpic(int epicId) ;

    SubTask getSubTask(int subTaskId) ;

    void deleteTask(int id) throws ManagerSaveException;

    boolean deleteSubTask(int id) throws ManagerSaveException;

    void deleteEpic(int id) throws ManagerSaveException;

    Task updateTask(Task task) throws ManagerSaveException;

    SubTask updateSubTask(SubTask newSubTask) throws ManagerSaveException;

    Epic updateEpic(Epic newEpic) throws ManagerSaveException;

    ArrayList<SubTask> getSubTasksByEpic(Epic epic);
    ArrayList<Task> getHistory();

    HistoryManager getHistoryManager();
}
