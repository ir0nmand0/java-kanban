package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {

    @Expose
    @SerializedName(value = "epicId")
    private final Epic epic;

    public Subtask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        epicIsEmpty(epic);
        this.epic = epic;
    }

    public Subtask(String name, String description, Status status,
                   LocalDateTime startTime, Duration duration, Epic epic) {
        super(name, description, status, startTime, duration);
        epicIsEmpty(epic);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }


    @Override
    public String toString() {
        return Subtask.class.getSimpleName() + "{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + durationToLong() +
                ", startTime=" + startTimeToString() +
                ", epicId=" + getEpicId() +
                '}';
    }

    private int getEpicId() {
        return Objects.isNull(epic) ? 0 : epic.getId();
    }

    @Override
    public String toCsv() {
        return String.format("%s%d",super.toCsv(), epic.getId());
    }

    private void epicIsEmpty(Epic epic) {
        if (epic == null) {
            throw new TaskAddException("Epic не может быть пустым");
        }
    }

}
