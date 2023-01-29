package kanban.managers.taskmanager;

import kanban.managers.Managers;
import kanban.managers.historymanager.HistoryManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createInstance() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        return new InMemoryTaskManager(historyManager);
    }
}
