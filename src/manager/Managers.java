package manager;

import java.time.format.DateTimeFormatter;

public class Managers {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");
    private static final HistoryManager historyManager = new InMemoryHistoryManager();
    private static final TaskManager taskManager = new InMemoryTaskManager();
    private static final FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager();

    public static TaskManager getTaskManager() {
        return taskManager;
    }

    public static HistoryManager getHistoryManager() {
        return historyManager;
    }

    public static FileBackedTaskManager getFileBackedTaskManager() {
        return fileBackedTaskManager;
    }
}
