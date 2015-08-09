package me.hfox.morphix.helper.lifecycle;

import me.hfox.morphix.Morphix;
import me.hfox.morphix.annotation.lifecycle.Lifecycle;
import me.hfox.morphix.exception.MorphixException;
import me.hfox.morphix.util.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultLifecycleHelper implements LifecycleHelper {

    private Morphix morphix;
    private Map<Class<?>, Map<Class<? extends Annotation>, List<Method>>> methods;

    public DefaultLifecycleHelper(Morphix morphix) {
        this.morphix = morphix;
        this.methods = new HashMap<>();
    }

    public List<Method> getMethods(Class<?> clazz, Class<? extends Annotation> anno) {
        return getMethods(clazz, anno, false);
    }

    public List<Method> getMethods(Class<?> clazz, Class<? extends Annotation> anno, boolean debug) {
        Map<Class<? extends Annotation>, List<Method>> annotations = methods.get(clazz);
        if (annotations == null) {
            annotations = new HashMap<>();

            boolean enabled = false;
            Lifecycle lifecycle = AnnotationUtils.getHierarchicalAnnotation(clazz, Lifecycle.class);
            if (lifecycle != null) {
                enabled = lifecycle.value();
            }

            for (Class<? extends Annotation> annotation : morphix.getEntityHelper().getLifecycleAnnotations()) {
                annotations.put(annotation, new ArrayList<Method>());
            }

            if (enabled) {
                List<Method> classMethods = morphix.getEntityHelper().getMethods(clazz);
                if (debug) {
                    System.out.println("Methods for " + clazz.getSimpleName() + ":");
                    for (Method method : classMethods) {
                        System.out.println("  " + method.getDeclaringClass().getSimpleName() + "#" + method.getName());
                        System.out.println("    Annotations:");

                        Annotation[] annos = method.getAnnotations();
                        for (Annotation annotation : annos) {
                            System.out.println("      " + annotation);
                        }
                    }
                }

                for (Method method : classMethods) {
                    method.setAccessible(true);
                    if (method.getParameterTypes().length > 0) {
                        continue;
                    }

                    if (debug) System.out.println("Started looping for annotations");
                    for (Class<? extends Annotation> annotation : morphix.getEntityHelper().getLifecycleAnnotations()) {
                        Annotation result = method.getAnnotation(annotation);
                        if (debug) System.out.println("Checking " + method.getDeclaringClass().getSimpleName()
                                + "#" + method.getName() + " for " + annotation.getSimpleName() + " (" + (result != null) + ")");
                        if (result != null) {
                            List<Method> annoMethods = annotations.get(annotation);
                            if (annoMethods == null) {
                                if (debug) System.out.println("Creating a new list of methods for " + annotation.getSimpleName());
                                annoMethods = new ArrayList<>();
                                annotations.put(annotation, annoMethods);
                            }

                            if (debug) System.out.println("Adding " + method + " to " + annoMethods);
                            annoMethods.add(method);
                        }
                    }
                    if (debug) System.out.println("Finished looping for annotations");
                }
            }

            methods.put(clazz, annotations);
        }

        return annotations.get(anno);
    }

    @Override
    public int call(Class<? extends Annotation> cls, Object object) {
        boolean debug = false;

        /*
        Class<?> type = null;

        try {
            type = Class.forName("net.forthwind.plugin.entity.ForthEntity");
            debug = type.isInstance(object);
        } catch (ClassNotFoundException ex) {
            // ignore
        }
        */

        int count = 0;
        List<Method> methods = getMethods(object.getClass(), cls, debug);
        if (methods == null) {
            System.out.println("Found null methods response for " + cls.getSimpleName() + " in " + object.getClass());
            return -1;
        }

        if (debug) {
            System.out.println("Methods for " + object + " (" + cls.getSimpleName() + "): " + methods);
        }

        for (Method method : methods) {
            try {
                method.invoke(object);
                count++;
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new MorphixException(ex);
            }
        }

        return count;
    }

}
