package me.hfox.morphix.helper.lifecycle;

import me.hfox.morphix.Morphix;
import me.hfox.morphix.annotation.lifecycle.AccessedAt;
import me.hfox.morphix.annotation.lifecycle.CreatedAt;
import me.hfox.morphix.annotation.lifecycle.Lifecycle;
import me.hfox.morphix.annotation.lifecycle.UpdatedAt;
import me.hfox.morphix.exception.MorphixException;
import me.hfox.morphix.util.AnnotationUtils;
import org.joda.time.DateTime;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultLifecycleHelper implements LifecycleHelper {

    private Morphix morphix;
    private Map<Class<?>, Map<Class<? extends Annotation>, List<Method>>> methods;
    private Map<Class<?>, Map<Class<? extends Annotation>, List<Field>>> fields;

    public DefaultLifecycleHelper(Morphix morphix) {
        this.morphix = morphix;
        this.methods = new HashMap<>();
        this.fields = new HashMap<>();
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
    public int callField(Class<? extends Annotation> cls, Object object) {
        return callField(cls, object, false);
    }

    @Override
    public int callField(Class<? extends Annotation> cls, Object object, boolean update) {
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
        List<Field> fields = getFields(object.getClass(), cls, debug);
        if (fields == null) {
            System.out.println("Found null fields response for " + cls.getSimpleName() + " in " + object.getClass());
            return -1;
        }

        if (debug) {
            System.out.println("Fields for " + object + " (" + cls.getSimpleName() + "): " + fields);
        }

        for (Field field : fields) {
            TimeLibrary library = TimeLibrary.DEFAULT;
            Annotation anno = field.getAnnotation(cls);
            if (anno instanceof AccessedAt) {
                library = ((AccessedAt) anno).value();
                if (((AccessedAt) anno).onlyUpdateOnLoad() && update) {
                    continue;
                }
            } else if (anno instanceof CreatedAt) {
                library = ((CreatedAt) anno).value();
            } else if (anno instanceof UpdatedAt) {
                library = ((UpdatedAt) anno).value();
            }

            Object time = null;
            switch (library) {
                case DEFAULT:
                    time = new Date();
                    break;
                case JODA:
                    time = DateTime.now();
                    break;
            }

            try {
                field.set(object, time);
                count++;
            } catch (IllegalAccessException ex) {
                throw new MorphixException(ex);
            }
        }

        return count;
    }

    public List<Field> getFields(Class<?> clazz, Class<? extends Annotation> anno) {
        return getFields(clazz, anno, false);
    }

    public List<Field> getFields(Class<?> clazz, Class<? extends Annotation> anno, boolean debug) {
        Map<Class<? extends Annotation>, List<Field>> annotations = fields.get(clazz);
        if (annotations == null) {
            annotations = new HashMap<>();

            boolean enabled = true;

            /*
            Lifecycle lifecycle = AnnotationUtils.getHierarchicalAnnotation(clazz, Lifecycle.class);
            if (lifecycle != null) {
                enabled = lifecycle.value();
            }
            */

            for (Class<? extends Annotation> annotation : morphix.getEntityHelper().getLifecycleAnnotations()) {
                annotations.put(annotation, new ArrayList<Field>());
            }

            if (enabled) {
                List<Field> classFields = morphix.getEntityHelper().getFields(clazz);
                if (debug) {
                    System.out.println("Fields for " + clazz.getSimpleName() + ":");
                    for (Field field : classFields) {
                        System.out.println("  " + field.getDeclaringClass().getSimpleName() + "#" + field.getName());
                        System.out.println("    Annotations:");

                        Annotation[] annos = field.getAnnotations();
                        for (Annotation annotation : annos) {
                            System.out.println("      " + annotation);
                        }
                    }
                }

                for (Field field : classFields) {
                    field.setAccessible(true);
                    if (debug) System.out.println("Started looping for annotations");
                    for (Class<? extends Annotation> annotation : morphix.getEntityHelper().getLifecycleAnnotations()) {
                        Annotation result = field.getAnnotation(annotation);
                        if (debug) System.out.println("Checking " + field.getDeclaringClass().getSimpleName()
                                + "#" + field.getName() + " for " + annotation.getSimpleName() + " (" + (result != null) + ")");
                        if (result != null) {
                            List<Field> annoFields = annotations.get(annotation);
                            if (annoFields == null) {
                                if (debug) System.out.println("Creating a new list of fields for " + annotation.getSimpleName());
                                annoFields = new ArrayList<>();
                                annotations.put(annotation, annoFields);
                            }

                            if (debug) System.out.println("Adding " + field + " to " + annoFields);
                            annoFields.add(field);
                        }
                    }
                    if (debug) System.out.println("Finished looping for annotations");
                }
            }

            fields.put(clazz, annotations);
        }

        return annotations.get(anno);
    }

    @Override
    public int callMethod(Class<? extends Annotation> cls, Object object) {
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
