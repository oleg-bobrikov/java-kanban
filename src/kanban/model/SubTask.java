package kanban.model;

public class SubTask extends Task {
    private Epic epic; // эпик, в рамках которого выполняется эта подзадача

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }


    public SubTask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
    }

 }
