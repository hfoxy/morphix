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
package uk.hfox.morphix.mapper;

import uk.hfox.morphix.annotations.Lifecycle;
import uk.hfox.morphix.annotations.field.Properties;
import uk.hfox.morphix.annotations.field.Transient;
import uk.hfox.morphix.exception.mapper.MorphixEntityException;
import uk.hfox.morphix.exception.mapper.MorphixFieldException;
import uk.hfox.morphix.exception.mapper.MorphixMethodException;
import uk.hfox.morphix.mapper.lifecycle.LifecycleAction;
import uk.hfox.morphix.transform.Transformer;
import uk.hfox.morphix.utils.Conditions;
import uk.hfox.morphix.utils.search.EntitySearchCriteria;
import uk.hfox.morphix.utils.search.Search;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MappedEntity<F extends MappedField, T extends Transformer> {

    private final T transformer;

    private final Class<?> clazz;
    private final Lifecycle lifecycle;
    private final Map<LifecycleAction, List<Method>> lifecycleMethods;
    private final Map<LifecycleAction, List<Field>> lifecycleFields;
    private final Map<String, F> fields;

    public MappedEntity(T transformer, Class<?> clazz) {
        this.transformer = transformer;
        this.clazz = clazz;
        this.lifecycle = Search.getInheritedAnnotation(this.clazz, Lifecycle.class);
        this.lifecycleMethods = isLifecycle() ? new HashMap<>() : null;
        this.lifecycleFields = isLifecycle() ? new HashMap<>() : null;
        this.fields = new HashMap<>();
    }

    /**
     * Gets the transformer used by this entity
     *
     * @return The transformer used by this entity
     */
    public T getTransformer() {
        return transformer;
    }

    /**
     * Determines if this mapped entity has lifecycle support enabled or disabled
     *
     * @return true if the lifecycle annotation is present and enabled, otherwise false
     */
    public boolean isLifecycle() {
        return this.lifecycle != null && this.lifecycle.value();
    }

    /**
     * Gets a map of the fields used by this entity
     *
     * @return The map of fields
     */
    public Map<String, F> getFields() {
        return fields;
    }

    /**
     * Clears and repopulates the entity with new mapped fields
     */
    public void map() {
        if (this.lifecycleMethods != null) {
            this.lifecycleMethods.clear();
            LifecycleAction.populateMethods(clazz, this.lifecycleMethods);
        }

        if (this.lifecycleFields != null) {
            this.lifecycleFields.clear();
            LifecycleAction.populateFields(clazz, this.lifecycleFields);
        }

        List<Field> allFields = Search.getAllFields(this.clazz, new EntitySearchCriteria());
        for (Field field : allFields) {
            insert(field);
        }
    }

    /**
     * Inserts fully validated fields into the list of fields
     *
     * @param field The field to validate and insert
     */
    private void insert(Field field) {
        Transient ignored = field.getAnnotation(Transient.class);
        if (ignored != null) {
            return;
        }

        if (this.fields.containsKey(field.getName())) {
            throw new MorphixEntityException("field name '" + field.getName() + "' already exists");
        }

        F mapped = getField(field.getAnnotation(Properties.class), field);
        this.fields.put(field.getName(), mapped);
    }

    /**
     * Gets the mapped field instance for the supplied reflective field
     *
     * @param field The reflective field
     *
     * @return A new mapped field instance
     */
    protected abstract F getField(Properties properties, Field field);

    public void call(LifecycleAction action, Object entity) {
        Conditions.notNull(action);
        Conditions.notNull(entity);

        if (action.isField()) {
            throw new IllegalArgumentException("action is of field type (" + action.name() + ")");
        }

        List<Method> methods = this.lifecycleMethods.get(action);
        callMethods(methods, entity);
    }

    private void callMethods(List<Method> methods, Object entity) {
        for (Method method : methods) {
            try {
                method.invoke(entity);
            } catch (Exception ex) {
                throw new MorphixMethodException("Unable to invoke method", ex);
            }
        }
    }

    public void set(LifecycleAction action, Object entity, Object value) {
        Conditions.notNull(action);
        Conditions.notNull(entity);

        if (!action.isField()) {
            throw new IllegalArgumentException("action is of field type (" + action.name() + ")");
        }

        List<Field> list = this.lifecycleFields.get(action);
        setFields(list, entity, value);
    }

    private void setFields(List<Field> fields, Object entity, Object value) {
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                field.set(entity, value);
            } catch (Exception ex) {
                throw new MorphixFieldException("Unable to set value on field", ex);
            }
        }
    }

}
