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
import me.hfox.morphix.mongo.MorphixDefaults;
import me.hfox.morphix.mongo.annotation.Id;
import me.hfox.morphix.mongo.annotation.Property;
import me.hfox.morphix.mongo.annotation.Transient;
import me.hfox.morphix.mongo.annotation.entity.Entity;
import me.hfox.morphix.mongo.mapping.MappingData;
import me.hfox.morphix.mongo.util.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class FieldMapper<T> {

    private static final Object lock = new Object();
    private static FieldMappers mappers = new FieldMappers();

    protected FieldData data;
    protected Class<?> type;
    protected Class<?> parent;
    protected Field field;
    protected String fieldName;
    protected Morphix morphix;

    public FieldMapper(Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        this(type, parent, field, morphix, true);
    }

    public FieldMapper(Class<T> type, Class<?> parent, Field field, Morphix morphix, boolean discover) {
        this.data = new FieldData(type, parent, field, morphix);
        this.type = type;
        this.parent = parent;
        this.field = field;
        this.morphix = morphix;
        mappers.put(data, this);

        if (discover) {
            discover();
        }
    }

    public Class<?> getParent() {
        return parent;
    }

    public Field getField() {
        return field;
    }

    public Morphix getMorphix() {
        return morphix;
    }

    protected void discover() {
        if (field != null) {
            fieldName = getName(field);
        }
    }

    /*
    public void unmarshal(DBObject dbObject, Object toMap) throws IllegalAccessException {
        Object store = null;
        Object value = dbObject.get(fieldName);
        if (value == null || value.getClass().isAssignableFrom(type)) {
            store = value;
        } else {
            unmarshal(value);
        }

        field.set(toMap, store);
    }
    */

    public Object unmarshal(Object obj) {
        return unmarshal(obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    public abstract Object unmarshal(Object obj, boolean lifecycle);

    /*
    public void marshal(DBObject dbObject, Object toMap) throws IllegalAccessException {
        Object value = field.get(toMap);
        dbObject.put(fieldName, marshal(value));
    }
    */

    public Object marshal(MappingData mappingData, Object obj) {
        return marshal(mappingData, obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    public abstract Object marshal(MappingData mappingData, Object obj, boolean lifecycle);

    private static String getName(Field field) {
        field.setAccessible(true);

        String name = ".";
        Property property = field.getAnnotation(Property.class);
        if (property != null) {
            name = property.value();
        }

        if (name == null || name.equals(MorphixDefaults.DEFAULT_FIELD_NAME)) {
            name = AnnotationUtils.getFieldName(field);
        }

        Id id = field.getAnnotation(Id.class);
        if (id != null) {
            name = "_id";
        }

        return name;
    }

    public static FieldMapper createFromName(Class<?> parent, Class<?> type, String fieldName, Morphix morphix) {
        List<Field> fields = morphix.getEntityHelper().getFields(parent);
        for (Field field : fields) {
            String name = getName(field);
            if (name.equals(fieldName)) {
                return createFromField(type, parent, field, morphix);
            }
        }

        return createFromField(type, parent, null, morphix);
    }

    public static FieldMapper createFromField(Class<?> parent, Field field, Morphix morphix) {
        return createFromField(field == null ? parent : field.getType(), parent, field, morphix);
    }

    @SuppressWarnings("unchecked")
    public static <T> FieldMapper<T> createFromField(Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        if (field != null && field.getAnnotation(Transient.class) != null) {
            return null;
        }

        synchronized (lock) {
            FieldMapper<T> mapper = mappers.get(new FieldData(type, parent, field, morphix));
            if (mapper != null) {
                return mapper;
            }

            if (type.isEnum()) {
                return new EnumMapper<>(type, parent, field, morphix);
            } else if (Map.class.isAssignableFrom(type)) {
                return (FieldMapper<T>) new MapMapper(parent, field, morphix);
            } else if (Collection.class.isAssignableFrom(type)) {
                return (FieldMapper<T>) new CollectionMapper(parent, field, morphix);
            } else if (type.isArray()) {
                return new ArrayMapper<>(type, parent, field, morphix);
            } else if (AnnotationUtils.getHierarchicalAnnotation(type, Entity.class) != null) {
                return new EntityMapper<>(type, parent, field, morphix);
            } else {
                return new ObjectMapper<>(type, parent, field, morphix);
            }
        }
    }

}
