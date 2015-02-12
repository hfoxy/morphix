package me.hfox.morphix.mapping;

import com.mongodb.DBObject;
import me.hfox.morphix.Morphix;
import me.hfox.morphix.exception.MorphixException;
import me.hfox.morphix.mapping.field.EntityMapper;

import java.util.HashMap;
import java.util.Map;

public class ObjectMapperImpl implements ObjectMapper {

    private Morphix morphix;
    private Map<Class<?>, EntityMapper> mappers;

    public ObjectMapperImpl(Morphix morphix) {
        this.morphix = morphix;
        this.mappers = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> EntityMapper<T> getMapper(Class<T> cls) {
        EntityMapper<T> mapper = mappers.get(cls);
        if (mapper == null) {
            mapper = new EntityMapper<>(cls, null, null, morphix);
            mappers.put(cls, mapper);
        }

        return mapper;
    }

    @Override
    public DBObject marshal(Object obj) {
        return marshal(obj.getClass(), obj);
    }

    @Override
    public DBObject marshal(Class<?> cls, Object object) {
        return (DBObject) getMapper(cls).marshal(object);
    }

    @Override
    public Object unmarshal(DBObject object) {
        Class<?> cls = morphix.getPolymorhpismHelper().generate(object);
        if (cls == null) {
            throw new MorphixException("Document does not represent a polymorphic entity");
        }

        return unmarshal(cls, object);
    }

    @Override
    public <T> T unmarshal(Class<T> cls, DBObject object) {
        return getMapper(cls).unmarshal(object);
    }

    @Override
    public <T> T update(T object, DBObject dbObject) {
        return null;
    }

}
