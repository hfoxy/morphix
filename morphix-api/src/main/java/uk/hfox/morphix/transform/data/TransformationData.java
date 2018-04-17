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
package uk.hfox.morphix.transform.data;

import java.lang.reflect.Field;

public class TransformationData {

    private final Object originalValue;
    private final Class<?> fieldType;
    private final Field field;

    private ArrayData arrayData;

    public TransformationData(Object originalValue, Class<?> fieldType, Field field) {
        this.originalValue = originalValue;
        this.fieldType = fieldType;
        this.field = field;
    }

    public TransformationData(Object originalValue, Field field) {
        this(originalValue, (field != null ? field.getType() : null), field);
    }

    public Object getOriginalValue() {
        return originalValue;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public Field getField() {
        return field;
    }

    public ArrayData getArrayData() {
        return arrayData;
    }

    public void setArrayData(ArrayData arrayData) {
        this.arrayData = arrayData;
    }

}
