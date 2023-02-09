package ru.yandex.bobrikov.kanban.adapter;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import ru.yandex.bobrikov.kanban.manager.Managers;
import ru.yandex.bobrikov.kanban.manager.server.HttpTaskManager;
import ru.yandex.bobrikov.kanban.task.Epic;
import ru.yandex.bobrikov.kanban.task.Subtask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskAdapterTest {
    @Test
    void check_serialization() throws IOException {
        HttpTaskManager taskManager = Managers.getDefault();
        taskManager.deleteEpics();
        taskManager.deleteTasks();

        Gson gson = Managers.getGson(taskManager);

        Epic epic1 = new Epic("Epic1 name", "Epic 1 desc");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("subtask name", "subtask descr", epic1);
        subtask1.setStartTime(LocalDateTime.now());
        subtask1.setDuration(Duration.ofMinutes(30));
        Subtask addedSubtask = taskManager.addSubtask(subtask1);

        String jsonFromSubtask = gson.toJson(addedSubtask);
        Subtask subtaskFromJson = gson.fromJson(jsonFromSubtask, Subtask.class);

        assertEquals(addedSubtask, subtaskFromJson);
    }

}