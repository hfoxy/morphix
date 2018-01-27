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
package uk.hfox.morphix.connector;

import uk.hfox.morphix.query.QueryBuilder;

import java.io.Serializable;

/**
 * Created by Harry on 27/11/2017.
 *
 * Connector interface used by the API to perform queries on the database
 */
public interface MorphixConnector extends Serializable {

    /**
     * Connect to the database
     */
    void connect();

    /**
     * Disconnect from the database
     */
    void disconnect();

    /**
     * QueryBuilder the database using the specified class as a collection and result reference
     * @param cls The expected resulting class, also used to find the collection
     * @param <T> The expected resulting type
     * @return A query based on the given class
     */
    <T> QueryBuilder<T> createQuery(Class<T> cls);

    /**
     * QueryBuilder the given collection with the specified class as an expected result
     * @param cls The expected resulting class
     * @param collection The collection to query
     * @param <T> The expected resulting type
     * @return A query based on the given class
     */
    <T> QueryBuilder<T> createQuery(Class<T> cls, String collection);

}
