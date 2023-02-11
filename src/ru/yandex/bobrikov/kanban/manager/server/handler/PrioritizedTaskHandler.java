package ru.yandex.bobrikov.kanban.manager.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.bobrikov.kanban.manager.TaskManager;
public class PrioritizedTaskHandler extends BaseHandler {

    public PrioritizedTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        super.handle(exchange);
        try {
            handleGet("/tasks/history/", this::prioritizedTasksToJson, null);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private String prioritizedTasksToJson() {
        return gson.toJson(taskManager.getPrioritizedTasks());
    }
}
