package ru.yandex.bobrikov.kanban.manager.server.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.bobrikov.kanban.manager.TaskManager;
import ru.yandex.bobrikov.kanban.task.Epic;

import java.util.regex.Pattern;

public class EpicHandler extends BaseHandler {

    public EpicHandler(TaskManager taskManager) {
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
                    if (path.equals("/tasks/epic/")) {
                        if (query == null) {
                            String response = gson.toJson(taskManager.getEpics());
                            sendText(exchange, response);
                            return;
                        }
                        if (Pattern.matches("id=\\d+", query)) {
                            String queryId = query.replaceFirst("id=", "");
                            int id = parsePathId(queryId);
                            if (id != -1) {
                                Epic epic = taskManager.getEpic(id);
                                if (epic != null) {
                                    String response = gson.toJson(epic);
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
                    if (query == null && path.equals("/tasks/epic/")) {
                        String body = readText(exchange);
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if (jsonElement.isJsonObject()) {
                            Epic epic = gson.fromJson(body, Epic.class);
                            int epicId = epic.getId();

                            Epic newEpic = taskManager.updateEpic(epic);
                            if (newEpic == null) {
                                newEpic = taskManager.addEpic(epic);
                                System.out.println("Создан эпик с идентификатором: " + newEpic.getId());
                            } else {
                                System.out.println("Обновлен эпик с идентификатором: " + epicId);
                            }
                            String response = gson.toJson(newEpic);
                            sendText(exchange, response);
                            return;
                        }
                    }
                    System.out.println("Запрос POST " + exchange.getRequestURI() + " не поддерживается.");
                    exchange.sendResponseHeaders(405, 0);
                    break;
                }

                case "DELETE": {
                    if (path.equals("/tasks/epic/")) {
                        if (query == null) {
                            taskManager.deleteEpics();
                            System.out.println("Все эпики удалены.");
                            exchange.sendResponseHeaders(200, 0);
                            return;
                        }
                        if (Pattern.matches("id=\\d+", query)) {
                            String queryId = query.replaceFirst("id=", "");
                            int id = parsePathId(queryId);
                            if (id != -1) {
                                taskManager.deleteEpic(id);
                                System.out.println("Удален эпик с идентификатором: " + id);
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
