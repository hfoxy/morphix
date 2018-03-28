package uk.hfox.morphix.mapper.builder.collection;

import java.util.LinkedList;

public class LinkedListBuilder implements CollectionBuilder {

    @Override
    public LinkedList build() {
        return new LinkedList();
    }

}
