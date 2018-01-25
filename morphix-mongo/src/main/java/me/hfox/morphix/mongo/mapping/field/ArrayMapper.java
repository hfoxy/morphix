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

import com.mongodb.BasicDBList;
import me.hfox.morphix.mongo.Morphix;
import me.hfox.morphix.mongo.MorphixDefaults;
import me.hfox.morphix.mongo.mapping.MappingData;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

public class ArrayMapper<T> extends FieldMapper<T> {

    private Class<?> arrayType;
    private int dimensions;
    private FieldMapper<?> mapper;

    public ArrayMapper(Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        super(type, parent, field, morphix);
    }

    public ArrayMapper(Class<T> type, ArrayMapper parent) {
        super(type, parent.parent, parent.field, parent.morphix);
    }

    @Override
    protected void discover() {
        super.discover();

        dimensions = 0;
        Class<?> cls = type;
        while (cls.isArray()) {
            dimensions++;
            cls = cls.getComponentType();
        }

        arrayType = cls;
        Class<?> component = type.getComponentType();
        mapper = component.isArray() ? new ArrayMapper<>(component, this) : FieldMapper.createFromField(component, parent, field, morphix);
    }

    @Override
    public Object unmarshal(Object obj, boolean lifecycle) {
        if (obj == null || !(obj instanceof BasicDBList)) {
            return null;
        }

        BasicDBList list = (BasicDBList) obj;
        int[] sizes = sizes(list);

        Object array = Array.newInstance(arrayType, sizes);
        for (int i = 0; i < list.size(); i++) {
            Object value = list.get(i);
            Object result = mapper.unmarshal(value, lifecycle);
            Array.set(array, i, result);
        }

        return array;
    }

    private int[] sizes(BasicDBList list) {
        int[] sizes = new int[dimensions];

        int i = 0;
        while (list != null && i < sizes.length) {
            sizes[i] = list.size();
            for (int j = 0; j < list.size(); j++) {
                Object value = list.get(j);
                if (value instanceof BasicDBList) {
                    list = (BasicDBList) value;
                    break;
                }
            }

            i++;
        }

        return sizes;
    }

    @Override
    public BasicDBList marshal(MappingData mappingData, Object obj) {
        return marshal(mappingData, obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    public BasicDBList marshal(MappingData mappingData, Object obj, boolean lifecycle) {
        if (obj == null || !obj.getClass().isArray()) {
            return null;
        }

        BasicDBList list = new BasicDBList();

        int length = Array.getLength(obj);
        for (int i = 0; i < length; i++) {
            Object value = Array.get(obj, i);
            Object result = mapper.marshal(mappingData, value);
            list.add(result);
        }

        return list;
    }

}
