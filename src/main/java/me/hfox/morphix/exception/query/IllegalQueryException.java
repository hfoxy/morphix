package me.hfox.morphix.exception.query;

import me.hfox.morphix.exception.MorphixException;

public class IllegalQueryException extends MorphixException {

    private static final long serialVersionUID = 2345476788517259233L;

    public IllegalQueryException() {
    }

    public IllegalQueryException(String message) {
        super(message);
    }

    public IllegalQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalQueryException(Throwable cause) {
        super(cause);
    }

}
