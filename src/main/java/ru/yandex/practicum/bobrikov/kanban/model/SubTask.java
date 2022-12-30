package ru.yandex.practicum.bobrikov.kanban.model;

import java.util.Objects;

public class SubTask extends Task {
    private static final long serialVersionUID = 2L;
    private Epic epic; // эпик, в рамках которого выполняется эта подзадача

    public Epic getEpic() {  return epic; }
    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public SubTask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
    }
    @Override
    public String toString() {
        return "SubTask{" +
                "epic=" + epic.getId() +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return Objects.equals(getEpic(), subTask.getEpic());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEpic());
    }
}
