package me.hfox.morphix.mapping;

import com.mongodb.DBObject;

public interface ObjectMapper {

    public DBObject marshal(Object obj);

    public DBObject marshal(Class<?> cls, Object object);

    public Object unmarshal(DBObject object);

    public <T> T unmarshal(Class<T> cls, DBObject object);

    public <T> T update(T object, DBObject dbObject);

}
