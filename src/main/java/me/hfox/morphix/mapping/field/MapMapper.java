package me.hfox.morphix.mapping.field;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import me.hfox.morphix.Morphix;
import me.hfox.morphix.exception.MorphixException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MapMapper extends FieldMapper<Map> {

    protected Type[] types;
    protected FieldMapper[] mappers;

    public MapMapper(Class<?> parent, Field field, Morphix morphix) {
        super(Map.class, parent, field, morphix);
    }

    public MapMapper(MapMapper parent, ParameterizedType type) {
        super(Map.class, parent.parent, parent.field, parent.morphix, false);
        types = type.getActualTypeArguments();
        discover();
    }

    public MapMapper(CollectionMapper parent) {
        super(Map.class, parent.parent, parent.field, parent.morphix, false);
        types = ((ParameterizedType) parent.type).getActualTypeArguments();
        discover();
    }

    @Override
    protected void discover() {
        super.discover();
        if (field != null && types == null) {
            types = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
        }

        mappers = new FieldMapper[]{getMapper(types[0]), getMapper(types[1])};
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map marshal(Object obj) {
        if (obj == null) {
            return null;
        }

        Map map = new HashMap<>();
        if (obj instanceof BasicDBList) {
            BasicDBList list = (BasicDBList) obj;
            for (Object object : list) {
                if (object instanceof DBObject) {
                    DBObject dbObject = (DBObject) object;
                    Object key = dbObject.get("key");
                    Object value = dbObject.get("value");
                    map.put(key, value);
                }
            }
        }

        return map;
    }

    @Override
    public BasicDBList unmarshal(Object obj) {
        BasicDBList list = new BasicDBList();

        if (obj instanceof Map) {
            Map map = (Map) obj;
            for (Object obj2 : map.entrySet()) {
                if (obj2 instanceof Entry) {
                    Entry entry = (Entry) obj2;
                    BasicDBObject dbObject = new BasicDBObject();
                    dbObject.put("key", entry.getKey());
                    dbObject.put("value", entry.getValue());
                    list.add(dbObject);
                }
            }
        }

        return list;
    }

    private FieldMapper getMapper(Type type) {
        FieldMapper mapper = null;
        if (type instanceof Class) {
            Class<?> cls = (Class) type;
            mapper = FieldMapper.create(cls, parent, field, morphix);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType param = (ParameterizedType) type;
            if (param.getRawType() instanceof Class) {
                Class<?> cls = (Class) param.getRawType();
                if (Collection.class.isAssignableFrom(cls)) {
                    mapper = new CollectionMapper(this, param);
                } else if (Map.class.isAssignableFrom(cls)) {
                    mapper = new MapMapper(this, param);
                }
            }
        }

        if (mapper == null) {
            throw new MorphixException("Could not find suitable Mapper for " + type);
        }

        return mapper;
    }

}
