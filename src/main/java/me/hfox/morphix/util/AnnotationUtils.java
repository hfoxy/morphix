package me.hfox.morphix.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;

public final class AnnotationUtils {

    private AnnotationUtils() {
        // can't be accessed
    }

    public static String getFieldName(Field field) {
        return field.getName();
    }

    public static <T extends Annotation> T getHierarchicalAnnotation(Class<?> cls, Class<T> annoCls) {
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

        /*
        do {
            System.out.println("Annotation: " + anno);
            System.out.println("Class: " + cls);
            anno = cls.getAnnotation(annoCls);
            cls = cls.getSuperclass();
        } while (cls != Object.class && anno == null);
        */

        return anno;
    }

}
