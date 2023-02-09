package ru.yandex.bobrikov.kanban.manager.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.bobrikov.kanban.manager.Managers;
import ru.yandex.bobrikov.kanban.task.Epic;
import ru.yandex.bobrikov.kanban.task.Subtask;
import ru.yandex.bobrikov.kanban.task.Task;
import ru.yandex.bobrikov.kanban.manager.file.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

public class HttpTaskManager extends FileBackedTaskManager {
    protected final int PORT;
    protected transient final HttpServer server;

    protected transient final Gson gson;
    protected transient final InetSocketAddress inetSocketAddress;
    protected transient final KVTaskClient client;

    public HttpTaskManager(InetSocketAddress kvServerAddress, File file, int port) throws IOException {
        super(Managers.getDefaultHistory(), file);
        this.PORT = port;
        // Отключить save() реализацию супрекласса
        this.isFileBackedTaskManager = false;

        // Подготовить клиента KVServer
        this.client = new KVTaskClient(kvServerAddress);
        this.inetSocketAddress = kvServerAddress;

        // Подготовить JSON ковертер
        gson = Managers.getGson(this);

        // Загрузить данные с KVServer
        String json = client.load("structure");

        if (json != null) {
            HashMap<String, String> structure = gson.fromJson(json, new TypeToken<HashMap<String, String>>() {
            }.getType());
            Task[] savedTasks = gson.fromJson(structure.get("tasks"), new TypeToken<Task[]>() {
            }.getType());
            Arrays.stream(savedTasks).forEach(HttpTaskManager.this::addTask);

            Epic[] savedEpics = gson.fromJson(structure.get("epics"), new TypeToken<Epic[]>() {
            }.getType());
            Arrays.stream(savedEpics).forEach(HttpTaskManager.this::addEpic);

            Subtask[] savedSubtasks = gson.fromJson(structure.get("subtasks"), new TypeToken<Subtask[]>() {
            }.getType());
            Arrays.stream(savedSubtasks).forEach(HttpTaskManager.this::addSubtask);

            Task[] savedHistory = gson.fromJson(structure.get("history"), new TypeToken<Task[]>() {
            }.getType());
            Arrays.stream(savedHistory).forEach(HttpTaskManager.this.getHistoryManager()::add);
        }


        // Создать сервер с конекстом
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            throw new IOException("Не могу создать сервер на порту " + PORT);
        }

        server.createContext("/tasks", HttpTaskManager.this::handleTasks);
        server.createContext("/tasks/subtask", HttpTaskManager.this::handleSubtasks);
        server.createContext("/tasks/epic", HttpTaskManager.this::handleEpics);
    }

    private void handleTasks(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String query = httpExchange.getRequestURI().getQuery();
            String requestMethod = httpExchange.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    if (Pattern.matches("/tasks/?$", path) && query == null) {
                        String response = gson.toJson(getPrioritizedTasks());
                        sendText(httpExchange, response);
                        return;
                    } else if (Pattern.matches("/tasks/history/?$", path) && query == null) {
                        String response = gson.toJson(getHistory());
                        sendText(httpExchange, response);
                        return;
                    } else if (Pattern.matches("^/tasks/task/?$", path)) {
                        if (query == null) {
                            String response = gson.toJson(getTasks());
                            sendText(httpExchange, response);
                            return;
                        }
                        if (Pattern.matches("id=\\d+", query)) {
                            String queryId = query.replaceFirst("id=", "");
                            int id = parsePathId(queryId);
                            if (id != -1) {
                                Task task = getTaskWithoutUpdatingHistory(id);
                                if (task != null) {
                                    String response = gson.toJson(task);
                                    sendText(httpExchange, response);
                                    return;
                                }
                            }
                            System.out.println("Получен некорректный идентификатор.");
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                    }
                    System.out.println("Запрос GET " + httpExchange.getRequestURI() + " не поддерживается.");
                    httpExchange.sendResponseHeaders(405, 0);
                    break;
                }
                case "POST": {
                    if (Pattern.matches("^/tasks/task/?$", path)) {
                        String body = readText(httpExchange);
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if (jsonElement.isJsonObject()) {
                            Task task = gson.fromJson(body, Task.class);
                            int taskId = task.getId();
                            Task newTask;
                            if (getTaskWithoutUpdatingHistory(taskId) != null) {
                                newTask = updateTask(task);
                                System.out.println("Обновлена задача с идентификатором: " + taskId);
                            } else {
                                newTask = addTask(task);
                                System.out.println("Создана новая задача с идентификатором: " + newTask.getId());
                            }
                            String response = gson.toJson(newTask);
                            sendText(httpExchange, response);
                            return;
                        }
                    }
                    System.out.println("Запрос POST " + httpExchange.getRequestURI() + " не поддерживается.");
                    httpExchange.sendResponseHeaders(405, 0);
                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("^/tasks/task/?$", path)) {
                        if (query == null) {
                            deleteTasks();
                            System.out.println("Все задачи удалены.");
                            httpExchange.sendResponseHeaders(200, 0);
                            return;
                        }
                        if (Pattern.matches("id=\\d+", query)) {
                            String queryId = query.replaceFirst("id=", "");
                            int id = parsePathId(queryId);
                            if (id != -1) {
                                deleteTask(id);
                                System.out.println("Удалена задача с идентификатором: " + id);
                                httpExchange.sendResponseHeaders(200, 0);
                                return;
                            }
                            System.out.println("Получен некорректный идентификатор.");
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                    }
                    System.out.println("Запрос DELETE " + httpExchange.getRequestURI() + " не поддерживается!");
                    httpExchange.sendResponseHeaders(405, 0);
                    break;
                }
                default:
                    System.out.println("Метод " + requestMethod + " не поддерживается!");
                    httpExchange.sendResponseHeaders(405, 0);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void handleSubtasks(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String query = httpExchange.getRequestURI().getQuery();
            String requestMethod = httpExchange.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    if (Pattern.matches("^/tasks/subtask/?$", path)) {
                        if (query == null) {
                            String response = gson.toJson(getSubtasks());
                            sendText(httpExchange, response);
                            return;
                        }
                        if (Pattern.matches("id=\\d+", query)) {
                            String queryId = query.replaceFirst("id=", "");
                            int id = parsePathId(queryId);
                            if (id != -1) {
                                Subtask subtask = getSubtaskWithoutUpdatingHistory(id);
                                if (subtask != null) {
                                    String response = gson.toJson(subtask);
                                    sendText(httpExchange, response);
                                    return;
                                }
                            }
                            System.out.println("Получен некорректный идентификатор.");
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                    }
                    System.out.println("Запрос GET " + httpExchange.getRequestURI() + " не поддерживается.");
                    httpExchange.sendResponseHeaders(405, 0);
                    break;
                }
                case "POST": {
                    if (Pattern.matches("^/tasks/subtask/?$", path)) {
                        String body = readText(httpExchange);
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if (jsonElement.isJsonObject()) {
                            Subtask subtask = gson.fromJson(body, Subtask.class);
                            int subtaskId = subtask.getId();
                            Subtask newSubtask;
                            if (getSubtaskWithoutUpdatingHistory(subtaskId) != null) {
                                newSubtask = updateSubTask(subtask);
                                System.out.println("Обновлена подзадача с идентификатором: " + subtaskId);
                            } else {
                                newSubtask = addSubtask(subtask);
                                System.out.println("Создана новая подзадача с идентификатором: " + newSubtask.getId());
                            }
                            String response = gson.toJson(newSubtask);
                            sendText(httpExchange, response);
                            return;
                        }
                    }
                    System.out.println("Запрос POST " + httpExchange.getRequestURI() + " не поддерживается.");
                    httpExchange.sendResponseHeaders(405, 0);
                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("^/tasks/subtask/?$", path)) {
                        if (query == null) {
                            deleteSubtasks();
                            System.out.println("Все подзадачи удалены.");
                            httpExchange.sendResponseHeaders(200, 0);
                            return;
                        }
                        if (Pattern.matches("id=\\d+", query)) {
                            String queryId = query.replaceFirst("id=", "");
                            int id = parsePathId(queryId);
                            if (id != -1) {
                                deleteSubTask(id);
                                System.out.println("Удалена подзадача с идентификатором: " + id);
                                httpExchange.sendResponseHeaders(200, 0);
                                return;
                            }
                            System.out.println("Получен некорректный идентификатор.");
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                    }
                    System.out.println("Запрос DELETE " + httpExchange.getRequestURI() + " не поддерживается!");
                    httpExchange.sendResponseHeaders(405, 0);
                    break;
                }
                default:
                    System.out.println("Метод " + requestMethod + " не поддерживается!");
                    httpExchange.sendResponseHeaders(405, 0);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void handleEpics(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String query = httpExchange.getRequestURI().getQuery();
            String requestMethod = httpExchange.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    if (Pattern.matches("^/tasks/epic/?$", path)) {
                        if (query == null) {
                            String response = gson.toJson(getEpics());
                            sendText(httpExchange, response);
                            return;
                        }
                        if (Pattern.matches("id=\\d+", query)) {
                            String queryId = query.replaceFirst("id=", "");
                            int id = parsePathId(queryId);
                            if (id != -1) {
                                Epic epic = getEpicWithoutUpdatingHistory(id);
                                if (epic != null) {
                                    String response = gson.toJson(epic);
                                    sendText(httpExchange, response);
                                    return;
                                }
                            }
                            System.out.println("Получен некорректный идентификатор.");
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                    }
                    System.out.println("Запрос GET " + httpExchange.getRequestURI() + " не поддерживается.");
                    httpExchange.sendResponseHeaders(405, 0);
                    break;
                }
                case "POST": {
                    if (Pattern.matches("^/tasks/epic/?$", path)) {
                        String body = readText(httpExchange);
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if (jsonElement.isJsonObject()) {
                            Epic epic = gson.fromJson(body, Epic.class);
                            int epicId = epic.getId();
                            Epic newEpic;
                            if (getEpicWithoutUpdatingHistory(epicId) != null) {
                                newEpic = updateEpic(epic);
                                System.out.println("Обновлен эпик с идентификатором: " + epicId);
                            } else {
                                newEpic = addEpic(epic);
                                System.out.println("Создан эпик с идентификатором: " + newEpic.getId());
                            }
                            String response = gson.toJson(newEpic);
                            sendText(httpExchange, response);
                            return;
                        }
                    }
                    System.out.println("Запрос POST " + httpExchange.getRequestURI() + " не поддерживается.");
                    httpExchange.sendResponseHeaders(405, 0);
                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("^/tasks/epic/?$", path)) {
                        if (query == null) {
                            deleteEpics();
                            System.out.println("Все эпики удалены.");
                            httpExchange.sendResponseHeaders(200, 0);
                            return;
                        }
                        if (Pattern.matches("id=\\d+", query)) {
                            String queryId = query.replaceFirst("id=", "");
                            int id = parsePathId(queryId);
                            if (id != -1) {
                                deleteEpic(id);
                                System.out.println("Удален эпик с идентификатором: " + id);
                                httpExchange.sendResponseHeaders(200, 0);
                                return;
                            }
                            System.out.println("Получен некорректный идентификатор.");
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                    }
                    System.out.println("Запрос DELETE " + httpExchange.getRequestURI() + " не поддерживается!");
                    httpExchange.sendResponseHeaders(405, 0);
                    break;
                }
                default:
                    System.out.println("Метод " + requestMethod + " не поддерживается!");
                    httpExchange.sendResponseHeaders(405, 0);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void start() {
        System.out.println("Server start http://localhost:" + PORT + "/tasks/");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Server stop http://localhost:" + PORT + "/tasks/");
    }

    private String readText(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendText(HttpExchange exchange, String text) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
    }

    @Override
    protected void save() {
        HashMap<String, String> structure = new HashMap<>();
        structure.put("tasks", gson.toJson(tasks.values().toArray()));
        structure.put("subtasks", gson.toJson(subtasks.values().toArray()));
        structure.put("epics", gson.toJson(epics.values().toArray()));
        structure.put("history", gson.toJson(getHistory().toArray()));

        client.put("HttpTaskManager", gson.toJson(structure));
    }

    @Override
    public Task addTask(Task task) {
        Task addedTask = super.addTask(task);
        save();
        return addedTask;
    }

    @Override
    public Task getTask(int taskId) {
        Task findTask = super.getTask(taskId);
        if (findTask == null) {
            return null;
        }
        save();
        return findTask;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask addedSubtask = super.addSubtask(subtask);
        save();
        return addedSubtask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic addedEpic = super.addEpic(epic);
        save();
        return addedEpic;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public boolean deleteSubTask(int id) {
        boolean isDeleted = super.deleteSubTask(id);
        save();
        return isDeleted;
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        if (updatedTask == null) {
            return null;
        }
        save();
        return updatedTask;
    }

    @Override
    public Subtask updateSubTask(Subtask srcSubtask) {
        Subtask updatedSubtask = super.updateSubTask(srcSubtask);
        if (updatedSubtask == null) {
            return null;
        }
        save();
        return updatedSubtask;
    }

    @Override
    public Epic updateEpic(Epic srcEpic) {
        Epic updatedEpic = super.updateEpic(srcEpic);
        if (updatedEpic == null) {
            return null;
        }
        save();
        return updatedEpic;
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic foundEpic = super.getEpic(epicId);
        save();
        return foundEpic;
    }

    @Override
    public Subtask getSubtask(int subTaskId) {
        Subtask foundSubtask = super.getSubtask(subTaskId);
        save();
        return foundSubtask;
    }
}
