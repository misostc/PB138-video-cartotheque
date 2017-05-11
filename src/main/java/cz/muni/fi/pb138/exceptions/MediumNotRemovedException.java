package cz.muni.fi.pb138.exceptions;

public class MediumNotRemovedException extends RuntimeException {
    public MediumNotRemovedException() {
    }

    public MediumNotRemovedException(String message) {
        super(message);
    }

    public MediumNotRemovedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MediumNotRemovedException(Throwable cause) {
        super(cause);
    }

}
