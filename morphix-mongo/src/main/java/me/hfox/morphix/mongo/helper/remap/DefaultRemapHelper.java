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
