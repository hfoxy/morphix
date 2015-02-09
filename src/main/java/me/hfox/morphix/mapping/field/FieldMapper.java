package me.hfox.morphix.mapping.field;

import com.mongodb.DBObject;
import me.hfox.morphix.Morphix;
import me.hfox.morphix.annotation.Entity;
import me.hfox.morphix.util.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public abstract class FieldMapper<T> {

    protected Class<?> type;
    protected Class<?> parent;
    protected Field field;
    protected String fieldName;
    protected Morphix morphix;

    public FieldMapper(Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        this.type = type;
        this.parent = parent;
        this.field = field;
        this.morphix = morphix;
        discover();
    }

    public Class<?> getParent() {
        return parent;
    }

    public Field getField() {
        return field;
    }

    public Morphix getMorphix() {
        return morphix;
    }

    protected void discover() {
        if (field != null) {
            field.setAccessible(true);
            fieldName = AnnotationUtils.getFieldName(field);
        }
    }

    public void marshal(DBObject dbObject, Object toMap) throws IllegalAccessException {
        Object store = null;
        Object value = dbObject.get(fieldName);
        if (value == null || value.getClass().isAssignableFrom(type)) {
            store = value;
        } else {
            marshal(value);
        }

        field.set(toMap, store);
    }

    public abstract Object marshal(Object obj);

    public void unmarshal(DBObject dbObject, Object toMap) throws IllegalAccessException {
        Object value = field.get(toMap);
        dbObject.put(fieldName, unmarshal(value));
    }

    public abstract Object unmarshal(Object obj);

    public static FieldMapper create(Class<?> parent, Field field, Morphix morphix) {
        return create(field == null ? parent : field.getType(), parent, field, morphix);
    }

    @SuppressWarnings("unchecked")
    public static <T> FieldMapper<T> create(Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        if (type.isEnum()) {
            return new EnumMapper<>(type, parent, field, morphix);
        } else if (Map.class.isAssignableFrom(type)) {
            return (FieldMapper<T>) new MapMapper(parent, field, morphix);
        } else if (Collection.class.isAssignableFrom(type)) {
            return (FieldMapper<T>) new CollectionMapper(parent, field, morphix);
        } else if (type.isArray()) {
            return new ArrayMapper<>(type, parent, field, morphix);
        } else if (AnnotationUtils.getHierarchicalAnnotation(type, Entity.class) != null) {
            return new EntityMapper<>(type, parent, field, morphix);
        } else {
            return new ObjectMapper<>(type, parent, field, morphix);
        }
    }

}
