package io.mybase;

public class StorageException extends MyBaseException {

    private static final long serialVersionUID = 2523552317818588681L;

    public StorageException() {
        super();
    }

    public StorageException(String msg) {
        super(msg);
    }

    public StorageException(String msg, Throwable t) {
        super(msg, t);
    }
}
