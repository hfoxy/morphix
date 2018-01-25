/*-
 * ========================LICENSE_START========================
 * Morphix Mongo
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
package me.hfox.morphix.mongo.mapping.field;

import me.hfox.morphix.mongo.Morphix;

import java.lang.reflect.Field;

public class FieldData {

    protected Class<?> type;
    protected Class<?> parent;
    protected Field field;
    protected Morphix morphix;

    public FieldData(Class<?> type, Class<?> parent, Field field, Morphix morphix) {
        this.type = type;
        this.parent = parent;
        this.field = field;
        this.morphix = morphix;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof FieldData) {
            FieldData data = (FieldData) object;
            boolean type = (this.type == null && data.type == null) || (this.type != null && data.type != null && this.type.equals(data.type));
            boolean parent = (this.parent == null && data.parent == null) || (this.parent != null && data.parent != null && this.parent.equals(data.parent));
            boolean field = (this.field == null && data.field == null) || (this.field != null && data.field != null && this.field.equals(data.field));
            boolean morphix = (this.morphix == null && data.morphix == null) || (this.morphix != null && data.morphix != null && this.morphix.equals(data.morphix));

            return type && parent && field && morphix;
        }

        return super.equals(object);
    }

}
