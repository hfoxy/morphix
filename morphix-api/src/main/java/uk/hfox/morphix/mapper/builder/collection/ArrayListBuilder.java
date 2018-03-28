package uk.hfox.morphix.mapper.builder.collection;

import java.util.ArrayList;

public class ArrayListBuilder implements CollectionBuilder {

    @Override
    public ArrayList build() {
        return new ArrayList();
    }

}
