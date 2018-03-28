package uk.hfox.morphix.mapper.builder.collection;

import java.util.HashSet;

public class HashSetBuilder implements CollectionBuilder {

    @Override
    public HashSet build() {
        return new HashSet();
    }

}
