package me.hfox.morphix.mapping.field;

import me.hfox.morphix.Morphix;

import java.lang.reflect.Field;

public class EnumMapper<T> extends FieldMapper<T> {

    public EnumMapper(Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        super(type, parent, field, morphix);
    }

    @Override
    public T marshal(Object obj) {
        return null;
    }

    @Override
    public Object unmarshal(Object obj) {
        return null;
    }

}
