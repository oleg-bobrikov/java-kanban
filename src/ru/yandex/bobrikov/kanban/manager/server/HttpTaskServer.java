package ru.yandex.bobrikov.kanban.manager.server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.bobrikov.kanban.manager.TaskManager;
import ru.yandex.bobrikov.kanban.manager.server.handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {

        this.taskManager = taskManager;

        // Создать сервер с конекстом
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            throw new IOException("Не могу создать сервер на порту " + PORT);
        }

        server.createContext("/tasks/task/", new TaskHandler(taskManager));
        server.createContext("/tasks/subtask/", new SubtaskHandler(taskManager));
        server.createContext("/tasks/epic/", new EpicHandler(taskManager));
        server.createContext("/tasks/history", new HistoryHandler(taskManager));
        server.createContext("/tasks/", new PrioritizedTaskHandler(taskManager));

        server.start();
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void start() {
        System.out.println("Server has been started at http://localhost:" + PORT + "/tasks/");
        try {
            server.start();
        } catch (IllegalStateException e) {
            System.out.println("Server has already been started at http://localhost:" + PORT + "/tasks/");
        }

    }

    public void stop() {
        server.stop(0);
        System.out.println("Server has stopped at http://localhost:" + PORT + "/tasks/");
    }


}
