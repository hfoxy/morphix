package me.hfox.morphix.exception.support;

import me.hfox.morphix.exception.MorphixException;
import me.hfox.morphix.helper.lifecycle.TimeLibrary;

/**
 * Created by Harry on 28/11/2017.
 *
 * Thrown by Morphix when the TimeLibrary that has been requested is not available
 */
public class InvalidTimeLibraryException extends MorphixException {

    private final TimeLibrary library;

    public InvalidTimeLibraryException(TimeLibrary library, String message) {
        super(message);
        this.library = library;
    }

    public InvalidTimeLibraryException(TimeLibrary library, String message, Throwable cause) {
        super(message, cause);
        this.library = library;
    }

    /**
     * Get the library which caused the error
     * @return The faulting library
     */
    public TimeLibrary getLibrary() {
        return library;
    }

}
