package ru.yandex.bobrikov.kanban.manager.server;

import ru.yandex.bobrikov.kanban.manager.Managers;
import ru.yandex.bobrikov.kanban.manager.TaskManagerTest;

import java.io.IOException;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    @Override
    protected HttpTaskManager createInstance() throws IOException {
        return (HttpTaskManager) Managers.getDefault();
    }
}


