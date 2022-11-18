package kanban.managers;

import kanban.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> taskHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        taskHistory.add(task);
        if (taskHistory.size() > 10) {
            taskHistory.remove(0);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return taskHistory;
    }
}
