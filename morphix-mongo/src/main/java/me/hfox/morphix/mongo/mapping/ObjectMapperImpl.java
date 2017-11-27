package me.hfox.morphix.mongo.mapping;

import com.mongodb.DBObject;
import me.hfox.morphix.mongo.Morphix;
import me.hfox.morphix.mongo.MorphixDefaults;
import me.hfox.morphix.mongo.exception.MorphixException;
import me.hfox.morphix.mongo.mapping.field.EntityMapper;

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
    public DBObject marshal(MappingData mappingData, Object obj) {
        return marshal(mappingData, obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    public DBObject marshal(MappingData mappingData, Object obj, boolean lifecycle) {
        return marshal(mappingData, obj.getClass(), obj, lifecycle);
    }

    @Override
    public DBObject marshal(MappingData mappingData, Class<?> cls, Object object) {
        return marshal(mappingData, cls, object, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    public DBObject marshal(MappingData mappingData, Class<?> cls, Object object, boolean lifecycle) {
        return (DBObject) getMapper(cls).marshal(mappingData, object, lifecycle);
    }

    @Override
    public Object unmarshal(DBObject object) {
        return unmarshal(object, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    public Object unmarshal(DBObject object, boolean lifecycle) {
        Class<?> cls = morphix.getPolymorhpismHelper().generate(object);
        if (cls == null) {
            throw new MorphixException("Document does not represent a polymorphic entity");
        }

        return unmarshal(cls, object);
    }

    @Override
    public <T> T unmarshal(Class<T> cls, DBObject object) {
        return unmarshal(cls, object, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    public <T> T unmarshal(Class<T> cls, DBObject object, boolean lifecycle) {
        T entity = getMapper(cls).unmarshal(object, lifecycle);
        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T update(T object, DBObject dbObject) {
        return update(object, dbObject, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T update(T object, DBObject dbObject, boolean lifecycle) {
        EntityMapper<T> mapper = getMapper((Class<T>) object.getClass());
        return mapper.update(dbObject, object, lifecycle);
    }

}
