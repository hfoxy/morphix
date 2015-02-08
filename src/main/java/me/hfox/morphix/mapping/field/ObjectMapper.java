package me.hfox.morphix.mapping.field;

import me.hfox.morphix.Morphix;

import java.lang.reflect.Field;

public class ObjectMapper<T> extends FieldMapper<T> {

    public ObjectMapper(Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        super(type, parent, field, morphix);
    }

    @Override
    public Object marshal(Object obj) {
        return obj;
    }

    @Override
    public Object unmarshal(Object obj) {
        return obj;
    }

}
