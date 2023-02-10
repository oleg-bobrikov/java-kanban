package ru.yandex.bobrikov.kanban.manager.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.bobrikov.kanban.manager.TaskManager;
public class PrioritizedTaskHandler extends BaseHandler {

    public PrioritizedTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equals("GET")) {
                if (path.equals("/tasks/")) {
                    String response = gson.toJson(taskManager.getPrioritizedTasks());
                    sendText(exchange, response);
                    return;
                }
                System.out.println("Запрос GET " + exchange.getRequestURI() + " не поддерживается.");
                exchange.sendResponseHeaders(405, 0);
            } else {
                System.out.println("Метод " + requestMethod + " не поддерживается!");
                exchange.sendResponseHeaders(405, 0);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            exchange.close();
        }

    }
}
