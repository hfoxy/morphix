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

import uk.hfox.morphix.exception.transformer.MorphixTransformationException;

import java.lang.reflect.Field;

/**
 * Used to transform an Object into a given type and vice versa
 *
 * @param <T> The DB type to transform from or into
 */
public interface Transformer<T> {

    /**
     * Gets the converter used to convert the supplied field
     *
     * @param field The field to get the converter for
     *
     * @return The converter for that field
     */
    default Converter<T> getConverter(Field field) {
        return getConverter(ConvertedType.findByField(field));
    }

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
    T toDB(Object object, Filter filter);

    /**
     * Transforms the database object into an Object of undefined type
     *
     * @param db The database object
     * @param entity The existing entity, or null if no entity currently exists
     * @param cls The type of entity to construct
     *
     * @return The undefined Object output
     */
    <O> O fromGenericDB(T db, Object entity, Class<O> cls);

    /**
     * Alias of {@link Transformer#fromGenericDB(Object, Object, Class, Filter)}
     */
    @SuppressWarnings("unchecked")
    default <O> O fromDB(Object db, O entity, Class<O> cls) {
        T dbg;
        try {
            dbg = (T) db;
        } catch (ClassCastException ex) {
            throw new MorphixTransformationException("Unable to cast to generic type", ex);
        }

        return fromGenericDB(dbg, entity, cls);
    }

    /**
     * Transforms the supplied database entry into a new Java object, populates new
     * data into the supplied entity, or populates new data into an entity from the
     * cache, depending on the arguments
     *
     * The return type of this method will match the type of the entity provided,
     * thus if no entity is provided, it will default to Object.
     *
     * If no entity is provided, an attempt will be made to find the existing entity in
     * the cache. If no entity can be found, one will be created instead.
     *
     * Either entity or cls must be provided, if neither are provided a search will be
     * made for the object in the cache. If no such object exists, a null response will
     * be given
     *
     * @param db     The database object
     * @param entity The existing entity, or null
     * @param cls    The type of entity to construct
     * @param filter The filter to be applied
     *
     * @return The undefined Object output
     */
    <O> O fromGenericDB(T db, Object entity, Class<O> cls, Filter filter);

    /**
     * Alias of {@link Transformer#fromGenericDB(Object, Object, Class, Filter)}
     */
    @SuppressWarnings("unchecked")
    default <O> O fromDB(Object db, O entity, Class<O> cls, Filter filter) {
        T dbg;
        try {
            dbg = (T) db;
        } catch (ClassCastException ex) {
            throw new MorphixTransformationException("Unable to cast to generic type", ex);
        }

        return fromGenericDB(dbg, entity, cls, filter);
    }

}
