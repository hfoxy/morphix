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
import uk.hfox.morphix.transform.FieldFilter;

import java.util.Map;

public interface EntityHelper {

    MorphixConnector getConnector();

    /**
     * Updates this entity using the supplied map
     * Fields within this map are pushed to both this object and the database
     *
     * @param fields The map of fields to update
     */
    default void update(Map<String, Object> fields) {
        // TODO: update
    }

    /**
     * Updates fields within the entity which are accepted by the supplied filter.
     * If the update parameter is marked as true, fields within the entity will be updated after the update operation
     *
     * @param filter The filter to check against
     * @param update true if the entity should be updated after the operation completes, otherwise false
     */
    default void update(FieldFilter filter, boolean update) {
        // TODO: update
    }

    /**
     * Alias of {@link EntityHelper#update(FieldFilter, boolean)}
     *
     * @param filter The filter to check against
     */
    default void update(FieldFilter filter) {
        // TODO: update
    }

    /**
     * Saves all fields in the entity
     */
    default void save() {
        // TODO: save
    }

}
