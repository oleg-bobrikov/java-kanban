package ru.yandex.bobrikov.kanban.managers.taskmanager;

import ru.yandex.bobrikov.kanban.managers.Managers;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createInstance() {
        return new InMemoryTaskManager(Managers.getDefaultHistory());
    }
}
