package kanban.model;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public class Subtask extends Task {
    private static final long serialVersionUID = 2L;
    private Epic epic; // эпик, в рамках которого выполняется эта подзадача

    public void setOnChange(Consumer<Subtask> onChange) {
        this.onChange = onChange;
    }

    private transient Consumer<Subtask>onChange;
    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;

    }

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
    }

    @Override
    public void setDuration(long duration) {
        super.setDuration(duration);
        if (onChange != null) {
            onChange.accept(this);
        }
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        super.setStartTime(startTime);
        if (onChange != null) {
            onChange.accept(this);
        }
    }

    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
        if (onChange != null) {
            onChange.accept(this);
        }
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epic=" + epic.getId() +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        if (!super.equals(o)) return false;
//        Subtask subtask = (Subtask) o;
//        return Objects.equals(epic, subtask.epic);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(super.hashCode(), epic);
//    }
}
