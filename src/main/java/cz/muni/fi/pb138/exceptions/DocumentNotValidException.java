package cz.muni.fi.pb138.exceptions;

/**
 * Created by Michal.Babel on 01-Jun-17.
 */
public class DocumentNotValidException extends Exception {

    public DocumentNotValidException() {
        super();
    }

    public DocumentNotValidException(String message) {
        super(message);
    }

    public DocumentNotValidException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentNotValidException(Throwable cause) {
        super(cause);
    }

    protected DocumentNotValidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
