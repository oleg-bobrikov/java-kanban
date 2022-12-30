package ru.yandex.practicum.bobrikov.kanban.model;

import java.util.HashMap;


public class Epic extends Task {
    private static final long serialVersionUID = 6103314303393686356L;

    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks.keySet() +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }


}
