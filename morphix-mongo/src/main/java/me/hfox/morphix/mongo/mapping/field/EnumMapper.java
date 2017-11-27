package me.hfox.morphix.mongo.mapping.field;

import me.hfox.morphix.mongo.Morphix;
import me.hfox.morphix.mongo.MorphixDefaults;
import me.hfox.morphix.mongo.mapping.MappingData;

import java.lang.reflect.Field;

public class EnumMapper<T> extends FieldMapper<T> {

    public EnumMapper(Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        super(type, parent, field, morphix);
    }

    @Override
    public T unmarshal(Object obj) {
        return unmarshal(obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T unmarshal(Object obj, boolean lifecycle) {
        if (obj == null) {
            return null;
        }

        Class<? extends Enum> cls = (Class<? extends Enum>) type;
        return (T) Enum.valueOf(cls, (String) obj);
    }

    @Override
    public Object marshal(MappingData mappingData, Object obj, boolean lifecycle) {
        if (obj == null) {
            return null;
        }

        return ((Enum) obj).name();
    }

}
