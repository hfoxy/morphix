package uk.hfox.morphix.mapper.builder.collection;

import java.util.Collection;

public interface CollectionBuilder {

    /**
     * Builds a collection with the specified generic type
     *
     * @return The new collection
     */
    Collection build();

}
