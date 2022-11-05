package Model;

import java.util.HashMap;

public class Epic extends Task {

    protected HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public Epic(String name, String description) {
        super(name, description);
    }

}
