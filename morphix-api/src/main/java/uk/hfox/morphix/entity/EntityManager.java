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
package uk.hfox.morphix.entity;

import uk.hfox.morphix.connector.MorphixConnector;
import uk.hfox.morphix.query.raw.FindQuery;
import uk.hfox.morphix.transform.FieldFilter;
import uk.hfox.morphix.transform.Filter;

/**
 * Manages the entities (saves/loads/updates/etc)
 */
public interface EntityManager {

    /**
     * Gets the connector that this manager belongs to
     *
     * @return The connector
     */
    MorphixConnector getConnector();

    /**
     * Updates all fields in this entity
     */
    default void update(Object[] entities) {
        update(new FieldFilter(true), entities);
    }

    /**
     * Updates fields within the entity which are accepted by the supplied filter.
     *
     * @param filter The filter to check against
     */
    void update(Filter filter, Object[] entity);

    /**
     * Updates fields within each of the query responses
     *
     * @param query  The response of objects to update
     * @param filter The filter to check against
     */
    void update(FindQuery query, Filter filter);

    /**
     * Saves all fields in the entity
     */
    default void save(Object[] entities) {
        save(new FieldFilter(true), entities);
    }

    /**
     * Saves fields within the entity which are accepted by the supplied filter.
     * If the update parameter is marked as true, fields within the entity will be updated after the update operation
     *
     * @param filter The filter to check against
     */
    void save(Filter filter, boolean update, Object[] entities);

    /**
     * Alias of {@link EntityManager#save(Filter, boolean, Object[])}
     *
     * @param filter The filter to check against
     */
    default void save(Filter filter, Object[] entities) {
        save(filter, false, entities);
    }

}
