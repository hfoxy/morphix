package me.hfox.morphix.mapping.field;

import me.hfox.morphix.Morphix;

import java.lang.reflect.Field;

public class EnumMapper<T> extends FieldMapper<T> {

    public EnumMapper(Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        super(type, parent, field, morphix);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T marshal(Object obj) {
        if (obj == null) {
            return null;
        }

        Class<? extends Enum> cls = (Class<? extends Enum>) type;
        return (T) Enum.valueOf(cls, (String) obj);
    }

    @Override
    public Object unmarshal(Object obj) {
        if (obj == null) {
            return null;
        }

        return ((Enum) obj).name();
    }

}
