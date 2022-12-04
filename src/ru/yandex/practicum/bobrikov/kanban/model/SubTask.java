package ru.yandex.practicum.bobrikov.kanban.model;

public class SubTask extends Task {
    private Epic epic; // эпик, в рамках которого выполняется эта подзадача

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        String description = " ";
        if (!this.description.isEmpty()) {
            description = " (" + this.description + "), Статус: ";
        }
        return "Подзадача: " + "id: " + this.id + " " + this.name + description + this.status + ", Эпик id:" +
                this.epic.getId();
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }


    public SubTask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
    }

}
