package ru.yandex.bobrikov.kanban.manager.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.bobrikov.kanban.manager.TaskManager;
import ru.yandex.bobrikov.kanban.task.Subtask;

public class SubtaskHandler extends BaseHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        super.handle(exchange);
        try {
            this.path = exchange.getRequestURI().getPath();
            this.query = exchange.getRequestURI().getQuery();
            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    handleGet("/tasks/subtask/", this::subtasksToJson, this::subtaskToJson);
                    break;
                }
                case "POST": {
                    handlePost("/tasks/subtask/", this::update, this::add);
                    break;
                }
                case "DELETE": {
                    handleDelete("/tasks/subtask/", this::deleteSubtask);
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

    private Subtask add(String body) {
        Subtask subtask = gson.fromJson(body, Subtask.class);
        return taskManager.addSubtask(subtask);
    }

    private Subtask update(String body) {
        Subtask subtask = gson.fromJson(body, Subtask.class);
        return taskManager.updateSubTask(subtask);
    }

    private boolean deleteSubtask(Integer id) {
        if (id == null) {
            taskManager.deleteSubtasks();
            return true;
        } else {
            return taskManager.deleteSubTask(id);
        }
    }

    private String subtaskToJson(Integer id) {
        Subtask subtask = taskManager.getSubtask(id);
        if (subtask != null) {
            return gson.toJson(subtask);
        } else {
            return null;
        }
    }

    private String subtasksToJson() {
        return gson.toJson(taskManager.getSubtasks());
    }

}
