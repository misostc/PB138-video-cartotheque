package cz.muni.fi.pb138.exceptions;

public class CategoriesNotAvailableException extends RuntimeException {
    public CategoriesNotAvailableException() {
    }

    public CategoriesNotAvailableException(String message) {
        super(message);
    }

    public CategoriesNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoriesNotAvailableException(Throwable cause) {
        super(cause);
    }

}
