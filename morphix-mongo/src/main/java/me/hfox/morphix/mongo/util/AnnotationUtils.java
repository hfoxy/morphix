package me.hfox.morphix.mongo.util;

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
        boolean debug = false;
        /*
        Class<?> type = null;

        try {
            type = Class.forName("net.forthwind.plugin.inventory.BaseInventory");
            debug = type.isAssignableFrom(cls) && Entity.class.equals(annoCls);
        } catch (ClassNotFoundException ex) {
            // ignore
        }
        */

        T anno = null;
        if (debug) System.out.println("START! Annotation (" + (cls == null ? "unknown" : cls.getName()) + "): " + (anno == null ? "none" : anno));
        boolean broke = false;
        while (true) {
            anno = cls.getAnnotation(annoCls);
            if (debug) System.out.println("Annotation (" + cls.getName() + "): " + (anno == null ? "none" : anno));
            if (anno != null) {
                if (debug) System.out.println("Breaking loop with " + anno + " from " + cls.getName());
                broke = true;
                break;
            }

            boolean done = false;
            Class<?>[] interfaces = cls.getInterfaces();
            for (Class<?> face : interfaces) {
                if (debug) System.out.println("Testing " + face.getName() + " for " + cls.getSimpleName());
                Entry<Class<?>, T> result = getClassAnnotation(face, annoCls);
                if (result != null) {
                    anno = result.getValue();
                    cls = result.getKey();
                    done = true;
                    break;
                }
            }

            if (done) break;

            cls = cls.getSuperclass();
            if (cls == null || cls == Object.class) {
                break;
            }
        }

        if (anno == null) {
            if (debug) System.out.println("Broke? " + broke);
            return null;
        }

        if (debug) System.out.println("RESULT! Annotation (" + (cls == null ? "unknown" : cls.getName()) + "): " + (anno == null ? "none" : anno));
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
