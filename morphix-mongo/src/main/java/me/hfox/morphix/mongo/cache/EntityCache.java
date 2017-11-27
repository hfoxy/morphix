package me.hfox.morphix.mongo.cache;

import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import java.util.List;

public interface EntityCache {

    public List<Object> getEntities();

    public <T> List<T> getEntities(Class<T> cls);

    public void put(Object object);

    public Object getEntity(DBObject object);

    public Object getEntity(ObjectId id);

    public void remove(Object object);

    public void clear();

}
