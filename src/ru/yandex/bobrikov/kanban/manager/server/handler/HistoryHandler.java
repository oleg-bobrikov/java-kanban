package ru.yandex.bobrikov.kanban.manager.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.bobrikov.kanban.manager.TaskManager;

public class HistoryHandler extends BaseHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        super.handle(exchange);
        try {
            handleGet("/tasks/history", this::historyToJson, null);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            exchange.close();
        }

    }

    private String historyToJson() {
        return gson.toJson(taskManager.getHistory());
    }

}
