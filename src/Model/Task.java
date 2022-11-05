package Model;

public class Task {
    protected int id; //Идентификатор задачи
    protected String name; //Название, кратко описывающее суть задачи (например, «Переезд»).
    protected String description; //Описание, в котором раскрываются детали.
    protected Enum.TaskStatus status; //Статус задачи

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Enum.TaskStatus getStatus() {
        return status;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Enum.TaskStatus.NEW;
    }
}
