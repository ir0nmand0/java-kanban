package test;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

import static manager.Managers.ZONE_ID;

public abstract class TasksEpicsSubtasks {
    protected static final LocalDateTime timeFirstArtificialSatellite = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(-386310686L), ZONE_ID);
    protected static final Task task1 = new Task("Task1 TaskTest", "TaskTest1 description", Status.NEW);
    protected static final Task task2 = new Task("Task2 TaskTest", "TaskTest2 description",
            Status.IN_PROGRESS);
    protected static final Task task3 = new Task("Task3 TaskTest", "TaskTest3 description", Status.NEW,
            timeFirstArtificialSatellite, Duration.ofHours(1));
    protected static final Task taskWithConflictTime = new Task("Task3 conflict", "TaskTest3 conflict time",
            Status.NEW, task3.getStartTime().get().plusMinutes(1), Duration.ofHours(5));
    protected static final Task task4 = new Task("Task4 TaskTest", "TaskTest4 description",
            Status.IN_PROGRESS, timeFirstArtificialSatellite.plusHours(2), Duration.ofHours(3));
    protected static final Task task5 = new Task("Task5 TaskTest", "TaskTest5 description",
            Status.IN_PROGRESS, timeFirstArtificialSatellite.with(task4.getStartTime().get()), Duration.ofHours(3));
    protected static final Epic epic1 = new Epic("Epic EpicTest1", "EpicTest1 description");
    protected static final Epic epic2 = new Epic("Epic EpicTest2", "EpicTest2 description");
    protected static final Epic epicWithoutSubtask = new Epic("Epic WithoutSubtask",
            "EpicTest WithoutSubtask description");
    protected static final Subtask subtask1 = new Subtask(
            "Subtask Subtask1",
            "Subtask1 description",
            Status.NEW,
            epic1
    );
    protected static final Subtask subtask2 = new Subtask(
            "Subtask Subtask2",
            "Subtask2 description",
            Status.IN_PROGRESS,
            epic1
    );
    protected static final Subtask subtask3 = new Subtask(
            "Subtask Subtask3",
            "Subtask3 description",
            Status.DONE,
            timeFirstArtificialSatellite.plusDays(1),
            Duration.ofHours(1),
            epic1
    );
    protected static final Subtask subtaskWithConflictTime = new Subtask(
            "Subtask conflict",
            "Subtask3 description",
            Status.DONE,
            subtask3.getStartTime().get(),
            Duration.ofHours(3),
            epic1);
    protected static final Subtask subtask4 = new Subtask(
            "Subtask Subtask4",
            "Subtask4 description",
            Status.NEW,
            epic1
    );
    protected static Subtask subtask5 = new Subtask(
            "Subtask Subtask5",
            "Subtask5 description",
            Status.IN_PROGRESS,
            epic2
    );
    protected static final Subtask subtask6 = new Subtask(
            "Subtask Subtask5",
            "Subtask5 description",
            Status.IN_PROGRESS,
            epic2
    );
    protected static final Subtask subtask7 = new Subtask(
            "Subtask Subtask6",
            "Subtask6 description",
            Status.NEW,
            epic2
    );

}
