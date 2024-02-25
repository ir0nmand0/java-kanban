package model;
import manager.ManagerSaveException;

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

    public Task(Integer id, String name, String description, Status status) {
        this.mainId = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && status == task.status && Objects.equals(mainId, task.mainId);
    }

    private static Integer getNewId() throws ManagerSaveException {
        if (newId == Integer.MAX_VALUE || newId < 0) {
            throw new ManagerSaveException("Не допустимый ID для задачи");
        }

        return newId++;
    }

    @Override
    public String toString() {
        return String.format("%d;%s;%s;%s;%s;", mainId, getClass().getSimpleName(), name, status, description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, mainId, status);
    }

    public int getId() {
        return mainId;
    }
}
