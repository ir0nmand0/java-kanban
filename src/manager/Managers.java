package manager;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Managers {
    public static final ZoneId ZONE_ID = ZoneId.systemDefault();
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final HistoryManager HISTORY_MANAGER = new InMemoryHistoryManager();
    private static final TaskManager TASK_MANAGER = new InMemoryTaskManager();
    private static final FileBackedTaskManager FILE_BACKED_TASK_MANAGER = new FileBackedTaskManager();

    public static TaskManager getTaskManager() {
        return TASK_MANAGER;
    }

    public static TaskManager getFileTaskManager() {
        return FILE_BACKED_TASK_MANAGER;
    }

    public static HistoryManager getHistoryManager() {
        return HISTORY_MANAGER;
    }

}
