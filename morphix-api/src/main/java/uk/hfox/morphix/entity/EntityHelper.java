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

public interface EntityHelper {

    MorphixConnector getConnector();

    /**
     * Alias of {@link EntityManager#update(Object)}
     */
    default void update() {
        getConnector().getEntityManager().update(this);
    }

    /**
     * Alias of {@link EntityManager#update(Object, FieldFilter)}
     */
    default void update(FieldFilter filter) {
        getConnector().getEntityManager().update(this, filter);
    }

    /**
     * Alias of {@link EntityManager#save(Object)}
     */
    default void save() {
        getConnector().getEntityManager().save(this);
    }

    /**
     * Alias of {@link EntityManager#save(Object, FieldFilter, boolean)}
     */
    default void save(FieldFilter filter, boolean update) {
        getConnector().getEntityManager().save(this, filter, update);
    }

    /**
     * Alias of {@link EntityManager#save(Object, FieldFilter)}
     */
    default void save(FieldFilter filter) {
        getConnector().getEntityManager().save(this, filter);
    }

}
