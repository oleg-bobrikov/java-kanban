package ru.yandex.bobrikov.kanban.manager.server.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.bobrikov.kanban.manager.TaskManager;
import ru.yandex.bobrikov.kanban.task.Subtask;

import java.util.regex.Pattern;

public class SubtaskHandler extends BaseHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    if (path.equals("/tasks/subtask/")) {
                        if (query == null) {
                            String response = gson.toJson(taskManager.getSubtasks());
                            sendText(exchange, response);
                            return;
                        }
                        if (Pattern.matches("id=\\d+", query)) {
                            String queryId = query.replaceFirst("id=", "");
                            int id = parsePathId(queryId);
                            if (id != -1) {
                                Subtask subtask = taskManager.getSubtask(id);
                                if (subtask != null) {
                                    String response = gson.toJson(subtask);
                                    sendText(exchange, response);
                                    return;
                                }
                            }
                            System.out.println("Получен некорректный идентификатор.");
                            exchange.sendResponseHeaders(405, 0);
                            return;
                        }
                    }
                    System.out.println("Запрос GET " + exchange.getRequestURI() + " не поддерживается.");
                    exchange.sendResponseHeaders(405, 0);
                    break;
                }
                case "POST": {
                    if (query == null && path.equals("/tasks/subtask/")) {
                        String body = readText(exchange);
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if (jsonElement.isJsonObject()) {
                            Subtask subtask = gson.fromJson(body, Subtask.class);
                            int subtaskId = subtask.getId();
                            Subtask newSubtask = taskManager.updateSubTask(subtask);
                            if (newSubtask != null) {
                                System.out.println("Обновлена подзадача с идентификатором: " + subtaskId);
                            } else {
                                newSubtask = taskManager.addSubtask(subtask);
                                System.out.println("Создана новая подзадача с идентификатором: " + newSubtask.getId());
                            }
                            String response = gson.toJson(newSubtask);
                            sendText(exchange, response);
                            return;
                        }
                    }
                    System.out.println("Запрос POST " + exchange.getRequestURI() + " не поддерживается.");
                    exchange.sendResponseHeaders(405, 0);
                    break;
                }
                case "DELETE": {
                    if (path.equals("/tasks/subtask/")) {
                        if (query == null) {
                            taskManager.deleteSubtasks();
                            System.out.println("Все подзадачи удалены.");
                            exchange.sendResponseHeaders(200, 0);
                            return;
                        }
                        if (Pattern.matches("id=\\d+", query)) {
                            String queryId = query.replaceFirst("id=", "");
                            int id = parsePathId(queryId);
                            if (id != -1) {
                                taskManager.deleteSubTask(id);
                                System.out.println("Удалена подзадача с идентификатором: " + id);
                                exchange.sendResponseHeaders(200, 0);
                                return;
                            }
                            System.out.println("Получен некорректный идентификатор.");
                            exchange.sendResponseHeaders(405, 0);
                            return;
                        }
                    }
                    System.out.println("Запрос DELETE " + exchange.getRequestURI() + " не поддерживается!");
                    exchange.sendResponseHeaders(405, 0);
                    break;
                }
                default:
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
