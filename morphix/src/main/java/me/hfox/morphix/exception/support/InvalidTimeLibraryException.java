/*-
 * ========================LICENSE_START========================
 * Morphix API
 * %%
 * Copyright (C) 2017 - 2018 Harry Fox
 * %%
 * This file is part of Morphix, licensed under the MIT License (MIT).
 * 
 * Copyright 2018 Harry Fox <https://hfox.uk/>
 * Copyright 2018 contributors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * ========================LICENSE_END========================
 */
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
