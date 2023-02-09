package ru.yandex.bobrikov.kanban.adapter;


import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.bobrikov.kanban.manager.TaskManager;
import ru.yandex.bobrikov.kanban.task.Epic;
import ru.yandex.bobrikov.kanban.task.Subtask;
import ru.yandex.bobrikov.kanban.task.TaskStatus;
import ru.yandex.bobrikov.kanban.task.TaskType;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskAdapter extends TypeAdapter<Subtask> {
    private final TaskManager taskManager;

    public SubtaskAdapter(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void write(JsonWriter jsonWriter, Subtask subtask) throws IOException {

        jsonWriter.beginObject();
        jsonWriter.name("type")
                .value("TASK")
                .name("epicId")
                .value(subtask.getEpic().getId())
                .name("id")
                .value(subtask.getId())
                .name("name")
                .value(subtask.getName())
                .name("description")
                .value(subtask.getDescription())
                .name("status")
                .value(subtask.getStatus().toString());

        jsonWriter.name("startTime");
        new LocalDateTimeAdapter().write(jsonWriter, subtask.getStartTime());

        jsonWriter.name("duration");
        new DurationAdapter().write(jsonWriter, subtask.getDuration());

        jsonWriter.endObject();
    }

    @Override
    public Subtask read(JsonReader in) throws IOException {
        Epic epic = null;
        TaskStatus status = TaskStatus.NEW;
        int id = 0;
        String name = "";
        String description = "";
        LocalDateTime startTime = null;
        Duration duration = Duration.ofMinutes(0);

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "epicId":
                    int epicId = in.nextInt();
                    epic = taskManager.getEpicWithoutUpdatingHistory(epicId);
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
        Subtask subtask = new Subtask(name, description, epic);
        subtask.setId(id);
        subtask.setStatus(status);
        subtask.setStartTime(startTime);
        subtask.setDuration(duration);

        return subtask;
    }
}
