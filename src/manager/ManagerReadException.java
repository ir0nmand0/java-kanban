package manager;

public class ManagerReadException extends RuntimeException {
    public ManagerReadException(String message) {
        super(message);
    }

    public ManagerReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManagerReadException(Throwable cause) {
        super(cause);
    }
}
