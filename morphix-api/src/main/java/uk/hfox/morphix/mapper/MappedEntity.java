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
import uk.hfox.morphix.mapper.lifecycle.LifecycleAction;
import uk.hfox.morphix.transform.Converter;
import uk.hfox.morphix.utils.Search;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappedEntity<T> {

    private final Class<?> clazz;
    private final Map<LifecycleAction, List<Method>> lifecycleMethods;
    private final Map<Field, Converter<T>> fields;

    public MappedEntity(Class<?> clazz) {
        this.clazz = clazz;
        this.lifecycleMethods = getLifecycleMethods();
        this.fields = new HashMap<>();
    }

    /**
     * Decides if lifecycle support should be enabled and creates a hashmap accordindly.
     *
     * @return An empty hashmap, or null if lifecycle support is disabled
     */
    private Map<LifecycleAction, List<Method>> getLifecycleMethods() {
        Lifecycle anno = Search.getInheritedAnnotation(this.clazz, Lifecycle.class);
        return anno != null && anno.value() ? new HashMap<>() : null;
    }

    /**
     * Clears and repopulates the entity with new mapped fields
     */
    public void map() {
        if (this.lifecycleMethods != null) {
            this.lifecycleMethods.clear();
            LifecycleAction.populateMethods(clazz, this.lifecycleMethods);
        }
    }

}
