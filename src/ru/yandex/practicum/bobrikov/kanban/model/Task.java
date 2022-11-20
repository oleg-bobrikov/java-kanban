package ru.yandex.practicum.bobrikov.kanban.model;

public class Task {
    protected Integer id; //Идентификатор задачи
    protected String name; //Название, кратко описывающее суть задачи (например, «Переезд»).
    protected String description; //Описание, в котором раскрываются детали.
    protected TaskStatus status; //Статус задачи

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
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

    public String toString() {
        String description = " ";
        if (!this.description.isEmpty()) {
            description = " (" + this.description + "): ";
        }
        return "Задача: " + "id: " + this.id + " " + this.name + description + this.status;
    }
}
