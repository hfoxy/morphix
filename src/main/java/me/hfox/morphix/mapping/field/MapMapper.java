package me.hfox.morphix.mapping.field;

import com.mongodb.BasicDBList;
import me.hfox.morphix.Morphix;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MapMapper extends FieldMapper<Map> {

    protected Type[] types;

    public MapMapper(Class<?> parent, Field field, Morphix morphix) {
        super(Map.class, parent, field, morphix);
    }

    public MapMapper(MapMapper parent, ParameterizedType type) {
        super(Map.class, parent.parent, null, parent.morphix);
        types = type.getActualTypeArguments();
    }

    public MapMapper(CollectionMapper parent) {
        super(Map.class, parent.parent, null, parent.morphix);
        types = ((ParameterizedType) parent.type).getActualTypeArguments();
    }

    @Override
    protected void discover() {
        super.discover();
        if (field != null) {
            types = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
        }
    }

    @Override
    public Map marshal(Object obj) {
        if (obj == null) {
            return null;
        }

        return null;
    }

    @Override
    public BasicDBList unmarshal(Object obj) {
        return null;
    }

}
