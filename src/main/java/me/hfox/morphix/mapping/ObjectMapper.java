package me.hfox.morphix.mapping;

import com.mongodb.DBObject;

public interface ObjectMapper {

    public DBObject marshal(Object obj);

    public Object unmarshal(DBObject object);

    public <T> T marshal(Class<? extends T> cls, DBObject object);

    public <T> T update(T object, DBObject dbObject);

}
