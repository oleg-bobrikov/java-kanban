package ru.yandex.bobrikov.kanban.task;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Serializable,Comparable {
    private static final long serialVersionUID = 1L;

    public TaskType getType() {
        return type;
    }

    protected TaskType type;
    protected Integer id; // Идентификатор задачи
    protected String name; // Название, кратко описывающее суть задачи (например, «Переезд»).
    protected String description; // Описание, в котором раскрываются детали.
    protected TaskStatus status; // Статус задачи
    protected LocalDateTime startTime; // Дата, когда предполагается приступить к выполнению задачи
    protected Duration duration; // Продолжительность задачи в минутах

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime == null ? null : startTime.plusMinutes(duration.toMinutes());
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }


    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.id = 0;
        this.duration = Duration.ofMinutes(0);
        this.type = TaskType.TASK;
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "type=" + type +
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
        Task task = (Task) o;
        return type == task.type
                && Objects.equals(id, task.id)
                && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && status == task.status
                && Objects.equals(startTime, task.startTime)
                && Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id, name, description, status, startTime, duration);
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;
        if (o == null || getClass() != o.getClass()) return -1;
        return this.id- ((Task) o).getId();
    }
}

