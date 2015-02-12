package me.hfox.morphix.cache;

import com.mongodb.DBObject;
import me.hfox.morphix.Morphix;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

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

    public void put(Object object) {
        put(morphix.getEntityHelper().getObjectId(object), object);
    }

    public void put(ObjectId id, Object object) {
        cache.put(id, object);
    }

    public Object getEntity(DBObject object) {
        ObjectId id = (ObjectId) object.get("_id");
        if (id == null) {
            return null;
        }

        Class<?> result = morphix.getPolymorhpismHelper().generate(object);
        Object cached = getEntity(id);
        if (cached == null) {
            return null;
        }

        if (!result.isAssignableFrom(cached.getClass())) {
            // TODO: display warning that cache has been updated with a new entity
            return null;
        }

        return cached;
    }

    public Object getEntity(ObjectId id) {
        return cache.get(id);
    }

    @Override
    public void clear() {
        cache.clear();
    }

}
