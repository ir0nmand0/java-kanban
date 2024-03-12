package model;
import manager.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

public class Task {
    private final String name;
    private final String description;
    private final Integer mainId;
    protected Status status;
    private static Integer newId = 1;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.mainId = getNewId();
        this.duration = null;
        this.startTime = null;
    }

    public Task(Integer id, String name, String description, Status status) {
        this.mainId = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = null;
        this.startTime = null;
    }

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.mainId = getNewId();
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(Integer id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.mainId = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Status getStatus() {
        return status;
    }

    public Optional<LocalDateTime> getEndTime() {
        return Optional.ofNullable(startTime).isPresent()
                && Optional.ofNullable(duration).isPresent()
                ? Optional.of(startTime.plus(duration)) : Optional.empty();
    }

    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public long getStartTimeToEpochSecond() {
        return startTime != null ? startTime.toEpochSecond(ZoneOffset.UTC) : 0;
    }

    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }

    private static Integer getNewId() {
        if (newId == Integer.MAX_VALUE || newId < 0) {
            throw new TaskAddException("Не допустимый ID для задачи");
        }

        return newId++;
    }

    public int getId() {
        return mainId;
    }

    @Override
    public String toString() {
        return String.format("%d;%s;%s;%s;%s;%s;%s;",
                mainId, getClass().getSimpleName(), name, status, description,
                getStartTime()
                        .map(localDateTime -> localDateTime.format(Managers.formatter))
                        .orElse(""),
                getEndTime()
                        .map(localDateTime -> localDateTime.format(Managers.formatter))
                        .orElse("")
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && Objects.equals(mainId, task.mainId)
                && status == task.status
                && Objects.equals(duration, task.duration)
                && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, mainId, status, duration, startTime);
    }
}
