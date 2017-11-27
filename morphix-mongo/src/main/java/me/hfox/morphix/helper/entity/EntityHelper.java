package me.hfox.morphix.helper.entity;

import me.hfox.morphix.annotation.entity.Entity;
import org.bson.types.ObjectId;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public interface EntityHelper {

    public List<Class<? extends Annotation>> getLifecycleMethodAnnotations();

    public List<Class<? extends Annotation>> getLifecycleFieldAnnotations();

    public String getCollectionName(Class<?> clazz);

    public String getCollectionName(Class<?> clazz, Entity entity);

    public ObjectId getObjectId(Object object);

    public ObjectId getObjectId(Object object, boolean error);

    public void setObjectId(Object object, ObjectId id);

    public List<Field> getFields(Class<?> clazz);

    public List<Field> getFields(Object object);

    public List<Method> getMethods(Class<?> clazz);

    public List<Method> getMethods(Object object);

}
