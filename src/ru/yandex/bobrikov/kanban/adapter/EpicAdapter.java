package ru.yandex.bobrikov.kanban.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import ru.yandex.bobrikov.kanban.manager.TaskManager;
import ru.yandex.bobrikov.kanban.task.Epic;
import ru.yandex.bobrikov.kanban.task.Subtask;
import ru.yandex.bobrikov.kanban.task.TaskStatus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class EpicAdapter extends TypeAdapter<Epic> {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicAdapter(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new Gson();
    }

    @Override
    public void write(JsonWriter jsonWriter, Epic epic) throws IOException {
        Set<Integer> subtasks = epic.getSubtasks().keySet();
        jsonWriter.beginObject();
        jsonWriter.name("type")
                .value("EPIC")
                .name("subtasks")
                .value(gson.toJson(subtasks))
                .name("id")
                .value(epic.getId())
                .name("name")
                .value(epic.getName())
                .name("description")
                .value(epic.getDescription())
                .name("status")
                .value(epic.getStatus().toString());

        jsonWriter.name("startTime");
        new LocalDateTimeAdapter().write(jsonWriter, epic.getStartTime());

        jsonWriter.name("duration");
        new DurationAdapter().write(jsonWriter, epic.getDuration());

        jsonWriter.endObject();
    }

    @Override
    public Epic read(JsonReader in) throws IOException {
        Set<Integer> subtasks = new HashSet<>();
        Type itemsSetType = new TypeToken<Set<Integer>>() {
        }.getType();
        TaskStatus status = TaskStatus.NEW;
        int id = 0;
        String name = "";
        String description = "";
        LocalDateTime startTime = null;
        Duration duration = Duration.ofMinutes(0);

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "subtasks":
                    String jsonArr = in.nextString();
                    subtasks = new Gson().fromJson(jsonArr, itemsSetType);
                    break;
                case "status":
                    switch (in.nextString()) {
                        case "DONE": {
                            status = TaskStatus.DONE;
                            break;
                        }
                        case "IN_PROGRESS": {
                            status = TaskStatus.IN_PROGRESS;
                            break;
                        }
                        default:
                            status = TaskStatus.NEW;
                    }
                    break;
                case "id": {
                    id = in.nextInt();
                    break;
                }
                case "name": {
                    name = in.nextString();
                    break;
                }
                case "description": {
                    description = in.nextString();
                    break;
                }
                case "startTime": {
                    startTime = new LocalDateTimeAdapter().read(in);
                    break;
                }
                case "duration": {
                    duration = new DurationAdapter().read(in);
                    break;
                }
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();
        Epic epic = new Epic(name, description);
        epic.setId(id);
        epic.setStatus(status);
        epic.setStartTime(startTime);
        epic.setDuration(duration);

        subtasks.forEach(subtaskId -> epic.getSubtasks().put(subtaskId, new Subtask("", "", epic)));
        Epic newEpic = taskManager.updateEpic(epic);
        if (newEpic == null) {
            newEpic = taskManager.addEpic(epic);
        }
        return newEpic;
    }
}
