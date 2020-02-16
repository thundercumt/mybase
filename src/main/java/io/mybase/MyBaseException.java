package io.mybase;

public class MyBaseException extends RuntimeException {

    private static final long serialVersionUID = -8377601347015570615L;

    public MyBaseException() {
        super();
    }

    public MyBaseException(String msg) {
        super(msg);
    }

    public MyBaseException(String msg, Throwable t) {
        super(msg, t);
    }
}
