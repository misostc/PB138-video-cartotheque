package cz.muni.fi.pb138.exceptions;

public class DocumentNotSavedException extends RuntimeException {
    public DocumentNotSavedException() {
    }

    public DocumentNotSavedException(String message) {
        super(message);
    }

    public DocumentNotSavedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentNotSavedException(Throwable cause) {
        super(cause);
    }

}
