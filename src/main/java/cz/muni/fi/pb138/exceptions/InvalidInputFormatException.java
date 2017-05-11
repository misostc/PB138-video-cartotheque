package cz.muni.fi.pb138.exceptions;

public class InvalidInputFormatException extends RuntimeException {
    public InvalidInputFormatException() {
    }

    public InvalidInputFormatException(String message) {
        super(message);
    }

    public InvalidInputFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInputFormatException(Throwable cause) {
        super(cause);
    }

}
