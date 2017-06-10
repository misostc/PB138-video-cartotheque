package cz.muni.fi.pb138.exceptions;

public class CategoryNotRemovedException extends Exception {
    public CategoryNotRemovedException() {
    }

    public CategoryNotRemovedException(String message) {
        super(message);
    }

    public CategoryNotRemovedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoryNotRemovedException(Throwable cause) {
        super(cause);
    }

}
