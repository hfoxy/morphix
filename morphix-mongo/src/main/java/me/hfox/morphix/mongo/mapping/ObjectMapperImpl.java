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
package me.hfox.morphix.mongo.mapping;

import com.mongodb.DBObject;
import me.hfox.morphix.mongo.Morphix;
import me.hfox.morphix.mongo.MorphixDefaults;
import me.hfox.morphix.mongo.exception.MorphixException;
import me.hfox.morphix.mongo.mapping.field.EntityMapper;

import java.util.HashMap;
import java.util.Map;

public class ObjectMapperImpl implements ObjectMapper {

    private Morphix morphix;
    private Map<Class<?>, EntityMapper> mappers;

    public ObjectMapperImpl(Morphix morphix) {
        this.morphix = morphix;
        this.mappers = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> EntityMapper<T> getMapper(Class<T> cls) {
        EntityMapper<T> mapper = mappers.get(cls);
        if (mapper == null) {
            mapper = new EntityMapper<>(cls, null, null, morphix);
            mappers.put(cls, mapper);
        }

        return mapper;
    }

    @Override
    public DBObject marshal(MappingData mappingData, Object obj) {
        return marshal(mappingData, obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    public DBObject marshal(MappingData mappingData, Object obj, boolean lifecycle) {
        return marshal(mappingData, obj.getClass(), obj, lifecycle);
    }

    @Override
    public DBObject marshal(MappingData mappingData, Class<?> cls, Object object) {
        return marshal(mappingData, cls, object, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    public DBObject marshal(MappingData mappingData, Class<?> cls, Object object, boolean lifecycle) {
        return (DBObject) getMapper(cls).marshal(mappingData, object, lifecycle);
    }

    @Override
    public Object unmarshal(DBObject object) {
        return unmarshal(object, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    public Object unmarshal(DBObject object, boolean lifecycle) {
        Class<?> cls = morphix.getPolymorhpismHelper().generate(object);
        if (cls == null) {
            throw new MorphixException("Document does not represent a polymorphic entity");
        }

        return unmarshal(cls, object);
    }

    @Override
    public <T> T unmarshal(Class<T> cls, DBObject object) {
        return unmarshal(cls, object, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    public <T> T unmarshal(Class<T> cls, DBObject object, boolean lifecycle) {
        T entity = getMapper(cls).unmarshal(object, lifecycle);
        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T update(T object, DBObject dbObject) {
        return update(object, dbObject, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T update(T object, DBObject dbObject, boolean lifecycle) {
        EntityMapper<T> mapper = getMapper((Class<T>) object.getClass());
        return mapper.update(dbObject, object, lifecycle);
    }

}
