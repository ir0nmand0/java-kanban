package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final Epic epic;

    public Subtask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        epicIsEmpty(epic);
        this.epic = epic;
    }

    public Subtask(Integer id, String name, String description, Status status, Epic epic) {
        super(id, name, description, status);
        epicIsEmpty(epic);
        this.epic = epic;
    }

    public Subtask(String name, String description, Status status,
                   LocalDateTime startTime, Duration duration, Epic epic) {
        super(name, description, status, startTime, duration);
        epicIsEmpty(epic);
        this.epic = epic;
    }

    public Subtask(Integer id, String name, String description,
                   Status status, LocalDateTime startTime, Duration duration, Epic epic) {
        super(id, name, description, status, startTime, duration);
        epicIsEmpty(epic);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return String.format("%s%d",super.toString(), epic.getId());
    }

    private void epicIsEmpty(Epic epic) {
        if (epic == null) {
            throw new TaskAddException("Epic не может быть пустым");
        }
    }

}
