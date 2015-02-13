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
                for (Method method : classMethods) {
                    method.setAccessible(true);
                    if (method.getParameterCount() > 0) {
                        continue;
                    }

                    for (Class<? extends Annotation> annotation : morphix.getEntityHelper().getLifecycleAnnotations()) {
                        Annotation result = method.getAnnotation(annotation);
                        if (result != null) {
                            List<Method> annoMethods = annotations.get(annotation);
                            if (annoMethods == null) {
                                annoMethods = new ArrayList<>();
                                annotations.put(annotation, annoMethods);
                            }

                            annoMethods.add(method);
                        }
                    }
                }
            }

            methods.put(clazz, annotations);
        }

        return annotations.get(anno);
    }

    @Override
    public int call(Class<? extends Annotation> cls, Object object) {
        int count = 0;
        List<Method> methods = getMethods(object.getClass(), cls);
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
