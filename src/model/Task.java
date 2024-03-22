package model;
import com.google.gson.annotations.Expose;
import manager.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static manager.Managers.*;

public class Task {
    @Expose
    protected final String name;
    @Expose
    protected final String description;
    @Expose(deserialize = false)
    protected final int id;
    @Expose(serialize = false, deserialize = false)
    private static int lastId = 1;
    @Expose
    protected Status status;
    @Expose
    protected Duration duration;
    @Expose
    protected LocalDateTime startTime;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = getFreeId();
        this.duration = null;
        this.startTime = null;
    }

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = getFreeId();
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

    public long getStartTimeToEpochMilli() {
        return Objects.nonNull(startTime) ? startTime.atZone(ZONE_ID).toInstant().toEpochMilli() : 0;
    }

    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }

    private int getFreeId() {
        if (lastId < Integer.MAX_VALUE) {
            return lastId++;
        }

        throw new TaskAddException("Свободного Id нет");
    }

    public int getId() {
        return id;
    }

    public String toCsv() {
        return String.format("%d;%s;%s;%s;%s;%s;%s;",
                id, getClass().getSimpleName(), name, status, description, startTimeToString(), endTimeToString()
        );
    }

    protected String startTimeToString() {
        return getStartTime()
                .map(localDateTime -> localDateTime.format(Managers.DATE_TIME_FORMATTER))
                .orElse("");
    }

    protected String endTimeToString() {
        return getEndTime().map(localDateTime -> localDateTime.format(Managers.DATE_TIME_FORMATTER)).orElse("");

    }

    protected long durationToLong() {
        return getDuration()
                .map(Duration::toSeconds)
                .orElse(0L);
    }

    @Override
    public String toString() {
        return Task.class.getSimpleName() + "{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + durationToLong() +
                ", startTime=" + startTimeToString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && status == task.status && Objects.equals(duration, task.duration)
                && Objects.equals(startTime, task.startTime);
    }

    public boolean equalsWithoutId(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && status == task.status && Objects.equals(duration, task.duration)
                && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, duration, startTime);
    }
}
