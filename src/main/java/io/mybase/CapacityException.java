package io.mybase;

public class CapacityException extends MyBaseException {

    private static final long serialVersionUID = 6062144302983280132L;

    public CapacityException() {
        super();
    }

    public CapacityException(String msg) {
        super(msg);
    }

    public CapacityException(String msg, Throwable t) {
        super(msg, t);
    }
}
