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
package me.hfox.morphix.mongo.cache;

import com.mongodb.DBObject;
import me.hfox.morphix.mongo.Morphix;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EntityCacheImpl implements EntityCache {

    private Morphix morphix;
    private Map<ObjectId, Object> cache;

    public EntityCacheImpl(Morphix morphix) {
        this.morphix = morphix;
        this.cache = new HashMap<>();
    }

    public Map<ObjectId, Object> getCache() {
        return cache;
    }

    @Override
    public List<Object> getEntities() {
        return new ArrayList<>(cache.values());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getEntities(Class<T> cls) {
        List<T> list = new ArrayList<>();
        for (Object object : cache.values()) {
            if (cls.isInstance(object)) {
                list.add((T) object);
            }
        }

        return list;
    }

    public void put(Object object) {
        ObjectId id = morphix.getEntityHelper().getObjectId(object);
        put(id, object);
        // System.out.println("Added ObjectId(\"" + id + "\") (" + object.getClass().getSimpleName() + ") to the cache");
    }

    public void put(ObjectId id, Object object) {
        cache.put(id, object);
    }

    public Object getEntity(DBObject object) {
        if (object == null) {
            return null;
        }

        ObjectId id = (ObjectId) object.get("_id");
        if (id == null) {
            // System.out.println("Could not find an entry with that id because the id was null");
            return null;
        }

        Class<?> result = morphix.getPolymorhpismHelper().generate(object);
        Object cached = getEntity(id);
        if (cached == null) {
            // System.out.println("Could not find an entry with the id, \"" + id + "\" because there was no such entity");
            return null;
        }

        if (result != null && !result.isAssignableFrom(cached.getClass())) {
            // TODO: display warning that cache has been updated with a new entity
            // System.out.println("Could not find an entry with the id, \"" + id + "\" because there was no entity that was of a suitable class");
            return null;
        }

        return cached;
    }

    public Object getEntity(ObjectId id) {
        return cache.get(id);
    }

    public void remove(Object object) {
        ObjectId key = null;
        for (Entry<ObjectId, Object> entry : cache.entrySet()) {
            if (entry.getValue().equals(object)) {
                key = entry.getKey();
                break;
            }
        }

        if (key != null) {
            cache.remove(key);
        }
    }

    @Override
    public void clear() {
        cache.clear();
    }

}
