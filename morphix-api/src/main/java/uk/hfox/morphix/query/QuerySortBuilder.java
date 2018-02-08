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

import uk.hfox.morphix.query.sort.QuerySortElement;

import java.util.List;

public interface QuerySortBuilder<T, S extends QuerySortElement> {

    /**
     * Returns a list of elements that are present in this sort.
     *
     * @return The (cloned) list of elements in order
     */
    List<S> getElements();

    /**
     * Sorts a field in ascending order.
     *
     * @param field The field to add
     *
     * @return This builder instance
     *
     * @throws IllegalArgumentException if the field already exists in the sort
     */
    QuerySortBuilder<T, S> asc(String field);

    /**
     * Sorts a field in descending order.
     *
     * @param field The field to add
     *
     * @return This builder instance
     *
     * @throws IllegalArgumentException if the field already exists in the sort
     */
    QuerySortBuilder<T, S> desc(String field);

    /**
     * Clears the list of sort elements
     *
     * @return This builder instance
     */
    QuerySortBuilder<T, S> clear();

    /**
     * Gets the query that this sort is part of. This method does <b>not</b> apply the sort to the query.
     * You should use this if you decide to cancel applying this sort to your query.
     *
     * @return The query parent
     */
    QueryBuilder<T> cancel();

    /**
     * Applies the sort to your query, use this method once you are done setting any sort parameters
     *
     * @return The query parent
     */
    QueryBuilder<T> done();

}
