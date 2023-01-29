package kanban.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Consumer;

public class Task implements Serializable, Comparable<Task> {
    private static final long serialVersionUID = 1L;
    protected Integer id; // Идентификатор задачи
    protected String name; // Название, кратко описывающее суть задачи (например, «Переезд»).
    protected String description; // Описание, в котором раскрываются детали.
    protected TaskStatus status; // Статус задачи
    protected LocalDateTime startTime; // Дата, когда предполагается приступить к выполнению задачи
    protected long duration; // Продолжительность задачи в минутах
    protected transient Consumer<Task> onChange;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime == null ? null : startTime.plusMinutes(duration);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }


    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.id = 0;
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
                "id=" + id +
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
        return duration == task.duration
                && Objects.equals(id, task.id)
                && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && status == task.status
                && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, startTime, duration, onChange);
    }

    @Override
    public int compareTo(Task o) {
        LocalDateTime start1 = getStartTime() == null ? LocalDateTime.MAX : getStartTime();
        LocalDateTime start2 = o.getStartTime() == null ? LocalDateTime.MAX : o.getStartTime();
        return start1.compareTo(start2);
    }

}

