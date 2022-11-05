package Model;

public class SubTask extends Task {
    protected Epic epic; // эпик, в рамках которого выполняется эта подзадача

    public SubTask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
    }

}
