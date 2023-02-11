package ru.yandex.bobrikov.kanban.manager.server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.bobrikov.kanban.adapter.DurationAdapter;
import ru.yandex.bobrikov.kanban.adapter.EpicAdapter;
import ru.yandex.bobrikov.kanban.adapter.LocalDateTimeAdapter;
import ru.yandex.bobrikov.kanban.adapter.SubtaskAdapter;
import ru.yandex.bobrikov.kanban.manager.TaskManager;
import ru.yandex.bobrikov.kanban.task.Epic;
import ru.yandex.bobrikov.kanban.task.Subtask;
import ru.yandex.bobrikov.kanban.task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public abstract class BaseHandler implements HttpHandler {
    protected TaskManager taskManager;
    protected Gson gson;
    protected HttpExchange exchange;
    protected String path;
    protected String query;
    protected String requestMethod;

    public BaseHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(Subtask.class, new SubtaskAdapter(taskManager));
        gsonBuilder.registerTypeAdapter(Epic.class, new EpicAdapter(taskManager));
        this.gson = gsonBuilder.create();
    }

    @Override
    public void handle(HttpExchange exchange) {
        this.exchange = exchange;
        this.path = exchange.getRequestURI().getPath();
        this.query = exchange.getRequestURI().getQuery();
        this.requestMethod = exchange.getRequestMethod();
    }

    protected void handleGet(String baseAPI,
                             Supplier<String> defaultResponseSupplier,
                             Function<Integer, String> idResponseFunction) throws IOException {
        if (path.equals(baseAPI)) {
            if (query == null) {
                String response = defaultResponseSupplier.get();
                sendText(response);
                System.out.println("Получены все объекты");
                return;
            }

            String[] queries = query.split("&");
            for (String subQuery : queries) {
                if (Pattern.matches("id=\\d+", subQuery)) {
                    String queryId = subQuery.replaceFirst("id=", "");
                    int id = parsePathId(queryId);
                    if (id != -1) {
                        String response = idResponseFunction != null ? idResponseFunction.apply(id) : null;
                        if (response != null) {
                            sendText(response);
                            System.out.println("Получен объект с идентификатором: " + id);
                            return;
                        }
                    }
                    System.out.println("Получен некорректный идентификатор.");
                    exchange.sendResponseHeaders(404, 0);
                    return;
                }
            }
        }
        System.out.println("Получен некорректный запрос: " + exchange.getRequestURI());
        exchange.sendResponseHeaders(400, 0);
    }

    protected void handlePost(String baseAPI,
                              Function<String, Task> updateFunction,
                              Function<String, Task> addFunction) throws IOException {
        if (query == null && path.equals(baseAPI)) {
            String body = readText(exchange);
            JsonElement jsonElement = JsonParser.parseString(body);
            if (jsonElement.isJsonObject()) {
                Task newTask = updateFunction.apply(body);
                if (newTask != null) {
                    System.out.println("Обновлен объект с идентификатором: " + newTask.getId());
                } else {
                    newTask = addFunction.apply(body);
                    if (newTask != null) {
                        System.out.println("Создан новый объект с идентификатором: " + newTask.getId());
                    } else {
                        System.out.println("Ошибка добавления объекта: ");
                        exchange.sendResponseHeaders(404, 0);
                        return;
                    }
                }
                String response = gson.toJson(newTask);
                sendText(response);
                return;
            }
        }
        System.out.println("Получен некорректный запрос: " + exchange.getRequestURI());
        exchange.sendResponseHeaders(400, 0);
    }

    protected void handleDelete(String baseAPI, Function<Integer, Boolean> deleteFunction) throws IOException {
        if (path.equals(baseAPI)) {
            if (query == null) {
                deleteFunction.apply(null);
                exchange.sendResponseHeaders(200, 0);
                System.out.println("Удалены все объекты");
                return;
            }
            String[] queries = query.split("&");
            for (String subQuery : queries) {
                if (Pattern.matches("id=\\d+", subQuery)) {
                    String queryId = subQuery.replaceFirst("id=", "");
                    int id = parsePathId(queryId);
                    if (id != -1) {
                        boolean hasDeleted = deleteFunction.apply(id);
                        if (hasDeleted) {
                            System.out.println("Удален объект с идентификатором: " + id);
                            exchange.sendResponseHeaders(200, 0);
                            return;
                        }
                    }
                }
                System.out.println("Получен некорректный идентификатор.");
                exchange.sendResponseHeaders(404, 0);
                return;
            }
        }

        System.out.println("Получен некорректный запрос: " + exchange.getRequestURI());
        exchange.sendResponseHeaders(400, 0);
    }

    String readText(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected void sendText(String text) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
    }

    protected int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
