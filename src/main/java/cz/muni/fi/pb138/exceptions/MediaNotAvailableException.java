package cz.muni.fi.pb138.exceptions;

public class MediaNotAvailableException extends Exception {
    public MediaNotAvailableException() {
    }

    public MediaNotAvailableException(String message) {
        super(message);
    }

    public MediaNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public MediaNotAvailableException(Throwable cause) {
        super(cause);
    }

}
