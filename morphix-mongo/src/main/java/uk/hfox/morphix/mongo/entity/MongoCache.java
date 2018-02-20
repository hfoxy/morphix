/*-
 * ========================LICENSE_START========================
 * Morphix MongoDB
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
package uk.hfox.morphix.mongo.entity;

import org.bson.types.ObjectId;
import uk.hfox.morphix.exception.mapper.MorphixEntityException;
import uk.hfox.morphix.mongo.connection.MorphixMongoConnector;
import uk.hfox.morphix.mongo.mapper.MongoEntity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MongoCache {

    private final MorphixMongoConnector connector;
    private final Map<ObjectId, Object> cache;
    private final Map<Object, ObjectId> ids;

    public MongoCache(MorphixMongoConnector connector) {
        this.connector = connector;
        this.cache = new HashMap<>();
        this.ids = new HashMap<>();
    }

    public Map<ObjectId, Object> getCache() {
        return cache;
    }

    public Map<Object, ObjectId> getIds() {
        return ids;
    }

    public ObjectId getId(Object entity) {
        ObjectId id = null;

        try {
            MongoEntity mongoEntity = this.connector.getTransformer().getOrMapEntity(entity.getClass());

            Field field = mongoEntity.getIdField();
            if (field != null) {
                id = (ObjectId) field.get(entity);
            }
        } catch (IllegalAccessException ex) {
            throw new MorphixEntityException("unable to access field", ex);
        }

        if (id != null) {
            this.ids.put(entity, id);
            this.cache.put(id, entity);
        } else {
            id = this.ids.get(entity);
        }

        return id;
    }

    public Object getEntity(ObjectId id) {
        return this.cache.get(id);
    }

    @SuppressWarnings("unchecked") // checked
    public <T> T getEntity(ObjectId key, Class<T> cls) {
        Object entity = getEntity(key);
        if (cls.isAssignableFrom(entity.getClass())) {
            return (T) entity;
        }

        return null;
    }

    public void put(ObjectId id, Object entity) {
        MongoEntity mongoEntity = this.connector.getTransformer().getOrMapEntity(entity.getClass());
        Field field = mongoEntity.getIdField();
        if (field != null) {
            try {
                field.setAccessible(true);
                field.set(entity, id);
            } catch (IllegalAccessException ex) {
                throw new MorphixEntityException("unable to access field", ex);
            }
        }

        this.ids.put(entity, id);
        this.cache.put(id, entity);
    }

}
