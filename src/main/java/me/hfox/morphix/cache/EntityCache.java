package me.hfox.morphix.cache;

import com.mongodb.DBObject;
import me.hfox.morphix.Morphix;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface EntityCache {

    public List<Object> getEntities();

    public <T> List<T> getEntities(Class<T> cls);

    public void put(Object object);

    public Object getEntity(DBObject object);

    public Object getEntity(ObjectId id);

    public void remove(Object object);

    public void clear();

}
