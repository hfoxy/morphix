package me.hfox.morphix.mongo.mapping;

import com.mongodb.DBObject;

public interface ObjectMapper {

    public DBObject marshal(MappingData mappingData, Object obj);

    public DBObject marshal(MappingData mappingData, Object obj, boolean lifecycle);

    public DBObject marshal(MappingData mappingData, Class<?> cls, Object object);

    public DBObject marshal(MappingData mappingData, Class<?> cls, Object object, boolean lifecycle);

    public Object unmarshal(DBObject object);

    public Object unmarshal(DBObject object, boolean lifecycle);

    public <T> T unmarshal(Class<T> cls, DBObject object);

    public <T> T unmarshal(Class<T> cls, DBObject object, boolean lifecycle);

    public <T> T update(T object, DBObject dbObject);

    public <T> T update(T object, DBObject dbObject, boolean lifecycle);

}
