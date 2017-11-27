package me.hfox.morphix.mongo.helper.remap;

public interface RemapHelper {

    public <T> Class<T> remap(Class<T> cls);

    public <O, N extends O> void register(Class<O> original, Class<N> replacement);

}
