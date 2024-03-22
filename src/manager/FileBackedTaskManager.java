package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static manager.Managers.*;

public class FileBackedTaskManager implements TaskManager {
    private final HistoryManager historyManager = Managers.getHistoryManager();
    private final TaskManager taskManager = Managers.getTaskManager();
    private final String nameBackedTaskManager = String.format("%s.csv", getClass().getSimpleName());
    private final String headCsv = "id;type;name;status;description;start;end;epicId";
    private final String fieldHistory = String.format("%s:", historyManager.getClass().getSimpleName());
    private final String nameHistoryManager = String.format("%s.txt", historyManager.getClass().getSimpleName());

    public FileBackedTaskManager() {
        loadTask();
    }

    private boolean fileExists(Path pathFile) {
        return Files.exists(pathFile) && !Files.isDirectory(pathFile);
    }

    private void saveTask() {
        try (Writer fileWriter = new FileWriter(nameBackedTaskManager, DEFAULT_CHARSET, false)) {
            fileWriter.write(String.format("%s%n", headCsv));

            for (Task task : getTasks()) {
                fileWriter.write(String.format("%s%n", task.toCsv()));
            }

            for (Epic epic : getEpics()) {
                fileWriter.write(String.format("%s%n", epic.toCsv()));
            }

            for (Subtask subtask : getSubtasks()) {
                fileWriter.write(String.format("%s%n", subtask.toCsv()));
            }

        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    private void saveHistory() {
        if (historyManager.getHistory().isEmpty()) {
            return;
        }

        try (Writer fileWriter = new FileWriter(nameHistoryManager, DEFAULT_CHARSET, false)) {

            fileWriter.write(String.format("%s%s%n", fieldHistory, historyManager.toCsv()));

        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    @Override
    public Status getStatus(final String string) {
        return taskManager.getStatus(string);
    }

    public Optional<LocalDateTime> getTime(final String string) {
        return Objects.isNull(string) || string.isEmpty() ? Optional.empty() : Optional.ofNullable(
                LocalDateTime.parse(string.trim().toLowerCase(), DATE_TIME_FORMATTER)
        );
    }

    private String getType(final String string) {
        return switch (string.trim().toLowerCase()) {
            case "task" -> Task.class.getSimpleName().toLowerCase();
            case "epic" -> Epic.class.getSimpleName().toLowerCase();
            case "subtask" -> Subtask.class.getSimpleName().toLowerCase();
            default -> throw new RuntimeException("не соответсвует ни одному из типов задач");
        };
    }

    private List<String> read(String nameFile) {
        List<String> read = new ArrayList<>();

        Path pathFile = Paths.get(nameFile);

        try {
            if (fileExists(pathFile) && Files.size(pathFile) >= 1) {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(nameFile,
                        DEFAULT_CHARSET))) {
                    while (bufferedReader.ready()) {
                        read.add(bufferedReader.readLine());
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerReadException(e);
        }

        return read;
    }

    @Override
    public void loadTask() {
        Map<Integer, List<String>> tmpTask = new LinkedHashMap<>();

        for (String string : read(nameBackedTaskManager)) {
            List<String> list = new ArrayList<>(Arrays.asList(string.split(";")));

            if (list.size() < 4) {
                continue;
            }

            try {
                tmpTask.put(Integer.parseInt(list.getFirst().trim()), list);
            } catch (NumberFormatException ignored) {

            }
        }

        Map <Integer, Task> history = new LinkedHashMap<>();

        for (String string : read(nameHistoryManager)) {
            if (!history.isEmpty()) {
                break;
            } else if (string.isEmpty()) {
                continue;
            }

            List<String> list = new ArrayList<>(Arrays.asList(string.split(",")));
            final String possibleHeader = list.getFirst().trim();

            if (possibleHeader.compareToIgnoreCase(fieldHistory) >= 0) {
                try {
                    //Разбираем строку с содержимым fieldHistory[0-9] и извлекаем сразу первый элемент после :
                    history.put(Integer.parseInt(list.getFirst().split(":")[1]), null);

                    for (int i = 1; i < list.size(); ++i) {
                        history.put(Integer.parseInt(list.get(i)), null);
                    }

                } catch (NumberFormatException ignored) {

                }
            }
        }

        historyManager.clear();
        taskManager.clearTasks();
        taskManager.clearEpics();

        Map<Integer, List<String>> subtasks = new LinkedHashMap<>();
        Map<Integer, Epic> epics = new HashMap<>();

        for (Map.Entry<Integer, List<String>> entry : tmpTask.entrySet()) {
            List<String> list = entry.getValue();
            final int id = entry.getKey();

            if (list.get(1).isEmpty()
                    || list.get(2).isEmpty()
                    || list.get(3).isEmpty()
                    || list.get(4).isEmpty()) {
                continue;
            }

            final String type = getType(list.get(1));
            final String name =  list.get(2);
            final String description = list.get(4);
            final Status status = getStatus(list.get(3));

            switch (type) {
                case "task" -> {
                    LocalDateTime startTime = null;
                    LocalDateTime endTime = null;

                    if (list.size() > 6) {
                        startTime = getTime(list.get(5)).orElse(null);
                        endTime = getTime(list.get(6)).orElse(null);
                    }

                    Duration duration = startTime != null && endTime != null
                            ? Duration.between(startTime, endTime) : null;

                    Task task = null;

                    if (Objects.nonNull(null) && Objects.nonNull(null)) {
                        task = new Task(name, description, status, startTime, duration);
                    } else {
                        task = new Task(name, description, status);
                    }

                    if (Objects.isNull(task)) {
                        continue;
                    }

                    taskManager.addTask(task);
                    if (history.containsKey(id)) {
                        history.put(id, task);
                    }
                }
                case "epic" -> {
                    Epic epic = new Epic(name, description);
                    epics.put(id, epic);

                    if (history.containsKey(id)) {
                        history.put(id, epic);
                    }

                    taskManager.addEpic(epic);
                }
                case "subtask" -> subtasks.put(id, list);
            }
        }

        for (Map.Entry<Integer, List<String>> entry : subtasks.entrySet()) {
            List<String> list = entry.getValue();
            int id = entry.getKey();

            try {
                int epicId = Integer.parseInt(list.getLast().trim());

                if (!epics.containsKey(epicId)) {
                    throw new ManagerReadException(String.format(
                            "ID-%d эпика в подзадаче не соответствует", epicId)
                    );
                }

                final Epic epic = epics.get(epicId);

                if (Objects.isNull(epic)
                        || list.get(2).isEmpty()
                        || list.get(3).isEmpty()
                        || list.get(4).isEmpty()) {
                    continue;
                }

                final String name =  list.get(2);
                final String description = list.get(4);
                final Status status = getStatus(list.get(3));
                LocalDateTime startTime = null;
                LocalDateTime endTime = null;

                if (list.size() > 6) {
                    startTime = getTime(list.get(5)).orElse(null);
                    endTime = getTime(list.get(6)).orElse(null);
                }

                Duration duration = Objects.nonNull(startTime) && Objects.nonNull(endTime)
                        ? Duration.between(startTime, endTime) : null;

                Subtask subtask = null;

                if (Objects.nonNull(startTime) && Objects.nonNull(duration)) {
                    subtask = new Subtask(name, description, status, startTime, duration, epic);
                } else {
                    subtask = new Subtask(name, description, status, epic);
                }

                if (Objects.isNull(subtask)) {
                    continue;
                }

                epic.addSubtask(subtask);

                if (history.containsKey(id)) {
                    history.put(id, subtask);
                }

            } catch (NumberFormatException e) {
                throw Objects.nonNull(e.getMessage()) ? new ManagerReadException(String.format(
                        "%s и нет id эпика", e.getMessage()))
                        : new ManagerReadException("нет id эпика");
            }
        }

        history.entrySet().stream()
                .forEach(integerTaskEntry -> historyManager.add(integerTaskEntry.getValue()));
    }

    @Override
    public boolean addTask(Task task) {
        if (taskManager.addTask(task)) {
            saveTask();
            return true;
        }



        return false;
    }

    @Override
    public boolean updateTask(Task oldTask, Task task) {
        if (taskManager.updateTask(oldTask, task)) {
            saveTask();
            saveHistory();
            return true;
        }

        return false;
    }

    @Override
    public boolean addSubtask(Subtask subtask) {
        if (taskManager.addSubtask(subtask)) {
            saveTask();
            return true;
        }

        return false;
    }

    @Override
    public boolean addEpic(Epic epic) {
        if (taskManager.addEpic(epic)) {
            saveTask();
            return true;
        }

        return false;
    }

    @Override
    public boolean addSubtask(Epic epic, Subtask subtask) {
        if (taskManager.addSubtask(epic, subtask)) {
            saveTask();
            return true;
        }

        return false;
    }

    @Override
    public boolean updateSubtask(Epic epic, Subtask oldSubtask, Subtask subtask) {
        if (taskManager.updateSubtask(epic, oldSubtask, subtask)) {
            saveTask();
            saveHistory();
            return true;
        }

        return false;
    }

    @Override
    public Optional<Epic> getEpic(int id) {
        Optional<Epic> epic = taskManager.getEpic(id);

        if (epic.isPresent()) {
            saveHistory();
        }

        return epic;
    }

    @Override
    public Optional<Epic> getEpicWithoutHistory(int id) {
        return taskManager.getEpicWithoutHistory(id);
    }

    @Override
    public Optional<Task> getTaskWithoutHistory(int id) {
        return taskManager.getTaskWithoutHistory(id);
    }

    @Override
    public Optional<Subtask> getSubtaskWithoutHistory(int id) {
        return taskManager.getSubtaskWithoutHistory(id);
    }

    @Override
    public Optional<Task> getTask(int id) {
        Optional<Task> task = taskManager.getTask(id);

        if (task.isPresent()) {
            saveHistory();
        }

        return task;
    }

    @Override
    public void removeTask(int id) {
        taskManager.removeTask(id);
        saveTask();
        saveHistory();
    }

    @Override
    public void removeTask(Task task) {
        taskManager.removeTask(task);
        saveTask();
        saveHistory();
    }

    @Override
    public void removeSubtask(int epicId, int subtaskId) {
        taskManager.removeSubtask(epicId, subtaskId);
        saveTask();
        saveHistory();
    }

    @Override
    public void removeSubtask(Epic epic, Subtask subtask) {
        taskManager.removeSubtask(epic, subtask);
        saveTask();
        saveHistory();
    }

    @Override
    public void removeSubtask(Subtask subtask) {
        taskManager.removeSubtask(subtask);
        saveTask();
        saveHistory();
    }

    @Override
    public void removeSubtask(int subtaskId) {
        taskManager.removeSubtask(subtaskId);
        saveTask();
        saveHistory();
    }

    @Override
    public void removeEpic(int id) {
        taskManager.removeEpic(id);
        saveTask();
        saveHistory();
    }

    @Override
    public void removeEpic(Epic epic) {
        taskManager.removeEpic(epic);
        saveTask();
        saveHistory();
    }

    @Override
    public void clearTasks() {
        taskManager.clearTasks();
        saveTask();
        saveHistory();
    }

    @Override
    public void clearEpics() {
        taskManager.clearEpics();
        saveTask();
        saveHistory();
    }

    @Override
    public Optional<Subtask> getSubtask(int idSubtask) {
        Optional<Subtask> subtask = taskManager.getSubtask(idSubtask);

        if (subtask.isPresent()) {
            saveHistory();
        }

        return subtask;
    }

    @Override
    public List<Task> getTasks() {
        return taskManager.getTasks();
    }

    @Override
    public boolean timeIsConflict(Task task) {
        return taskManager.timeIsConflict(task);
    }

    @Override
    public boolean updateSubtask(Subtask oldSubtask, Subtask subtask) {
        if (taskManager.updateSubtask(oldSubtask, subtask)) {
            saveTask();
            saveHistory();
            return true;
        }


        return false;
    }

    @Override
    public List<Epic> getEpics() {
        return taskManager.getEpics();
    }

    @Override
    public boolean containsKeyInTasks(int id) {
        return taskManager.containsKeyInTasks(id);
    }

    @Override
    public boolean containsKeyInEpics(int id) {
        return taskManager.containsKeyInEpics(id);
    }

    @Override
    public boolean containsKeyInSubtasks(int id) {
        return taskManager.containsKeyInSubtasks(id);
    }

    @Override
    public List<Subtask> getSubtasks() {
        return taskManager.getSubtasks();
    }

    @Override
    public List<Subtask> getSubtasks(int idEpic) {
        return taskManager.getSubtasks(idEpic);
    }

    @Override
    public List<Subtask> getSubtasks(Epic epic) {
        return taskManager.getSubtasks(epic);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return taskManager.getPrioritizedTasks();
    }

    @Override
    public boolean idIsEmpty(int id) {
        return taskManager.idIsEmpty(id);
    }

    public String getNameBackedTaskManager() {
        return nameBackedTaskManager;
    }

    public String getNameHistoryManager() {
        return nameHistoryManager;
    }
}
