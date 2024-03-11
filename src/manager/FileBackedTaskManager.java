package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTaskManager implements TaskManager {
    private final TaskManager taskManager;
    private final HistoryManager historyManager;
    private final String nameBackedTaskManager;
    private final String head;
    private final String fieldHistory;
    private final String nameHistoryManager;

    public FileBackedTaskManager() {
        this.historyManager = Managers.getHistoryManager();
        this.taskManager = Managers.getTaskManager();
        this.fieldHistory = String.format("%s:", historyManager.getClass().getSimpleName());
        this.nameHistoryManager = String.format("%s.txt", historyManager.getClass().getSimpleName());
        this.nameBackedTaskManager = String.format("%s.csv", getClass().getSimpleName());
        this.head = "id;type;name;status;description;start;end;epic";
        loadTask();
        loadHistory();
    }

    private boolean fileExists(Path pathFile) {
        return Files.exists(pathFile) && !Files.isDirectory(pathFile);
    }

    private void saveTask() {
        try (Writer fileWriter = new FileWriter(nameBackedTaskManager, StandardCharsets.UTF_8, false)) {
            fileWriter.write(String.format("%s%n", head));

            for (Task task : getTasks()) {
                fileWriter.write(String.format("%s%n", task));
            }

            for (Epic epic : getEpics()) {
                fileWriter.write(String.format("%s%n", epic));
            }

            for (Subtask subtask : getSubtasks()) {
                fileWriter.write(String.format("%s%n", subtask));
            }

        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    private void saveHistory() {
        if (historyManager.getHistory().isEmpty()) {
            return;
        }

        try (Writer fileWriter = new FileWriter(nameHistoryManager, StandardCharsets.UTF_8, false)) {

            fileWriter.write(String.format("%s%s%n", fieldHistory, historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    private Status getStatus(final String string) {
        return switch (string.trim().toLowerCase()) {
            case "new" -> Status.NEW;
            case "in_progress" -> Status.IN_PROGRESS;
            case "done" -> Status.DONE;
            default -> throw new RuntimeException("не соответсвует ни одному из типов статуса задач");
        };
    }

    private Optional<LocalDateTime> getTime(final String string) {
        return string.isEmpty() ? Optional.empty() : Optional.ofNullable(
                LocalDateTime.parse(string.trim().toLowerCase(), Managers.formatter)
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
                        StandardCharsets.UTF_8))) {
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

        taskManager.clearTasks();
        taskManager.clearEpics();

        Map<Integer, List<String>> subtasks = new LinkedHashMap<>();

        for (Map.Entry<Integer, List<String>> entry : tmpTask.entrySet()) {
            List<String> list = entry.getValue();
            final Integer id = entry.getKey();
            final String type = getType(list.get(1));
            final String name =  list.get(2);
            final String description = list.get(4);
            final Status status = getStatus(list.get(3));

            switch (type) {
                case "task" -> {
                    LocalDateTime startTime;
                    LocalDateTime endTime;

                    try {
                        startTime = getTime(list.get(5)).orElse(null);
                        endTime = getTime(list.get(6)).orElse(null);
                    } catch (IndexOutOfBoundsException ignored) {
                        startTime = null;
                        endTime = null;
                    }

                    Duration duration = startTime != null && endTime != null
                            ? Duration.between(startTime, endTime) : null;

                    if (startTime != null && duration != null) {
                        taskManager.addTask(new Task(id, name, description, status, startTime, duration));
                    } else {
                        taskManager.addTask(new Task(id, name, description, status));
                    }
                }
                case "epic" -> {
                    taskManager.addEpic(new Epic(id, name, description));
                }
                case "subtask" -> subtasks.put(id, list);
            }
        }

        for (Map.Entry<Integer, List<String>> entry : subtasks.entrySet()) {
            List<String> list = entry.getValue();
            int id = entry.getKey();

            try {
                int epicId = Integer.parseInt(list.getLast().trim());

                if (!containsKeyInEpics(epicId)) {
                    throw new ManagerReadException(String.format(
                            "ID-%d эпика в подзадаче не соответствует", epicId)
                    );
                }

                final Epic epic = taskManager.getEpic(epicId).orElse(null);
                final String name =  list.get(2);
                final String description = list.get(4);
                final Status status = getStatus(list.get(3));
                LocalDateTime startTime;
                LocalDateTime endTime;

                try {
                    startTime = getTime(list.get(5)).orElse(null);
                    endTime = getTime(list.get(6)).orElse(null);
                } catch (IndexOutOfBoundsException ignored) {
                    startTime = null;
                    endTime = null;
                }

                Duration duration = startTime != null && endTime != null
                        ? Duration.between(startTime, endTime) : null;

                if (startTime != null && duration != null) {
                    epic.addSubtask(new Subtask(id, name, description, status, startTime, duration, epic));
                } else {
                    epic.addSubtask(new Subtask(id, name, description, status, epic));
                }

            } catch (NumberFormatException e) {
                throw e.getMessage() != null ? new ManagerReadException(String.format(
                        "%s и нет id эпика", e.getMessage()))
                        : new ManagerReadException("нет id эпика");
            }
        }
    }

    @Override
    public void loadHistory() {
        List<Integer> idHistory = new ArrayList<>();

        for (String string : read(nameHistoryManager)) {
            if (!idHistory.isEmpty()) {
                break;
            } else if (string.isEmpty()) {
                continue;
            }

            List<String> list = new ArrayList<>(Arrays.asList(string.split(",")));
            final String possibleHeader =  list.getFirst().trim();

            if (possibleHeader.compareToIgnoreCase(fieldHistory) >= 0) {
                try {
                    //Разбираем строку с содержимым fieldHistory[0-9] и извлекаем сразу первый элемент после :
                    idHistory.add(Integer.parseInt(list.getFirst().split(":")[1]));

                    for (int i = 1; i < list.size(); ++i) {
                        idHistory.add(Integer.parseInt(list.get(i)));
                    }

                } catch (NumberFormatException ignored) {

                }
            }

            historyManager.clear();

            for (Integer id : idHistory) {
                if (containsKeyInTasks(id)) {
                    historyManager.add(getTask(id).orElse(null));
                } else if (containsKeyInEpics(id)) {
                    historyManager.add(getEpic(id).orElse(null));
                } else {
                    getMapEpics().values().stream()
                            .filter(epic -> epic.getMapSubtasks().containsKey(id))
                            .map(epic -> epic.getMapSubtasks().get(id))
                            .forEach(historyManager::add);
                }
            }
        }
    }

    @Override
    public void addTask(Task task) {
        taskManager.addTask(task);
        saveTask();
    }

    @Override
    public void updateTask(Task oldTask, Task task) {
        taskManager.updateTask(oldTask, task);
        saveTask();
    }

    @Override
    public void addEpic(Epic epic) {
        taskManager.addEpic(epic);
        saveTask();
    }

    @Override
    public void addSubtask(Epic epic, Subtask subtask) {
        taskManager.addSubtask(epic, subtask);
        saveTask();
    }

    @Override
    public void updateSubtask(Epic epic, Subtask oldSubtask, Subtask subtask) {
        taskManager.updateSubtask(epic, oldSubtask, subtask);
        saveTask();
    }

    @Override
    public Optional<Epic> getEpic(int id) {
        if (taskManager.getEpic(id).isPresent()) {
            saveHistory();
        }

        return taskManager.getEpic(id);
    }

    @Override
    public Optional<Task> getTask(int id) {
        if (taskManager.getTask(id).isPresent()) {
            saveHistory();
        }

        return taskManager.getTask(id);
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
        if (taskManager.getSubtask(idSubtask).isPresent()) {
            saveHistory();
        }

        return taskManager.getSubtask(idSubtask);
    }

    @Override
    public Map<Integer, Epic> getMapEpics() {
        return taskManager.getMapEpics();
    }

    @Override
    public List<Task> getTasks() {
        return taskManager.getTasks();
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

    public String getNameBackedTaskManager() {
        return nameBackedTaskManager;
    }

    public String getNameHistoryManager() {
        return nameHistoryManager;
    }
}
