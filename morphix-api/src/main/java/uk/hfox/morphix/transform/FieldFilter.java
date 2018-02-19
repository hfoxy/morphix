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

public class FieldFilter implements Filter {

    private final boolean excludes;
    private final String[] fields;

    public FieldFilter(boolean excludes, String... fields) {
        this.excludes = excludes;
        this.fields = fields;
    }

    /**
     * Returns if this field is an exclude or include filter
     *
     * @return true for exclude, false for include
     */
    public boolean isExcludes() {
        return excludes;
    }

    /**
     * Gets an array of fields used by this filter
     *
     * @return The fields used by this filter
     */
    public String[] getFields() {
        return fields;
    }

    /**
     * Checks if the field is allowed in the filter
     *
     * @param field The name of the field to check
     *
     * @return true if this field is accepted, otherwise false
     */
    @Override
    public boolean isAccepted(String field) {
        boolean contains = false;
        for (String string : fields) {
            if (string.equals(field)) {
                contains = true;
                break;
            }
        }

        // If exclude enabled, return !contains
        // If exclude disabled, return contains
        return excludes != contains;
    }

}
