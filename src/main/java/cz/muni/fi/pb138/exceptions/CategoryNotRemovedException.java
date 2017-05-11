package cz.muni.fi.pb138.exceptions;

/**
 * Created by micha on 11.05.2017.
 */
public class CategoryNotRemovedException extends RuntimeException {
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

    public CategoryNotRemovedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
