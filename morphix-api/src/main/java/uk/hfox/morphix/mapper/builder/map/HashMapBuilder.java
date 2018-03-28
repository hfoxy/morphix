package uk.hfox.morphix.mapper.builder.map;

import java.util.HashMap;

public class HashMapBuilder implements MapBuilder {

    @Override
    public HashMap build() {
        return new HashMap();
    }

}
