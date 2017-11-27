package me.hfox.morphix.mapping.field;

import me.hfox.morphix.Morphix;
import me.hfox.morphix.MorphixDefaults;
import me.hfox.morphix.mapping.MappingData;

import java.lang.reflect.Field;

public class ObjectMapper<T> extends FieldMapper<T> {

    public ObjectMapper(Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        super(type, parent, field, morphix);
    }

    @Override
    public T unmarshal(Object obj) {
        return unmarshal(obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T unmarshal(Object obj, boolean lifecycle) {
        return (T) convert(obj);
    }

    @Override
    public T marshal(MappingData mappingData, Object obj) {
        return marshal(mappingData, obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T marshal(MappingData mappingData, Object obj, boolean lifecycle) {
        return (T) convert(obj);
    }

    public Object convert(Object obj) {
        if (obj instanceof Number) {
            Number num = (Number) obj;
            if (Integer.class.equals(type) || int.class.equals(type)) {
                return num.intValue();
            } else if (Double.class.equals(type) || double.class.equals(type)) {
                return num.doubleValue();
            } else if (Float.class.equals(type) || float.class.equals(type)) {
                return num.floatValue();
            } else if (Byte.class.equals(type) || byte.class.equals(type)) {
                return num.byteValue();
            } else if (Short.class.equals(type) || short.class.equals(type)) {
                return num.shortValue();
            } else if (Long.class.equals(type) || long.class.equals(type)) {
                return num.longValue();
            }
        }

        return obj;
    }

}
