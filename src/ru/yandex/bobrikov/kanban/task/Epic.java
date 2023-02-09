package ru.yandex.bobrikov.kanban.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class Epic extends Task {
    private static final long serialVersionUID = 6103314303393686356L;

    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public  HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public Epic(String name, String description) {
        super(name, description);
        this.type = TaskType.EPIC;
    }
    public void updateStatus() {
        boolean isInProgress = false;
        boolean hasDone = false;
        boolean isNew = false;
        for (Subtask subTask : subtasks.values()) {
            if (subTask.getStatus() == TaskStatus.IN_PROGRESS) {
                isInProgress = true;
                break;
            } else if (subTask.getStatus() == TaskStatus.DONE) {
                hasDone = true;
            } else {
                isNew = true;
            }
        }
        if (isInProgress || hasDone && isNew) {
            this.status = TaskStatus.IN_PROGRESS;
        } else if (hasDone) {
            this.status = TaskStatus.DONE;
        } else {
            this.status = TaskStatus.NEW;
        }

    }

    @Override
    public LocalDateTime getStartTime() {
        Optional<LocalDateTime> min = subtasks.values().stream()
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        return min.orElse(null);
    }

    @Override
    public LocalDateTime getEndTime() {
        Optional<LocalDateTime> max = subtasks.values().stream()
                .map(subtask -> {
                            if (subtask.getStartTime() == null) {
                                return null;
                            } else {
                                return subtask.getStartTime().plusMinutes(subtask.getDuration().toMinutes());
                            }
                        }
                )
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo);
        return max.orElse(null);
    }

    @Override
    public Duration getDuration() {
        LocalDateTime startTime = getStartTime();
        LocalDateTime endTime = getEndTime();
        if (startTime != null && endTime != null) {
            return Duration.between(startTime, endTime);
        } else {
            return Duration.ofMinutes(0);
        }
    }

    @Override
    public void setDuration(Duration duration) {
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks.keySet() +
                ", type=" + type +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Arrays.equals(subtasks.keySet().toArray(), epic.subtasks.keySet().toArray());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks.keySet());
    }
}
