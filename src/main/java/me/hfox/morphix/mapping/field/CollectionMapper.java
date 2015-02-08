package me.hfox.morphix.mapping.field;

import com.mongodb.BasicDBList;
import me.hfox.morphix.Morphix;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CollectionMapper extends FieldMapper<Collection> {

    protected Type type;
    protected FieldMapper<?> mapper;

    public CollectionMapper(Class<?> parent, Field field, Morphix morphix) {
        super(Collection.class, parent, field, morphix);
    }

    public CollectionMapper(CollectionMapper parent) {
        super(Collection.class, parent.parent, null, parent.morphix);
        this.type = ((ParameterizedType) parent.type).getActualTypeArguments()[0];
    }

    @Override
    protected void discover() {
        super.discover();
        if (field != null) {
            type = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        }

        mapper = null;
        if (type instanceof Class) {
            Class<?> cls = (Class) type;
            if (Collection.class.isAssignableFrom(cls)) {
                mapper = new CollectionMapper(this);
            } else {
                mapper = FieldMapper.create(cls, parent, null, morphix);
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType param = (ParameterizedType) type;
            Class<?> cls = (Class) param.getRawType();
            if (Map.class.isAssignableFrom(cls)) {
                mapper = new MapMapper(this);
            }
        }

        if (mapper == null) {
            throw new RuntimeException("Could not find suitable Mapper for " + type);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection marshal(Object obj) {
        if (obj == null) {
            return null;
        }

        Collection collection = new ArrayList<>();
        if (obj instanceof BasicDBList) {
            BasicDBList list = (BasicDBList) obj;
            for (Object object : list) {
                collection.add(mapper.marshal(object));
            }
        }

        return collection;
    }

    @Override
    public BasicDBList unmarshal(Object obj) {
        BasicDBList list = new BasicDBList();

        if (obj instanceof Collection) {
            Collection collection = (Collection) obj;
            for (Object object : collection) {
                list.add(mapper.unmarshal(object));
            }
        }

        return list;
    }

}
