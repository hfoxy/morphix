package me.hfox.morphix.mapping;

import com.mongodb.BasicDBObject;

import java.util.HashMap;
import java.util.Map;

public class MappingData {

    private Map<Object, BasicDBObject> objects;

    public MappingData() {
        this.objects = new HashMap<>();
    }

    public BasicDBObject put(Object object) {
        BasicDBObject db = new BasicDBObject();
        objects.put(object, db);
        return db;
    }

    public BasicDBObject get(Object object) {
        return objects.get(object);
    }

}
