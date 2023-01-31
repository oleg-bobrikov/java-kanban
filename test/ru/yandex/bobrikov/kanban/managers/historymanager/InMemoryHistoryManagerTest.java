package ru.yandex.bobrikov.kanban.managers.historymanager;

import ru.yandex.bobrikov.kanban.managers.Managers;
import ru.yandex.bobrikov.kanban.managers.taskmanager.InMemoryTaskManager;

class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createInstance() {
        return Managers.getDefault();
    }
}