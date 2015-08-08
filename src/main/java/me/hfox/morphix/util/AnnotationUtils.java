package me.hfox.morphix.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

public final class AnnotationUtils {

    private AnnotationUtils() {
        // can't be accessed
    }

    public static String getFieldName(Field field) {
        return field.getName();
    }

    public static <T extends Annotation> T getHierarchicalAnnotation(Class<?> cls, Class<T> annoCls) {
        Entry<Class<?>, T> entry = getClassAnnotation(cls, annoCls);
        if (entry == null) {
            return null;
        }

        return entry.getValue();
    }

    public static <T extends Annotation> Entry<Class<?>, T> getClassAnnotation(Class<?> cls, Class<T> annoCls) {
        T anno = null;
        while (true) {
            anno = cls.getAnnotation(annoCls);
            if (anno != null) {
                break;
            }

            Class<?>[] interfaces = cls.getInterfaces();
            for (Class<?> face : interfaces) {
                T result = getHierarchicalAnnotation(face, annoCls);
                if (result != null) {
                    anno = result;
                    break;
                }
            }

            cls = cls.getSuperclass();
            if (cls == null || cls == Object.class) {
                break;
            }
        }

        if (anno == null) {
            return null;
        }

        return new SimpleEntry<Class<?>, T>(cls, anno);
    }

    public static <T extends Annotation> Entry<Class<?>, T> getHighestClassAnnotation(Class<?> cls, Class<T> annoCls) {
        T end = null;
        Class<?> endCls = null;

        T anno = null;
        while (true) {
            anno = cls.getAnnotation(annoCls);
            if (anno != null) {
                end = anno;
                endCls = cls;
            }

            Class<?>[] interfaces = cls.getInterfaces();
            for (Class<?> face : interfaces) {
                Entry<Class<?>, T> result = getHighestClassAnnotation(face, annoCls);
                if (result != null) {
                    anno = result.getValue();
                    end = anno;
                    endCls = result.getKey();
                }
            }

            cls = cls.getSuperclass();
            if (cls == null || cls == Object.class) {
                break;
            }
        }

        if (end == null) {
            return null;
        }

        return new SimpleEntry<Class<?>, T>(endCls, end);
    }

}
