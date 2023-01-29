package kanban.managers;

import kanban.managers.taskmanager.FileBackedTaskManager;
import kanban.managers.historymanager.HistoryManager;
import kanban.managers.historymanager.InMemoryHistoryManager;
import kanban.managers.taskmanager.InMemoryTaskManager;

import java.io.File;


public class Managers {
    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(getDefaultHistory(), file);
    }
}

