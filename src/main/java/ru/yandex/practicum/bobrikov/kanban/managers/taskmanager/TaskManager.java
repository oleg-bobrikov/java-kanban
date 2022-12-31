package ru.yandex.practicum.bobrikov.kanban.managers.taskmanager;

import ru.yandex.practicum.bobrikov.kanban.managers.historymanager.HistoryManager;
import ru.yandex.practicum.bobrikov.kanban.model.Epic;
import ru.yandex.practicum.bobrikov.kanban.model.SubTask;
import ru.yandex.practicum.bobrikov.kanban.model.Task;
import java.util.ArrayList;

public interface TaskManager {

    ArrayList<Task> getTasks();

    ArrayList<SubTask> getSubTasks();

    ArrayList<Epic> getEpics();

    Task addTask(Task task);

    SubTask addSubTask(SubTask subTask);

    Epic addEpic(Epic epic) ;

    void deleteTasks() ;

    void deleteSubTasks();

    void deleteEpics() ;

    Task getTask(int taskId) ;

    Epic getEpic(int epicId) ;

    SubTask getSubTask(int subTaskId) ;

    void deleteTask(int id) ;

    boolean deleteSubTask(int id) ;

    void deleteEpic(int id) ;

    Task updateTask(Task task) ;

    SubTask updateSubTask(SubTask newSubTask) ;

    Epic updateEpic(Epic newEpic) ;

    ArrayList<SubTask> getSubTasksByEpic(Epic epic);
    ArrayList<Task> getHistory();

    HistoryManager getHistoryManager();
}