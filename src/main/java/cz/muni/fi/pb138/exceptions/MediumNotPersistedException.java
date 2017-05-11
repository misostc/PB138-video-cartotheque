package cz.muni.fi.pb138.exceptions;

public class MediumNotPersistedException extends RuntimeException {
    public MediumNotPersistedException() {
    }

    public MediumNotPersistedException(String message) {
        super(message);
    }

    public MediumNotPersistedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MediumNotPersistedException(Throwable cause) {
        super(cause);
    }

}
