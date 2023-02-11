package ru.yandex.bobrikov.kanban.manager.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.bobrikov.kanban.manager.TaskManager;
import ru.yandex.bobrikov.kanban.task.Task;

public class TaskHandler extends BaseHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        super.handle(exchange);
        try {
            switch (requestMethod) {
                case "GET": {
                    handleGet("/tasks/task/", this::tasksToJson, this::taskToJson);
                    break;
                }
                case "POST": {
                    handlePost("/tasks/task/", this::update, this::add);
                    break;
                }
                case "DELETE": {
                    handleDelete("/tasks/task/", this::deleteTask);
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

    private Task update(String body) {
        Task task = gson.fromJson(body, Task.class);
        return taskManager.updateTask(task);
    }

    private Task add(String body) {
        Task task = gson.fromJson(body, Task.class);
        return taskManager.addTask(task);
    }

    private String taskToJson(Integer id) {
        Task task = taskManager.getTask(id);
        if (task != null) {
            return gson.toJson(task);
        } else {
            return null;
        }
    }

    private String tasksToJson() {
        return gson.toJson(taskManager.getTasks());
    }

    private boolean deleteTask(Integer id) {
        if (id == null) {
            taskManager.deleteTasks();
            return true;
        } else {
            return taskManager.deleteTask(id);
        }
    }

}
