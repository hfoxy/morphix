package me.hfox.morphix.mapping.field;

import me.hfox.morphix.Morphix;
import me.hfox.morphix.MorphixDefaults;
import me.hfox.morphix.annotation.Id;
import me.hfox.morphix.annotation.Property;
import me.hfox.morphix.annotation.Transient;
import me.hfox.morphix.annotation.entity.Entity;
import me.hfox.morphix.mapping.MappingData;
import me.hfox.morphix.util.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class FieldMapper<T> {

    private static FieldMappers mappers = new FieldMappers();

    protected MappingData mappingData;

    protected FieldData data;
    protected Class<?> type;
    protected Class<?> parent;
    protected Field field;
    protected String fieldName;
    protected Morphix morphix;

    public FieldMapper(MappingData mappingData, Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        this(mappingData, type, parent, field, morphix, true);
    }

    public FieldMapper(MappingData mappingData, Class<T> type, Class<?> parent, Field field, Morphix morphix, boolean discover) {
        this.mappingData = mappingData;
        this.data = new FieldData(type, parent, field, morphix);
        this.type = type;
        this.parent = parent;
        this.field = field;
        this.morphix = morphix;
        mappers.put(data, this);

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
            fieldName = getName(field);
        }
    }

    /*
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
    */

    public Object unmarshal(Object obj) {
        return unmarshal(obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    public abstract Object unmarshal(Object obj, boolean lifecycle);

    /*
    public void marshal(DBObject dbObject, Object toMap) throws IllegalAccessException {
        Object value = field.get(toMap);
        dbObject.put(fieldName, marshal(value));
    }
    */

    public Object marshal(Object obj) {
        return marshal(obj, MorphixDefaults.DEFAULT_LIFECYCLE);
    }

    public abstract Object marshal(Object obj, boolean lifecycle);

    private static String getName(Field field) {
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

        return name;
    }

    public static FieldMapper createFromName(MappingData mappingData, Class<?> parent, Class<?> type, String fieldName, Morphix morphix) {
        List<Field> fields = morphix.getEntityHelper().getFields(parent);
        for (Field field : fields) {
            String name = getName(field);
            if (name.equals(fieldName)) {
                return createFromField(mappingData, type, parent, field, morphix);
            }
        }

        return createFromField(mappingData, type, parent, null, morphix);
    }

    public static FieldMapper createFromField(MappingData mappingData, Class<?> parent, Field field, Morphix morphix) {
        return createFromField(mappingData, field == null ? parent : field.getType(), parent, field, morphix);
    }

    @SuppressWarnings("unchecked")
    public static <T> FieldMapper<T> createFromField(MappingData mappingData, Class<T> type, Class<?> parent, Field field, Morphix morphix) {
        if (field != null && field.getAnnotation(Transient.class) != null) {
            return null;
        }

        FieldMapper<T> mapper = mappers.get(new FieldData(type, parent, field, morphix));
        if (mapper != null) {
            return mapper;
        }

        if (type.isEnum()) {
            return new EnumMapper<>(mappingData, type, parent, field, morphix);
        } else if (Map.class.isAssignableFrom(type)) {
            return (FieldMapper<T>) new MapMapper(mappingData, parent, field, morphix);
        } else if (Collection.class.isAssignableFrom(type)) {
            return (FieldMapper<T>) new CollectionMapper(mappingData, parent, field, morphix);
        } else if (type.isArray()) {
            return new ArrayMapper<>(mappingData, type, parent, field, morphix);
        } else if (AnnotationUtils.getHierarchicalAnnotation(type, Entity.class) != null) {
            return new EntityMapper<>(mappingData, type, parent, field, morphix);
        } else {
            return new ObjectMapper<>(mappingData, type, parent, field, morphix);
        }
    }

}
