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
package uk.hfox.morphix.helper;

import uk.hfox.morphix.connector.MorphixConnector;

import java.util.HashMap;
import java.util.Map;

public class HelperManager<C extends MorphixConnector> {

    protected final C connector;
    private final Map<String, Helper<C>> helpers;

    public HelperManager(C connector) {
        this.connector = connector;
        this.helpers = new HashMap<>();
    }

    /**
     * Gets the connector that this helper manager belongs to
     *
     * @return The connector
     */
    public C getConnector() {
        return connector;
    }

    /**
     * Gets if the supplied helper is currently setup
     *
     * @param name The name of the helper to check for
     *
     * @return true if the helper is present, false otherwise
     */
    public boolean hasHelper(String name) {
        return this.helpers.get(name) != null;
    }

    /**
     * Gets the helper with the specified name
     *
     * @param name The name of the helper to get
     *
     * @return The helper associated with the name, null if no helper is found
     */
    public Helper<C> getHelper(String name) {
        return this.helpers.get(name);
    }

    /**
     * Gets the helper and casts it to the specified class
     *
     * @param name The name of the helper
     * @param cls  The class to cast to
     *
     * @return The cast helper associated with the name, null if no helper is found
     *
     * @throws IllegalArgumentException if the supplied class can't be assigned from the helper class
     */
    @SuppressWarnings("unchecked")
    public <H extends Helper> H getHelper(String name, Class<H> cls) {
        Helper<C> helper = getHelper(name);
        if (helper == null) {
            return null;
        }

        if (!cls.isAssignableFrom(helper.getClass())) {
            throw new IllegalArgumentException(helper.getClass().getSimpleName() + " cannot be cast to " + cls.getSimpleName());
        }

        return (H) helper;
    }

    /**
     * Sets the helper for the supplied name.
     * Subclasses are to override this method to include tests to ensure that
     * when a helper is set, is is the correct type of helper for that name.
     *
     * @param name   The name of the helper to set
     * @param helper The helper to set
     */
    public void setHelper(String name, Helper<C> helper) {
        this.helpers.put(name, helper);
    }

}
