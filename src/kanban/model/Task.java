package kanban.model;

public class Task {
    protected Integer id; //Идентификатор задачи
    protected String name; //Название, кратко описывающее суть задачи (например, «Переезд»).
    protected String description; //Описание, в котором раскрываются детали.

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected TaskStatus status; //Статус задачи

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public String toString() {
        String description = " ";
        if (!this.description.isEmpty()) {
            description = " (" + this.description + "): ";
        }
        return "Задача: " + "id: " + this.id + " " + this.name + description + this.status;
    }
}
