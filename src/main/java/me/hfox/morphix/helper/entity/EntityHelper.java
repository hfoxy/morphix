package me.hfox.morphix.helper.entity;

import me.hfox.morphix.annotation.entity.Entity;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.util.List;

public interface EntityHelper {

    public String getCollectionName(Class<?> clazz);

    public String getCollectionName(Class<?> clazz, Entity entity);

    public ObjectId getObjectId(Object object);

    public List<Field> getFields(Class<?> clazz);

    public List<Field> getFields(Object object);

}
