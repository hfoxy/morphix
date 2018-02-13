/*-
 * ========================LICENSE_START========================
 * Morphix API
 * %%
 * Copyright (C) 2017 - 2018 Harry Fox
 * %%
 * This file is part of Morphix, licensed under the MIT License (MIT).
 *
 * Copyright 2018 Harry Fox <https://hfox.uk/>
 * Copyright 2018 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * ========================LICENSE_END========================
 */
package uk.hfox.morphix.mapper.lifecycle;

import uk.hfox.morphix.annotations.field.Transient;
import uk.hfox.morphix.annotations.lifecycle.field.AccessedAt;
import uk.hfox.morphix.annotations.lifecycle.field.CreatedAt;
import uk.hfox.morphix.annotations.lifecycle.field.UpdatedAt;
import uk.hfox.morphix.annotations.lifecycle.method.AfterCreate;
import uk.hfox.morphix.annotations.lifecycle.method.BeforeCreate;
import uk.hfox.morphix.exception.mapper.MorphixEntityException;
import uk.hfox.morphix.utils.Conditions;
import uk.hfox.morphix.utils.search.Search;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum LifecycleAction {

    BEFORE_CREATE(BeforeCreate.class),
    AFTER_CREATE(AfterCreate.class),
    CREATED_AT(CreatedAt.class, true, LocalDateTime.class),
    ACCESSED_AT(AccessedAt.class, true, LocalDateTime.class),
    UPDATED_AT(UpdatedAt.class, true, LocalDateTime.class);

    private final boolean field;
    private final Class<? extends Annotation> annotation;
    private final Class<?> fieldType;

    LifecycleAction(Class<? extends Annotation> annotation) {
        this(annotation, false, null);
    }

    LifecycleAction(Class<? extends Annotation> annotation, boolean field, Class<?> fieldType) {
        this.annotation = annotation;
        this.field = field;

        if (field) {
            Conditions.notNull(fieldType);
        }

        this.fieldType = fieldType;
    }

    /**
     * Returns if this action is designed for fields or methods
     *
     * @return true if this is a field-only annotation, false if this is a method-only annotation
     */
    public boolean isField() {
        return field;
    }

    /**
     * Checks if the provided field is supported by this lifecycle action
     *
     * @param field The field to check
     *
     * @return true if it is supported, otherwise false
     */
    public boolean isSupported(Field field) {
        if (!this.field) {
            return false;
        }

        Annotation anno = field.getAnnotation(this.annotation);
        if (anno == null) {
            return false;
        }

        if (field.getAnnotation(Transient.class) != null) {
            String message = "lifecycle field " + field.getName() + " in " + field.getDeclaringClass().getName()
                    + " is declared as transient";

            throw new MorphixEntityException(message);
        }

        boolean stc = Modifier.isStatic(field.getModifiers());
        if (stc) {
            String message = "lifecycle field " + field.getName() + " in " + field.getDeclaringClass().getName()
                    + " is declared as static";

            throw new MorphixEntityException(message);
        }

        return this.fieldType.isAssignableFrom(field.getType());
    }

    /**
     * Checks if the provided method is supported by this lifecycle action
     *
     * @param method The method to check
     *
     * @return true if it is supported, otherwise false
     */
    public boolean isSupported(Method method) {
        if (this.field) {
            return false;
        }

        Annotation anno = method.getAnnotation(this.annotation);
        if (anno == null) {
            return false;
        }

        boolean stc = Modifier.isStatic(method.getModifiers());
        if (stc) {
            String message = "lifecycle method " + method.getName() + " in " + method.getDeclaringClass().getName()
                    + " is declared as static";

            throw new MorphixEntityException(message);
        }

        Class<?>[] args = method.getParameterTypes();
        if (args.length != 0) {
            String message = "lifecycle method " + method.getName() + " in " + method.getDeclaringClass().getName()
                    + " is declared with " + args.length + " parameters";

            throw new MorphixEntityException(message);
        }

        return true;
    }

    public static void populateMethods(Class<?> clazz, Map<LifecycleAction, List<Method>> lifecycle) {
        for (LifecycleAction action : values()) {
            if (!action.isField()) {
                lifecycle.put(action, new ArrayList<>());
            }
        }

        List<Method> methods = Search.getAllMethods(clazz);
        for (Method method : methods) {
            for (LifecycleAction action : values()) {
                if (action.isSupported(method)) {
                    lifecycle.get(action).add(method);
                }
            }
        }
    }

    public static void populateFields(Class<?> clazz, Map<LifecycleAction, List<Field>> lifecycle) {
        for (LifecycleAction action : values()) {
            if (action.isField()) {
                lifecycle.put(action, new ArrayList<>());
            }
        }

        List<Field> fields = Search.getAllFields(clazz);
        for (Field field : fields) {
            for (LifecycleAction action : values()) {
                if (action.isSupported(field)) {
                    lifecycle.get(action).add(field);
                }
            }
        }
    }

}
