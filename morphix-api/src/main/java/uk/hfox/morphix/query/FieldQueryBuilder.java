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
package uk.hfox.morphix.query;

/**
 * Created by Harry on 28/11/2017.
 * <p>
 * Interface used to build connector-specific field queries
 */
public interface FieldQueryBuilder<T> {

    /**
     * Check if the field data matches the specified object
     *
     * @param object The object to check against
     *
     * @return The parent query that the field query belongs to
     */
    QueryBuilder<T> matches(Object object);

    /**
     * Flip if this field is an inverse query
     * For example !(x &gt;= 2)
     *
     * @return The current query with the not flipped
     */
    FieldQueryBuilder<T> not();

    /**
     * Set if this field is an inverse query
     *
     * @param not true if this should be an inverse query, otherwise false
     *
     * @return The current query with the not set
     *
     * @see FieldQueryBuilder#not()
     */
    FieldQueryBuilder<T> not(boolean not);

    /**
     * Check if the field data doesn't match the specified object
     *
     * @param object The object to check against
     *
     * @return The parent query that the field query belongs to
     */
    QueryBuilder<T> notEqual(Object object);

    /**
     * Check if the field value is greater than the supplied object
     *
     * @param object The object to check against
     *
     * @return The parent query that the field query belongs to
     */
    QueryBuilder<T> greaterThan(Object object);

    /**
     * Check if the field value is greater than or equal to the supplied object
     *
     * @param object The object to check against
     *
     * @return The parent query that the field query belongs to
     */
    QueryBuilder<T> greaterThanOrEqual(Object object);

    /**
     * Check if the field value is less than the supplied object
     *
     * @param object The object to check against
     *
     * @return The parent query that the field query belongs to
     */
    QueryBuilder<T> lessThan(Object object);

    /**
     * Check if the field value is less than or equal to the supplied object
     *
     * @param object The object to check against
     *
     * @return The parent query that the field query belongs to
     */
    QueryBuilder<T> lessThanOrEqual(Object object);

}
