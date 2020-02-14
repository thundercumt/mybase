package io.mybase.storage;

public class StorageException extends RuntimeException {

    private static final long serialVersionUID = 2523552317818588681L;

    public StorageException(String msg) {
        super(msg);
    }

    public StorageException(String msg, Throwable t) {
        super(msg, t);
    }
}
