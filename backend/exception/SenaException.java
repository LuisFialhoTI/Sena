package exception;

public class SenaException extends Exception {
    public SenaException(String message) {
        super(message);
    }

    public SenaException(String message, Throwable cause) {
        super(message, cause);
    }
}
