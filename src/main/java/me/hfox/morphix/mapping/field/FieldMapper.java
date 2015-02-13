package me.hfox.morphix.mapping.field;

import com.mongodb.DBObject;
import me.hfox.morphix.Morphix;
import me.hfox.morphix.MorphixDefaults;
import me.hfox.morphix.annotation.Id;
import me.hfox.morphix.annotation.Property;
import me.hfox.morphix.annotation.Transient;
import me.hfox.morphix.annotation.entity.Entity;
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
        this(type, parent, field, morphix, true);
    }

    public FieldMapper(Class<T> type, Class<?> parent, Field field, Morphix morphix, boolean discover) {
        this.type = type;
        this.parent = parent;
        this.field = field;
        this.morphix = morphix;

        if (discover) {
            discover();
        }
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

            String name = ".";
            Property property = field.getAnnotation(Property.class);
            if (property != null) {
                name = property.value();
            }

            if (name == null || name.equals(MorphixDefaults.DEFAULT_FIELD_NAME)) {
                name = AnnotationUtils.getFieldName(field);
            }

            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                name = "_id";
            }

            fieldName = name;
        }
    }

    public void unmarshal(DBObject dbObject, Object toMap) throws IllegalAccessException {
        Object store = null;
        Object value = dbObject.get(fieldName);
        if (value == null || value.getClass().isAssignableFrom(type)) {
            store = value;
        } else {
            unmarshal(value);
        }

        field.set(toMap, store);
    }

    public Object unmarshal(Object obj) {
        return unmarshal(obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    public abstract Object unmarshal(Object obj, boolean lifecycle);

    public void marshal(DBObject dbObject, Object toMap) throws IllegalAccessException {
        Object value = field.get(toMap);
        dbObject.put(fieldName, marshal(value));
    }

    public Object marshal(Object obj) {
        return marshal(obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    public abstract Object marshal(Object obj, boolean lifecycle);

    public static FieldMapper create(Class<?> parent, Field field, Morphix morphix) {
        return create(field == null ? parent : field.getType(), parent, field, morphix);
    }

    @SuppressWarnings("unchecked")
    public static <T> FieldMapper<T> create(Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        if (field != null && field.getAnnotation(Transient.class) != null) {
            return null;
        }

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
