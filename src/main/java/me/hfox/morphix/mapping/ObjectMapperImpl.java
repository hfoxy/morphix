package me.hfox.morphix.mapping;

import com.mongodb.DBObject;

public class ObjectMapperImpl implements ObjectMapper {

    public ObjectMapperImpl() {

    }

    @Override
    public DBObject marshal(Object obj) {
        return null;
    }

    @Override
    public Object unmarshal(DBObject object) {
        return null;
    }

    @Override
    public <T> T marshal(Class<? extends T> cls, DBObject object) {
        return null;
    }

    @Override
    public <T> T update(T object, DBObject dbObject) {
        return null;
    }

}
