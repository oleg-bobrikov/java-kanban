package ru.yandex.bobrikov.kanban.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import ru.yandex.bobrikov.kanban.manager.Managers;
import ru.yandex.bobrikov.kanban.manager.TaskManager;
import ru.yandex.bobrikov.kanban.task.Epic;
import ru.yandex.bobrikov.kanban.task.Subtask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicAdapterTest {
    @Test
    void check_serialization() throws IOException {
        TaskManager taskManager = Managers.getDefault();
        taskManager.deleteEpics();
        taskManager.deleteTasks();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(Subtask.class, new SubtaskAdapter(taskManager));
        gsonBuilder.registerTypeAdapter(Epic.class, new EpicAdapter(taskManager));
        Gson gson = gsonBuilder.create();

        Epic epic = new Epic("Epic name", "Epic descr");
        Epic addedEpic = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("subtask name", "subtask descr", addedEpic);
        subtask.setStartTime(LocalDateTime.now());
        subtask.setDuration(Duration.ofMinutes(30));
        taskManager.addSubtask(subtask);

        String jsonFromEpic = gson.toJson(addedEpic);
        Epic epicFromJson = gson.fromJson(jsonFromEpic, Epic.class);

        assertEquals(addedEpic, epicFromJson);
         }

}