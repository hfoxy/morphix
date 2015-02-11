package me.hfox.morphix.mapping.field;

import com.mongodb.DBObject;
import me.hfox.morphix.Morphix;
import me.hfox.morphix.MorphixDefaults;
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

    public abstract Object unmarshal(Object obj);

    public void marshal(DBObject dbObject, Object toMap) throws IllegalAccessException {
        Object value = field.get(toMap);
        dbObject.put(fieldName, marshal(value));
    }

    public abstract Object marshal(Object obj);

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
