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
        this.head = "id;type;name;status;description;epic";
        loadTask();
        loadHistory();
    }

    private boolean fileExists(Path pathFile) {
        return Files.exists(pathFile) && !Files.isDirectory(pathFile);
    }

    private void saveTask() throws ManagerSaveException {
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

    private void saveHistory() throws ManagerSaveException {
        if (historyManager.getHistory().isEmpty()) {
            return;
        }

        try (Writer fileWriter = new FileWriter(nameHistoryManager, StandardCharsets.UTF_8, false)) {

            fileWriter.write(String.format("%s%s%n", fieldHistory, historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    private Status getStatus(final String string) throws ManagerSaveException {
        return switch (string.trim().toLowerCase()) {
            case "new" -> Status.NEW;
            case "in_progress" -> Status.IN_PROGRESS;
            case "done" -> Status.DONE;
            default -> throw new ManagerSaveException("не соответсвует ни одному из типов статуса задач");
        };
    }

    private List<String> read(String nameFile) throws ManagerSaveException {
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
            throw new ManagerSaveException(e);
        }

        return read;
    }

    @Override
    public void loadTask() throws ManagerSaveException {
        Map<Integer, List<String>> tmpTask = new LinkedHashMap<>();

        for (String string : read(nameBackedTaskManager)) {
            List<String> list = new ArrayList<>(Arrays.asList(string.split(";")));

            if (list.size() < 4) {
                continue;
            }

            try {
                tmpTask.put(Integer.parseInt(list.getFirst().trim()), list);
            } catch (NumberFormatException e) {
                //Игнорируем все, кроме чисел
            }
        }

        taskManager.clearTasks();
        taskManager.clearEpics();

        Map<Integer, List<String>> subtasks = new LinkedHashMap<>();

        for (Map.Entry<Integer, List<String>> entry : tmpTask.entrySet()) {
            List<String> list = entry.getValue();
            Integer id = entry.getKey();

            switch (list.get(1).trim().toLowerCase()) {
                case "task": {
                    taskManager.addTask(new Task(id, list.get(2), list.get(4), getStatus(list.get(3))));
                    break;
                }
                case "epic": {
                    taskManager.addEpic(new Epic(id, list.get(2), list.get(4)));
                    break;
                }
                case "subtask": {
                    subtasks.put(id, list);
                    break;
                }
                default:
                    throw new ManagerSaveException("Тип задачи не распознан");
            }
        }

        for (Map.Entry<Integer, List<String>> entry : subtasks.entrySet()) {
            List<String> list = entry.getValue();
            Integer id = entry.getKey();
            try {
                int epicId = Integer.parseInt(list.getLast().trim());

                if (!containsKeyInEpics(epicId)) {
                    throw new ManagerSaveException(String.format(
                            "ID-%d эпика в подзадаче не соответствует", epicId)
                    );
                }

                Epic epic = taskManager.getEpic(epicId);

                epic.addSubtask(new Subtask(id, list.get(2),
                        list.get(4), getStatus(list.get(3)), epic));
            } catch (NumberFormatException e) {
                throw e.getMessage() != null ? new ManagerSaveException(String.format(
                        "%s и нет id эпика", e.getMessage()))
                        : new ManagerSaveException("нет id эпика");
            }
        }
    }

    @Override
    public void loadHistory() {
        List<Integer> idHistory = new ArrayList<>();

        for (String string : read(nameHistoryManager)) {
            List<String> list = new ArrayList<>(Arrays.asList(string.split(",")));

            try {
                if (!list.isEmpty() && idHistory.isEmpty()
                        && list.getFirst().trim().compareToIgnoreCase(fieldHistory) >= 0) {
                    idHistory.add(Integer.parseInt(list.getFirst().split(":")[1]));

                    for (int i = 1; i < list.size(); ++i) {
                        idHistory.add(Integer.parseInt(list.get(i)));
                    }

                }
            } catch (NumberFormatException e) {
                //Игнорируем все, кроме чисел
            }

            historyManager.clear();

            for (Integer id : idHistory) {
                if (containsKeyInTasks(id)) {
                    historyManager.add(getTask(id));
                } else if (containsKeyInEpics(id)) {
                    historyManager.add(getEpic(id));
                } else {
                    for (Map.Entry<Integer, Epic> entry : getMapEpics().entrySet()) {
                        if (entry.getValue().getMapSubtasks().containsKey(id)) {
                            historyManager.add(entry.getValue().getMapSubtasks().get(id));
                        }
                    }
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
    public Epic getEpic(Integer id) {
        Epic epic = taskManager.getEpic(id);

        saveHistory();

        return epic;
    }

    @Override
    public Task getTask(Integer id) {
        Task task = taskManager.getTask(id);

        saveHistory();

        return task;
    }

    @Override
    public void removeTask(Integer id) {
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
    public void removeSubtask(Integer epicId, Integer subtaskId) {
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
    public void removeSubtask(Integer subtaskId) {
        taskManager.removeSubtask(subtaskId);
        saveTask();
        saveHistory();
    }

    @Override
    public void removeEpic(Integer id) {
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
    public Subtask getSubtask(Integer idSubtask) {
        Subtask subtask = taskManager.getSubtask(idSubtask);

        saveHistory();

        return subtask;
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
    public List<Subtask> getSubtasks(Integer idEpic) {
        return taskManager.getSubtasks(idEpic);
    }

    @Override
    public List<Subtask> getSubtasks(Epic epic) {
        return taskManager.getSubtasks(epic);
    }

    public String getNameBackedTaskManager() {
        return nameBackedTaskManager;
    }

    public String getNameHistoryManager() {
        return nameHistoryManager;
    }
}
