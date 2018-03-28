package uk.hfox.morphix.helper;

import uk.hfox.morphix.connector.MorphixConnector;
import uk.hfox.morphix.mapper.builder.collection.CollectionBuilder;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class CollectionBuilderHelper<C extends MorphixConnector> extends Helper<C> {

    private final Map<Class<? extends CollectionBuilder>, CollectionBuilder> builders;

    public CollectionBuilderHelper(C connector) {
        super(connector);
        this.builders = new HashMap<>();
    }

    public CollectionBuilder getOrCreate(Class<? extends CollectionBuilder> type) {
        CollectionBuilder builder = this.builders.get(type);
        if (builder == null) {
            try {
                Constructor<? extends CollectionBuilder> constructor = type.getConstructor();
                builder = constructor.newInstance();
                this.builders.put(type, builder);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Unable to construct " + type.getName(), ex);
            }
        }

        return builder;
    }

}
