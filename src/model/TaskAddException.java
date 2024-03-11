package model;

public class TaskAddException extends RuntimeException {
    public TaskAddException() {
    }

    public TaskAddException(String message) {
        super(message);
    }

    public TaskAddException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskAddException(Throwable cause) {
        super(cause);
    }
}
