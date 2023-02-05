package ru.yandex.bobrikov.kanban.manager.memory;

import ru.yandex.bobrikov.kanban.manager.Managers;
import ru.yandex.bobrikov.kanban.manager.TaskManagerTest;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createInstance() {
        return new InMemoryTaskManager(Managers.getDefaultHistory());
    }
}
