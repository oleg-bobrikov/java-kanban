package ru.yandex.bobrikov.kanban.manager.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import ru.yandex.bobrikov.kanban.adapter.DurationAdapter;
import ru.yandex.bobrikov.kanban.adapter.EpicAdapter;
import ru.yandex.bobrikov.kanban.adapter.LocalDateTimeAdapter;
import ru.yandex.bobrikov.kanban.adapter.SubtaskAdapter;
import ru.yandex.bobrikov.kanban.manager.Managers;
import ru.yandex.bobrikov.kanban.manager.TaskManager;
import ru.yandex.bobrikov.kanban.task.Epic;
import ru.yandex.bobrikov.kanban.task.Subtask;
import ru.yandex.bobrikov.kanban.task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    public static final int PORT = 8080;

    private static HttpTaskServer httpTaskServer;
    private static Gson gson;
    private static TaskManager taskManager;

    @BeforeAll
    static void beforeAll() throws IOException {
        httpTaskServer = Managers.getDefaultHttpTaskServer();

        taskManager = httpTaskServer.getTaskManager();
        taskManager.deleteEpics();
        taskManager.deleteTasks();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(Subtask.class, new SubtaskAdapter(taskManager));
        gsonBuilder.registerTypeAdapter(Epic.class, new EpicAdapter(taskManager));
        gson = gsonBuilder.create();

    }

    @AfterAll
    static void afterAll() {
        httpTaskServer.stop();

    }

    @BeforeEach
    void beforeEach() {
        taskManager.deleteEpics();
        taskManager.deleteTasks();
    }

    private Task createTask1() {
        Task task = new Task("Задача 1", "Описание 1");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(60));
        return task;
    }

    private Subtask addSubtask1() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);

        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1);
        subtask.setStartTime(LocalDateTime.now());
        subtask.setDuration(Duration.ofMinutes(60));
        return taskManager.addSubtask(subtask);
    }

    private Subtask createSubtask1() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);

        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1);
        subtask.setStartTime(LocalDateTime.now());
        subtask.setDuration(Duration.ofMinutes(60));
        return subtask;
    }

    protected Task addTask1() {
        Task newTask = new Task("Задача 1", "Описание задачи 1");
        newTask.setStartTime(LocalDateTime.now());
        newTask.setDuration(Duration.ofMinutes(60));
        taskManager.addTask(newTask);
        return newTask;
    }

    @Test
    public void getTasks() throws IOException, InterruptedException {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        addTask1();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + PORT + "/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());

        ArrayList<Task> actual = gson.fromJson(response.body(),
                new TypeToken<ArrayList<Task>>() {
                }.getType());
        assertArrayEquals(taskManager.getTasks().toArray(), actual.toArray(), "список задач не совпадает");
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        Task task1 = addTask1();
        taskManager.getTask(task1.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + PORT + "/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());

        ArrayList<Task> actual = gson.fromJson(response.body(),
                new TypeToken<ArrayList<Task>>() {
                }.getType());

        assertTrue(Arrays.deepEquals(taskManager.getHistory().toArray(), actual.toArray()));
    }

    @Test
    public void getTask() throws IOException, InterruptedException {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        Task task = addTask1();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + PORT + "/tasks/task/?id=" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());

        Task actual = gson.fromJson(response.body(), Task.class);
        assertEquals(task, actual, "Задачи не совпадают");
    }

    @Test
    public void deleteTasks() throws IOException, InterruptedException {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        addTask1();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + PORT + "/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode(), "неверный код ответа");

        assertEquals(0, taskManager.getTasks().size(), "список задач не пустой.");
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        Task task = addTask1();
        int taskId = task.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + PORT + "/tasks/task/?id=" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());

        assertEquals(0, taskManager.getTasks().size(), "список задач не пустой.");
    }

    @Test
    public void addTask() throws IOException, InterruptedException {
        assertEquals(0, taskManager.getPrioritizedTasks().size(), "список задач не очищен");
        assertEquals(0, taskManager.getHistory().size());

        Task task = createTask1();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + PORT + "/tasks/task/");
        String bodyRequest = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(bodyRequest, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());

        assertEquals(1, taskManager.getTasks().size(), "задача не добавлена");

        int taskId = gson.fromJson(response.body(), Task.class).getId();
        Task addedTask = taskManager.getTask(taskId);

        assertEquals(task.getName(), addedTask.getName());
        assertEquals(task.getDescription(), addedTask.getDescription());
        assertEquals(task.getStatus(), addedTask.getStatus());
        assertEquals(task.getStartTime(), addedTask.getStartTime());
        assertEquals(task.getDuration(), addedTask.getDuration());

        bodyRequest = gson.toJson(addedTask);
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(bodyRequest, StandardCharsets.UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode());
        int addedTaskTwiceId = gson.fromJson(response.body(), Task.class).getId();
        Task addedTaskTwice = taskManager.getTask(addedTaskTwiceId);

        assertEquals(addedTask, addedTaskTwice, "не выполнено условие идемпонетности");
    }


    @Test
    public void getEpics() throws IOException, InterruptedException {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        Epic epic = new Epic("Epic name", "Epic description");
        taskManager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + PORT + "/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());

        ArrayList<Epic> actual = gson.fromJson(response.body(),
                new TypeToken<ArrayList<Epic>>() {
                }.getType());
        assertArrayEquals(taskManager.getEpics().toArray(), actual.toArray(), "список эпиков не совпадает");

    }

    @Test
    public void getEpic() throws IOException, InterruptedException {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        Epic epic = new Epic("Epic name", "Epic description");
        Epic addedEpic = taskManager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + PORT + "/tasks/epic/?id=" + addedEpic.getId()+"&status=done1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());

        Task actual = gson.fromJson(response.body(), Epic.class);
        assertEquals(addedEpic, actual, "Эпики не совпадают");

    }

    @Test
    public void deleteEpics() throws IOException, InterruptedException {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        Epic epic = new Epic("Epic name", "Epic description");
        taskManager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + PORT + "/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode(), "неверный код ответа");

        assertEquals(0, taskManager.getEpics().size(), "список эпиков не пустой.");
    }

    @Test
    public void deleteEpic() throws IOException, InterruptedException {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        Epic epic = new Epic("Epic name", "Epic description");
        Epic addedEpic = taskManager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + PORT + "/tasks/epic/?id=" + addedEpic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());

        assertEquals(0, taskManager.getEpics().size(), "список эпиков не пустой.");

    }


    @Test
    public void addEpic() throws IOException, InterruptedException {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        Epic epic = new Epic("Epic name", "Epic description");

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + PORT + "/tasks/epic/");
        String bodyRequest = gson.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(bodyRequest, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());
        assertEquals(1, taskManager.getEpics().size(), "эпик не добавлен");

        Epic addedEpic = gson.fromJson(response.body(), Epic.class);

        assertEquals(epic.getName(), addedEpic.getName());
        assertEquals(epic.getDescription(), addedEpic.getDescription());
        assertEquals(epic.getStatus(), addedEpic.getStatus());
        assertEquals(epic.getStartTime(), addedEpic.getStartTime());
        assertEquals(epic.getDuration(), addedEpic.getDuration());
        assertEquals(epic.getSubtasks(), addedEpic.getSubtasks());

        bodyRequest = gson.toJson(addedEpic);
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(bodyRequest, StandardCharsets.UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode());
        int addedEpicTwiceId = gson.fromJson(response.body(), Epic.class).getId();
        Epic addedEpicTwice = taskManager.getEpic(addedEpicTwiceId);
        assertEquals(addedEpic, addedEpicTwice, "не выполнено условие идемпонетности");
    }

    @Test
    public void getSubtasks() throws IOException, InterruptedException {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        addSubtask1();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + PORT + "/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());


        ArrayList<Subtask> actual = gson.fromJson(response.body(),
                new TypeToken<ArrayList<Subtask>>() {
                }.getType());
        assertArrayEquals(taskManager.getSubtasks().toArray(), actual.toArray(), "список подзадач не совпадает");

    }

    @Test
    public void getSubtask() throws IOException, InterruptedException {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        Subtask subtask = addSubtask1();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + PORT + "/tasks/subtask/?id=" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());

        Task actual = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subtask, actual, "Подзадачи не совпадают");

    }

    @Test
    public void deleteSubtasks() throws IOException, InterruptedException {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        addSubtask1();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + PORT + "/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode(), "неверный код ответа");

        assertEquals(0, taskManager.getSubtasks().size(), "список подзадач не пустой.");
    }

    @Test
    public void deleteSubtask() throws IOException, InterruptedException {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        Subtask subtask = addSubtask1();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + PORT + "/tasks/subtask/?id=" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());

        assertEquals(0, taskManager.getSubtasks().size(), "список задач не пустой.");

    }

    @Test
    public void addSubtask() throws IOException, InterruptedException {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        Subtask subtask = createSubtask1();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:" + PORT + "/tasks/subtask/");
        String bodyRequest = gson.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(bodyRequest, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());

        assertEquals(1, taskManager.getSubtasks().size(), "подзадача не добавлена");

        int subtaskId = gson.fromJson(response.body(), Subtask.class).getId();
        Subtask addedSubtask = taskManager.getSubtask(subtaskId);

        assertEquals(subtask.getName(), addedSubtask.getName());
        assertEquals(subtask.getDescription(), addedSubtask.getDescription());
        assertEquals(subtask.getType(), addedSubtask.getType());
        assertEquals(subtask.getStatus(), addedSubtask.getStatus());
        assertEquals(subtask.getStartTime(), addedSubtask.getStartTime());
        assertEquals(subtask.getDuration(), addedSubtask.getDuration());

        bodyRequest = gson.toJson(addedSubtask);
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(bodyRequest, StandardCharsets.UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode());
        int addedSubtaskTwiceId = gson.fromJson(response.body(), Subtask.class).getId();
        Subtask addedSubtaskTwice = taskManager.getSubtask(addedSubtaskTwiceId);
        assertEquals(addedSubtask, addedSubtaskTwice, "не выполнено условие идемпонетности");
    }

}