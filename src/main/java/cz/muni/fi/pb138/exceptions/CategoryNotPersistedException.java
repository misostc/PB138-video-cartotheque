package cz.muni.fi.pb138.exceptions;

public class CategoryNotPersistedException extends RuntimeException {
    public CategoryNotPersistedException() {
    }

    public CategoryNotPersistedException(String message) {
        super(message);
    }

    public CategoryNotPersistedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoryNotPersistedException(Throwable cause) {
        super(cause);
    }

}
