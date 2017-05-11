package cz.muni.fi.pb138.exceptions;

/**
 * Created by micha on 11.05.2017.
 */
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

    public CategoryNotPersistedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
