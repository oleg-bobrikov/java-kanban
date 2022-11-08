package kanban.model;

import java.util.HashMap;

public class Epic extends Task {

    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();

    @Override
    public String toString() {
        String description = " ";
        if (!this.description.isEmpty()) {
            description = " (" + this.description + "): ";
        }
        return "Эпик: " + "id: " + this.id + " " + this.name + description + this.status;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public Epic(String name, String description) {
        super(name, description);
    }

}
