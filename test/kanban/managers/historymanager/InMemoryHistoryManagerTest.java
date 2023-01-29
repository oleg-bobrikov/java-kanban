package kanban.managers.historymanager;

import kanban.managers.Managers;
import kanban.managers.taskmanager.InMemoryTaskManager;

class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createInstance() {
        return Managers.getDefault();
    }
}