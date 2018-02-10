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
package uk.hfox.morphix.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Search {

    Search() {
        throw new UnsupportedOperationException();
    }

    /**
     * Follows the class inheritance tree until the annotation can be found.
     * If the annotation is not available in the tree, null is returned.
     *
     * @param clazz      The base class to check for the annotation
     * @param annotation The class of annotation to search for
     * @param <T>        The generic type of the annotation
     *
     * @return The first annotation in the reverse lookup, null if no annotation is found
     */
    public static <T extends Annotation> T getInheritedAnnotation(Class<?> clazz, Class<T> annotation) {
        Conditions.notNull(clazz, "clazz");
        Conditions.notNull(annotation, "annotation");

        if (clazz.isAssignableFrom(Object.class)) {
            return null;
        }

        T anno = clazz.getAnnotation(annotation);
        if (anno == null) {
            return getInheritedAnnotation(clazz.getSuperclass(), annotation);
        }

        return anno;
    }

    /**
     * Gets a complete list of all fields for the provided class and it's superclasses
     *
     * @param clazz The class to search
     *
     * @return The full list of fields
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        Conditions.notNull(clazz);

        List<Field> fields = new ArrayList<>();

        Class<?> superClazz = clazz.getSuperclass();
        if (!superClazz.isAssignableFrom(Object.class)) {
            fields.addAll(getAllFields(superClazz));
        }

        for (Field field : clazz.getDeclaredFields()) {
            System.out.println(field.getName());
            if (field.getName().equals("$jacocoData")) {
                continue;
            }

            fields.add(field);
        }

        return fields;
    }

    /**
     * Gets a complete list of all methods for the provided class and it's superclasses
     *
     * @param clazz The class to search
     *
     * @return The full list of methods
     */
    public static List<Method> getAllMethods(Class<?> clazz) {
        Conditions.notNull(clazz);

        List<Method> methods = new ArrayList<>();

        Class<?> superClazz = clazz.getSuperclass();
        if (!superClazz.isAssignableFrom(Object.class)) {
            methods.addAll(getAllMethods(superClazz));
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals("$jacocoInit")) {
                continue;
            }

            methods.add(method);
        }

        return methods.stream().distinct().collect(Collectors.toList());
    }

}
