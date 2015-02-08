package me.hfox.morphix.mapping;

import com.mongodb.DBObject;

public interface ObjectMapper {

    public DBObject unmarshal(Object obj);

    public Object marshal(DBObject object);

    public <T> T marshal(Class<? extends T> cls, DBObject object);

    public <T> T update(T object, DBObject dbObject);

}
