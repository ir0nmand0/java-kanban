package manager;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Managers {
    public static final ZoneId ZONE_ID = ZoneId.systemDefault();
    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.ofTotalSeconds(0);
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final HistoryManager HISTORY_MANAGER = new InMemoryHistoryManager();
    public static final TaskManager TASK_MANAGER = new InMemoryTaskManager();
    public static final FileBackedTaskManager FILE_BACKED_TASK_MANAGER = new FileBackedTaskManager();
}
