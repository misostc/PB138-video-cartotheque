package cz.muni.fi.pb138.exceptions;

public class MediumNotFoundException extends RuntimeException {
    public MediumNotFoundException() {
    }

    public MediumNotFoundException(String message) {
        super(message);
    }

    public MediumNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MediumNotFoundException(Throwable cause) {
        super(cause);
    }

}
