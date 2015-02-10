package me.hfox.morphix.mapping.field;

import me.hfox.morphix.Morphix;

import java.lang.reflect.Field;

public class ObjectMapper<T> extends FieldMapper<T> {

    public ObjectMapper(Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        super(type, parent, field, morphix);
    }

    @Override
    public Object marshal(Object obj) {
        if (obj instanceof Number) {
            Number num = (Number) obj;
            if (Integer.class.equals(obj.getClass()) || int.class.equals(obj.getClass())) {
                return num.intValue();
            } else if (Double.class.equals(obj.getClass()) || double.class.equals(obj.getClass())) {
                return num.doubleValue();
            } else if (Float.class.equals(obj.getClass()) || float.class.equals(obj.getClass())) {
                return num.floatValue();
            } else if (Byte.class.equals(obj.getClass()) || byte.class.equals(obj.getClass())) {
                return num.byteValue();
            } else if (Short.class.equals(obj.getClass()) || short.class.equals(obj.getClass())) {
                return num.byteValue();
            } else if (Long.class.equals(obj.getClass()) || long.class.equals(obj.getClass())) {
                return num.byteValue();
            }
        }

        return obj;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T unmarshal(Object obj) {
        return (T) obj;
    }

}
