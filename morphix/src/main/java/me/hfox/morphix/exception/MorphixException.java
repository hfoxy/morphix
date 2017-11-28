package me.hfox.morphix.exception;

/**
 * Created by Harry on 28/11/2017.
 *
 * Parent class for all Morphix-related exceptions.
 */
public class MorphixException extends Exception {

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
