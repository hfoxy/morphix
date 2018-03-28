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
package uk.hfox.morphix.transform;

import java.lang.reflect.Field;

/**
 * Converts a DB type into the correct type
 *
 * @param <T> The DB entry type
 */
public interface Converter<T> {

    /**
     * Converts the DB value to the stored value
     *
     * @param value The DB value
     * @param current The current value, or null if no value exists
     * @param type The type of the owner field
     * @return The stored value
     */
    default Object pull(Object value, Object current, Class<?> type) {
        return value;
    }

    /**
     * Alias of {@link Converter#pull(String, Object, Object, Class)}
     */
    default Object pull(String key, T entry) {
        Object value = pull(key, entry, null, null);
        return pull(value, null, null);
    }

    /**
     * Pulls the object from the DB entry
     *
     * @param key   The key to pull
     * @param entry The DB entry to pull from
     * @param value The current value, or null if no value exists
     * @param type The type of the owner field
     *
     * @return The constructed object created from the entry
     */
    default Object pull(String key, T entry, Object value, Class<?> type) {
        Object val = pull(key, entry);
        return pull(val, value, type);
    }

    /**
     * Converts the stored value to the DB value
     *
     * @param value The stored value
     * @return The DB value
     */
    default Object push(Object value) {
        return value;
    }

    /**
     * Used specifically for iterable-result converters (array, set, collection)
     *
     * @param value The value to convert
     * @param field The field it was stored in
     * @return The converted value
     */
    default Object push(Object value, Field field) {
        return push(value);
    }

    /**
     * Pushes the object to the DB entry
     *
     * @param key   The key to push to
     * @param entry The DB entry to push to
     * @param value The value to push
     * @param field The field it was stored in
     */
    void push(String key, T entry, Object value, Field field);

}
