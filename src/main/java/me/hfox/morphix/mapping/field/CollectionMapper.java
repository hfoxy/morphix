package me.hfox.morphix.mapping.field;

import com.mongodb.BasicDBList;
import me.hfox.morphix.Morphix;
import me.hfox.morphix.MorphixDefaults;
import me.hfox.morphix.exception.MorphixException;

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
        super(Collection.class, parent.parent, parent.field, parent.morphix, false);
        this.type = ((ParameterizedType) parent.type).getActualTypeArguments()[0];
        discover();
    }

    public CollectionMapper(MapMapper parent, ParameterizedType type) {
        super(Collection.class, parent.parent, parent.field, parent.morphix, false);
        this.type = type.getActualTypeArguments()[0];
        discover();
    }

    @Override
    protected void discover() {
        super.discover();
        if (field != null && type == null) {
            type = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        }

        mapper = null;
        if (type instanceof Class) {
            Class<?> cls = (Class) type;
            mapper = FieldMapper.createFromField(cls, parent, field, morphix);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType param = (ParameterizedType) type;
            if (param.getRawType() instanceof Class) {
                Class<?> cls = (Class) param.getRawType();
                if (Collection.class.isAssignableFrom(cls)) {
                    mapper = new CollectionMapper(this);
                } else if (Map.class.isAssignableFrom(cls)) {
                    mapper = new MapMapper(this);
                }
            }
        }

        if (mapper == null) {
            throw new MorphixException("Could not find suitable Mapper for " + type);
        }
    }

    @Override
    public Collection unmarshal(Object obj) {
        return unmarshal(obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection unmarshal(Object obj, boolean lifecycle) {
        if (obj == null) {
            return null;
        }

        Collection collection = new ArrayList();
        if (obj instanceof BasicDBList) {
            BasicDBList list = (BasicDBList) obj;
            for (Object object : list) {
                collection.add(mapper.unmarshal(object));
            }
        }

        return collection;
    }

    @Override
    public BasicDBList marshal(Object obj) {
        return marshal(obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    public BasicDBList marshal(Object obj, boolean lifecycle) {
        BasicDBList list = new BasicDBList();

        if (obj instanceof Collection) {
            Collection collection = (Collection) obj;
            for (Object object : collection) {
                list.add(mapper.marshal(object));
            }
        }

        return list;
    }

}
