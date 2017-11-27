package me.hfox.morphix.cache;

import com.mongodb.DBObject;
import me.hfox.morphix.Morphix;
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
