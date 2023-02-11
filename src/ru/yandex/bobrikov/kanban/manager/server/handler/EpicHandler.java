package ru.yandex.bobrikov.kanban.manager.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.bobrikov.kanban.manager.TaskManager;
import ru.yandex.bobrikov.kanban.task.Epic;


public class EpicHandler extends BaseHandler {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        super.handle(exchange);
        try {
            switch (requestMethod) {
                case "GET": {
                    handleGet("/tasks/epic/", this::epicsToJson, this::epicToJson);
                    break;
                }
                case "POST": {
                    handlePost("/tasks/epic/", this::update, this::add);
                    break;
                }

                case "DELETE": {
                    handleDelete("/tasks/epic/", this::deleteEpic);
                    break;
                }
                default:
                    System.out.println("Метод " + requestMethod + " не поддерживается!");
                    exchange.sendResponseHeaders(405, 0);
            }

        } catch (
                Exception exception) {
            exception.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private Epic add(String body) {
        Epic epic = gson.fromJson(body, Epic.class);
        return taskManager.addEpic(epic);
    }

    private Epic update(String body) {
        Epic epic = gson.fromJson(body, Epic.class);
        return taskManager.updateEpic(epic);
    }

    private Boolean deleteEpic(Integer id) {
        if (id == null) {
            taskManager.deleteEpics();
            return true;
        } else {
            return taskManager.deleteEpic(id);
        }
    }

    private String epicsToJson() {
        return gson.toJson(taskManager.getEpics());
    }

    private String epicToJson(Integer id) {
        Epic epic = taskManager.getEpic(id);
        if (epic != null) {
            return gson.toJson(epic);
        } else {
            return null;
        }
    }
}
