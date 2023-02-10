package ru.yandex.bobrikov.kanban.manager.server.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.bobrikov.kanban.manager.TaskManager;
import ru.yandex.bobrikov.kanban.task.Task;

import java.util.regex.Pattern;

public class TaskHandler extends BaseHandler {
    public TaskHandler(TaskManager taskManager) {
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
                    if (path.equals("/tasks/task/")) {
                        if (query == null) {
                            String response = gson.toJson(taskManager.getTasks());
                            sendText(exchange, response);
                            return;
                        }
                        if (Pattern.matches("id=\\d+", query)) {
                            String queryId = query.replaceFirst("id=", "");
                            int id = parsePathId(queryId);
                            if (id != -1) {
                                Task task = taskManager.getTask(id);
                                if (task != null) {
                                    String response = gson.toJson(task);
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
                    if (query == null && path.equals("/tasks/task/")) {
                        String body = readText(exchange);
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if (jsonElement.isJsonObject()) {
                            Task task = gson.fromJson(body, Task.class);
                            int taskId = task.getId();
                            Task newTask = taskManager.updateTask(task);
                            if (newTask != null) {
                                System.out.println("Обновлена задача с идентификатором: " + taskId);
                            } else {
                                newTask = taskManager.addTask(task);
                                System.out.println("Создана новая задача с идентификатором: " + newTask.getId());
                            }
                            String response = gson.toJson(newTask);
                            sendText(exchange, response);
                            return;
                        }
                    }
                    System.out.println("Запрос POST " + exchange.getRequestURI() + " не поддерживается.");
                    exchange.sendResponseHeaders(405, 0);
                    break;
                }
                case "DELETE": {
                    if (path.equals("/tasks/task/")) {
                        if (query == null) {
                            taskManager.deleteTasks();
                            System.out.println("Все задачи удалены.");
                            exchange.sendResponseHeaders(200, 0);
                            return;
                        }
                        if (Pattern.matches("id=\\d+", query)) {
                            String queryId = query.replaceFirst("id=", "");
                            int id = parsePathId(queryId);
                            if (id != -1) {
                                taskManager.deleteTask(id);
                                System.out.println("Удалена задача с идентификатором: " + id);
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
