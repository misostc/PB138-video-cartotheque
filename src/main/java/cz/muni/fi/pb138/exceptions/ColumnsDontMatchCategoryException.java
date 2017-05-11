package cz.muni.fi.pb138.exceptions;

public class ColumnsDontMatchCategoryException extends RuntimeException {
    public ColumnsDontMatchCategoryException() {
    }

    public ColumnsDontMatchCategoryException(String message) {
        super(message);
    }

    public ColumnsDontMatchCategoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public ColumnsDontMatchCategoryException(Throwable cause) {
        super(cause);
    }

}
