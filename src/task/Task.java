package task;

import java.util.Objects;

public class Task {
    private final String name;
    private final String description;
    private final Integer mainId;
    protected Status status;
    private static Integer newId = 1;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.mainId = getNewId();
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) &&
                status == task.status && Objects.equals(mainId, task.mainId);
    }

    private static Integer getNewId() {
        if (newId == Integer.MAX_VALUE) {
            return 0;
        }

        return newId++;
    }

    @Override
    public int hashCode() {
        return mainId;
    }

    @Override
    public String toString() {
        return "task.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + mainId +
                ", status=" + status +
                '}';
    }

}
