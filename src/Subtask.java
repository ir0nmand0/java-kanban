public class Subtask extends Task {
    private final Epic epic;

    public Subtask(String name, String description, Integer id, Status status, Epic epic) {
        super(name, description, id, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }
}
