package uk.hfox.morphix.helper;

import uk.hfox.morphix.connector.MorphixConnector;
import uk.hfox.morphix.mapper.builder.map.MapBuilder;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class MapBuilderHelper<C extends MorphixConnector> extends Helper<C> {

    private final Map<Class<? extends MapBuilder>, MapBuilder> builders;

    public MapBuilderHelper(C connector) {
        super(connector);
        this.builders = new HashMap<>();
    }

    public MapBuilder getOrCreate(Class<? extends MapBuilder> type) {
        MapBuilder builder = this.builders.get(type);
        if (builder == null) {
            try {
                Constructor<? extends MapBuilder> constructor = type.getConstructor();
                builder = constructor.newInstance();
                this.builders.put(type, builder);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Unable to construct " + type.getName(), ex);
            }
        }

        return builder;
    }

}
