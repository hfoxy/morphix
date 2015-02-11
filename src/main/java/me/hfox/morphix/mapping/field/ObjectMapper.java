package me.hfox.morphix.mapping.field;

import me.hfox.morphix.Morphix;

import java.lang.reflect.Field;

public class ObjectMapper<T> extends FieldMapper<T> {

    public ObjectMapper(Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        super(type, parent, field, morphix);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T unmarshal(Object obj) {
        return (T) convert(obj);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T marshal(Object obj) {
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
