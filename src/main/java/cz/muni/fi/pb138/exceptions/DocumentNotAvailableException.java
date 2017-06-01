package cz.muni.fi.pb138.exceptions;

public class DocumentNotAvailableException extends RuntimeException {
    public DocumentNotAvailableException() {
    }

    public DocumentNotAvailableException(String message) {
        super(message);
    }

    public DocumentNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentNotAvailableException(Throwable cause) {
        super(cause);
    }

}
