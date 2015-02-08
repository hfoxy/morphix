package me.hfox.morphix.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class AnnotationHelper {

    public static String getFieldName(Field field) {
        return field.getName();
    }

    public static <T extends Annotation> T getHierarchicalAnnotation(Class<?> cls, Class<T> annoCls) {
        T anno = null;
        do {
            anno = cls.getAnnotation(annoCls);
        } while (anno == null && cls != Object.class);

        return anno;
    }

}
