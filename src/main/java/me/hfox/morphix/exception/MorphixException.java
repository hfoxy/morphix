package me.hfox.morphix.exception;

public class MorphixException extends RuntimeException {

    private static final long serialVersionUID = 113486276190351540L;

    public MorphixException() {
        // empty
    }

    public MorphixException(String message) {
        super(message);
    }

    public MorphixException(String message, Throwable cause) {
        super(message, cause);
    }

    public MorphixException(Throwable cause) {
        super(cause);
    }

}
