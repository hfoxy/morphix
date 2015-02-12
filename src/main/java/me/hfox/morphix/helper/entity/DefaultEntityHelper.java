package me.hfox.morphix.helper.entity;

import me.hfox.morphix.Morphix;
import me.hfox.morphix.MorphixDefaults;
import me.hfox.morphix.annotation.Id;
import me.hfox.morphix.annotation.entity.Entity;
import me.hfox.morphix.exception.MorphixException;
import me.hfox.morphix.mapping.field.FieldMapper;
import me.hfox.morphix.util.AnnotationUtils;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultEntityHelper implements EntityHelper {

    private Morphix morphix;

    public DefaultEntityHelper(Morphix morphix) {
        this.morphix = morphix;
    }

    @Override
    public String getCollectionName(Class<?> clazz) {
        Entity entity = AnnotationUtils.getHierarchicalAnnotation(clazz, Entity.class);
        if (entity == null) {
            throw new MorphixException("No Entity annotation could be found for " + clazz.getSimpleName());
        }

        return getCollectionName(clazz, entity);
    }

    @Override
    public String getCollectionName(Class<?> clazz, Entity entity) {
        String name;
        if (entity.value() == null || entity.value().equals(MorphixDefaults.DEFAULT_COLLECTION_NAME)) {
            name = morphix.getNameHelper(entity.nameHelper()).generate(clazz);
        } else {
            name = entity.value();
        }

        return name;
    }

    @Override
    public ObjectId getObjectId(Object object) {
        return getObjectId(object, false);
    }

    @Override
    public ObjectId getObjectId(Object object, boolean error) {
        for (Field field : getFields(object)) {
            if (field.getAnnotation(Id.class) != null) {
                try {
                    return (ObjectId) field.get(object);
                } catch (IllegalAccessException ex) {
                    throw new MorphixException(ex);
                }
            }
        }

        if (error) {
            throw new MorphixException("No @Id found for " + object.getClass().getSimpleName());
        }

        return null;
    }

    @Override
    public void setObjectId(Object object, ObjectId id) {
        for (Field field : getFields(object)) {
            if (field.getAnnotation(Id.class) != null) {
                try {
                    field.set(object, id);
                } catch (IllegalAccessException ex) {
                    throw new MorphixException(ex);
                }
            }
        }
    }

    public List<Field> getFields(Object object) {
        return getFields(object.getClass());
    }

    public List<Field> getFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();

        do {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                fields.add(field);
            }

            clazz = clazz.getSuperclass();
        } while (cont(clazz));

        return fields;
    }

    private boolean cont(Class<?> cls) {
        if (cls == Object.class) {
            return false;
        }

        Entity entity = cls.getAnnotation(Entity.class);
        return entity == null || entity.inheritParentFields();
    }

}
