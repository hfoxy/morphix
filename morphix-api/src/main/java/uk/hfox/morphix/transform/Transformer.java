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

/**
 * Used to transform an Object into a given type and vice versa
 *
 * @param <T> The DB type to transform from or into
 */
public interface Transformer<T> {

    /**
     * Gets the converter used to convert the supplied class
     *
     * @param cls The class to convert
     *
     * @return The converter for that class
     */
    Converter<T> getConverter(Class<?> cls);

    /**
     * Gets the converter used to convert the supplied type
     *
     * @param type The type of converter to get
     *
     * @return The converter for that class
     */
    Converter<T> getConverter(ConvertedType type);

    /**
     * Sets the converter for the supplied type
     *
     * @param type      The converted type to set
     * @param converter The converter to use for the type
     */
    void setConverter(ConvertedType type, Converter<T> converter);

    /**
     * Transforms the given Object into a database object
     *
     * @param object The object to transform
     *
     * @return The database object output
     */
    T toDB(Object object);

    /**
     * Transforms the given Object into a database object
     *
     * @param object The object to transform
     * @param filter The filter to be applied
     *
     * @return The database object output
     */
    T toDB(Object object, FieldFilter filter);

    /**
     * Transforms the database object into an Object of undefined type
     *
     * @param db The database object
     * @param entity The existing entity, or null if no entity currently exists
     *
     * @return The undefined Object output
     */
    <O> O fromDB(T db, O entity);

    /**
     * Transforms the database object into an Object of undefined type
     *
     * @param db     The database object
     * @param entity The existing entity, or null if no entity currently exists
     * @param entity The filter to be applied
     *
     * @return The undefined Object output
     */
    <O> O fromDB(T db, O entity, FieldFilter filter);

}
