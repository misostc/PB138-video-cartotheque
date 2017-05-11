package cz.muni.fi.pb138.exceptions;

/**
 * Created by micha on 11.05.2017.
 */
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

    public CategoriesNotAvailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
