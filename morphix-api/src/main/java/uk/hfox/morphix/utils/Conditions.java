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
package uk.hfox.morphix.utils;

import java.util.List;

public final class Conditions {

    Conditions() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an UnsupportedOperationException stating that this feature is not currently implemented.
     * Used in various places around the project to reduce duplicates
     *
     * @return A new UnsupportedOperationException
     */
    public static UnsupportedOperationException unimplemented() {
        return new UnsupportedOperationException("This feature is not currently implemented");
    }

    /**
     * Checks if the supplied value is not null.
     *
     * @param value The value to check
     *
     * @return true if the value is not null
     *
     * @throws IllegalArgumentException if the argument is null
     */
    public static boolean notNull(Object value) {
        return notNull(value, "argument");
    }

    /**
     * Checks if the supplied value is not null. Includes the name in the error message
     *
     * @param value The value to check
     * @param name The name to include
     * @return true if the value is not null
     * @throws IllegalArgumentException if the argument is null
     */
    public static boolean notNull(Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }

        return true;
    }

    /**
     * Checks if the supplied list is empty or filled with null objects
     *
     * @param value The list to check
     * @return true if the list is not null, not empty and not filled with null objects
     * @throws IllegalArgumentException if the argument is null, empty or filled with null objects
     */
    public static boolean notEmptyOrNullFilled(List value) {
        return notEmptyOrNullFilled(value, "argument");
    }

    /**
     * Checks if the supplied list is empty or filled with null objects.
     * Includes name in the error message
     *
     * @param value The list to check
     * @param name The name to include
     * @return true if the list is not null, not empty and not filled with null objects
     * @throws IllegalArgumentException if the argument is null, empty or filled with null objects
     */
    public static boolean notEmptyOrNullFilled(List value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }

        if (value.isEmpty()) {
            throw new IllegalArgumentException(name + " cannot be empty");
        }

        for (Object item : value) {
            if (item != null) {
                return true;
            }
        }

        throw new IllegalArgumentException(name + " cannot be full of null objects");
    }

}
