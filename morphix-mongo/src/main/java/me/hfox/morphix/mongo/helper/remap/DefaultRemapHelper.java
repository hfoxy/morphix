/*-
 * ========================LICENSE_START========================
 * Morphix Mongo
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
package me.hfox.morphix.mongo.helper.remap;

import java.util.ArrayList;
import java.util.List;

public class DefaultRemapHelper implements RemapHelper {

    private List<ClassEntry<?, ?>> classes = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public <O> ClassEntry<O, ?> getEntry(Class<O> cls) {
        for (ClassEntry<?, ?> entry : classes) {
            if (entry.matches(cls)) {
                return (ClassEntry<O, ?>) entry;
            }
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Class<T> remap(Class<T> cls) {
        ClassEntry<T, ?> entry = getEntry(cls);
        if (entry == null) {
            return cls;
        }

        return (Class<T>) entry.getReplacement();
    }

    @Override
    public <O, R extends O> void register(Class<O> original, Class<R> replacement) {
        ClassEntry<O, ?> entry = getEntry(original);
        if (entry != null) {
            classes.remove(entry);
        }

        entry = new ClassEntry<>(original, replacement);
        classes.add(entry);
    }

    public class ClassEntry<O, R extends O> {

        private Class<O> original;
        private Class<R> replacement;

        public ClassEntry(Class<O> original, Class<R> replacement) {
            this.original = original;
            this.replacement = replacement;
        }

        public Class<O> getOriginal() {
            return original;
        }

        public Class<R> getReplacement() {
            return replacement;
        }

        public boolean matches(Class<?> cls) {
            return original.equals(cls);
        }

    }

}
