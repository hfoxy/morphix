package me.hfox.morphix.cache;

import com.mongodb.DBObject;
import me.hfox.morphix.Morphix;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

public interface EntityCache {

    public void put(Object object);

    public Object getEntity(DBObject object);

    public Object getEntity(ObjectId id);

}
