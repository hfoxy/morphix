package uk.hfox.morphix.transform;

import java.util.ArrayList;
import java.util.List;

public class IterableType {

    protected static final List<ConvertedType> types;

    public static List<ConvertedType> getTypes() {
        return new ArrayList<>(types);
    }

    static {
        types = new ArrayList<>();
    }

}
